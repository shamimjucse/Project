/*
 * Copyright (C) 2013-2019 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.feio.android.omninotes;


import android.Manifest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


public class BaseEspressoTest extends BaseAndroidTestCase {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    );

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    static Matcher<View> childAtPosition(
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

    @Before
    public void setUp() throws Exception {
        super.setUp();
        activityRule.launchActivity(null);
    }

    void createNote(String title, String content) {
        ViewInteraction viewInteraction = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        withParent(withId(R.id.fab)),
                        isDisplayed()));

        if (activityRule.getActivity().getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            viewInteraction.perform(click());
        }
        viewInteraction.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_note),
                        withParent(withId(R.id.fab)),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.detail_title),
                        withParent(allOf(withId(R.id.title_wrapper),
                                withParent(withId(R.id.detail_tile_card)))),
                        isDisplayed()));
        editText.perform(click());

        onView(allOf(withId(R.id.detail_title),
                withParent(allOf(withId(R.id.title_wrapper),
                        withParent(withId(R.id.detail_tile_card)))),
                isDisplayed())).perform(replaceText(title), closeSoftKeyboard());

        onView(withId(R.id.detail_content)).perform(scrollTo(), replaceText(content), closeSoftKeyboard());

        navigateUp();
    }

    void navigateUp() {
        onView(allOf(childAtPosition(allOf(withId(R.id.toolbar),
                childAtPosition(withClassName(is("android.widget.RelativeLayout")), 0)
        ), 0), isDisplayed())).perform(click());
    }

    void navigateUpSettings() {
        onView(allOf(withContentDescription(R.string.abc_action_bar_up_description),
                childAtPosition(allOf(withId(R.id.toolbar),
                        childAtPosition(withClassName(is("android.widget.RelativeLayout")), 0)),
                        1), isDisplayed())).perform(click());
    }

}
