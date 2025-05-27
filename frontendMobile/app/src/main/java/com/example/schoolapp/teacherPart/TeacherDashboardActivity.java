package com.example.schoolapp.teacherPart;

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
import com.example.schoolapp.adminPart.ManageStudentsActivity;
import com.example.schoolapp.model.Group;
import com.example.schoolapp.model.Program;
import com.example.schoolapp.model.Student;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity {
    private static final String TAG = "TeacherDashboardActivity";
    private ApiService apiService;
    private TextView textTotalStudents, textTotalPrograms;
    private Long teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting TeacherDashboardActivity");
        setContentView(R.layout.activity_teacher_dashboard);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get teacher ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        teacherId = prefs.getLong("teacher_id", -1); // Use teacher_id as per previous fix
        if (teacherId == -1) {
            Log.e(TAG, "Teacher ID not found in SharedPreferences");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize TextViews
        textTotalStudents = findViewById(R.id.textTotalStudents);
        textTotalPrograms = findViewById(R.id.textTotalPrograms);

        // Initialize Buttons
        Button buttonManageAssignments = findViewById(R.id.buttonManageAssignments);
        Button buttonGradeSubmissions = findViewById(R.id.buttonGradeSubmissions);
        Button buttonProfile = findViewById(R.id.buttonProfile);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // Set Button Click Listeners
        buttonManageAssignments.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageAssignmentsActivity");
            startActivity(new Intent(this, ManageAssignmentsActivity.class));
        });

        buttonGradeSubmissions.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to GradeSubmissionsActivity");
            startActivity(new Intent(this, GradeWorkReturnsActivity.class));
        });

        buttonProfile.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to TeacherProfileActivity");
            startActivity(new Intent(this, TeacherProfileActivity.class));
        });

        buttonLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logging out");
            // Clear shared preferences
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
            // Navigate to LoginActivity
            Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Close TeacherDashboardActivity
        });

        // Set up Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                return true; // Stay on dashboard
            } else if (item.getItemId() == R.id.nav_teacher_assignments) {
                Log.d(TAG, "Navigating to ManageAssignmentsActivity from bottom nav");
                startActivity(new Intent(this, ManageAssignmentsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_teacher_students) {
                Log.d(TAG, "Navigating to ManageStudentsActivity from bottom nav");
                startActivity(new Intent(this, ManageStudentsActivity.class)); // Assuming this exists for teacher context
                return true;
            } else if (item.getItemId() == R.id.nav_teacher_profile) {
                Log.d(TAG, "Navigating to TeacherProfileActivity from bottom nav");
                startActivity(new Intent(this, TeacherProfileActivity.class));
                return true;
            }
            return false;
        });

        // Fetch totals when activity starts
        fetchTotals();
    }

    private void fetchTotals() {
        fetchProgramCount();
        fetchStudentCount();
    }

    private void fetchProgramCount() {
        Call<List<Program>> call = apiService.getPrograms();
        call.enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Program> allPrograms = response.body();
                    List<Program> teacherPrograms = new ArrayList<>();
                    for (Program program : allPrograms) {
                        if (program.getTeacher() != null && program.getTeacher().getId() != null &&
                                program.getTeacher().getId().equals(teacherId)) {
                            teacherPrograms.add(program);
                        }
                    }
                    int count = teacherPrograms.size();
                    textTotalPrograms.setText("Total Programs: " + count);
                    Log.d(TAG, "Fetched program count: " + count);
                } else {
                    Log.e(TAG, "Failed to fetch programs: " + response.code());
                    textTotalPrograms.setText("Total Programs: Error");
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Log.e(TAG, "Network error fetching programs: " + t.getMessage(), t);
                textTotalPrograms.setText("Total Programs: Network Error");
            }
        });
    }

    private void fetchStudentCount() {
        Call<List<Program>> programCall = apiService.getPrograms();
        programCall.enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Program> allPrograms = response.body();
                    List<Program> teacherPrograms = new ArrayList<>();
                    for (Program program : allPrograms) {
                        if (program.getTeacher() != null && program.getTeacher().getId() != null &&
                                program.getTeacher().getId().equals(teacherId)) {
                            teacherPrograms.add(program);
                        }
                    }

                    List<Long> groupIds = new ArrayList<>();
                    for (Program program : teacherPrograms) {
                        if (program.getGroup() != null && program.getGroup().getId() != null) {
                            groupIds.add(program.getGroup().getId());
                        }
                    }

                    Call<List<Student>> studentCall = apiService.getStudents();
                    studentCall.enqueue(new Callback<List<Student>>() {
                        @Override
                        public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Student> allStudents = response.body();
                                List<Student> teacherStudents = new ArrayList<>();
                                for (Student student : allStudents) {
                                    if (student.getGroup() != null && student.getGroup().getId() != null &&
                                            groupIds.contains(student.getGroup().getId())) {
                                        teacherStudents.add(student);
                                    }
                                }
                                int count = teacherStudents.size();
                                textTotalStudents.setText("Total Students: " + count);
                                Log.d(TAG, "Fetched student count: " + count);
                            } else {
                                Log.e(TAG, "Failed to fetch students: " + response.code());
                                textTotalStudents.setText("Total Students: Error");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Student>> call, Throwable t) {
                            Log.e(TAG, "Network error fetching students: " + t.getMessage(), t);
                            textTotalStudents.setText("Total Students: Network Error");
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch programs: " + response.code());
                    textTotalStudents.setText("Total Students: Error");
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Log.e(TAG, "Network error fetching programs: " + t.getMessage(), t);
                textTotalStudents.setText("Total Students: Network Error");
            }
        });
    }
}