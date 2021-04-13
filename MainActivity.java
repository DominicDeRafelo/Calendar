package edu.moravian.csci299.mocalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.Date;

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
public class MainActivity extends AppCompatActivity implements CalendarFragment.Callbacks {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            // If no fragment is displayed in fragment_container, add one with a transaction
            CalendarFragment fragment = CalendarFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();

//            ListFragment listFragment = ListFragment.newInstance();
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fragment_container, listFragment)
//                    .commit();
        }


    }

    @Override
    public void onDayChanged(Date date) {

    }
}
