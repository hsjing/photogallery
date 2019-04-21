package com.example.photogallery;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);
    //public IntentsTestRule<MainActivity> intentsRule = new IntentsTestRule<>(MainActivity.class);

    // Location search test
    @Test
    public void locTest() throws InterruptedException{
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.search_editLoc)).perform(typeText("Italy"),closeSoftKeyboard());
        onView(withId(R.id.search_btnSearch)).perform(click());

        Thread.sleep(500);
        onView(withId(R.id.btnRight)).perform((click()));
        Thread.sleep(500);
        onView(withId(R.id.btnRight)).perform((click()));
        Thread.sleep(500);
        onView(withId(R.id.btnLeft)).perform((click()));
        Thread.sleep(500);
        onView(withId(R.id.btnLeft)).perform((click()));

        Thread.sleep(500);

        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.search_editLoc)).perform(typeText("Burnaby"),closeSoftKeyboard());
        onView(withId(R.id.search_btnSearch)).perform(click());
    }


    // Date search test
    @Test
    public void dateTest() throws InterruptedException {
        int fromYr = 1998;
        int fromMon = 11;
        int fromDay = 15;

        int toYr = 2010;
        int toMon = 11;
        int toDay = 15;
        onView(withId(R.id.btnSearch)).perform(click());

        onView(withId(R.id.search_btnFromDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(fromYr, fromMon, fromDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.search_btnToDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(toYr, toMon, toDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.search_btnSearch)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.btnRight)).perform((click()));
        Thread.sleep(500);
        onView(withId(R.id.btnRight)).perform((click()));
        Thread.sleep(500);
        onView(withId(R.id.btnLeft)).perform((click()));
        Thread.sleep(500);
        onView(withId(R.id.btnLeft)).perform((click()));

        int fromYrNew = 2010;
        int fromMonNew = 11;
        int fromDayNew = 15;
        onView(withId(R.id.btnSearch)).perform(click());

        onView(withId(R.id.search_btnFromDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(fromYrNew, fromMonNew, fromDayNew));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.search_btnSearch)).perform(click());
        Thread.sleep(500);
    }
    @Test
    public void mapTest() throws InterruptedException {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.search_editLoc)).perform(typeText("Italy"),closeSoftKeyboard());
        onView(withId(R.id.search_btnSearch)).perform(click());

        Thread.sleep(500);

        onView(withId(R.id.locationShort)).perform(click());

        Thread.sleep(1000);
    }
    /*

    @Test
    public void camTest() {
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher
                );

        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.android.camera2")).respondWith(result);

        onView(withId(R.id.btnSnap)).perform(click());

        intended(toPackage("com.android.camera2"));
    }

    */
}
