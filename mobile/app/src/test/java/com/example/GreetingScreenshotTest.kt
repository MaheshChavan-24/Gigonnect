package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.EscrowStatus
import com.example.data.model.Job
import com.example.data.model.UrgencyLevel
import com.example.ui.components.JobCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleJob = Job(
        id = 1L,
        clientId = 1L,
        clientName = "Aditi Sharma",
        title = "Fix Kitchen Sink Leak & Drain Pipe",
        description = "Water leakage under the sink with dripping pipe.",
        serviceType = "Plumber",
        address = "Kothrud, Pune",
        latitude = 18.5074,
        longitude = 73.8077,
        budget = 750.0,
        urgencyLevel = UrgencyLevel.STANDARD,
        escrowStatus = EscrowStatus.HELD,
        distanceKm = 2.4
    )

    composeTestRule.setContent {
        MyApplicationTheme {
            JobCard(job = sampleJob, onClick = {})
        }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
