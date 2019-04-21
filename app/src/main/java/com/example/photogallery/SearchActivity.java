package com.example.photogallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class SearchActivity
        extends AppCompatActivity implements View.OnClickListener {

    // Variables

    // Listener handler opens up date selector
    private View.OnClickListener fromDateListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment dialogFragment = new fromDatePickerDialog();
            dialogFragment.show(getSupportFragmentManager(), "From");
            v.setBackgroundColor(0xffffff);
        }
    };
    private View.OnClickListener toDateListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment dialogFragment = new toDatePickerDialog();
            dialogFragment.show(getSupportFragmentManager(), "To");
            v.setBackgroundColor(0xffffff);
        }
    };
    // Listener handles location selection
    private View.OnClickListener locListener = new View.OnClickListener() {
        public void onClick(View v) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#3F51B5")));

        Window window = getWindow();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3F51B5"));

        // Buttons
        Button fromDateBtn = (Button) findViewById(R.id.search_btnFromDate);
        Button toDateBtn = (Button) findViewById(R.id.search_btnToDate);

        Button searchBtn = (Button) findViewById(R.id.search_btnSearch);
        Button backBtn = (Button) findViewById(R.id.search_btnBack);

        fromDateBtn.setOnClickListener(fromDateListener);
        toDateBtn.setOnClickListener(toDateListener);

        searchBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    // Search and back buttons
    public void onClick(View v) {
        if (v.getId() == R.id.search_btnSearch) {
            TextView tempFromDate = findViewById(R.id.search_btnFromDate);
            TextView tempToDate = findViewById(R.id.search_btnToDate);
            TextView tempLoc = findViewById(R.id.search_editLoc);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("SEARCH_FROM_DATE",
                    tempFromDate.getText().toString());
            returnIntent.putExtra("SEARCH_TO_DATE", tempToDate.getText().toString());
            returnIntent.putExtra("SEARCH_LOCATION", tempLoc.getText().toString());

            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } else if (v.getId() == R.id.search_btnBack) {
            // Finish startActivityForResult callback
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    public static class fromDatePickerDialog
            extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(
                    getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, day, month);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView textview = getActivity().findViewById(R.id.search_btnFromDate);

            textview.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    public static class toDatePickerDialog
            extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(
                    getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView textview = getActivity().findViewById(R.id.search_btnToDate);

            textview.setText(day + "/" + (month + 1) + "/" + year);
        }
    }
}
