package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.model.LoginRequest;
import com.example.schoolapp.model.LoginResponse;
import com.example.schoolapp.model.Student;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import com.example.schoolapp.studentPart.StudentDashboardActivity;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailField, passwordField;
    private Button loginButton;
    private ProgressBar progressBar;
    private Intent signupIntent, adminIntent, studentIntent, teacherIntent;

    // Enum for user roles
    private enum UserRole {
        ADMIN("admin"), STUDENT("student"), TEACHER("teacher"), UNKNOWN("unknown");

        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        public static UserRole fromString(String role) {
            if (role == null) return UNKNOWN;
            for (UserRole userRole : values()) {
                if (userRole.value.equalsIgnoreCase(role)) {
                    return userRole;
                }
            }
            return UNKNOWN;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        signupIntent = new Intent(this, SignUpActivity.class);
        try {
            adminIntent = new Intent(this, com.example.schoolapp.adminPart.DashboardActivityMain.class);
            studentIntent = new Intent(this, StudentDashboardActivity.class);
            teacherIntent = new Intent(this, com.example.schoolapp.teacherPart.TeacherDashboardActivity.class);
            Log.d(TAG, "Intents initialized: admin=" + com.example.schoolapp.adminPart.DashboardActivityMain.class.getName() +
                    ", student=" + StudentDashboardActivity.class.getName() +
                    ", teacher=" + com.example.schoolapp.teacherPart.TeacherDashboardActivity.class.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing intents: " + e.getMessage(), e);
            Toast.makeText(this, "Setup error: Dashboard activities missing", Toast.LENGTH_LONG).show();
        }

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(email, password);
        Log.d(TAG, "Sending login request: email=" + email);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                Log.d(TAG, "Response code: " + response.code());
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Gson gson = new Gson();
                        String rawJson = gson.toJson(response.body());
                        Log.d(TAG, "Raw JSON response: " + rawJson);
                        Log.d(TAG, "LoginResponse: " + loginResponse.toString());
                        handleSuccessfulLogin(loginResponse, email);
                    } else {
                        String errorMessage = "Invalid credentials";
                        if (response.code() == 401) {
                            errorMessage = "Unauthorized: Incorrect email or password";
                        } else if (response.code() == 400) {
                            errorMessage = "Bad request: Invalid input format";
                        } else if (response.code() == 404) {
                            errorMessage = "Endpoint not found: Check server URL";
                        } else if (response.code() >= 500) {
                            errorMessage = "Server error: Please try again later";
                        }
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMessage += " (" + errorBody + ")";
                        }
                        Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Response handling error: " + e.getMessage(), e);
                    Toast.makeText(LoginActivity.this, "Response error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network error: Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessfulLogin(LoginResponse loginResponse, String email) {
        // Validate critical fields
        if (loginResponse.getId() == null || loginResponse.getRole() == null) {
            Log.e(TAG, "Login response missing id or role: " + loginResponse.toString());
            Toast.makeText(this, "Login error: Missing user ID or role", Toast.LENGTH_LONG).show();
            return;
        }

        // Save user data to SharedPreferences
        Long userId = loginResponse.getId();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("user_id", userId);
        editor.putString("email", loginResponse.getEmail());
        editor.putString("role", loginResponse.getRole());

        // Save specific role-based IDs
        String roleStr = loginResponse.getRole();
        UserRole role = UserRole.fromString(roleStr);
        switch (role) {
            case STUDENT:
                editor.putLong("student_id", userId);
                Log.d(TAG, "Saved student_id: " + userId);
                break;
            case TEACHER:
                editor.putLong("teacher_id", userId);
                Log.d(TAG, "Saved teacher_id: " + userId);
                break;
            case ADMIN:
                editor.putLong("admin_id", userId);
                Log.d(TAG, "Saved admin_id: " + userId);
                break;
            default:
                Log.e(TAG, "Unknown role received: " + roleStr);
                Toast.makeText(this, "Unknown role: " + roleStr, Toast.LENGTH_LONG).show();
                return;
        }

        // Fetch student details if role is STUDENT to get group_id
        if (role == UserRole.STUDENT) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Student> studentCall = apiService.getStudentByEmail(email);
            studentCall.enqueue(new Callback<Student>() {
                @Override
                public void onResponse(Call<Student> call, Response<Student> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Student student = response.body();
                        Long groupId = (student.getGroup() != null && student.getGroup().getId() != null) ? student.getGroup().getId() : -1L;
                        editor.putLong("group_id", groupId);
                        editor.putString("email", student.getEmail() != null ? student.getEmail() : email);
                        boolean committed = editor.commit();
                        if (!committed) {
                            Log.e(TAG, "Failed to commit SharedPreferences");
                            Toast.makeText(LoginActivity.this, "Failed to save login data", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.d(TAG, "Student details fetched: student_id=" + userId + ", group_id=" + groupId + ", email=" + student.getEmail());
                        navigateToDashboard(role);
                    } else {
                        Log.e(TAG, "Failed to fetch student details: " + response.code());
                        try {
                            Log.e(TAG, "Error body: " + (response.errorBody() != null ? response.errorBody().string() : "No error body"));
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                        Toast.makeText(LoginActivity.this, "Failed to fetch student details: " + response.code(), Toast.LENGTH_LONG).show();
                        // Still commit what we have and proceed
                        editor.commit();
                        navigateToDashboard(role);
                    }
                }

                @Override
                public void onFailure(Call<Student> call, Throwable t) {
                    Log.e(TAG, "Network error fetching student details: " + t.getMessage(), t);
                    Toast.makeText(LoginActivity.this, "Network error fetching student details", Toast.LENGTH_LONG).show();
                    // Still commit what we have and proceed
                    editor.commit();
                    navigateToDashboard(role);
                }
            });
        } else {
            // For non-student roles, commit and navigate
            boolean committed = editor.commit();
            if (!committed) {
                Log.e(TAG, "Failed to commit SharedPreferences");
                Toast.makeText(this, "Failed to save login data", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "SharedPreferences committed successfully: user_id=" + userId + ", role=" + roleStr);
            navigateToDashboard(role);
        }
    }

    private void navigateToDashboard(UserRole role) {
        // Verify the saved data
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        Long savedStudentId = prefs.getLong("student_id", -1);
        Long savedGroupId = prefs.getLong("group_id", -1);
        String savedEmail = prefs.getString("email", null);
        Log.d(TAG, "Verified saved data: student_id=" + savedStudentId + ", group_id=" + savedGroupId + ", email=" + savedEmail);

        // Navigate based on role
        Log.d(TAG, "Navigating to role: " + role + " (raw: " + role.toString() + ")");
        try {
            switch (role) {
                case ADMIN:
                    Log.d(TAG, "Starting DashboardActivityMain, intent: " + adminIntent);
                    startActivity(adminIntent);
                    break;
                case STUDENT:
                    Log.d(TAG, "Starting StudentDashboardActivity, intent: " + studentIntent);
                    startActivity(studentIntent);
                    break;
                case TEACHER:
                    Log.d(TAG, "Starting TeacherDashboardActivity, intent: " + teacherIntent);
                    startActivity(teacherIntent);
                    break;
                default:
                    Log.e(TAG, "Unknown role: " + role.toString());
                    Toast.makeText(this, "Unknown role: " + role.toString(), Toast.LENGTH_LONG).show();
                    return;
            }
            Log.d(TAG, "Calling finish()");
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: " + e.getMessage(), e);
            Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onTextClick(View view) {
        Log.d(TAG, "Signup text clicked");
        startActivity(signupIntent);
    }
}