// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    id("vkid.manifest.placeholders") version "1.1.0" apply true
}

vkidManifestPlaceholders {
    vkidRedirectHost = "vk.com"
    vkidRedirectScheme = "vk53523600"
    vkidClientId = "53523600"
    vkidClientSecret = project.findProperty("VKID_CLIENT_SECRET").toString()

}