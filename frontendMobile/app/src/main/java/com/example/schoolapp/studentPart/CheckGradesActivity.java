package com.example.schoolapp.studentPart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Grade;
import com.example.schoolapp.model.GradeDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class CheckGradesActivity extends AppCompatActivity {
    private static final String TAG = "CheckGradesActivity";
    private ApiService apiService;
    private Long studentId;
    private ListView listViewGrades;
    private List<Grade> gradeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting CheckGradesActivity");
        setContentView(R.layout.activity_check_grades);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get student ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        studentId = prefs.getLong("student_id", -1);
        if (studentId == -1) {
            Log.e(TAG, "Student ID not found in SharedPreferences");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI component
        listViewGrades = findViewById(R.id.listViewGrades);

        // Fetch grades
        fetchGrades();
    }

    private void fetchGrades() {
        Call<List<Grade>> call = apiService.getGrades(); // Fetching List<Grade>
        call.enqueue(new Callback<List<Grade>>() {
            @Override
            public void onResponse(Call<List<Grade>> call, Response<List<Grade>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gradeList.clear();
                    gradeList.addAll(response.body());
                    // Convert Grade to GradeDTO and filter
                    List<GradeDTO> gradeDTOs = convertToGradeDTOs(gradeList);
                    List<GradeDTO> studentGradeDTOs = new ArrayList<>();
                    for (GradeDTO gradeDTO : gradeDTOs) {
                        if (gradeDTO.getStudentId() != null && gradeDTO.getStudentId().equals(studentId)) {
                            studentGradeDTOs.add(gradeDTO);
                        }
                    }
                    // Use ArrayAdapter with GradeDTO (toString will be used for display)
                    android.widget.ArrayAdapter<GradeDTO> adapter = new android.widget.ArrayAdapter<>(
                            CheckGradesActivity.this, android.R.layout.simple_list_item_1, studentGradeDTOs);
                    listViewGrades.setAdapter(adapter);
                    Log.d(TAG, "Fetched " + studentGradeDTOs.size() + " grades");
                } else {
                    Log.e(TAG, "Failed to fetch grades: " + response.code());
                    Toast.makeText(CheckGradesActivity.this, "Failed to load grades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Grade>> call, Throwable t) {
                Log.e(TAG, "Network error fetching grades: " + t.getMessage(), t);
                Toast.makeText(CheckGradesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<GradeDTO> convertToGradeDTOs(List<Grade> grades) {
        List<GradeDTO> gradeDTOs = new ArrayList<>();
        for (Grade grade : grades) {
            String assignmentName = null;
            if (grade.getAssignment() != null && grade.getAssignment().getTitle() != null) {
                assignmentName = grade.getAssignment().getTitle();
            }
            GradeDTO gradeDTO = new GradeDTO(
                    grade.getScore(),
                    grade.getStudent() != null && grade.getStudent().getId() != null ? grade.getStudent().getId() : null,
                    grade.getSubject() != null && grade.getSubject().getId() != null ? grade.getSubject().getId() : null,
                    grade.getAssignment() != null && grade.getAssignment().getId() != null ? grade.getAssignment().getId() : null,
                    assignmentName
            );
            gradeDTOs.add(gradeDTO);
        }
        return gradeDTOs;
    }
}