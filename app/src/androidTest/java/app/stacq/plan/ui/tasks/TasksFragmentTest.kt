package app.stacq.plan.ui.tasks

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.stacq.plan.R
import org.hamcrest.CoreMatchers.either
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksFragmentTest {

    @Test
    fun createTaskWithoutCategoriesNavigationToCreateCategoryScreen() {

        // Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Create a graphical FragmentScenario for the TasksScreen
        val titleScenario =
            launchFragmentInContainer<TasksFragment>(themeResId = R.style.Theme_Plan)

        titleScenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withId(R.id.create_task_fab)).perform(click())
        assertThat(
            navController.currentDestination?.id,
            either(`is`(R.id.nav_create_category)).or(`is`(R.id.nav_create_task))
        )
    }
}