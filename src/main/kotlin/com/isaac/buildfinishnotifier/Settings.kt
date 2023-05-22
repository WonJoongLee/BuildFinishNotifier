package com.isaac.buildfinishnotifier

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.isaac.buildfinishnotifier.icon.BFNIcons

class Settings : AnAction() {

    init {
        templatePresentation.icon = BFNIcons.icon
    }

    override fun actionPerformed(p0: AnActionEvent) {
        TODO("Not yet implemented")
    }
}