package com.hqnguyen.weetest.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hqnguyen.weetest.view.MediaViewModel

class MediaViewModelFactory constructor(
    private val repository: MediaRepository,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            MediaViewModel(this.repository, application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}