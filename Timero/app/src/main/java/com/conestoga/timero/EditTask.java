package com.conestoga.timero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditTask extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Button btnSubmit;
    private TextView txtTask;
    private TextView txtError;
    private DatabaseHelper databaseHelper;

    View.OnClickListener btnSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (txtTask.getText() == null || txtTask.getText().toString().trim().equals("")) {
                final String errorMessage = "Task must have a character";
                txtError.setText(errorMessage);
            }
            else {
                int id = sharedPreferences.getInt("edit_task_id", 0);

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTask(txtTask.getText().toString());
                todo.setIsCompleted(1);
                databaseHelper.updateTodo(todo);
                startActivity(new Intent(getApplicationContext(), TasksActivity.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isDarkTheme = sharedPreferences.getBoolean("theme_value", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_Timero_Dark);
        }
        else {
            setTheme(R.style.Theme_Timero);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        int id = sharedPreferences.getInt("edit_task_id", 0);

        Todo todo = databaseHelper.getTodo(id);

        txtTask = findViewById(R.id.txtTask);

        if (id != 0) {
            txtTask.setText(todo.getTask());
        }

        txtError = findViewById(R.id.txtError);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(btnSubmitListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isDarkTheme = sharedPreferences.getBoolean("theme_value", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_Timero_Dark);
        }
        else {
            setTheme(R.style.Theme_Timero);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timero_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.menu_tasks:
                startActivity(new Intent(getApplicationContext(), TasksActivity.class));
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }
}