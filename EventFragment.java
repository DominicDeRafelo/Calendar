package edu.moravian.csci299.mocalendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

/**
 * The fragment for a single event. It allows editing all of the details of the event, either with
 * text edit boxes (for the name and description) or popup windows (for the date, start time,
 * time and type). The event is not updated in the database until the user leaves this fragment.
 */
public class EventFragment extends Fragment implements TextWatcher, DatePickerFragment.Callbacks, EventTypePickerFragment.Callbacks,
        TimePickerFragment.Callbacks {

    // fragment initialization parameters
    private static final String ARG_EVENT_ID = "event_id";

    // dialog fragment tags
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_EVENT_TYPE = "DialogEventType";

    // dialog fragment codes
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_EVENT_TYPE = 2;

    // argument once loaded from database
    private Event event;

    //The views
    private EditText editEventName;
    private TextView date;
    private EditText description;
    private TextView startTime;
    private TextView endTime;
    private TextView until;
    private ImageView typeView;

    /**
     * Use this factory method to create a new instance of this fragment that
     * show the details for the given event.
     *
     * @param event the event to show information about
     * @return a new instance of fragment EventFragment
     */
    public static EventFragment newInstance(Event event) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, event.id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Upon creation load the data. Once the data is loaded, update the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: load the event and update the UI
        if (getArguments() != null && getArguments().containsKey(ARG_EVENT_ID)) {
            UUID id = (UUID) getArguments().getSerializable(ARG_EVENT_ID);
            CalendarRepository.get().getEventById(id).observe(this, event -> {
                this.event = event;
                updateUI();
            });
        }
    }

    /**
     * Create the view from the layout, save references to all of the important
     * views within in, then hook up the listeners.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_event, container, false);

        // TODO
        typeView = base.findViewById(R.id.imageView);
        editEventName = base.findViewById(R.id.EditEventName);
        date = base.findViewById(R.id.date);
        startTime = base.findViewById(R.id.eventTime);
        until = base.findViewById(R.id.until);
        endTime = base.findViewById(R.id.endTime);
        description = base.findViewById(R.id.description);

        editEventName.addTextChangedListener(this);
        description.addTextChangedListener(this);

        date.setOnClickListener(v -> {
            DatePickerFragment fragment = DatePickerFragment.newInstance(event.startTime);
            fragment.setTargetFragment(this, REQUEST_DATE);
            fragment.show(requireFragmentManager(), DIALOG_DATE);
        });

        typeView.setOnClickListener(v -> {
            EventTypePickerFragment fragment = EventTypePickerFragment.newInstance(event.type);
            fragment.setTargetFragment(this, REQUEST_EVENT_TYPE);
            fragment.show(requireFragmentManager(), DIALOG_EVENT_TYPE);
        });

        startTime.setOnClickListener(v -> {
            TimePickerFragment fragment = TimePickerFragment.newInstance(true, event.startTime);
            fragment.setTargetFragment(this, REQUEST_TIME);
            fragment.show(requireFragmentManager(), DIALOG_TIME);
        });

        endTime.setOnClickListener(v -> {
            TimePickerFragment fragment = TimePickerFragment.newInstance(false, event.endTime);
            fragment.setTargetFragment(this, REQUEST_TIME);
            fragment.show(requireFragmentManager(), DIALOG_TIME);
        });
        // Return the base view
        return base;
    }

    // TODO: save the event to the database at some point
    public void onStop() {
        super.onStop();
        CalendarRepository.get().updateEvent(event);
    }

    /**
     * Updates the UI to match the event.
     */
    private void updateUI() {
        // TODO
        this.typeView.setImageResource(this.event.type.iconResourceId);
        this.editEventName.setText(this.event.name);
        this.date.setText(DateUtils.toFullDateString(this.event.startTime));
        this.startTime.setText(DateUtils.toTimeString(this.event.startTime));
        this.description.setText(this.event.description);
        if (this.event.endTime == null) {
            this.until.setText(" ");
            this.endTime.setText(" ");
        } else {
            this.until.setText(R.string.until);
            this.endTime.setText(DateUtils.toTimeString(this.event.endTime));
        }

    }

    // TODO: maybe some helpful functions for showing dialogs and the callback functions
    @Override
    public void onDateSelected(Date date) {
        event.startTime = DateUtils.combineDateAndTime(date, event.startTime);
        if (this.event.endTime != null) {
            event.endTime = DateUtils.combineDateAndTime(date, event.endTime);
        }
        updateUI();
    }

    @Override
    public void onTypeSelected(EventType type) {
        this.event.type = type;
        updateUI();
    }


    @Override
    public void onTimeSelected(boolean isStartTime, Date time) {
        if(isStartTime) {
            event.startTime = DateUtils.combineDateAndTime(event.startTime, time);
        }
        else{
            event.endTime = DateUtils.combineDateAndTime(event.endTime, time);
        }
        updateUI();
    }

    /**
     * When an EditText updates we update the corresponding Event field. Need to register this
     * object with the EditText objects with addTextChangedListener(this).
     *
     * @param s the editable object that just updated, equal to some EditText.getText() object
     */
    @Override
    public void afterTextChanged(Editable s) {
        // TODO
        if (s == this.editEventName.getEditableText()) {
            this.event.name = s.toString();
            Log.d("EventFragment", "Changed Event Name");
        } else if (s == this.description.getEditableText()) {
            this.event.description = s.toString();
            Log.d("EventFragment", "Changed Event description");
        }
    }

    /**
     * Required to be implemented but not needed.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Required to be implemented but not needed.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }


}
