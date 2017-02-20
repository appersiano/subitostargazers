package appersiano.subitostargazers.getstargazers;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import appersiano.subitostargazers.R;
import appersiano.subitostargazers.util.Util;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assume.assumeTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchGsonByGoogleUITest {

    @Rule
    public ActivityTestRule<GetStargazersActivity> mActivityTestRule = new ActivityTestRule<>(GetStargazersActivity.class);

    private IdlingResource mIdlingResource;
    private boolean isConnected;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        isConnected = Util.isConnected(InstrumentationRegistry.getContext());
    }

    @Test
    public void searchGsonByGoogleUITest() {
        assumeTrue("Device must be connected to internet", isConnected);

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.fOwner), isDisplayed()));
        appCompatEditText.perform(replaceText("google"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.fRepo), isDisplayed()));
        appCompatEditText2.perform(replaceText("gson"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonShowStargazers), withText("Show Stargazers"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.additionInfoText), withText("Stargazers of gson by google"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contentFrame),
                                        0),
                                3),
                        isDisplayed()));
        textView.check(matches(withText("Stargazers of gson by google")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.stargazerName), withText("falcon6n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recyclerStargazer),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.stargazerName), withText("falcon6n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recyclerStargazer),
                                        0),
                                1),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
