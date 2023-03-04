package app.stacq.plan.ui.categories

import app.stacq.plan.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriesFragmentTest {

    @Test
    fun addCategoryNavigatesToCreateCategoryScreen() {
        // Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Create a graphical FragmentScenario for the TasksScreen
        val scenario =
            launchFragmentInContainer<CategoriesFragment>(themeResId = R.style.Theme_Plan)

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)


            // Set the current destination on the TestNavHostController
            navController.setCurrentDestination(R.id.nav_categories)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withId(R.id.add_category_fab)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.nav_create_category))
    }
}