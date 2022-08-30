package app.stacq.plan.util

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.`is`
import org.junit.Test

class UtilUnitTest {


    @Test
    fun test_SentenceCase() {
        val expected = "Unit test"
        val case = "unit test"

        val actual: String = case.sentenceCase()

        assertThat(expected, `is`(actual))
    }

}