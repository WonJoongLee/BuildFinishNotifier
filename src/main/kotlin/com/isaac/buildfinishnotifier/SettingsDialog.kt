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
import com.isaac.buildfinishnotifier.data.ConfigService
import java.io.File
import javax.swing.JComponent

class SettingsDialog(private val project: Project?) : DialogWrapper(project, true) {

    private var customSucceedSoundPath = ""
    private var customFailedSoundPath = ""
    private var isDefault = true
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
    private val configService = ConfigService.getInstance()
    private var isDefaultSoundRecentlyChosen = configService.shouldPlayDefaultSound()

    init {
        title = "Build Finish Settings"
        init()
    }

    override fun createCenterPanel(): JComponent = newTempDialog()

    override fun doValidate(): ValidationInfo? {
        return when {
            defaultSoundRadioButton.isSelected -> null

            customSoundRadioButton.isSelected -> {
                if (successPathText.isEmpty() && failedPathText.isEmpty()) {
                    null
                } else if ( // (path is not empty) && (file is not valid)
                    (successPathText.isNotEmpty() && isNotValidFile(successPathText)) ||
                    (failedPathText.isNotEmpty() && isNotValidFile(failedPathText))
                ) {
                    ValidationInfo("Please check the file path again.", customSoundRadioButton)
                } else if ( // (check file audio extension)
                    !isFileAudioExtension(successPathText) || !isFileAudioExtension(failedPathText)
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
        if (successPathText.isNotEmpty()) {
            copySoundEffectToCacheDirectory(successPathText, SoundType.BUILD_SUCCESS)
        }

        if (failedPathText.isNotEmpty()) {
            copySoundEffectToCacheDirectory(failedPathText, SoundType.BUILD_FAILED)
        }

        configService.setShouldPlayDefaultSound(defaultSoundRadioButton.isSelected)

        super.doOKAction()
    }

    private fun copySoundEffectToCacheDirectory(
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
                        val copyOfUserSelectedSoundFile = File(VfsUtilCore.copyFile(this, fileToCopy, toDir).path)
                        val customSoundFileName = "${soundType.getCustomFileName()}.mp3"
                        val customSoundFile = File(toDir.path, customSoundFileName)
                        val newName =
                            "${customSoundFile.path.substringBeforeLast("/")}/${soundType.getCustomFileName()}.mp3"
                        copyOfUserSelectedSoundFile.renameTo(File(newName))
                    }
                }
            }
        }.onFailure {
            println("@@@ error occurred in BuildFinishNotifier : $it")
        }
    }

    private fun newTempDialog(): DialogPanel {
        return panel {
            group("Play Sound") {

                buttonsGroup {
                    row {
                        defaultSoundRadioButton = radioButton("Default sound", isDefaultSoundRecentlyChosen).component
                    }
                    row {
                        customSoundRadioButton = radioButton("Custom sound", !isDefaultSoundRecentlyChosen).component
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

    private fun isNotValidFile(path: String): Boolean {
        val pathFile = File(path)
        return !pathFile.exists() || !pathFile.isFile
    }

    private fun isFileAudioExtension(filePath: String): Boolean {
        return filePath.isEmpty() || (filePath.endsWith(".mp3") || filePath.endsWith(".wav"))
    }
}