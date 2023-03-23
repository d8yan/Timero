package com.conestoga.timero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Button btnAdd;
    private DatabaseHelper databaseHelper;

    public void createTasks() {
        ArrayList<Todo> todos = databaseHelper.getAllTodos();

        LinearLayout mainLayout = findViewById(R.id.layout_main);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        for (int taskIndex = 0; taskIndex < todos.size(); taskIndex++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView task = new TextView(this);
            task.setText(todos.get(taskIndex).getTask());
            layout.addView(task);

            final String btnEditText = "Edit";
            Button btnEdit = new Button(this);
            btnEdit.setId(todos.get(taskIndex).getId());
            btnEdit.setText(btnEditText);
            btnEdit.setLayoutParams(params);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedPreferences.edit().putInt("edit_task_id", btnEdit.getId()).apply();
                    startActivity(new Intent(getApplicationContext(), EditTask.class));
                }
            });

            layout.addView(btnEdit);

            final String btnRemoveText = "Complete";
            Button btnRemove = new Button(this);
            btnRemove.setId(todos.get(taskIndex).getId());
            btnRemove.setText(btnRemoveText);
            btnRemove.setLayoutParams(params);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseHelper.removeTodo(btnRemove.getId());
                    refresh();
                    startService(new Intent(getApplicationContext(), NotificationService.class));
                }
            });

            layout.addView(btnRemove);
            mainLayout.addView(layout);
        }
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }

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
        setContentView(R.layout.activity_tasks);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddTask.class));
            }
        });

        createTasks();
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
        getMenuInflater().inflate(R.menu.timero_tasks_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }
}