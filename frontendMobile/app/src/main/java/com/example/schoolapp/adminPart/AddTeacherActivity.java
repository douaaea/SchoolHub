package com.example.schoolapp.adminPart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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

public class AddTeacherActivity extends AppCompatActivity {
    private static final String TAG = "AddTeacherActivity";
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Button buttonAddTeacher, buttonReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        buttonReturn = findViewById(R.id.buttonReturn);

        buttonAddTeacher.setOnClickListener(v -> saveTeacher());
        buttonReturn.setOnClickListener(v -> {
            Log.d(TAG, "Return button clicked");
            finish();
        });
    }

    private void saveTeacher() {
        String firstname = editTextFirstName.getText().toString().trim();
        String lastname = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        Teacher teacher = new Teacher();
        teacher.setFirstname(firstname);
        teacher.setLastname(lastname);
        teacher.setEmail(email);
        teacher.setPassword(password);

        Log.d(TAG, "Teacher payload: email=" + teacher.getEmail() + ", password=" + teacher.getPassword() +
                ", firstname=" + teacher.getFirstname() + ", lastname=" + teacher.getLastname());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Teacher> call = apiService.addTeacher(teacher);
        Log.d(TAG, "Sending add teacher request to: " + call.request().url());

        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Teacher added: " + response.body());
                    Toast.makeText(AddTeacherActivity.this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to add teacher: " + response.code() + " - " + response.message());
                    Toast.makeText(AddTeacherActivity.this, "Failed to add teacher: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage() + ", URL: " + call.request().url(), t);
                Toast.makeText(AddTeacherActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}