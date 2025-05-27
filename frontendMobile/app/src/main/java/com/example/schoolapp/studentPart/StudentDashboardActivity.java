package com.example.schoolapp.studentPart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.LoginActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "StudentDashboardActivity";
    private ApiService apiService;
    private Long studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting StudentDashboardActivity");
        setContentView(R.layout.activity_student_dashboard);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get student ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        studentId = prefs.getLong("student_id", -1); // Assuming student_id is used
        if (studentId == -1) {
            Log.e(TAG, "Student ID not found in SharedPreferences");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Buttons
        Button buttonCheckAssignments = findViewById(R.id.buttonCheckAssignments);
        Button buttonCheckGrades = findViewById(R.id.buttonCheckGrades);
        Button buttonEditProfile = findViewById(R.id.buttonEditProfile);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // Set Button Click Listeners
        buttonCheckAssignments.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to CheckAssignmentsActivity");
            startActivity(new Intent(this, CheckAssignmentsActivity.class)); // Placeholder
        });

        buttonCheckGrades.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to CheckGradesActivity");
            startActivity(new Intent(this, CheckGradesActivity.class)); // Placeholder
        });

        buttonEditProfile.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to EditProfileActivity");
            startActivity(new Intent(this, EditProfileActivity.class)); // Placeholder
        });

        buttonLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logging out");
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Set up Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                return true; // Stay on dashboard
            } else if (item.getItemId() == R.id.nav_student_assignments) {
                Log.d(TAG, "Navigating to CheckAssignmentsActivity from bottom nav");
                startActivity(new Intent(this, CheckAssignmentsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_student_grades) {
                Log.d(TAG, "Navigating to CheckGradesActivity from bottom nav");
                startActivity(new Intent(this, CheckGradesActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_student_profile) {
                Log.d(TAG, "Navigating to EditProfileActivity from bottom nav");
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}