package app.stacq.plan.util


import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StringUtilUnitTest {


    @Test
    fun test_SentenceCase() {
        val expected = "Unit test"
        val case = "unit test"

        val actual: String = case.sentenceCase()

        assertThat(expected, `is`(actual))
    }
}
