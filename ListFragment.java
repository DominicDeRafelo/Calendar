package edu.moravian.csci299.mocalendar;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * A fragment that displays a list of events. The list is a RecyclerView. When an event on the list
 * is clicked, a callback method is called to inform the hosting activity. When an item on the list
 * is swiped, it causes the event to be deleted (see https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e).
 * This is the fragment that also controls the menu of options in the app bar.
 *
 * Above the list is a text box that states the date being displayed on the list.
 *
 * NOTE: Finish CalendarFragment first then work on this one. Also, look at how a few things
 * related to dates are dealt with in the CalendarFragment and use similar ideas here.
 */
public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";
    private EventListAdapter adapter;

    public interface Callbacks {
        void getEventById(UUID id);
        void openIndividualEvent(Event event);
    }



    // fragment initialization parameters
    private static final String ARG_DATE = "date";

    // data
    private Date date;
    private List<Event> events = Collections.emptyList();
    private Callbacks callbacks;
    private RecyclerView list;
    private LiveData<List<Event>> liveDataItems;


    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for today.
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance() {
        return newInstance(new Date());
    }

    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for the given day.
     * @param date the date to show the event list for
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance(Date date) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Set the day for the events being listed.
     * @param date the new day for the list to show events for
     */
    public void setDay(Date date) {
        this.date = date;
        getArguments().putSerializable(ARG_DATE, date);
        onDateChange();
    }

    /**
     * Upon creation need to enable the options menu and update the view for the initial date.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = DateUtils.useDateOrNow((Date)getArguments().getSerializable(ARG_DATE));
        onDateChange();
        // TODO: maybe something related to the menu?
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    /**
     * Create the view for this layout. Also sets up the adapter for the RecyclerView, its swipe-to-
     * delete helper, and gets the date text view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_list, container, false);
        // Setup the recycler
        list = base.findViewById(R.id.list_view);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        EventListAdapter eventListAdapter = new EventListAdapter();
        //this.adapter = eventListAdapter;
        list.setAdapter(eventListAdapter);
        // return the base view
        return base;
    }

    /**
     * When the date is changed for this fragment we need to grab a new list of events and update
     * the UI.
     */
    private void onDateChange() {
        // TODO
        liveDataItems = CalendarRepository.get().getEventsOnDay(date);
        liveDataItems.observe(this, (events) -> {
            this.events = events;
            this.events.add(new Event());
            list.getAdapter().notifyDataSetChanged();
        });

    }

    // TODO: some code for (un)registering callbacks?
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    // TODO: some code for the menu options?
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.new_event){
            Event event = new Event();
            event.startTime = this.date;
            event.endTime = new Date(this.date.getTime() + 3600000);
            CalendarRepository.get().addEvent(event);
            callbacks.openIndividualEvent(event);
            return true;
        } else if(item.getItemId() == R.id.new_assignment){
            Event event = new Event();
            event.startTime = this.date;
            event.type = EventType.ASSIGNMENT;
            CalendarRepository.get().addEvent(event);
            callbacks.openIndividualEvent(event);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }


    // TODO: some code for the recycler view?
    /**
     * Each item in the list (RecyclerView) uses this ViewHolder to be
     * displayed. The main purpose of a view holder is to cache the individual
     * views instead of needing to be repetitively finding them by id.
     */
    private class EventViewHolder extends RecyclerView.ViewHolder {
        Event event;
        final ImageView icon;
        EditText name;
        TextView startTime;
        TextView endTime;
        EditText description;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            icon = (ImageView) itemView.findViewById(R.id.eventIcon);
            description = itemView.findViewById(R.id.eventDescription);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.eventEndTime);
            itemView.setOnClickListener(v -> {
                callbacks.getEventById(event.id);
            });

        }
    }

    //TODO set listners for

    /**
     * The adapter for the items list to be displayed in a RecyclerView.
     */
    private class EventListAdapter extends RecyclerView.Adapter<EventViewHolder> {
        /**
         * To create the view holder we inflate the layout we want to use for
         * each item and then return an ItemViewHolder holding the inflated
         * view.
         */
        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false); //TODO: Replace with itemView
            return new EventViewHolder(v);
        }

        /**
         * When we bind a view holder to an item (i.e. use the view with a view
         * holder to display a specific item in the list) we need to update the
         * various views within the holder for our new values.
         * @param holder the ItemViewHolder holding the view to be updated
         * @param position the position in the list of the item to display
         */
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.event = event;
            holder.name.setText(event.name);
            holder.icon.setImageResource(event.type.iconResourceId);
            holder.description.setText(event.description);
            holder.startTime.setText(DateUtils.toFullDateString(event.startTime));
        }

        /**
         * @return the total number of items to be displayed in the list
         */
        @Override
        public int getItemCount() {
            return ListFragment.this.events.size();
        }
    }
    // TODO: some code for the swipe-to-delete?
}