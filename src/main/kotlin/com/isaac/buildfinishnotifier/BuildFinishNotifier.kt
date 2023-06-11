package com.isaac.buildfinishnotifier

import com.android.tools.idea.gradle.project.build.BuildContext
import com.android.tools.idea.gradle.project.build.BuildStatus
import com.android.tools.idea.gradle.project.build.GradleBuildListener
import com.intellij.util.ui.UIUtil
import com.isaac.buildfinishnotifier.data.ConfigService
import javazoom.jl.player.advanced.AdvancedPlayer
import java.io.BufferedInputStream
import java.io.FileInputStream

class BuildFinishNotifier : GradleBuildListener {

    private val configService = ConfigService.getInstance()

    override fun buildStarted(context: BuildContext) = Unit

    override fun buildFinished(status: BuildStatus, context: BuildContext?) {
        playEndSound(status)
    }

    private fun playEndSound(status: BuildStatus) {
        when (status) {
            BuildStatus.SUCCESS -> {
                if (configService.shouldPlayDefaultSound()) {
                    UIUtil.playSoundFromResource("/sounds/success_sound_1.wav")
                } else {
                    playSound(
                        filePath = SoundType.BUILD_SUCCESS.getCustomFilePath(),
                        onFailure = this::playDefaultSuccessSound
                    )
                }
            }

            BuildStatus.FAILED -> {
                if (configService.shouldPlayDefaultSound()) {
                    UIUtil.playSoundFromResource("/sounds/fail_sound_1.wav")
                } else {
                    playSound(
                        filePath = SoundType.BUILD_FAILED.getCustomFilePath(),
                        onFailure = this::playDefaultFailedSound
                    )
                }
            }

            else -> Unit
        }
    }

    private fun playSound(filePath: String, onFailure: () -> Unit) {
        kotlin.runCatching {
            val inputStream = BufferedInputStream(FileInputStream(filePath))
            val player = AdvancedPlayer(inputStream)
            player.play()
        }.onFailure {
            onFailure()
            println("@@@ error occurred in BuildFinishNotifier : $it")
        }
    }

    private fun playDefaultSuccessSound() {
        UIUtil.playSoundFromResource("/sounds/success_sound_1.wav")
    }

    private fun playDefaultFailedSound() {
        UIUtil.playSoundFromResource("/sounds/fail_sound_1.wav")
    }
}
