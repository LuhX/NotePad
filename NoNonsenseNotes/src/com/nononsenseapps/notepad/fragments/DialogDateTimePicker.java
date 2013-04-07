/*
 * Copyright (C) 2013 Jonas Kalderstam
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nononsenseapps.notepad.fragments;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nononsenseapps.notepad.R;
import com.nononsenseapps.notepad.prefs.MainPrefs;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;

@EFragment(R.layout.fragment_dialog_datetimepicker)
public class DialogDateTimePicker extends DialogFragment {

	@ViewById
	DatePicker datePicker;

	@ViewById
	TimePicker timePicker;

	public interface DateTimeSetListener {
		void onDateTimeSet(long time);
	}

	DateTimeSetListener listener;
	private Long mTime = null;

	public void setListener(final DateTimeSetListener l) {
		listener = l;
	}

	public DialogDateTimePicker() {
	}

	public void setTime(final Long time) {
		mTime = time;
	}

	public static void showDialog(final FragmentManager fm, final Long time,
			final DateTimeSetListener listener) {
		final DialogDateTimePicker_ fragment = new DialogDateTimePicker_();
		fragment.setTime(time);
		fragment.setListener(listener);
		fragment.show(fm, "datetime");
	}

	@Click(R.id.dialog_no)
	void cancelClicked() {
		dismiss();
	}

	@Click(R.id.dialog_yes)
	void okClicked() {
		final Calendar localTime = Calendar.getInstance();

		localTime.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());

		if (listener != null) {
			listener.onDateTimeSet(localTime.getTimeInMillis());
		}
		dismiss();
	}

	@AfterViews
	void setValues() {
		// TODO GIVE ME A TITLE!
		getDialog().setTitle(R.string.select_a_time);
		final CalendarView cv = datePicker.getCalendarView();
		cv.setFirstDayOfWeek(getFirstDayOfWeek(getActivity()));
		timePicker.setIs24HourView(DateFormat.is24HourFormat(getActivity()));

		if (mTime != null) {
			final Calendar localTime = Calendar.getInstance();
			localTime.setTimeInMillis(mTime);

			datePicker.updateDate(localTime.get(Calendar.YEAR),
					localTime.get(Calendar.MONTH),
					localTime.get(Calendar.DAY_OF_MONTH));

			timePicker.setCurrentHour(localTime.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(localTime.get(Calendar.MINUTE));
		}
	}

	/**
	 * Get first day of week as android.text.format.Time constant.
	 * 
	 * @return the first day of week in android.text.format.Time
	 */
	public static int getFirstDayOfWeek(final Context context) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		final String pref = prefs.getString(MainPrefs.KEY_WEEK_START_DAY,
				MainPrefs.WEEK_START_DEFAULT);

		int startDay;
		if (MainPrefs.WEEK_START_DEFAULT.equals(pref)) {
			startDay = Calendar.getInstance().getFirstDayOfWeek();
		}
		else {
			startDay = Integer.parseInt(pref);
		}

		return startDay;
	}
}
