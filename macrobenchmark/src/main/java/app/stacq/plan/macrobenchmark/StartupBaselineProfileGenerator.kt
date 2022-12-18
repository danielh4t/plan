package app.stacq.plan.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupBaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        baselineProfileRule.collectBaselineProfile(packageName = "app.stacq.plan") {
            startActivityAndWait()
            device.wait(Until.hasObject(By.text("Tasks")), 10_000)
        }
    }
}