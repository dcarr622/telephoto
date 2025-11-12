plugins {
  id("me.saket.android.library")
  id("me.saket.library.publishing")
  id("me.saket.kotlin.android")
  id("me.saket.compose")
  id("me.saket.android.test")
}

android {
  namespace = "me.saket.telephoto.zoomablepeekoverlay"
}

dependencies {
  api(projects.zoomable)
  androidTestImplementation(projects.testUtil)
}
