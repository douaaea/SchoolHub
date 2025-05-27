package com.example.schoolapp.teacherPart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Assignment;
import com.example.schoolapp.model.AssignmentDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ManageAssignmentsActivity extends AppCompatActivity {
    private static final String TAG = "ManageAssignmentsActivity";
    private ApiService apiService;
    private ListView listViewAssignments;
    private List<Assignment> assignmentList = new ArrayList<>();
    private ArrayAdapter<Assignment> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_assignments);

        apiService = ApiClient.getClient().create(ApiService.class);

        listViewAssignments = findViewById(R.id.listViewAssignments);
        Button buttonAddAssignment = findViewById(R.id.buttonAddAssignment);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assignmentList);
        listViewAssignments.setAdapter(adapter);

        buttonAddAssignment.setOnClickListener(v -> {
            startActivity(new Intent(ManageAssignmentsActivity.this, AddAssignmentActivity.class));
        });

        listViewAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Assignment assignment = assignmentList.get(position);
                // Convert Assignment to AssignmentDTO for compatibility with EditAssignmentActivity
                AssignmentDTO assignmentDTO = convertToDTO(assignment);
                Log.d(TAG, "Passing AssignmentDTO to EditAssignmentActivity: " + assignmentDTO.toString());
                Intent intent = new Intent(ManageAssignmentsActivity.this, EditAssignmentActivity.class);
                intent.putExtra("assignment", assignmentDTO);
                startActivity(intent);
            }
        });

        fetchAssignments();
    }

    private void fetchAssignments() {
        Call<List<Assignment>> call = apiService.getAssignmentsAsAssignment();
        call.enqueue(new Callback<List<Assignment>>() {
            @Override
            public void onResponse(Call<List<Assignment>> call, Response<List<Assignment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    assignmentList.clear();
                    assignmentList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Fetched " + assignmentList.size() + " assignments");
                } else {
                    Toast.makeText(ManageAssignmentsActivity.this, "Failed to fetch assignments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Assignment>> call, Throwable t) {
                Toast.makeText(ManageAssignmentsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AssignmentDTO convertToDTO(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDelay(assignment.getDelay()); // Assumes Assignment uses java.time.LocalDateTime
        dto.setStatus(assignment.getStatus());
        if (assignment.getSubject() != null) dto.setSubjectId(assignment.getSubject().getId());
        if (assignment.getGroup() != null) dto.setGroupId(assignment.getGroup().getId());
        if (assignment.getProgram() != null) dto.setProgramId(assignment.getProgram().getId());
        return dto;
    }
}