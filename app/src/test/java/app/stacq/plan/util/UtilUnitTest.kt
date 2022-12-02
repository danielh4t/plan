package app.stacq.plan.util


import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.Instant

class UtilUnitTest {


    @Test
    fun test_SentenceCase() {
        val expected = "Unit test"
        val case = "unit test"

        val actual: String = case.sentenceCase()

        assertThat(expected, `is`(actual))
    }

    @Test
    fun test_IsFinishAtInFuture() {
        val finishAt: Long = 1600000000

        val expected = true
        val actual: Boolean = isFinishAtInFuture(finishAt)

        assertThat(expected, `is`(actual))
    }

    @Test
    fun test_MillisInFuture() {
        val finishAt: Long = 1600016000


        val expected: Long = 1440016000000
        val actual: Long = millisInFuture(finishAt)

        assertThat(expected, `is`(actual))
    }

}
