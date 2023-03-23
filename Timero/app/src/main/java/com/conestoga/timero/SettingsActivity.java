package com.conestoga.timero;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private Button btnClearTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isDarkTheme = sharedPreferences.getBoolean("theme_value", false);

        if (isDarkTheme) {
            setTheme(R.style.Theme_Timero_Dark);
        }
        else {
            // Sets the theme for the app
            setTheme(R.style.Theme_Timero);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnClearTasks = findViewById(R.id.btnClearTasks);
        btnClearTasks.setOnClickListener(btnClearTasksListener);

        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    View.OnClickListener btnClearTasksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            databaseHelper.clearTasks();
            Snackbar.make(view,R.string.clear_task_msg,Snackbar.LENGTH_LONG).show();
        }
    };
}