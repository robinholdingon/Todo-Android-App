package com.feng.jian.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ItemActivity extends AppCompatActivity {
    private Item item;

    private DatePicker dp;
    private TextView itemDescription;
    private Spinner prioritySpinner;
//    private Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Intent intent = getIntent();
        long id = intent.getLongExtra("id", -1);
        item = ItemDatabaseHelper.getInstance(getApplicationContext()).getItem(id);

        dp = (DatePicker) findViewById(R.id.datePicker2);
        itemDescription = (TextView) findViewById(R.id.itemDescription);
        prioritySpinner = (Spinner) findViewById(R.id.itemPriority);
//        statusSpinner = (Spinner) findViewById(R.id.itemStatus);

        populateViewWithItem();
    }

    private void populateViewWithItem() {
        populateTitle(item.getTitle());
        populateNotes(item.getDescription());
        populateDatePicker(item.getDue());
        populatePriority(item.getPriority());
//        populateStatus(item.isComplete());

        bindSaveButton();
    }

    private void populateTitle(String title) {
        TextView textView = (TextView) findViewById(R.id.itemActivityTitle);
        textView.setText(title);
    }

    private void populateNotes(String description) {
        itemDescription.setText(description);
    }

    private void populateDatePicker(Date date) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        dp.updateDate(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
    }

    private void populatePriority(Item.Priority priority) {
        ArrayAdapter adapter = (ArrayAdapter) prioritySpinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItem(position).toString().equals(priority.toString())) {
                prioritySpinner.setSelection(position);
                return;
            }
        }
    }
//
//    private void populateStatus(boolean isComplete) {
//        statusSpinner.setSelection(isComplete ? 0 : 1);
//    }

    private void bindSaveButton() {
        Button saveButton = (Button) findViewById(R.id.itemSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setDescription(itemDescription.getText().toString());

                Calendar cal = Calendar.getInstance();
                cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                item.setDue(cal.getTime());
                item.setPriority(Item.Priority.valueOf(prioritySpinner.getSelectedItem().toString()));
//                item.setComplete(statusSpinner.getSelectedItem().toString().equals("Complete"));

                ItemDatabaseHelper.getInstance(getApplicationContext()).updateItem(item);
                finish();
            }
        });
    }

}
