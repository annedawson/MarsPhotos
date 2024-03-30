/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.model.MarsPhoto
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen.
 * At any one time it can only be one of the three values
 * Success, Error, Loading
 */
sealed interface MarsUiState {
    //data class Success(val photos:  MarsPhoto) : MarsUiState

    //The Success data class represents a successful state in the MarsPhotos app,
    // where the list of photos has been successfully retrieved from the API.
    // It has a single property called photos, which is a list of MarsPhoto objects.
    data class Success(val photos: List<MarsPhoto>) : MarsUiState
    // Represents a successful retrieval of Mars photos.
    // The original starter code stored the retrieved photos
    // as a long String of JSON key, value pairs and displayed
    // that in the HomeScreen.
    // Later the code was amended in MarsViewModel.kt
    // to read the data into a list of MarsPhoto.
    // The length of the list is displayed in a string using string formatting.
    // In the final version of the app, the list of actual Mars photos
    // is displayed in a grid.


    //The Error object represents an error state in the MarsPhotos app,
    // where there was an error retrieving the photos from the API.
    object Error : MarsUiState
    // Signifies an error during the photo fetching process.
    // Doesn't hold additional data, but its presence indicates a problem.
    object Loading : MarsUiState
    // Indicates that photos are currently being fetched.
    // Used to display loading indicators or messages to the user.
}

// you can't instantiate the MarsUiState interface,
// but you can instantiate a MarsUiState.Success object e.g.
// MarsUiState.Success(marsPhotosRepository.getMarsPhotos()),
// or a MarsUiState.Error or a MarsUiState.Loading object

class MarsViewModel(private val marsPhotosRepository: MarsPhotosRepository) : ViewModel() {
    // This is dependency injection, injecting the repository dependency

    /** The mutable State that stores the status of the most recent request */
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service
     * and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading
            marsUiState = try { // possible network access exception
                //val result = marsPhotosRepository.getMarsPhotos()[0]
                //MarsUiState.Success(marsPhotosRepository.getMarsPhotos()[0])
                /*
                In the line of code below, in the case of a successful response,
                you receive Mars photo information from the server.
                In order to store the data, add a constructor parameter
                to the Success data class for the property.
                 */
                MarsUiState.Success(marsPhotosRepository.getMarsPhotos())
            } catch (e: IOException) {
                MarsUiState.Error
            } catch (e: HttpException) {
                MarsUiState.Error
            }
        }
    }

    /**
     * Factory for [MarsViewModel]
     * that takes [MarsPhotosRepository] as a dependency
     */

    // This is the definition of Factory. It is used in MarsPhotosApp
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                val marsPhotosRepository = application.container.marsPhotosRepository
                MarsViewModel(marsPhotosRepository = marsPhotosRepository)
            }
            // this is how the ViewModel is created in MarsPhotosApp using Factory


        }
    }

}
