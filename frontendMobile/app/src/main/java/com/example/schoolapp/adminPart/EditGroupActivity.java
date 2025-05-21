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
import com.example.schoolapp.model.Group;
import com.example.schoolapp.model.GroupInputDTO;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class EditGroupActivity extends AppCompatActivity {
    private static final String TAG = "EditGroupActivity";
    private EditText editTextName;
    private Spinner spinnerLevel;
    private Button buttonSave, buttonCancel;
    private Long groupId;
    private List<Level> levels = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        editTextName = findViewById(R.id.editTextName);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        groupId = getIntent().getLongExtra("groupId", -1);
        if (groupId == -1) {
            Toast.makeText(this, "Invalid group ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchLevels();
        fetchGroup();

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            int levelPosition = spinnerLevel.getSelectedItemPosition();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (levelPosition == -1) {
                Toast.makeText(this, "Please select a level", Toast.LENGTH_SHORT).show();
                return;
            }

            Long levelId = levels.get(levelPosition).getId();
            GroupInputDTO groupInputDTO = new GroupInputDTO(name, levelId);
            Log.d(TAG, "Group payload: name=" + groupInputDTO.name + ", levelId=" + groupInputDTO.levelId);

            Call<Group> call = apiService.updateGroup(groupId, groupInputDTO);
            Log.d(TAG, "Sending update group request to: " + call.request().url());
            call.enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Group updated: " + response.body().getName());
                        Toast.makeText(EditGroupActivity.this, "Group updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error: " + error);
                            Toast.makeText(EditGroupActivity.this, "Failed to update group: " + error, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    Toast.makeText(EditGroupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonCancel.setOnClickListener(v -> finish());
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
                    ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(EditGroupActivity.this,
                            android.R.layout.simple_spinner_item, levelNames);
                    levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLevel.setAdapter(levelAdapter);
                } else {
                    Toast.makeText(EditGroupActivity.this, "Failed to load levels", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Toast.makeText(EditGroupActivity.this, "Network error loading levels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGroup() {
        Call<Group> call = apiService.getGroup(groupId);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Group group = response.body();
                    editTextName.setText(group.getName());
                    if (group.getLevel() != null) {
                        for (int i = 0; i < levels.size(); i++) {
                            if (levels.get(i).getId().equals(group.getLevel().getId())) {
                                spinnerLevel.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(EditGroupActivity.this, "Failed to fetch group", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(EditGroupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}