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
import com.example.schoolapp.model.Teacher;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ManageTeachersActivity extends AppCompatActivity {
    private static final String TAG = "ManageTeachersActivity";
    private ListView listViewTeachers;
    private Button buttonAddTeacher, buttonReturn;
    private List<Teacher> teachers;
    private ArrayAdapter<String> adapter;
    private List<String> teacherNames;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teachers);

        listViewTeachers = findViewById(R.id.listViewTeachers);
        buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        buttonReturn = findViewById(R.id.buttonReturn);

        teachers = new ArrayList<>();
        teacherNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teacherNames);
        listViewTeachers.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonAddTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTeacherActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> finish());

        // Edit on click
        listViewTeachers.setOnItemClickListener((parent, view, position, id) -> {
            Teacher teacher = teachers.get(position);
            Intent intent = new Intent(this, EditTeacherActivity.class);
            intent.putExtra("teacherId", teacher.getId());
            startActivity(intent);
        });

        // Delete on long click
        listViewTeachers.setOnItemLongClickListener((parent, view, position, id) -> {
            Teacher teacher = teachers.get(position);
            deleteTeacher(teacher);
            return true;
        });

        fetchTeachers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTeachers();
    }

    private void fetchTeachers() {
        Call<List<Teacher>> call = apiService.getTeachers();
        Log.d(TAG, "Fetching teachers from: " + call.request().url());
        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teachers.clear();
                    teachers.addAll(response.body());
                    teacherNames.clear();
                    for (Teacher teacher : teachers) {
                        teacherNames.add(teacher.getFirstname() + " " + teacher.getLastname());
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Teachers fetched: " + teachers.size());
                    Toast.makeText(ManageTeachersActivity.this, "Teachers loaded: " + teachers.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to fetch teachers: " + response.code() + " - " + response.message());
                    Toast.makeText(ManageTeachersActivity.this, "Failed to fetch teachers: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Log.e(TAG, "Network error fetching teachers: " + t.getMessage(), t);
                Toast.makeText(ManageTeachersActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTeacher(Teacher teacher) {
        Call<Void> call = apiService.deleteTeacher(teacher.getId());
        Log.d(TAG, "Sending delete teacher request to: " + call.request().url());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Teacher deleted: " + teacher.getEmail());
                    Toast.makeText(ManageTeachersActivity.this, "Teacher deleted", Toast.LENGTH_SHORT).show();
                    fetchTeachers();
                } else {
                    Log.e(TAG, "Failed to delete teacher: " + response.code() + " - " + response.message());
                    if (response.code() == 500) {
                        Toast.makeText(ManageTeachersActivity.this, "Cannot delete teacher: likely assigned to programs", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageTeachersActivity.this, "Failed to delete teacher: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error deleting teacher: " + t.getMessage(), t);
                Toast.makeText(ManageTeachersActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}