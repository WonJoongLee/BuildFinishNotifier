package com.isaac.buildfinishnotifier

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.isaac.buildfinishnotifier.icon.BFNIcons

class SettingsAction : AnAction() {

    init {
        templatePresentation.icon = BFNIcons.icon
    }

    override fun actionPerformed(event: AnActionEvent) {
        SettingsDialog(event.project).show()
    }
}