package app.stacq.plan.util


import app.stacq.plan.util.time.TimeUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


private const val EPOCH = 1640000000L // 2021-12-20T11:33:20Z

class TimeUtilUnitTest {


    @Test
    fun test_MillisInFuture() {

        val timeUtil: TimeUtil = mock()
        whenever(timeUtil.millisInFuture(EPOCH)).thenReturn(0L)

        val expected = 0L
        val actual: Long = timeUtil.millisInFuture(EPOCH)
        assertThat(expected, `is`(actual))
    }
}
