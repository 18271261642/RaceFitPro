package com.example.bozhilun.android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.CalendarView;

import com.example.bozhilun.android.R;

/**
 * 日期选择
 * Created by Admin
 * Date 2020/4/16
 */
public class DateSelectDialogView extends Dialog {

    private CalendarView dateSelectCalendarView;

    private OnDateSelectListener onDateSelectListener;

    public void setOnDateSelectListener(OnDateSelectListener onDateSelectListener) {
        this.onDateSelectListener = onDateSelectListener;
    }

    public DateSelectDialogView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_date_layout);

        dateSelectCalendarView = findViewById(R.id.dateSelectCalendarView);

        dateSelectCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month+1;
                if(onDateSelectListener != null)
                    onDateSelectListener.selectDateStr(year+"-"+(month<10?"0"+month : month)+"-"+(dayOfMonth<10?"0"+dayOfMonth:dayOfMonth));
            }
        });

    }


    public interface OnDateSelectListener{

        void selectDateStr(String str);
    }


}
