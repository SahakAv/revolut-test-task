package com.revolut.task.config

enum class ApplicationProfile {
  Local,
  Dev,
  Test,
  Prod;

  fun isLocal() = this == Local
  fun isDev() = this == Dev
  fun isTest() = this == Test
  fun isProd() = this == Prod

  companion object {

    const val DEFAULT_PROFILE = "local"
    const val PROFILE_ENV_NAME = "PROFILE"

    fun get(profile: String): ApplicationProfile = ApplicationProfile::class.java.enumConstants
      .find { p -> p.name.toLowerCase() == profile }?:Local
  }
}
