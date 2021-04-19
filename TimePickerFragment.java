package edu.moravian.csci299.mocalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Date;

/**
 * A fragment that acts as a popup window for picking a time. Any fragment that
 * uses this must implement the Callbacks interface defined here and set the
 * target fragment before showing it.
 *
 * HINTS: use the DatePickerFragment as inspiration for completing this one.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    /** The name of the argument for the start time (a boolean) */
    private static final String ARG_IS_START_TIME = "is_start_time";


    /** The name of the argument for the time (a Date object) */
    private static final String ARG_TIME = "time";

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }


    interface Callbacks {
        /**
         * This function is called when a time is selected and the dialog is
         * confirmed.
         *
         * @param time the time that was picked
         */
        void onTimeSelected(boolean isStartTime, Date time);
    }
    /**
     * Create a new instance of the time picking fragment dialog.
     * @param isStartTime whether this is picking a start or end time, this has
     *                    no influence on this picker but is used in when
     *                    calling the callback method
     * @param time the time to initially display in the picker
     * @return a new TimePickerFragment instance
     */
    public static TimePickerFragment newInstance(boolean isStartTime, Date time) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_START_TIME, isStartTime);
        args.putSerializable(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = DateUtils.useDateOrNow((Date)getArguments().getSerializable(ARG_TIME));
        int[] hourMinute = DateUtils.getHourMinute(date);
        return new TimePickerDialog(requireContext(), this, hourMinute[0], hourMinute[1], true);
    }
}
