package app.stacq.plan.ui.task


import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.stacq.plan.domain.Task
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@MediumTest
class TaskFragmentTest {

    @Test
    fun taskDetails_displayedToTaskFragment() {
        val task = Task(name="Task", categoryId = "1")

        val bundle = TaskFragmentArgs(task.id).toBundle()
        val scenario = launchFragmentInContainer<TaskFragment>(bundle)


       // assertEquals(navController.currentDestination?.id, R.id.nav_create_task)
    }
}