package com.example.schoolapp.teacherPart;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.AssignmentDTO;
import com.example.schoolapp.model.Group;
import com.example.schoolapp.model.Program;
import com.example.schoolapp.model.SubjectDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddAssignmentActivity extends AppCompatActivity {
    private static final String TAG = "AddAssignmentActivity";
    private ApiService apiService;
    private EditText editTextTitle, editTextDescription;
    private Button buttonPickDelay;
    private Spinner spinnerSubject, spinnerGroup, spinnerProgram;
    private List<SubjectDTO> subjectList = new ArrayList<>();
    private List<Group> groupList = new ArrayList<>();
    private List<Program> programList = new ArrayList<>();
    private LocalDateTime selectedDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize UI elements
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonPickDelay = findViewById(R.id.buttonPickDelay);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerProgram = findViewById(R.id.spinnerProgram);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Set up date picker button
        buttonPickDelay.setOnClickListener(v -> showDateTimePicker());

        // Fetch data for spinners
        fetchSubjects();
        fetchGroups();
        fetchPrograms();

        // Save button listener
        buttonSave.setOnClickListener(v -> saveAssignment());
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (view1, selectedHour, selectedMinute) -> {
                                selectedDelay = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay,
                                        selectedHour, selectedMinute, 0);
                                buttonPickDelay.setText(selectedDelay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            }, hour, minute, true);
                    timePickerDialog.show();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void fetchSubjects() {
        Call<List<SubjectDTO>> call = apiService.getSubjectsDTO();
        call.enqueue(new Callback<List<SubjectDTO>>() {
            @Override
            public void onResponse(Call<List<SubjectDTO>> call, Response<List<SubjectDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subjectList = response.body();
                    ArrayAdapter<SubjectDTO> adapter = new ArrayAdapter<>(AddAssignmentActivity.this,
                            android.R.layout.simple_spinner_item, subjectList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(adapter);
                } else {
                    Toast.makeText(AddAssignmentActivity.this, "Failed to fetch subjects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubjectDTO>> call, Throwable t) {
                Toast.makeText(AddAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGroups() {
        Call<List<Group>> call = apiService.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupList = response.body();
                    ArrayAdapter<Group> adapter = new ArrayAdapter<>(AddAssignmentActivity.this,
                            android.R.layout.simple_spinner_item, groupList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGroup.setAdapter(adapter);
                } else {
                    Toast.makeText(AddAssignmentActivity.this, "Failed to fetch groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(AddAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPrograms() {
        Call<List<Program>> call = apiService.getPrograms();
        call.enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    programList = response.body();
                    ArrayAdapter<Program> adapter = new ArrayAdapter<>(AddAssignmentActivity.this,
                            android.R.layout.simple_spinner_item, programList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProgram.setAdapter(adapter);
                } else {
                    Toast.makeText(AddAssignmentActivity.this, "Failed to fetch programs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Toast.makeText(AddAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAssignment() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || selectedDelay == null) {
            Toast.makeText(this, "Please fill all fields and select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        SubjectDTO selectedSubject = (SubjectDTO) spinnerSubject.getSelectedItem();
        Group selectedGroup = (Group) spinnerGroup.getSelectedItem();
        Program selectedProgram = (Program) spinnerProgram.getSelectedItem();

        if (selectedSubject == null || selectedGroup == null || selectedProgram == null) {
            Toast.makeText(this, "Please select a subject, group, and program", Toast.LENGTH_SHORT).show();
            return;
        }

        AssignmentDTO assignmentDTO = new AssignmentDTO();
        assignmentDTO.setTitle(title);
        assignmentDTO.setDescription(description);
        assignmentDTO.setDelay(selectedDelay);
        assignmentDTO.setStatus("Not Started");
        assignmentDTO.setSubjectId(selectedSubject.getId());
        assignmentDTO.setGroupId(selectedGroup.getId());
        assignmentDTO.setProgramId(selectedProgram.getId());

        Log.d(TAG, "Sending AssignmentDTO: " + assignmentDTO.toString());

        Call<AssignmentDTO> call = apiService.createAssignment(assignmentDTO);
        call.enqueue(new Callback<AssignmentDTO>() {
            @Override
            public void onResponse(Call<AssignmentDTO> call, Response<AssignmentDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddAssignmentActivity.this, "Assignment created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Failed to create assignment: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(AddAssignmentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AssignmentDTO> call, Throwable t) {
                Toast.makeText(AddAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}