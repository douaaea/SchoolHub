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
import com.example.schoolapp.model.SubjectInputDTO;
import com.example.schoolapp.model.Subject;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddSubjectActivity extends AppCompatActivity {
    private static final String TAG = "AddSubjectActivity";
    private EditText editTextSubjectName;
    private Spinner spinnerLevel;
    private Button buttonSave, buttonCancel;
    private ApiService apiService;
    private List<Level> levelList;
    private ArrayAdapter<String> levelAdapter;
    private List<String> levelNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        apiService = ApiClient.getClient().create(ApiService.class);

        levelList = new ArrayList<>();
        levelNames = new ArrayList<>();
        levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelNames);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);

        buttonSave.setOnClickListener(v -> {
            Log.d(TAG, "Save button clicked");
            saveSubject();
        });
        buttonCancel.setOnClickListener(v -> finish());

        fetchLevels();
    }

    private void fetchLevels() {
        Call<List<Level>> call = apiService.getLevels();
        Log.d(TAG, "Fetching levels from: " + call.request().url());

        new Thread(() -> {
            try {
                Response<List<Level>> response = call.execute();
                runOnUiThread(() -> {
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
                        Log.e(TAG, "Failed to fetch levels: " + response.code() + " - " + response.message());
                        Toast.makeText(AddSubjectActivity.this, "Failed to fetch levels: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Network error fetching levels: " + e.getMessage(), e);
                    Toast.makeText(AddSubjectActivity.this, "Network error fetching levels: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveSubject() {
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
        Log.d(TAG, "Sending subject: name=" + name + ", levelId=" + levelId);

        new Thread(() -> {
            try {
                Call<Subject> call = apiService.addSubject(input);
                Log.d(TAG, "Request URL: " + call.request().url());
                Log.d(TAG, "Request Body: " + new Gson().toJson(input));
                Response<Subject> response = call.execute();

                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Subject subject = response.body();
                        Log.d(TAG, "Subject added: id=" + subject.getId() + ", name=" + subject.getName() + ", levelId=" + subject.getLevelId());
                        Toast.makeText(AddSubjectActivity.this, "Subject added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMessage = response.message();
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage(), e);
                        }
                        Log.e(TAG, "Failed to add subject: " + response.code() + " - " + errorMessage);
                        Toast.makeText(AddSubjectActivity.this, "Failed to add subject: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Network error adding subject: " + e.getMessage(), e);
                    Toast.makeText(AddSubjectActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}