package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLevelActivity extends AppCompatActivity {
    private static final String TAG = "EditLevelActivity";
    private EditText editTextName;
    private Button buttonSave, buttonCancel;
    private Long levelId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_level);

        editTextName = findViewById(R.id.editTextName);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        levelId = getIntent().getLongExtra("levelId", -1);
        if (levelId == -1) {
            Toast.makeText(this, "Invalid level ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchLevel();

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter level name", Toast.LENGTH_SHORT).show();
                return;
            }

            Level level = new Level(name);
            Log.d(TAG, "Level payload: name=" + level.getName());

            Call<Level> call = apiService.updateLevel(levelId, level);
            Log.d(TAG, "Sending update level request to: " + call.request().url());
            call.enqueue(new Callback<Level>() {
                @Override
                public void onResponse(Call<Level> call, Response<Level> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Level updated: " + response.body().getName());
                        Toast.makeText(EditLevelActivity.this, "Level updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error: " + error);
                            Toast.makeText(EditLevelActivity.this, "Failed to update level: " + error, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Level> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    Toast.makeText(EditLevelActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void fetchLevel() {
        Call<Level> call = apiService.getLevel(levelId);
        call.enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Level level = response.body();
                    editTextName.setText(level.getName());
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch level: " + error);
                        Toast.makeText(EditLevelActivity.this, "Failed to fetch level: " + error, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(EditLevelActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}