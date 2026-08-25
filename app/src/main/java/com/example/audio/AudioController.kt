package com.example.audio

import com.example.dsp.DspEngine

class AudioController {
    private val dspEngine = DspEngine()
    
    init {
        // Init with session 0 (system output) for global mix if permission/device allows,
        // otherwise it would attach to a specific player session.
        dspEngine.initialize(0)
    }

    fun applyEq(bands: List<Float>) {
        bands.forEachIndexed { index, level ->
            dspEngine.setBandLevel(index, level)
        }
    }
    
    fun setPreamp(level: Float) {
        dspEngine.setPreamp(level)
    }
    
    fun setBassEnhancement(amount: Float) {
        dspEngine.setBassBoost(amount)
    }
    
    fun enableProcessing(enable: Boolean) {
        dspEngine.isEnabled = enable
    }
    
    fun release() {
        dspEngine.release()
    }
}
