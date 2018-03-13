package com.ck.clump.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nhat on 5/22/17.
 */

public class SetDateActivity extends BaseActivity {
    private static final String TAG = SetDateActivity.class.getSimpleName();

    @Bind(R.id.date_picker_month_year)
    TextView textMonthYear;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvLeft)
    TextView imvLeft;
    @Bind(R.id.btnToday)
    Button btnToday;
    @Bind(R.id.btnTomorrow)
    Button btnTomorrow;
    @Bind(R.id.btnDone)
    Button btnDone;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    @Bind(R.id.calendar)
    GridView calendarView;
    private GridCellAdapter adapter;
    private Calendar mCalendar;
    private int month, year, day;
    private static final String dateTemplate = "MMMM yyyy";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);
        ButterKnife.bind(this);
        mContext = this;

        mCalendar = Calendar.getInstance(Locale.getDefault());
        month = mCalendar.get(Calendar.MONTH) + 1;
        year = mCalendar.get(Calendar.YEAR);
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        // Initialised
        adapter = new GridCellAdapter(mContext,
                R.id.calendar_day_gridcell);
        calendarView.setAdapter(adapter);
        adapter.printMonth(month, year);
        adapter.notifyDataSetChanged();

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvLeft.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnToday.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnTomorrow.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        textMonthYear.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnDone.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
    }


    @OnClick(R.id.imvLeft)
    public void imvLeftClick() {
        finish();
    }

    @OnClick(R.id.btnDone)
    public void done() {
        Intent intent = new Intent();
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        setResult(RESULT_OK, intent);
        finish();
    }


    @OnClick(R.id.btnToday)
    public void today() {
        mCalendar = Calendar.getInstance(Locale.getDefault());
        month = mCalendar.get(Calendar.MONTH) + 1;
        year = mCalendar.get(Calendar.YEAR);
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        setGridCellAdapterToDate(month, year, day);
    }

    @OnClick(R.id.btnTomorrow)
    public void tomorrow() {
        month = mCalendar.get(Calendar.MONTH) + 1;
        year = mCalendar.get(Calendar.YEAR);
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = adapter.getNumberOfDaysOfMonth(month - 1);
        if (day < daysInMonth) {
            day++;
        } else {
            day = 1;
            if (month < 12) {
                month++;
            } else {
                month = 1;
                year++;
            }
        }
        setGridCellAdapterToDate(month, year, day);
    }

    @OnClick(R.id.imvPrevious)
    public void previousMonth() {
        if (month <= 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
        setGridCellAdapterToDate(month, year, 0);
    }

    @OnClick(R.id.imvNext)
    public void nextMonth() {
        if (month > 11) {
            month = 1;
            year++;
        } else {
            month++;
        }
        setGridCellAdapterToDate(month, year, 0);
    }


    private void setGridCellAdapterToDate(int month, int year, int daySelected) {
        adapter.setDaySelected(daySelected);
        adapter.printMonth(month, year);
        adapter.notifyDataSetChanged();
    }

    public class GridCellAdapter extends BaseAdapter {
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = new String[]{"Sun", "Mon", "Tue",
                "Wed", "Thu", "Fri", "Sat"};
        private final String[] months = {"January", "February", "March",
                "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
                31, 30, 31};
        private int daysInMonth;
        private int currentDayOfMonth;
        private int daySelected;
        private int currentWeekDay;
        private TextView gridcell;
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId) {
            super();
            this._context = context;
            this.list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setDaySelected(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        private void printMonth(int mm, int yy) {
            list.clear();
            Calendar calendar = Calendar.getInstance();
            calendar.set(yy, mm - 1, 1);
            textMonthYear.setText(DateFormat.format(dateTemplate,
                    calendar.getTime()));
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++daysInMonth;
                else if (mm == 3)
                    ++daysInPrevMonth;

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i)
                        + "-GREY1"
                        + "-"
                        + getMonthAsString(prevMonth)
                        + "-"
                        + prevYear);
            }

            // Current Month Days
            int month = mCalendar.get(Calendar.MONTH);
            for (int i = 1; i <= daysInMonth; i++) {
                Log.d(currentMonthName, String.valueOf(i) + " "
                        + getMonthAsString(currentMonth) + " " + yy);
                if (month != currentMonth) {
                    if (i == getDaySelected()) {
                        list.add(String.valueOf(i) + "-SELECTED" + "-"
                                + getMonthAsString(currentMonth) + "-" + yy);
                    } else {
                        list.add(String.valueOf(i) + "-NORMAL" + "-"
                                + getMonthAsString(currentMonth) + "-" + yy);
                    }
                } else {
                    if (i == getCurrentDayOfMonth() && getCurrentDayOfMonth() == getDaySelected()) {
                        list.add(String.valueOf(i) + "-SELECTED" + "-"
                                + getMonthAsString(currentMonth) + "-" + yy);
                    } else if (i == getCurrentDayOfMonth() || i == getDaySelected()) {
                        if (i == getCurrentDayOfMonth()) {
                            String text = "CURRENT";
                            if (getDaySelected() == 0) {
                                text = "SELECTED";
                            }
                            list.add(String.valueOf(i) + "-" + text + "-"
                                    + getMonthAsString(currentMonth) + "-" + yy);
                        } else {
                            list.add(String.valueOf(i) + "-SELECTED" + "-"
                                    + getMonthAsString(currentMonth) + "-" + yy);
                        }
                    } else {
                        list.add(String.valueOf(i) + "-NORMAL" + "-"
                                + getMonthAsString(currentMonth) + "-" + yy);
                    }
                }
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                list.add(String.valueOf(i + 1) + "-GREY2" + "-"
                        + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.screen_gridcell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (TextView) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

            // ACCOUNT FOR SPACING
            String[] day_color = list.get(position).split("-");
            String theday = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];

            // Set the Day GridCell
            gridcell.setText(theday);
            gridcell.setTag(theday + "-" + themonth + "-" + theyear);

            if (day_color[1].equals("GREY1") || day_color[1].equals("GREY2")) {
                gridcell.setTextColor(getResources()
                        .getColor(R.color.textHeaderSecondary));
            }
            if (day_color[1].equals("NORMAL")) {
                gridcell.setBackgroundResource(R.drawable.calendar_button_normal);
                gridcell.setTextColor(getResources().getColor(android.R.color.black));
            }
            if (day_color[1].equals("CURRENT")) {
                gridcell.setBackgroundResource(R.drawable.calendar_button_current_day);
                gridcell.setTextColor(getResources().getColor(android.R.color.black));
            }
            if (day_color[1].equals("SELECTED")) {
                gridcell.setBackgroundResource(R.drawable.calendar_button_selected);
                gridcell.setTextColor(getResources().getColor(android.R.color.white));
            }
            gridcell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_month_year = (String) v.getTag();
                    try {
                        Date parsedDate = dateFormatter.parse(date_month_year);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parsedDate);

                        month = calendar.get(Calendar.MONTH) + 1;
                        year = calendar.get(Calendar.YEAR);
                        day = calendar.get(Calendar.DAY_OF_MONTH);

                        setDaySelected(day);
                        printMonth(month, year);
                        notifyDataSetChanged();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        public int getDaySelected() {
            return daySelected;
        }

        public void setDaySelected(int daySelected) {
            this.daySelected = daySelected;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, Constant.OTHER_ACTIVITIES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, null);
        handler.removeCallbacks(dismissNotificationRunnable);
        llNotification.setVisibility(View.GONE);
    }

    private Handler handler = new Handler();
    private Runnable dismissNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            llNotification.setVisibility(View.GONE);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChatMessageEvent event) {
        if (llNotification.getVisibility() == View.VISIBLE) {
            handler.removeCallbacks(dismissNotificationRunnable);
        } else {
            llNotification.setVisibility(View.VISIBLE);
        }
        txtNotificationName.setText(event.getName());
        txtNotificationMessage.setText(event.getContent());
        handler.postDelayed(dismissNotificationRunnable, 2000);
    }

}
