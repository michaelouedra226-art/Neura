package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NEURA AI", appName)
  }

  @Test
  fun `test url normalizers`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val service = com.example.data.repository.AiEngineService(
        com.example.data.local.SettingsManager(context)
    )
    val chatUrl = service.normalizeChatUrl("https://apihub.agnes-ai.com/v1")
    assertEquals("https://apihub.agnes-ai.com/v1/chat/completions", chatUrl)

    val imageUrl = service.normalizeAgnesImageUrl("https://apihub.agnes-ai.com/v1")
    assertEquals("https://apihub.agnes-ai.com/v1/images/generations", imageUrl)

    val modelsUrl = service.normalizeAgnesModelsUrl("https://apihub.agnes-ai.com/v1")
    assertEquals("https://apihub.agnes-ai.com/v1/models", modelsUrl)
  }
}
