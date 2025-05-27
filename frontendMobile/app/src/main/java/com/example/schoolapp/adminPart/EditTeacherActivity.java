package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

public class EditTeacherActivity extends AppCompatActivity {
    private static final String TAG = "EditTeacherActivity";
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Button buttonSave, buttonCancel;
    private Long teacherId;
    private String originalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        teacherId = getIntent().getLongExtra("teacherId", -1);
        if (teacherId == -1) {
            Toast.makeText(this, "Invalid teacher ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchTeacher();

        buttonSave.setOnClickListener(v -> saveTeacher());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void fetchTeacher() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Teacher> call = apiService.getTeacher(teacherId);
        Log.d(TAG, "Fetching teacher from: " + call.request().url());
        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Teacher teacher = response.body();
                    Log.d(TAG, "Teacher fetched: " + teacher);
                    originalEmail = teacher.getEmail();
                    editTextFirstName.setText(teacher.getFirstname());
                    editTextLastName.setText(teacher.getLastname());
                    editTextEmail.setText(teacher.getEmail());
                    editTextPassword.setText(teacher.getPassword());
                } else {
                    Log.e(TAG, "Failed to fetch teacher: " + response.code() + " - " + response.message());
                    Toast.makeText(EditTeacherActivity.this, "Failed to fetch teacher", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                Log.e(TAG, "Network error fetching teacher: " + t.getMessage(), t);
                Toast.makeText(EditTeacherActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveTeacher() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFirstname(firstName);
        teacher.setLastname(lastName);
        teacher.setEmail(email);
        teacher.setPassword(password);

        Log.d(TAG, "Teacher payload: email=" + teacher.getEmail() + ", password=" + teacher.getPassword() +
                ", firstname=" + teacher.getFirstname() + ", lastname=" + teacher.getLastname());

        updateTeacher(teacher);
    }

    private void updateTeacher(Teacher teacher) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Teacher> call = apiService.updateTeacher(teacherId, teacher);
        Log.d(TAG, "Sending update teacher request to: " + call.request().url());
        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Teacher updated: " + response.body());
                    Toast.makeText(EditTeacherActivity.this, "Teacher updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to update teacher: " + response.code() + " - " + response.message());
                    if (response.code() == 409) {
                        Toast.makeText(EditTeacherActivity.this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditTeacherActivity.this, "Failed to update teacher: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                Log.e(TAG, "Network error updating teacher: " + t.getMessage(), t);
                Toast.makeText(EditTeacherActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}