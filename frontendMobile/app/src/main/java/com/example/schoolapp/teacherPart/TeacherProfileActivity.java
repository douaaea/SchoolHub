package com.example.schoolapp.teacherPart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Teacher;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherProfileActivity extends AppCompatActivity {
    private static final String TAG = "TeacherProfileActivity";
    private ApiService apiService;
    private EditText editTextEmail, editTextPassword, editTextFirstname, editTextLastname;
    private Long teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFirstname = findViewById(R.id.editTextFirstname);
        editTextLastname = findViewById(R.id.editTextLastname);
        Button buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);

        // Get teacher ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        teacherId = prefs.getLong("teacher_id", -1);
        if (teacherId == -1) {
            Log.e(TAG, "Teacher ID not found in SharedPreferences");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch and display current profile
        fetchProfile();
        buttonUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void fetchProfile() {
        Call<Teacher> call = apiService.getTeacher(teacherId);
        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Teacher teacher = response.body();
                    editTextEmail.setText(teacher.getEmail());
                    editTextPassword.setText(teacher.getPassword());
                    editTextFirstname.setText(teacher.getFirstname());
                    editTextLastname.setText(teacher.getLastname());
                    Log.d(TAG, "Fetched profile for ID: " + teacher.getId());
                } else {
                    Toast.makeText(TeacherProfileActivity.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                Toast.makeText(TeacherProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstname = editTextFirstname.getText().toString().trim();
        String lastname = editTextLastname.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(teacherId);
        updatedTeacher.setEmail(email);
        updatedTeacher.setPassword(password);
        updatedTeacher.setFirstname(firstname);
        updatedTeacher.setLastname(lastname);

        Call<Teacher> call = apiService.updateTeacher(teacherId, updatedTeacher);
        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeacherProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TeacherProfileActivity.this, "Failed to update profile: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                Toast.makeText(TeacherProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}