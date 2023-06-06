package com.isaac.buildfinishnotifier

enum class SoundType {
    BUILD_SUCCESS,
    BUILD_FAILED;

    fun getCustomFileName(): String {
        return when (this.ordinal) {
            BUILD_SUCCESS.ordinal -> {
                "custom_success_sound_1.wav"
            }

            BUILD_FAILED.ordinal -> {
                "custom_fail_sound_1.wav"
            }

            else -> {
                "soundEffect"
            }
        }
    }
}
