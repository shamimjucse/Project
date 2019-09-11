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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import de.greenrobot.event.EventBus;
import it.feio.android.omninotes.async.bus.NotesUpdatedEvent;
import it.feio.android.omninotes.utils.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class FabCameraNoteTest extends BaseEspressoTest {

	@Test
	public void fabCameraNoteTest() {
		EventBus.getDefault().register(this);
		Intents.init();
		Bitmap icon = BitmapFactory.decodeResource(
				InstrumentationRegistry.getTargetContext().getResources(),
				R.mipmap.ic_launcher);

		Intent resultData = new Intent();
		resultData.putExtra("data", icon);
		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

		intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

		onView(allOf(withId(R.id.fab_expand_menu_button),
				childAtPosition(
						allOf(withId(R.id.fab),
								childAtPosition(
										withClassName(is("android.widget.FrameLayout")),
										2)),
						3),
				isDisplayed())).perform(click());

		onView(allOf(withId(R.id.fab_camera),
				childAtPosition(
						allOf(withId(R.id.fab),
								childAtPosition(
										withClassName(is("android.widget.FrameLayout")),
										2)),
						0),
				isDisplayed())).perform(click());

		pressBack();

	}

	public void onEvent(NotesUpdatedEvent notesUpdatedEvent) {
		assertEquals(0, notesUpdatedEvent.notes.get(0).getAttachmentsList().size());
		assertEquals(Constants.MIME_TYPE_IMAGE, notesUpdatedEvent.notes.get(0).getAttachmentsList().get(0).getMime_type());
	}

}
