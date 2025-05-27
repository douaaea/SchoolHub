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
import com.example.schoolapp.model.Program;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ManageProgramActivity extends AppCompatActivity {
    private static final String TAG = "ManageProgramActivity";
    private ListView listViewPrograms;
    private Button buttonAddProgram, buttonReturn;
    private List<Program> programList;
    private ArrayAdapter<String> adapter;
    private List<String> programDetails;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_programs);

        listViewPrograms = findViewById(R.id.listViewPrograms);
        buttonAddProgram = findViewById(R.id.buttonAddProgram);
        buttonReturn = findViewById(R.id.buttonReturn);
        programList = new ArrayList<>();
        programDetails = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, programDetails);
        listViewPrograms.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonAddProgram.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProgramActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> finish());

        listViewPrograms.setOnItemClickListener((parent, view, position, id) -> {
            Program program = programList.get(position);
            Intent intent = new Intent(this, EditProgramActivity.class);
            intent.putExtra("programId", program.getId());
            startActivity(intent);
        });

        listViewPrograms.setOnItemLongClickListener((parent, view, position, id) -> {
            Program program = programList.get(position);
            deleteProgram(program.getId());
            return true;
        });

        fetchPrograms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPrograms();
    }

    private void fetchPrograms() {
        Log.d(TAG, "Fetching programs...");
        Call<List<Program>> call = apiService.getPrograms();
        Log.d(TAG, "Request URL: " + call.request().url());
        call.enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Programs fetched successfully, count: " + response.body().size());
                    programList.clear();
                    programList.addAll(response.body());
                    programDetails.clear();
                    if (programList.isEmpty()) {
                        Log.d(TAG, "No programs found in the response.");
                        programDetails.add("No programs available");
                    } else {
                        for (Program program : programList) {
                            String detail = "Teacher: " + (program.getTeacher() != null ? program.getTeacher().toString() : "N/A") +
                                    ", Group: " + (program.getGroup() != null ? program.getGroup().getName() : "N/A") +
                                    ", Subject: " + (program.getSubject() != null ? program.getSubject().getName() : "N/A");
                            programDetails.add(detail);
                            Log.d(TAG, "Program added to list: " + detail);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch programs: " + response.code() + " - " + error);
                        Toast.makeText(ManageProgramActivity.this, "Failed to fetch programs: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(ManageProgramActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ManageProgramActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProgram(Long id) {
        Log.d(TAG, "Deleting program with ID: " + id);
        Call<Void> call = apiService.deleteProgram(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Program deleted successfully");
                    Toast.makeText(ManageProgramActivity.this, "Program deleted", Toast.LENGTH_SHORT).show();
                    fetchPrograms();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to delete program: " + response.code() + " - " + error);
                        Toast.makeText(ManageProgramActivity.this, "Failed to delete program: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ManageProgramActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}