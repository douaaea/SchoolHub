package com.example.schoolapp.adminPart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ManageLevelActivity extends AppCompatActivity {
    private static final String TAG = "ManageLevelActivity";
    private ListView listViewLevels;
    private Button buttonAddLevel, buttonReturn;
    private List<Level> levelList;
    private ArrayAdapter<String> adapter;
    private List<String> levelNames;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_levels);

        listViewLevels = findViewById(R.id.listViewLevels);
        buttonAddLevel = findViewById(R.id.buttonAddLevel);
        buttonReturn = findViewById(R.id.buttonReturn);
        levelList = new ArrayList<>();
        levelNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, levelNames);
        listViewLevels.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonAddLevel.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLevelActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> finish());

        listViewLevels.setOnItemClickListener((parent, view, position, id) -> {
            Level level = levelList.get(position);
            Intent intent = new Intent(this, EditLevelActivity.class);
            intent.putExtra("levelId", level.getId());
            startActivity(intent);
        });

        listViewLevels.setOnItemLongClickListener((parent, view, position, id) -> {
            Level level = levelList.get(position);
            deleteLevel(level.getId());
            return true;
        });

        fetchLevels();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLevels();
    }

    private void fetchLevels() {
        Log.d(TAG, "Fetching levels...");
        Call<List<Level>> call = apiService.getLevels();
        Log.d(TAG, "Request URL: " + call.request().url());
        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Levels fetched successfully, count: " + response.body().size());
                    levelList.clear();
                    levelList.addAll(response.body());
                    levelNames.clear();
                    if (levelList.isEmpty()) {
                        Log.d(TAG, "No levels found in the response.");
                        levelNames.add("No levels available");
                    } else {
                        for (Level level : levelList) {
                            levelNames.add(level.getName());
                            Log.d(TAG, "Level added to list: " + level.getName());
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch levels: " + response.code() + " - " + error);
                        Toast.makeText(ManageLevelActivity.this, "Failed to fetch levels: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(ManageLevelActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ManageLevelActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteLevel(Long id) {
        Log.d(TAG, "Deleting level with ID: " + id);
        Call<Void> call = apiService.deleteLevel(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Level deleted successfully");
                    Toast.makeText(ManageLevelActivity.this, "Level deleted", Toast.LENGTH_SHORT).show();
                    fetchLevels();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to delete level: " + response.code() + " - " + error);
                        Toast.makeText(ManageLevelActivity.this, "Failed to delete level: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ManageLevelActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}