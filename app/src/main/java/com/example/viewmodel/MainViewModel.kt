package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioController
import com.example.data.AppDatabase
import com.example.data.Preset
import com.example.data.PresetRepository
import com.example.models.HeadphoneProfile
import com.example.models.defaultHeadphoneProfiles
import com.example.models.defaultPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PresetRepository
    private val audioController = AudioController()

    val allPresets: StateFlow<List<Preset>>
    
    private val _currentPreset = MutableStateFlow(defaultPresets[1]) // Default to Soundcore Bass+
    val currentPreset = _currentPreset.asStateFlow()
    
    private val _currentProfile = MutableStateFlow(defaultHeadphoneProfiles[0])
    val currentProfile = _currentProfile.asStateFlow()
    
    private val _eqEnabled = MutableStateFlow(true)
    val eqEnabled = _eqEnabled.asStateFlow()
    
    // Live EQ Band Values (for interactive adjustments)
    private val _bands = MutableStateFlow(List(10) { 0f })
    val bands = _bands.asStateFlow()
    
    private val _bassAmount = MutableStateFlow(0f)
    val bassAmount = _bassAmount.asStateFlow()

    private val _midsAmount = MutableStateFlow(0f)
    val midsAmount = _midsAmount.asStateFlow()
    
    private val _trebleAmount = MutableStateFlow(0f)
    val trebleAmount = _trebleAmount.asStateFlow()
    
    private val _preamp = MutableStateFlow(0f)
    val preamp = _preamp.asStateFlow()

    init {
        val presetDao = AppDatabase.getDatabase(application).presetDao()
        repository = PresetRepository(presetDao)
        
        allPresets = repository.allPresets.map { customPresets ->
            defaultPresets + customPresets
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = defaultPresets
        )
        
        // Initial setup
        loadPreset(_currentPreset.value)
    }
    
    fun setEqEnabled(enabled: Boolean) {
        _eqEnabled.value = enabled
        audioController.enableProcessing(enabled)
    }
    
    fun setProfile(profile: HeadphoneProfile) {
        _currentProfile.value = profile
        // In a real app, this might load a specific compensation curve
    }
    
    fun selectPreset(preset: Preset) {
        loadPreset(preset)
    }
    
    private fun loadPreset(preset: Preset) {
        _currentPreset.value = preset
        _bands.value = listOf(
            preset.band31, preset.band62, preset.band125, preset.band250, preset.band500,
            preset.band1k, preset.band2k, preset.band4k, preset.band8k, preset.band16k
        )
        _bassAmount.value = preset.bassAmount
        _midsAmount.value = preset.midsAmount
        _trebleAmount.value = preset.trebleAmount
        _preamp.value = preset.preamp
        
        applyToAudioController()
    }
    
    fun updateBand(index: Int, value: Float) {
        val newBands = _bands.value.toMutableList()
        newBands[index] = value
        _bands.value = newBands
        applyToAudioController()
    }
    
    fun updateBass(amount: Float) {
        _bassAmount.value = amount
        applyToAudioController()
    }
    
    fun updateMids(amount: Float) {
        _midsAmount.value = amount
        applyToAudioController()
    }
    
    fun updateTreble(amount: Float) {
        _trebleAmount.value = amount
        applyToAudioController()
    }
    
    fun updatePreamp(amount: Float) {
        _preamp.value = amount
        applyToAudioController()
    }
    
    fun resetCurrent() {
        val preset = _currentPreset.value
        // reset to 0 if custom, or back to preset defaults if built-in
        if (preset.isCustom) {
            val resetPreset = Preset(preset.id, preset.name, true)
            loadPreset(resetPreset)
        } else {
            val original = defaultPresets.find { it.id == preset.id } ?: defaultPresets[0]
            loadPreset(original)
        }
    }
    
    private fun applyToAudioController() {
        audioController.applyEq(_bands.value)
        audioController.setBassEnhancement(_bassAmount.value)
        audioController.setPreamp(_preamp.value)
    }

    override fun onCleared() {
        super.onCleared()
        audioController.release()
    }
}
