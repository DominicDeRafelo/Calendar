package edu.moravian.csci299.mocalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The main (and only) activity for the application that hosts all of the fragments.
 *
 * It starts out with a calendar and list fragment (if vertical then they are above/below each
 * other, if horizontal then they are left/right of each other). When a day is clicked in the
 * calendar, the list shows all events for that day.
 *
 * When an event is being edited/viewed (because it was clicked in the list or a new event is being
 * added) then the fragments are replaced with an event fragment which shows the details for a
 * specific event and allows editing.
 *
 * NOTE: This Activity is the bare-bones, empty, Activity. Work will be definitely needed in
 * onCreate() along with implementing some callbacks.
 */
public class MainActivity extends AppCompatActivity implements CalendarFragment.Callbacks, ListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment == null) {
            // If no fragment is displayed in fragment_container, add one with a transaction
            CalendarFragment fragment = CalendarFragment.newInstance();
            ListFragment listFragment = ListFragment.newInstance();
            DateFragment dateFragment = DateFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    //.add(R.id.fragment_container, dateFragment)
                    .add(R.id.fragment_container, listFragment)
                    .commit();
        }


    }

    @Override
    public void onDayChanged(Date date) {
        ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.setDay(date);
//        DateFragment dateFragment = (DateFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        dateFragment.setDate(date);
    }

    @Override
    public void getEventById(UUID id) {
        ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

    }

    @Override
    public void openIndividualEvent(Event event) {
        EventFragment fragment = EventFragment.newInstance(event);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
