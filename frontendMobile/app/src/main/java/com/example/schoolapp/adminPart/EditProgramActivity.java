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

public class EditProgramActivity extends AppCompatActivity {
    private static final String TAG = "EditProgramActivity";
    private Spinner spinnerTeacher, spinnerGroup, spinnerSubject;
    private Button buttonSave, buttonCancel;
    private Long programId;
    private List<Teacher> teachers;
    private List<Group> groups;
    private List<Subject> subjects;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_program);

        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        programId = getIntent().getLongExtra("programId", -1);
        if (programId == -1) {
            Toast.makeText(this, "Invalid program ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        teachers = new ArrayList<>();
        groups = new ArrayList<>();
        subjects = new ArrayList<>();
        apiService = ApiClient.getClient().create(ApiService.class);

        fetchTeachers();
        fetchGroups();
        fetchSubjects();
        fetchProgram();

        buttonSave.setOnClickListener(v -> {
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

            Call<Program> call = apiService.updateProgram(programId, program);
            Log.d(TAG, "Sending update program request to: " + call.request().url());
            call.enqueue(new Callback<Program>() {
                @Override
                public void onResponse(Call<Program> call, Response<Program> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Program updated: " + response.body().toString());
                        Toast.makeText(EditProgramActivity.this, "Program updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error: " + error);
                            Toast.makeText(EditProgramActivity.this, "Failed to update program: " + error, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Program> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    Toast.makeText(EditProgramActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonCancel.setOnClickListener(v -> finish());
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
                    ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(EditProgramActivity.this,
                            android.R.layout.simple_spinner_item, teacherNames);
                    teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTeacher.setAdapter(teacherAdapter);
                    // Re-run fetchProgram to set spinner selection after teachers are loaded
                    fetchProgram();
                } else {
                    Toast.makeText(EditProgramActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Toast.makeText(EditProgramActivity.this, "Network error loading teachers", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(EditProgramActivity.this,
                            android.R.layout.simple_spinner_item, groupNames);
                    groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGroup.setAdapter(groupAdapter);
                    // Re-run fetchProgram to set spinner selection after groups are loaded
                    fetchProgram();
                } else {
                    Toast.makeText(EditProgramActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(EditProgramActivity.this, "Network error loading groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubjects() {
        Call<List<Subject>> call = apiService.getSubjects(); // Updated to match ApiService
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
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(EditProgramActivity.this,
                            android.R.layout.simple_spinner_item, subjectNames);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(subjectAdapter);
                    // Re-run fetchProgram to set spinner selection after subjects are loaded
                    fetchProgram();
                } else {
                    Toast.makeText(EditProgramActivity.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Subject>> call, Throwable t) {
                Toast.makeText(EditProgramActivity.this, "Network error loading subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProgram() {
        // Ensure all lists are loaded before setting spinner selections
        if (teachers.isEmpty() || groups.isEmpty() || subjects.isEmpty()) {
            return;
        }

        Call<Program> call = apiService.getProgram(programId);
        call.enqueue(new Callback<Program>() {
            @Override
            public void onResponse(Call<Program> call, Response<Program> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Program program = response.body();
                    if (program.getTeacher() != null) {
                        for (int i = 0; i < teachers.size(); i++) {
                            if (teachers.get(i).getId().equals(program.getTeacher().getId())) {
                                spinnerTeacher.setSelection(i);
                                break;
                            }
                        }
                    }
                    if (program.getGroup() != null) {
                        for (int i = 0; i < groups.size(); i++) {
                            if (groups.get(i).getId().equals(program.getGroup().getId())) {
                                spinnerGroup.setSelection(i);
                                break;
                            }
                        }
                    }
                    if (program.getSubject() != null) {
                        for (int i = 0; i < subjects.size(); i++) {
                            if (subjects.get(i).getId().equals(program.getSubject().getId())) {
                                spinnerSubject.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch program: " + error);
                        Toast.makeText(EditProgramActivity.this, "Failed to fetch program: " + error, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Program> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(EditProgramActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}