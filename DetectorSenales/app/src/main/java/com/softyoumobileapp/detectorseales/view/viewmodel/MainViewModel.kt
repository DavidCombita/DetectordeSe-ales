package com.softyoumobileapp.detectorseales.view.viewmodel

import android.R.attr
import android.R.attr.name
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import com.softyoumobileapp.detectorseales.domain.PredictUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val predictUseCase: PredictUseCase
): ViewModel() {

    private val _signal = MutableStateFlow<SignalTransit?>(null)
    val signalPre: StateFlow<SignalTransit?> get() = _signal

    fun predictImage(bitmap: Bitmap?, context: Context){
        if (bitmap != null) {
            val wrapper = ContextWrapper(context)
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file,"file.jpeg")
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream)
            stream.flush()
            stream.close()
            viewModelScope.launch {
                predictUseCase(file).collect{ result ->
                    _signal.value = result
                }
            }
        }
    }

    fun clearAlert(){
        _signal.value = null
    }

}