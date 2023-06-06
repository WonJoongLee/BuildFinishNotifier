package com.isaac.buildfinishnotifier

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.selected
import javazoom.jl.player.advanced.AdvancedPlayer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.swing.JComponent

class SettingsDialog(private val project: Project?) : DialogWrapper(project, true) {

    private var customSucceedSoundPath = ""
    private var customFailedSoundPath = ""
    private var isDefault = true
    private var isCustomSelected = false
    private lateinit var defaultSoundRadioButton: JBRadioButton
    private lateinit var customSoundRadioButton: JBRadioButton
    private lateinit var customSucceedButton: TextFieldWithBrowseButton
    private lateinit var customFailedButton: TextFieldWithBrowseButton
    private val successPathText: String
        get() = when {
            customSucceedSoundPath.isNotEmpty() -> customSucceedSoundPath
            customSucceedButton.text.isNotEmpty() -> customSucceedButton.text
            else -> ""
        }
    private val failedPathText: String
        get() = when {
            customFailedSoundPath.isNotEmpty() -> customFailedSoundPath
            customFailedButton.text.isNotEmpty() -> customFailedButton.text
            else -> ""
        }

    init {
        title = "Build Finish Settings"
        init()
    }

    override fun createCenterPanel(): JComponent = newTempDialog()

    override fun doValidate(): ValidationInfo? {
        return when {
            defaultSoundRadioButton.isSelected -> null

            customSoundRadioButton.isSelected -> {
                println("customSucceedSoundPath : $successPathText")
                println("customSucceedSoundPath : $failedPathText")
                if (successPathText.isEmpty() && failedPathText.isEmpty()) {
                    null
                } else if ( // (path is not empty) && (file is not valid)
                    (successPathText.isNotEmpty() && isNotValidFile(successPathText)) ||
                    (failedPathText.isNotEmpty() && isNotValidFile(failedPathText))
                ) {
                    ValidationInfo("Please check the file path again.", customSoundRadioButton)
                } else if ( // (path is not empty) && (file doesn't end with ".mp3")
                    (successPathText.isNotEmpty() && !successPathText.endsWith(".mp3")) ||
                    (failedPathText.isNotEmpty() && !failedPathText.endsWith(".mp3"))
                ) {
                    ValidationInfo("The sound effect file does not end in \"mp3\".", customSoundRadioButton)
                } else {
                    null
                }
            }

            else -> null
        }
    }

    override fun doOKAction() {
        println("@@@doOKAction - successPathText : ${successPathText}")
        if (successPathText.isNotEmpty()) {
            copySoundEffect(successPathText, SoundType.BUILD_SUCCESS)
        }

        if (failedPathText.isNotEmpty()) {
            copySoundEffect(failedPathText, SoundType.BUILD_FAILED)
        }

        super.doOKAction()
    }

    private fun copySoundEffect(
        soundEffectPath: String,
        soundType: SoundType
    ) {
        kotlin.runCatching {
            val localFileSystem = LocalFileSystem.getInstance()
            if (localFileSystem != null) {
                ApplicationManager.getApplication().runWriteAction {
                    val fileToCopy = localFileSystem.findFileByPath(soundEffectPath)
                    val toDir =
                        VfsUtil.createDirectoryIfMissing("${PathManager.getBinPath()}/buildFinishSounds")
                    if (fileToCopy != null && toDir != null) {
                        val copiedFilePath = VfsUtilCore.copyFile(this, fileToCopy, toDir)
                        copiedFilePath.rename(this, "${soundType.getCustomFileName()}.mp3")
                    }
                }
            }
        }.onFailure {
            println("@@@ error occurred in BuildFinishNotifier : $it")
        }
    }

    private fun playSound(audioFile: File) {
        val mp3FilePath = audioFile.path
        val inputStream = BufferedInputStream(FileInputStream(mp3FilePath))
        val player = AdvancedPlayer(inputStream)
        player.play()
    }

    private fun isNotValidFile(path: String): Boolean {
        val pathFile = File(path)
        return !pathFile.exists() || !pathFile.isFile
    }

    private fun newTempDialog(): DialogPanel {
        return panel {
            group("Play Sound") {

                buttonsGroup {
                    row {
                        defaultSoundRadioButton = radioButton("Default sound", isCustomSelected).component
                    }
                    row {
                        customSoundRadioButton = radioButton("Custom sound", !isCustomSelected).component
                    }
                }.bind({ isDefault }, { isDefault = it })

                row("Build succeed sound") {
                    val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                    customSucceedButton = textFieldWithBrowseButton(
                        "Custom Sound",
                        project,
                        fileChooserDescriptor
                    ) {
                        customSucceedSoundPath = it.path
                        it.path
                    }.component
                }.enabledIf(customSoundRadioButton.selected)

                row("Build failed sound") {
                    val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                    customFailedButton = textFieldWithBrowseButton(
                        "Custom Sound",
                        project,
                        fileChooserDescriptor
                    ) {
                        customFailedSoundPath = it.path
                        it.path
                    }.comment(
                        "The sound effect file must end in \"mp3\". " +
                                "If it's empty, it will be set with default sound effect."
                    ).component
                }.enabledIf(customSoundRadioButton.selected)
            }
        }
    }
}