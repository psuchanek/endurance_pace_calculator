package dev.psuchanek.endurancepacecalculator.ui.zones

import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.psuchanek.endurancepacecalculator.R
import dev.psuchanek.endurancepacecalculator.adapters.ZonesListAdapter
import dev.psuchanek.endurancepacecalculator.launchFragmentInHiltContainer
import dev.psuchanek.endurancepacecalculator.ui.MainCollectionFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class ZonesCalculatorFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun inputInBPM_thenSwitchToFTPwithInput_returnBackToLTHR_expectListStillVisible() {
        launchMainCollectionFragment()


        onView(withText(R.string.zones_calculator_tab_label)).perform(click())

        onView(withId(R.id.tiBPM)).perform(ViewActions.typeText("170"))

        onView(withId(R.id.dropDownZonesSpinner)).perform(click())
        onView(withText("Functional Threshold Power")).inRoot(RootMatchers.isPlatformPopup()).perform(
            click())

        onView(withId(R.id.tiFTP)).perform(ViewActions.typeText("250"))

        onView(withId(R.id.dropDownZonesSpinner)).perform(click())
        onView(withText("Lactate Threshold Heart Rate")).inRoot(RootMatchers.isPlatformPopup()).perform(
            click())

        onView(withId(R.id.zonesRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<ZonesListAdapter.ZonesViewHolder>(0, click()))
        onView(withId(R.id.zonesRecyclerView)).perform(RecyclerViewActions.scrollToPosition<ZonesListAdapter.ZonesViewHolder>(4))

    }


    @Test
    fun openSwimPace_thenChangeValues_switchToLTHR_inputLTHR_switchBackToSwimPace_expectRecyclerViewDisplayingSwimZones() {
        launchMainCollectionFragment()


        onView(withText(R.string.zones_calculator_tab_label)).perform(click())
        onView(withId(R.id.dropDownZonesSpinner)).perform(click())
        onView(withText("Swim Pace")).inRoot(RootMatchers.isPlatformPopup()).perform(
            click())


        onView(withId(R.id.dropDownZonesSpinner)).perform(click())
        onView(withText("Lactate Threshold Heart Rate")).inRoot(RootMatchers.isPlatformPopup()).perform(
            click())
        onView(withId(R.id.tiBPM)).perform(ViewActions.typeText("170"), closeSoftKeyboard())


        onView(withId(R.id.dropDownZonesSpinner)).perform(click())
        onView(withText("Swim Pace")).inRoot(RootMatchers.isPlatformPopup()).perform(
            click())

        onView(withId(R.id.zonesRecyclerView)).perform(RecyclerViewActions.scrollToPosition<ZonesListAdapter.ZonesViewHolder>(6))

    }


    private fun launchMainCollectionFragment() {
        launchFragmentInHiltContainer<MainCollectionFragment> {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.mainCollectionFragment)
            this.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    // The fragment’s view has just been created
                    Navigation.setViewNavController(this.requireView(), navController)
                }
            }
        }
    }



}