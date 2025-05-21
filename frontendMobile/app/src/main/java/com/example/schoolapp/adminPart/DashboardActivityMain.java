package com.example.schoolapp.adminPart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Group;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.model.Program;
import com.example.schoolapp.model.Student;
import com.example.schoolapp.model.Subject; // Updated import
import com.example.schoolapp.model.Teacher;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class DashboardActivityMain extends AppCompatActivity {
    private static final String TAG = "DashboardActivityMain";
    private ApiService apiService;
    private TextView textTotalTeachers, textTotalStudents, textTotalGroups, textTotalLevels, textTotalPrograms, textTotalSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting DashboardActivityMain");
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize TextViews
        textTotalTeachers = findViewById(R.id.textTotalTeachers);
        textTotalStudents = findViewById(R.id.textTotalStudents);
        textTotalGroups = findViewById(R.id.textTotalGroups);
        textTotalLevels = findViewById(R.id.textTotalLevels);
        textTotalPrograms = findViewById(R.id.textTotalPrograms);
        textTotalSubjects = findViewById(R.id.textTotalSubjects);

        // Initialize Buttons
        Button buttonManageTeachers = findViewById(R.id.buttonManageTeachers);
        Button buttonManageStudents = findViewById(R.id.buttonManageStudents);
        Button buttonManageGroups = findViewById(R.id.buttonManageGroups);
        Button buttonManageLevels = findViewById(R.id.buttonManageLevels);
        Button buttonManagePrograms = findViewById(R.id.buttonManagePrograms);
        Button buttonManageSubjects = findViewById(R.id.buttonManageSubjects);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // Set Button Click Listeners
        buttonManageTeachers.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageTeachersActivity");
            startActivity(new Intent(this, ManageTeachersActivity.class));
        });

        buttonManageStudents.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageStudentsActivity");
            startActivity(new Intent(this, ManageStudentsActivity.class));
        });

        buttonManageGroups.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageGroupActivity");
            startActivity(new Intent(this, ManageGroupActivity.class));
        });

        buttonManageLevels.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageLevelActivity");
            startActivity(new Intent(this, ManageLevelActivity.class));
        });

        buttonManagePrograms.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageProgramsActivity");
            startActivity(new Intent(this, ManageProgramActivity.class));
        });

        buttonManageSubjects.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ManageSubjectActivity");
            startActivity(new Intent(this, ManageSubjectActivity.class));
        });

        buttonLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logging out");
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
            finish();
        });

        // Set up Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                return true; // Stay on dashboard
            } else if (item.getItemId() == R.id.nav_teachers) {
                Log.d(TAG, "Navigating to ManageTeachersActivity from bottom nav");
                startActivity(new Intent(this, ManageTeachersActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_subjects) {
                Log.d(TAG, "Navigating to ManageSubjectActivity from bottom nav");
                startActivity(new Intent(this, ManageSubjectActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_students) {
                Log.d(TAG, "Navigating to ManageStudentsActivity from bottom nav");
                startActivity(new Intent(this, ManageStudentsActivity.class));
                return true;
            }
            return false;
        });

        // Fetch data when activity starts
        fetchTotals();
    }

    private void fetchTotals() {
        fetchTeacherCount();
        fetchStudentCount();
        fetchGroupCount();
        fetchLevelCount();
        fetchProgramCount();
        fetchSubjectCount();
    }

    private void fetchTeacherCount() {
        Call<List<Teacher>> call = apiService.getTeachers();
        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    textTotalTeachers.setText("Total Teachers: " + count);
                    Log.d(TAG, "Fetched teacher count: " + count);
                } else {
                    Log.e(TAG, "Failed to fetch teachers: " + response.code());
                    textTotalTeachers.setText("Total Teachers: Error");
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Log.e(TAG, "Network error fetching teachers: " + t.getMessage(), t);
                textTotalTeachers.setText("Total Teachers: Network Error");
            }
        });
    }

    private void fetchStudentCount() {
        Call<List<Student>> call = apiService.getStudents();
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
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
    }

    private void fetchGroupCount() {
        Call<List<Group>> call = apiService.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    textTotalGroups.setText("Total Groups: " + count);
                    Log.d(TAG, "Fetched group count: " + count);
                } else {
                    Log.e(TAG, "Failed to fetch groups: " + response.code());
                    textTotalGroups.setText("Total Groups: Error");
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e(TAG, "Network error fetching groups: " + t.getMessage(), t);
                textTotalGroups.setText("Total Groups: Network Error");
            }
        });
    }

    private void fetchLevelCount() {
        Call<List<Level>> call = apiService.getLevels();
        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    textTotalLevels.setText("Total Levels: " + count);
                    Log.d(TAG, "Fetched level count: " + count);
                } else {
                    Log.e(TAG, "Failed to fetch levels: " + response.code());
                    textTotalLevels.setText("Total Levels: Error");
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Log.e(TAG, "Network error fetching levels: " + t.getMessage(), t);
                textTotalLevels.setText("Total Levels: Network Error");
            }
        });
    }

    private void fetchProgramCount() {
        Call<List<Program>> call = apiService.getPrograms();
        call.enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
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

    private void fetchSubjectCount() {
        Call<List<Subject>> call = apiService.getSubjects(); // Updated to match ApiService
        call.enqueue(new Callback<List<Subject>>() {
            @Override
            public void onResponse(Call<List<Subject>> call, Response<List<Subject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    textTotalSubjects.setText("Total Subjects: " + count);
                    Log.d(TAG, "Fetched subject count: " + count);
                } else {
                    Log.e(TAG, "Failed to fetch subjects: " + response.code());
                    textTotalSubjects.setText("Total Subjects: Error");
                }
            }

            @Override
            public void onFailure(Call<List<Subject>> call, Throwable t) {
                Log.e(TAG, "Network error fetching subjects: " + t.getMessage(), t);
                textTotalSubjects.setText("Total Subjects: Network Error");
            }
        });
    }
}