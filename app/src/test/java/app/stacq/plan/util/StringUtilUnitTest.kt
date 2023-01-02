package app.stacq.plan.util

import app.stacq.plan.R
import android.content.Context
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private const val FAKE_COLOR = 123

@RunWith(MockitoJUnitRunner::class)
class StringUtilUnitTest {

    @Test
    fun test_SentenceCase() {
        val expected = "Unit test"
        val case = "unit test"
        val actual: String = case.sentenceCase()

        assertThat(expected, `is`(actual))
    }

    @Test
    fun test_DefaultColors() {
        val mockContext = mock<Context> {
            on { getColor(R.color.plan_orange) } doReturn FAKE_COLOR
        }

        val expected = FAKE_COLOR
        val case = "Code"
        val actual: Int = mockContext.getColor(defaultColors(case))

        assertThat(expected, `is`(actual))
    }
}
