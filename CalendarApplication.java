package edu.moravian.csci299.mocalendar;

import android.app.Application;

/**
 * Public class extending Application that is used by our application to Initialize our CalendarRepository singleton which
 * passes itself as the context for the initialize() parameter.
 */

public class CalendarApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the repository with this application as the context
        CalendarRepository.initialize(this);
    }
}
