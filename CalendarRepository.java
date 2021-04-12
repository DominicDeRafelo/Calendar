package edu.moravian.csci299.mocalendar;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creates the repository for our database in this application. We make the room database a singleton and have
 * three class variables to save the singleton instance of our application's database, our executor, and its DAO.
 */
public class CalendarRepository {
    private final CalendarDatabase database;
    private final CalendarDao calendarDao;
    private final Executor executor = Executors.newSingleThreadExecutor(); //for assigning tasks to background thread

    /**
     * Private constructor for creating instance of singleton repository. Set the database and the DAO to the respective
     * class variables and build database using databaseBuilder with the context as the parameter of context from class
     * that calls initialize.
     *
     * @param context context that is passed from initialize() as parameter for building the database repository, in our case
     *                it is CalendarApplication.
     */
    private CalendarRepository(Context context){
        database = Room.databaseBuilder(
                context.getApplicationContext(),
                CalendarDatabase.class,
                "calendar_database").build();
        calendarDao = database.calendarDao();
    }

    /**
     * public method for for accessing the getAllEvents() method in CalendarDao.
     *
     * @return all events in database in form of live data list
     */
    public LiveData<List<Event>> getAllEvents() { return calendarDao.getAllEvents(); }

    /**
     *  public method for for accessing the getEventById() method in CalendarDao.
     * @param id UUID of the event we which to return from database
     * @return LiveDataList<<Event>> that has the id specified by the parameter
     */
    public LiveData<Event> getEventById(UUID id) { return calendarDao.getEventById(id); }

    /**
     * public method for for accessing the getEventsBetween() method in CalendarDao.
     * @param start the start Date
     * @param end the end Date
     * @return LiveList<List<Event>> from the start date to the end date
     */
    public LiveData<List<Event>> getEventsBetween(Date start, Date end){
        return calendarDao.getEventsBetween(start, end);
    }

    /**
     * public method for for accessing the getEventsOnDay() method in CalendarDao
     * @param date Date to get the events of
     * @return LiveData list of events from that day
     */
    public LiveData<List<Event>> getEventsOnDay(Date date){
        return calendarDao.getEventsOnDay(date);
    }

    /**
     * public method for accessing addEvent() in CalendarDao to add an event to the database using background thread
     * @param event event to add to the Database
     */

    public void addEvent(Event event){
        executor.execute(() ->{
            calendarDao.addEvent(event);
        });
    }
    /**
     * public method for accessing updateEvent() in CalendarDao to update an event in the database using background thread
     * @param event event to update within the Database
     */
    public void updateEvent(Event event){
        executor.execute(() -> {
            calendarDao.updateEvent(event);
        });
    }
    /**
     * public method for accessing removeEvent() in CalendarDao to remove an event in the database using background thread
     * @param event event to removed within the Database
     */
    public void removeEvent(Event event){
        executor.execute(() ->{
            calendarDao.removeEvent(event);
        });
    }
    // Creating the single instance of the repository to ensure singleton format
    private static CalendarRepository INSTANCE;
    /**
     * Public method to get the repository to be accessed in our fragments. If singleton is null throws exception.
     *
     * @return the singleton repository for this application's database.
     */
    public static CalendarRepository get() {
        if (INSTANCE == null) { throw new IllegalStateException("CollectibleRepository must be initialized"); }
        return INSTANCE;
    }
    /**
     * method used to initialize the repository for database, if singleton instance is null, pass the context given as parameter
     * to the constructor to initialize the repository.
     *
     * @param context context used to pass as argument for repository constructor, for this project context is CalendarApplication
     */
    public static void initialize(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CalendarRepository(context);
        }
    }
}
