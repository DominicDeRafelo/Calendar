package edu.moravian.csci299.mocalendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.UUID;

public class DateFragment extends Fragment {

    private TextView dateTextView;
    private static final String ARG_DATE = "date";
    public DateFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of this fragment that
     * highlights today initially.
     * @return a new instance of fragment CalendarFragment.
     */
    public static DateFragment newInstance() {
        return newInstance(new Date());
    }

    /**
     * Use this factory method to create a new instance of this fragment that
     * highlights the given day initially.
     * @param date the date to highlight initially.
     * @return a new instance of fragment CalendarFragment.
     */
    public static DateFragment newInstance(Date date) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The date to initially highlight
        Date date = DateUtils.useDateOrNow((Date) getArguments().getSerializable(ARG_DATE));
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_date, container, false);
        dateTextView = base.findViewById(R.id.dateText);
        dateTextView.setText(DateUtils.toFullDateString(date)); //passes a long as the argument which is got through Date's getTime()
        return base;
    }

    public void setDate(Date date){
        dateTextView.setText(DateUtils.toFullDateString(date));
    }

}
