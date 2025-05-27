package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Group;
import com.example.schoolapp.model.Program;
import com.example.schoolapp.model.Subject;
import com.example.schoolapp.model.Teacher;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class AddProgramActivity extends AppCompatActivity {
    private static final String TAG = "AddProgramActivity";
    private Spinner spinnerTeacher, spinnerGroup, spinnerSubject;
    private Button buttonAddProgram, buttonReturn;
    private List<Teacher> teachers;
    private List<Group> groups;
    private List<Subject> subjects;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_program);

        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        buttonAddProgram = findViewById(R.id.buttonAddProgram);
        buttonReturn = findViewById(R.id.buttonReturn);

        teachers = new ArrayList<>();
        groups = new ArrayList<>();
        subjects = new ArrayList<>();
        apiService = ApiClient.getClient().create(ApiService.class);

        fetchTeachers();
        fetchGroups();
        fetchSubjects();

        buttonAddProgram.setOnClickListener(v -> {
            int teacherPosition = spinnerTeacher.getSelectedItemPosition();
            int groupPosition = spinnerGroup.getSelectedItemPosition();
            int subjectPosition = spinnerSubject.getSelectedItemPosition();

            if (teacherPosition == -1) {
                Toast.makeText(this, "Please select a teacher", Toast.LENGTH_SHORT).show();
                return;
            }
            if (groupPosition == -1) {
                Toast.makeText(this, "Please select a group", Toast.LENGTH_SHORT).show();
                return;
            }
            if (subjectPosition == -1) {
                Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher selectedTeacher = teachers.get(teacherPosition);
            Group selectedGroup = groups.get(groupPosition);
            Subject selectedSubject = subjects.get(subjectPosition);

            Program program = new Program(selectedTeacher, selectedGroup, selectedSubject);
            Log.d(TAG, "Program payload: " + program.toString());

            Call<Program> call = apiService.addProgram(program);
            Log.d(TAG, "Sending add program request to: " + call.request().url());
            call.enqueue(new Callback<Program>() {
                @Override
                public void onResponse(Call<Program> call, Response<Program> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Program added: " + response.body().toString());
                        Toast.makeText(AddProgramActivity.this, "Program added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error: " + error);
                            Toast.makeText(AddProgramActivity.this, "Failed to add program: " + error, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Program> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    Toast.makeText(AddProgramActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonReturn.setOnClickListener(v -> finish());
    }

    private void fetchTeachers() {
        Call<List<Teacher>> call = apiService.getTeachers();
        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teachers.clear();
                    teachers.addAll(response.body());
                    List<String> teacherNames = new ArrayList<>();
                    for (Teacher teacher : teachers) {
                        teacherNames.add(teacher.getFirstname() + " " + teacher.getLastname());
                    }
                    ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(AddProgramActivity.this,
                            android.R.layout.simple_spinner_item, teacherNames);
                    teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTeacher.setAdapter(teacherAdapter);
                } else {
                    Toast.makeText(AddProgramActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Toast.makeText(AddProgramActivity.this, "Network error loading teachers", Toast.LENGTH_SHORT).show();
            }
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
                    ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(AddProgramActivity.this,
                            android.R.layout.simple_spinner_item, groupNames);
                    groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGroup.setAdapter(groupAdapter);
                } else {
                    Toast.makeText(AddProgramActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(AddProgramActivity.this, "Network error loading groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubjects() {
        Call<List<Subject>> call = apiService.getSubjects();
        call.enqueue(new Callback<List<Subject>>() {
            @Override
            public void onResponse(Call<List<Subject>> call, Response<List<Subject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subjects.clear();
                    subjects.addAll(response.body());
                    List<String> subjectNames = new ArrayList<>();
                    for (Subject subject : subjects) {
                        subjectNames.add(subject.getName());
                    }
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(AddProgramActivity.this,
                            android.R.layout.simple_spinner_item, subjectNames);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(subjectAdapter);
                } else {
                    Toast.makeText(AddProgramActivity.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Subject>> call, Throwable t) {
                Toast.makeText(AddProgramActivity.this, "Network error loading subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }
}