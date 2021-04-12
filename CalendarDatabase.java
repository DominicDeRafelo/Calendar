package edu.moravian.csci299.mocalendar;

import androidx.room.Database;
import androidx.room.RoomDatabase;
/**
 * Abstract class extending RoomDatabase that Room uses to create class for database. We list all entities for Room and in this case
 * it is just our Event class and we create method for assigning our DAO class named CalendarDao.
 */
@Database(entities = {Event.class}, version = 1)
public abstract class CalendarDatabase extends RoomDatabase {
    public abstract CalendarDao calendarDao();
}

