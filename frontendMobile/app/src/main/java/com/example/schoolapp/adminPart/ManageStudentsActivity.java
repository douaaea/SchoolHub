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
import com.example.schoolapp.model.Student;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {
    private static final String TAG = "ManageStudentsActivity";
    private ListView listViewStudents;
    private Button buttonAddStudent, buttonReturn;
    private List<Student> studentList;
    private ArrayAdapter<String> adapter;
    private List<String> studentNames;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        listViewStudents = findViewById(R.id.listViewStudents);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);
        buttonReturn = findViewById(R.id.buttonReturn);

        studentList = new ArrayList<>();
        studentNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentNames);
        listViewStudents.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStudentActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> finish());

        // Edit on click
        listViewStudents.setOnItemClickListener((parent, view, position, id) -> {
            Student student = studentList.get(position);
            Intent intent = new Intent(this, EditStudentActivity.class);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        });

        // Delete on long click
        listViewStudents.setOnItemLongClickListener((parent, view, position, id) -> {
            Student student = studentList.get(position);
            deleteStudent(student);
            return true;
        });

        fetchStudents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStudents();
    }

    private void fetchStudents() {
        Call<List<Student>> call = apiService.getStudents();
        Log.d(TAG, "Fetching students from: " + call.request().url());
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentList.clear();
                    studentList.addAll(response.body());
                    studentNames.clear();
                    for (Student student : studentList) {
                        studentNames.add(student.getFirstname() + " " + student.getLastname());
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Students fetched: " + studentList.size());
                    Toast.makeText(ManageStudentsActivity.this, "Students loaded: " + studentList.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to fetch students: " + response.code() + " - " + response.message());
                    Toast.makeText(ManageStudentsActivity.this, "Failed to fetch students: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                Log.e(TAG, "Network error fetching students: " + t.getMessage(), t);
                Toast.makeText(ManageStudentsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteStudent(Student student) {
        Call<Void> call = apiService.deleteStudent(student.getId());
        Log.d(TAG, "Sending delete student request to: " + call.request().url());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Student deleted: " + student.getEmail());
                    Toast.makeText(ManageStudentsActivity.this, "Student deleted", Toast.LENGTH_SHORT).show();
                    fetchStudents();
                } else {
                    Log.e(TAG, "Failed to delete student: " + response.code() + " - " + response.message());
                    if (response.code() == 500) {
                        Toast.makeText(ManageStudentsActivity.this, "Cannot delete student: likely assigned to groups or programs", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageStudentsActivity.this, "Failed to delete student: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error deleting student: " + t.getMessage(), t);
                Toast.makeText(ManageStudentsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}