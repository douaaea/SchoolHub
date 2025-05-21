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

public class EditStudentActivity extends AppCompatActivity {
    private static final String TAG = "EditStudentActivity";
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Spinner spinnerGroup, spinnerLevel;
    private Button buttonSave, buttonCancel;
    private Long studentId;
    private List<Group> groups = new ArrayList<>();
    private List<Level> levels = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        studentId = getIntent().getLongExtra("studentId", -1);
        if (studentId == -1) {
            Toast.makeText(this, "Invalid student ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchGroups();
        fetchLevels();
        fetchStudent();

        buttonSave.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            int groupPosition = spinnerGroup.getSelectedItemPosition();
            int levelPosition = spinnerLevel.getSelectedItemPosition();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (groupPosition == -1 || levelPosition == -1) {
                Toast.makeText(this, "Please select group and level", Toast.LENGTH_SHORT).show();
                return;
            }

            Long groupId = groups.get(groupPosition).getId();
            Long levelId = levels.get(levelPosition).getId();

            StudentDTO studentDTO = new StudentDTO(email, password, firstName, lastName, groupId, levelId);
            updateStudent(studentDTO);
        });

        buttonCancel.setOnClickListener(v -> finish());
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditStudentActivity.this,
                            android.R.layout.simple_spinner_item, groupNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGroup.setAdapter(adapter);
                } else {
                    Toast.makeText(EditStudentActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(EditStudentActivity.this, "Network error loading groups", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditStudentActivity.this,
                            android.R.layout.simple_spinner_item, levelNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLevel.setAdapter(adapter);
                } else {
                    Toast.makeText(EditStudentActivity.this, "Failed to load levels", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Toast.makeText(EditStudentActivity.this, "Network error loading levels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudent() {
        Call<Student> call = apiService.getStudent(studentId);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Student student = response.body();
                    editTextFirstName.setText(student.getFirstname());
                    editTextLastName.setText(student.getLastname());
                    editTextEmail.setText(student.getEmail());
                    editTextPassword.setText(student.getPassword());
                    if (student.getGroup() != null) {
                        for (int i = 0; i < groups.size(); i++) {
                            if (groups.get(i).getId().equals(student.getGroup().getId())) {
                                spinnerGroup.setSelection(i);
                                break;
                            }
                        }
                    }
                    if (student.getLevel() != null) {
                        for (int i = 0; i < levels.size(); i++) {
                            if (levels.get(i).getId().equals(student.getLevel().getId())) {
                                spinnerLevel.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(EditStudentActivity.this, "Failed to fetch student", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(EditStudentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateStudent(StudentDTO studentDTO) {
        Call<Student> call = apiService.updateStudent(studentId, studentDTO);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditStudentActivity.this, "Student updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to update student: " + response.code() + " - " + response.message());
                    Toast.makeText(EditStudentActivity.this, "Failed to update student: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(EditStudentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}