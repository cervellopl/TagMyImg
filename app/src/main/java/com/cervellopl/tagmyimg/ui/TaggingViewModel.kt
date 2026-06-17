package com.cervellopl.tagmyimg.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cervellopl.tagmyimg.BuildConfig
import com.cervellopl.tagmyimg.data.AppDatabase
import com.cervellopl.tagmyimg.data.ImageTagRepository
import com.cervellopl.tagmyimg.data.ImageWithTags
import com.cervellopl.tagmyimg.data.PlantNetApiService
import com.cervellopl.tagmyimg.data.TagEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaggingUiState(
    val selectedImageUri: Uri? = null,
    val currentTags: List<TagEntity> = emptyList(),
    val isLabeling: Boolean = false,
    val savedImages: List<ImageWithTags> = emptyList(),
    val error: String? = null
)

class TaggingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageTagRepository(
        AppDatabase.getInstance(application).imageDao()
    )
    private val plantNet = PlantNetApiService(BuildConfig.PLANTNET_API_KEY)

    private val _uiState = MutableStateFlow(TaggingUiState())
    val uiState: StateFlow<TaggingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getImagesWithTags().collect { images ->
                _uiState.update { it.copy(savedImages = images) }
            }
        }
    }

    fun onImagePicked(uri: Uri) {
        val context = getApplication<Application>()
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Transient-access URIs don't support persistable permissions — proceed anyway
        }

        _uiState.update { it.copy(selectedImageUri = uri, isLabeling = true, error = null, currentTags = emptyList()) }

        viewModelScope.launch {
            try {
                val results = plantNet.identify(context, uri)
                val tagEntities = results.map { r ->
                    TagEntity(imageId = 0, label = r.label, confidence = r.score)
                }
                _uiState.update { it.copy(currentTags = tagEntities, isLabeling = false) }
                repository.saveImageWithTags(uri.toString(), results.map { it.label to it.score })
            } catch (e: Exception) {
                _uiState.update { it.copy(isLabeling = false, error = e.message) }
            }
        }
    }
}
