package app.stacq.plan.ui.task


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.stacq.plan.domain.Task
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@MediumTest
class TaskFragmentTest {

    @Test
    fun taskDetails_displayedToTaskFragment() {
        // GIVEN a task
        val task = Task(name="Task", categoryId = "1")
        val expected =  task.name
        // WHEN task fragment launched
        val bundle = TaskFragmentArgs(task.id).toBundle()
        // THEN display task details
//        val scenario = launchFragmentInContainer<TaskFragment>(bundle)
//        scenario.onFragment {
//            assertEquals(expected, it.requireActivity().title)
//        }
    }
}