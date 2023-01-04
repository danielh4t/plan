package app.stacq.plan.util


import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

// 2021-12-20T11:33:20Z
private const val EPOCH = 1640000000L

class CalendarUtilUnitTest {


    @Test
    fun test_YearStartAt() {
        // 2022
        val expected: Long = 1640908800
        val actual: Long = CalendarUtil().yearStartAt()

        assertThat(expected, `is`(actual))
    }
}
