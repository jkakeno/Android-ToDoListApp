package com.example.jkakeno.todolist;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


//This is app is a simple ToDoList which the user can add, delete task and assign due dates to each task.
//The use is also able to sort the task by the due dates in ascending or descending order.


public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    DbHelper mDbHelper;
    RecyclerViewAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<Entry> mEntry;

    public EditText taskEditText;
    public EditText dateEditText;
    public DatePickerDialog datePickerDialog;
    Toolbar mToolBar;
    Spinner mSpinner;
    Button mAddButton;

    int mYear;
    int mMonth;
    int mDay;
    long dateLong;
    long entryId;
    String dateFormatted;
    String taskDetail;
    static String selection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//Create view variables
        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        mSpinner = (Spinner)findViewById(R.id.spinner);
        mAddButton = (Button) findViewById(R.id.btnAdd);
        mRecyclerView = (RecyclerView) findViewById(R.id.lstTask);

//Set tool bar tittle as app name
        mToolBar.setTitle(getResources().getString(R.string.app_name));
//Create helper object
        mDbHelper = new DbHelper(this);

//Create spinner to sort entry
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.custom_spinner_item,getResources().getStringArray(R.array.spinner_list_item_array));
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(MainActivity.this,mSpinner.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                selection = mSpinner.getSelectedItem().toString();
                loadTaskList();
                if (selection.equals("Ascending")){
                    Toast.makeText(MainActivity.this,"Ascend Selected",Toast.LENGTH_SHORT).show();
                } else if (selection.equals("Descending")){
                    Toast.makeText(MainActivity.this,"Descend Selected",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

//Set onClick to Add Button
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//Set the Alert Dialog Layout
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Enter a Task...");
                dialog.setMessage("");
                dateEditText = new EditText(MainActivity.this);
                dateEditText.setHint("yyyy/mm/dd");
                taskEditText = new EditText(MainActivity.this);
                taskEditText.setHint("Task...");

                LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(dateEditText);
                linearLayout.addView(taskEditText);
                dialog.setView(linearLayout);

//Set date picker
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR); // current year
                mMonth = calendar.get(Calendar.MONTH); // current month
                mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day
                datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

//Convert date to dateLong in milliseconds since 1/1/1970.
                        calendar.set(Calendar.YEAR,datePicker.getYear());
                        calendar.set(Calendar.MONTH,datePicker.getMonth());
                        calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
                        dateLong = calendar.getTimeInMillis();
//Format dateLong to string representation of date
                        dateFormatted = new SimpleDateFormat("yyyy/MM/dd").format(new Date(dateLong));
//Set dateFormatted to date picker dialog dateEditText
                        dateEditText.setText(dateFormatted);

//Set dateEditText with selected date from date picker
//                        dateEditText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

//Add an OnClick to the positive button of the Alert Dialog
                dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
//Pass task and date to entry object
                        taskDetail = String.valueOf(taskEditText.getText());
//Pass entry object to insertNewTask() and get a entryId
                        entryId = mDbHelper.insertNewTask(taskDetail, dateLong);
                        Log.d(TAG,String.valueOf(entryId));
                        loadTaskList();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                dialog.show();
                datePickerDialog.show();
            }
        });
    }

    private void loadTaskList() {
//Get the list of entry from the db
        ArrayList<Entry> entryList = mDbHelper.sortEntry(selection);
//If the adapter is empty create an adapter and pass data to the adapter
        if(mAdapter==null){
//Set the recycler view as linear layout
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(true);
//Inset the list of entry to the adapter
            mAdapter = new RecyclerViewAdapter(this,entryList, mDbHelper);
//Set the adapter to the recycler view to display the data in the adapter
            mRecyclerView.setAdapter(mAdapter);
        }
//If the adapter is not empty clear the adapter, add all data to the adapter, and notify the adapter that data has changed
        else{
            mAdapter.clear();
            mAdapter.addAll(entryList);
            mAdapter.notifyDataSetChanged();
        }
    }

//NOTE: This method gets the btnDelete button as the view. Since the btnDelete onClick property is set to this method, when btnDelete is pressed this method will run
    public void deleteTask(View view) {
//Pass the entryId to mDbHelper delete method
            mDbHelper.deleteTask(entryId);
            loadTaskList();
    }
}
