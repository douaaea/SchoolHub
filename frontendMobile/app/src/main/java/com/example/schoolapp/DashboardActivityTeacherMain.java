package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.adminPart.AddGroupActivity;
import com.example.schoolapp.adminPart.AddTeacherActivity;

public class DashboardActivityTeacherMain extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_teacher_main);
    }

    public static class DashboardActivityMain extends AppCompatActivity {
        private static final String TAG = "DashboardActivityMain";
        private Button buttonManageTeachers, buttonManageGroups;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "Starting DashboardActivityMain");
            setContentView(R.layout.activity_dashboard_main);

            buttonManageTeachers = findViewById(R.id.buttonManageTeachers);
            buttonManageGroups = findViewById(R.id.buttonManageGroups);

            buttonManageTeachers.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to AddTeacherActivity");
                startActivity(new Intent(this, AddTeacherActivity.class));
            });

            buttonManageGroups.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to AddGroupActivity");
                startActivity(new Intent(this, AddGroupActivity.class));
            });
        }
    }
}