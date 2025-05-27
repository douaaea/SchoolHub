package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Group;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.model.Student;
import com.example.schoolapp.model.StudentDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {
    private static final String TAG = "AddStudentActivity";
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Spinner spinnerGroup, spinnerLevel;
    private Button buttonAddStudent, buttonReturn;
    private ApiService apiService;
    private List<Group> groups = new ArrayList<>();
    private List<Level> levels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);
        buttonReturn = findViewById(R.id.buttonReturn);

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchGroups();
        fetchLevels();

        buttonAddStudent.setOnClickListener(v -> saveStudent());
        buttonReturn.setOnClickListener(v -> {
            Log.d(TAG, "Return button clicked");
            finish();
        });
    }

    private void fetchGroups() {
        Call<List<Group>> call = apiService.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groups.clear();
                    groups.addAll(response.body());
                    List<String> groupNames = new ArrayList<>();
                    for (Group group : groups) {
                        groupNames.add(group.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStudentActivity.this,
                            android.R.layout.simple_spinner_item, groupNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGroup.setAdapter(adapter);
                } else {
                    Toast.makeText(AddStudentActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Network error loading groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLevels() {
        Call<List<Level>> call = apiService.getLevels();
        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    levels.clear();
                    levels.addAll(response.body());
                    List<String> levelNames = new ArrayList<>();
                    for (Level level : levels) {
                        levelNames.add(level.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStudentActivity.this,
                            android.R.layout.simple_spinner_item, levelNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLevel.setAdapter(adapter);
                } else {
                    Toast.makeText(AddStudentActivity.this, "Failed to load levels", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Network error loading levels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveStudent() {
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
        if (spinnerGroup.getSelectedItem() == null || spinnerLevel.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a group and level", Toast.LENGTH_SHORT).show();
            return;
        }

        int groupPosition = spinnerGroup.getSelectedItemPosition();
        int levelPosition = spinnerLevel.getSelectedItemPosition();
        Long groupId = groups.get(groupPosition).getId();
        Long levelId = levels.get(levelPosition).getId();

        // Create a StudentDTO instead of a Student
        StudentDTO studentDTO = new StudentDTO(email, password, firstname, lastname, groupId, levelId);

        Log.d(TAG, "Student payload: email=" + studentDTO.email + ", password=" + studentDTO.password +
                ", firstname=" + studentDTO.firstname + ", lastname=" + studentDTO.lastname +
                ", groupId=" + studentDTO.groupId + ", levelId=" + studentDTO.levelId);

        Call<Student> call = apiService.addStudent(studentDTO); // Pass StudentDTO instead of Student
        Log.d(TAG, "Sending add student request to: " + call.request().url());

        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Student added: " + response.body());
                    Toast.makeText(AddStudentActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to add student: " + response.code() + " - " + response.message());
                    Toast.makeText(AddStudentActivity.this, "Failed to add student: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage() + ", URL: " + call.request().url(), t);
                Toast.makeText(AddStudentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}