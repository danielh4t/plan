package app.stacq.plan.ui.task

import app.stacq.plan.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
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
        val task = Task(name="Task", categoryId = "1")

        val bundle = TaskFragmentArgs(task.id).toBundle()
        val scenario = launchFragmentInContainer<TaskFragment>(bundle)


       // assertEquals(navController.currentDestination?.id, R.id.nav_create_task)
    }
}