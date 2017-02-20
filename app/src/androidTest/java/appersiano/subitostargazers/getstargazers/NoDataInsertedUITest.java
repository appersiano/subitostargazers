package appersiano.subitostargazers.getstargazers;


import android.support.test.InstrumentationRegistry;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import appersiano.subitostargazers.R;
import appersiano.subitostargazers.util.Util;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assume.assumeTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoDataInsertedUITest {

    @Rule
    public ActivityTestRule<GetStargazersActivity> mActivityTestRule = new ActivityTestRule<>(GetStargazersActivity.class);
    private boolean isConnected;

    @Before
    public void registerIdlingResource() {
        isConnected = Util.isConnected(InstrumentationRegistry.getContext());
    }

    @Test
    public void noDataInsertedUITest() {
        assumeTrue("Device must be connected to internet", isConnected);

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.fOwner), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.fRepo), isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonShowStargazers), withText("Show Stargazers"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.additionInfoText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.contentFrame),
                                        0),
                                3),
                        isDisplayed()));
        textView.check(matches(withText("")));

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
}
