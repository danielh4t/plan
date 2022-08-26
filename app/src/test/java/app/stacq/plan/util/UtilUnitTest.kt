package app.stacq.plan.util

import junit.framework.TestCase.assertEquals
import org.junit.Test

class UtilUnitTest {


    @Test
    fun test_SentenceCase() {
        val expected = "Unit test"
        val case = "unit test"

        val actual: String = case.sentenceCase()

        assertEquals(expected, actual)
    }

}