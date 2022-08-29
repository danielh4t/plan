package app.stacq.plan

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.stacq.plan.ui.create.CreateActivity
import app.stacq.plan.ui.tasks.TaskAdapter
import app.stacq.plan.ui.tasks.TasksFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun click_fab_startCreateActivity() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(ViewMatchers.withText(R.string.text_empty_create))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


}

