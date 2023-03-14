package app.stacq.plan.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.d
 * for investigating your app's performance.d
 * for investigating your app's performance.d                                                                                                                              ddaas
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startCompilationNone() = startup(CompilationMode.None())

    @Test
    fun startupCompilationPartial() = startup(CompilationMode.Partial())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "app.stacq.plan",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = compilationMode
    ) {
        pressHome()
        startActivityAndWait()
    }
}