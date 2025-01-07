package com.dream.customtransformplugin

import org.gradle.api.provider.Property

abstract class AnalyticsExtension {
    abstract val enable: Property<Boolean>
}