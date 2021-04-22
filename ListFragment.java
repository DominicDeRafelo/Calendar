package edu.moravian.csci299.mocalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private RecyclerView recyclerView;
    private LiveData<List<Event>> liveDataItems;


    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for today.
     *
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance() {
        return newInstance(new Date());
    }

    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for the given day.
     *
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
     *
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
        date = DateUtils.useDateOrNow((Date) getArguments().getSerializable(ARG_DATE));
//        liveDataItems = CalendarRepository.get().getEventsOnDay(date);
//        liveDataItems.observe(this, (events) -> {
//            this.events = events;
//        });
        onDateChange();
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

        // TODO
        // Setup the recycler
        recyclerView = base.findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EventListAdapter adapter = new EventListAdapter();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
            recyclerView.getAdapter().notifyDataSetChanged(); // is this how we update UI?
        });
    }

    // TODO: some code for (un)registering callbacks?
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    // TODO: some code for the menu options?
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_event) {
            Event event = new Event();
            event.startTime = this.date;
            event.endTime = new Date(this.date.getTime() + 3600000);
            CalendarRepository.get().addEvent(event);
            callbacks.openIndividualEvent(event);
            recyclerView.getAdapter().notifyDataSetChanged();
            return true;
        } else if (item.getItemId() == R.id.new_assignment) {
            Event event = new Event();
            event.startTime = this.date;
            event.type = EventType.ASSIGNMENT;
            CalendarRepository.get().addEvent(event);
            callbacks.openIndividualEvent(event);
            recyclerView.getAdapter().notifyDataSetChanged();
            return true;
        } else {
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
        ImageView icon;
        TextView name;
        TextView startTime;
        TextView endTime;
        TextView description;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.EventName);
            name.setMaxLines(1);
            name.setEllipsize(TextUtils.TruncateAt.END);
            icon = itemView.findViewById(R.id.eventIcon);
            description = itemView.findViewById(R.id.eventDescription);
            description.setMaxLines(1);
            description.setEllipsize(TextUtils.TruncateAt.END);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.eventEndTime);
            itemView.setOnClickListener(v -> {
                callbacks.openIndividualEvent(event);
            });
        }
    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private EventListAdapter listAdapter;
        private Drawable icon;
        private final ColorDrawable background;

        public SwipeToDeleteCallback(EventListAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            listAdapter = adapter;
            icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_white_36);
            background = new ColorDrawable(Color.BLACK);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            listAdapter.deleteItem(position);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    }


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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new EventViewHolder(v);
        }

        /**
         * When we bind a view holder to an item (i.e. use the view with a view
         * holder to display a specific item in the list) we need to update the
         * various views within the holder for our new values.
         *
         * @param holder   the ItemViewHolder holding the view to be updated
         * @param position the position in the list of the item to display
         */
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.event = event;
            holder.name.setText(event.name);
            holder.icon.setImageResource(event.type.iconResourceId);
            holder.description.setText(event.description);
            holder.startTime.setText(DateUtils.toTimeString(event.startTime));
            if (event.endTime != null)
                holder.endTime.setText(DateUtils.toTimeString(event.endTime));
        }

        /**
         * @return the total number of items to be displayed in the list
         */
        @Override
        public int getItemCount() {
            return ListFragment.this.events.size();
        }


        public void deleteItem(int position) {
            CalendarRepository.get().removeEvent(ListFragment.this.events.get(position));
            notifyItemRemoved(position);
        }

        // TODO: some code for the swipe-to-delete?
    }
}