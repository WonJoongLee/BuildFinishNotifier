package com.isaac.buildfinishnotifier

import com.intellij.openapi.application.PathManager

enum class SoundType {
    BUILD_SUCCESS,
    BUILD_FAILED;

    fun getCustomFileName(): String {
        return when (this.ordinal) {
            BUILD_SUCCESS.ordinal -> {
                "custom_success_sound_1"
            }

            BUILD_FAILED.ordinal -> {
                "custom_fail_sound_1"
            }

            else -> {
                "soundEffect"
            }
        }
    }

    fun getCustomFilePath(): String {
        return when (this.ordinal) {
            BUILD_SUCCESS.ordinal -> {
                "${PathManager.getBinPath()}/buildFinishSounds/custom_success_sound_1.mp3"
            }

            BUILD_FAILED.ordinal -> {
                "${PathManager.getBinPath()}/buildFinishSounds/custom_fail_sound_1.mp3"
            }

            else -> {
                "${PathManager.getBinPath()}/buildFinishSounds/soundEffect.mp3"
            }
        }
    }
}
