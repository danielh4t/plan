package app.stacq.plan

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.stacq.plan.ui.create.CreateActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.StringContains.containsString
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CreateActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CreateActivity::class.java)

    @Test
    fun clickCreateButton_withoutTitleAndCategory_showsSnackBar() {
        onView(withId(R.id.create_fab))
            .perform(click())

        onView(withText(R.string.text_empty_create))
            .check(matches(isDisplayed()))
    }
}