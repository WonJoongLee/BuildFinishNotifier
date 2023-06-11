package com.isaac.buildfinishnotifier.data

import com.intellij.ide.util.PropertiesComponent
import com.isaac.buildfinishnotifier.data.StringKeySet.SHOULD_PLAY_DEFAULT_SOUND

class ConfigServiceImpl : ConfigService {

    override fun setShouldPlayDefaultSound(shouldPlayDefaultSound: Boolean) {
        PropertiesComponent.getInstance().setValue(SHOULD_PLAY_DEFAULT_SOUND, shouldPlayDefaultSound)
    }

    override fun shouldPlayDefaultSound(): Boolean {
        return PropertiesComponent.getInstance().getBoolean(SHOULD_PLAY_DEFAULT_SOUND)
    }
}
