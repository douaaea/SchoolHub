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
import com.example.schoolapp.model.Subject;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ManageSubjectActivity extends AppCompatActivity {
    private static final String TAG = "ManageSubjectActivity";
    private ListView listViewSubjects;
    private Button buttonAddSubject, buttonReturn;
    private List<Subject> subjectList;
    private ArrayAdapter<String> adapter;
    private List<String> subjectNames;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subject);

        listViewSubjects = findViewById(R.id.listViewSubjects);
        buttonAddSubject = findViewById(R.id.buttonAddSubject);
        buttonReturn = findViewById(R.id.buttonReturn);

        subjectList = new ArrayList<>();
        subjectNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjectNames);
        listViewSubjects.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSubjectActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> finish());

        // Edit on click
        listViewSubjects.setOnItemClickListener((parent, view, position, id) -> {
            Subject subject = subjectList.get(position);
            Intent intent = new Intent(this, EditSubjectActivity.class);
            intent.putExtra("subjectId", subject.getId());
            startActivity(intent);
        });

        // Delete on long click
        listViewSubjects.setOnItemLongClickListener((parent, view, position, id) -> {
            Subject subject = subjectList.get(position);
            deleteSubject(subject);
            return true;
        });

        fetchSubjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSubjects();
    }

    private void fetchSubjects() {
        Call<List<Subject>> call = apiService.getSubjects(); // Updated to match ApiService
        Log.d(TAG, "Fetching subjects from: " + call.request().url());

        call.enqueue(new Callback<List<Subject>>() {
            @Override
            public void onResponse(Call<List<Subject>> call, Response<List<Subject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subjectList.clear();
                    subjectNames.clear();

                    subjectList.addAll(response.body());
                    for (Subject subject : subjectList) {
                        subjectNames.add(subject.getName());
                    }

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Subjects fetched: " + subjectList.size());
                    Toast.makeText(ManageSubjectActivity.this, "Subjects loaded: " + subjectList.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to fetch subjects: " + response.code() + " - " + response.message());
                    Toast.makeText(ManageSubjectActivity.this, "Failed to fetch subjects: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Subject>> call, Throwable t) {
                Log.e(TAG, "Network error fetching subjects: " + t.getMessage(), t);
                Toast.makeText(ManageSubjectActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSubject(Subject subject) {
        Call<Void> call = apiService.deleteSubject(subject.getId());
        Log.d(TAG, "Sending delete subject request to: " + call.request().url());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Subject deleted: " + subject.getName());
                    Toast.makeText(ManageSubjectActivity.this, "Subject deleted", Toast.LENGTH_SHORT).show();
                    fetchSubjects();
                } else {
                    Log.e(TAG, "Failed to delete subject: " + response.code() + " - " + response.message());
                    if (response.code() == 500) {
                        Toast.makeText(ManageSubjectActivity.this, "Cannot delete subject: likely assigned to programs or groups", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageSubjectActivity.this, "Failed to delete subject: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error deleting subject: " + t.getMessage(), t);
                Toast.makeText(ManageSubjectActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}