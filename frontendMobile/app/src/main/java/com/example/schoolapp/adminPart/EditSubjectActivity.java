package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.model.Subject;
import com.example.schoolapp.model.SubjectInputDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class EditSubjectActivity extends AppCompatActivity {
    private static final String TAG = "EditSubjectActivity";
    private EditText editTextSubjectName;
    private Spinner spinnerLevel;
    private Button buttonUpdate, buttonCancel;
    private ApiService apiService;
    private Long subjectId;
    private List<Level> levelList;
    private ArrayAdapter<String> levelAdapter;
    private List<String> levelNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonCancel = findViewById(R.id.buttonCancel);

        apiService = ApiClient.getClient().create(ApiService.class);
        subjectId = getIntent().getLongExtra("subjectId", -1);

        if (subjectId == -1) {
            Toast.makeText(this, "Invalid subject ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        levelList = new ArrayList<>();
        levelNames = new ArrayList<>();
        levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelNames);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);

        fetchLevels();
        fetchSubject();

        buttonUpdate.setOnClickListener(v -> updateSubject());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void fetchLevels() {
        Call<List<Level>> call = apiService.getLevels();
        Log.d(TAG, "Fetching levels from: " + call.request().url());

        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    levelList.clear();
                    levelNames.clear();

                    levelList.addAll(response.body());
                    for (Level level : levelList) {
                        levelNames.add(level.getName());
                        Log.d(TAG, "Level: id=" + level.getId() + ", name=" + level.getName());
                    }

                    levelAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Levels fetched: " + levelList.size());
                } else {
                    Log.e(TAG, "Failed to fetch levels: " + response.code());
                    Toast.makeText(EditSubjectActivity.this, "Failed to fetch levels: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Log.e(TAG, "Network error fetching levels: " + t.getMessage());
                Toast.makeText(EditSubjectActivity.this, "Network error fetching levels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubject() {
        Call<Subject> call = apiService.getSubject(subjectId);
        call.enqueue(new Callback<Subject>() {
            @Override
            public void onResponse(Call<Subject> call, Response<Subject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Subject subject = response.body();
                    editTextSubjectName.setText(subject.getName());
                    // Pre-select the current level in the Spinner
                    if (subject.getLevelId() != null) {
                        for (int i = 0; i < levelList.size(); i++) {
                            if (levelList.get(i).getId().equals(subject.getLevelId())) {
                                spinnerLevel.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(EditSubjectActivity.this, "Failed to load subject: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Subject> call, Throwable t) {
                Toast.makeText(EditSubjectActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSubject() {
        String name = editTextSubjectName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedLevelPosition = spinnerLevel.getSelectedItemPosition();
        if (selectedLevelPosition == -1 || levelList.isEmpty()) {
            Toast.makeText(this, "Please select a level", Toast.LENGTH_SHORT).show();
            return;
        }

        Long levelId = levelList.get(selectedLevelPosition).getId();
        SubjectInputDTO input = new SubjectInputDTO(name, levelId);
        Log.d(TAG, "Updating subject: id=" + subjectId + ", name=" + name + ", levelId=" + levelId);

        Call<Subject> call = apiService.updateSubject(subjectId, input);
        call.enqueue(new Callback<Subject>() {
            @Override
            public void onResponse(Call<Subject> call, Response<Subject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditSubjectActivity.this, "Subject updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = response.message();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to update subject: " + response.code() + " - " + errorMessage);
                    Toast.makeText(EditSubjectActivity.this, "Failed to update subject: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Subject> call, Throwable t) {
                Log.e(TAG, "Network error updating subject: " + t.getMessage());
                Toast.makeText(EditSubjectActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}