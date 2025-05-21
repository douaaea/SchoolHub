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

public class EditAssignmentActivity extends AppCompatActivity {
    private static final String TAG = "EditAssignmentActivity";
    private ApiService apiService;
    private EditText editTextTitle, editTextDescription;
    private Button buttonPickDelay;
    private Spinner spinnerSubject, spinnerGroup, spinnerProgram;
    private List<SubjectDTO> subjectList = new ArrayList<>();
    private List<Group> groupList = new ArrayList<>();
    private List<Program> programList = new ArrayList<>();
    private AssignmentDTO assignmentDTO;
    private LocalDateTime selectedDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize UI elements
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonPickDelay = findViewById(R.id.buttonPickDelay);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerProgram = findViewById(R.id.spinnerProgram);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Retrieve AssignmentDTO from Intent
        assignmentDTO = (AssignmentDTO) getIntent().getSerializableExtra("assignment");
        if (assignmentDTO == null) {
            Log.e(TAG, "AssignmentDTO not found in Intent");
            Toast.makeText(this, "Error: Assignment data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Received AssignmentDTO: " + assignmentDTO.toString());

        // Populate fields with AssignmentDTO data
        editTextTitle.setText(assignmentDTO.getTitle());
        editTextDescription.setText(assignmentDTO.getDescription());
        selectedDelay = assignmentDTO.getDelay();
        if (selectedDelay != null) {
            buttonPickDelay.setText(selectedDelay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            buttonPickDelay.setText("Pick Due Date and Time");
        }

        // Set up date picker button
        buttonPickDelay.setOnClickListener(v -> showDateTimePicker());

        // Fetch spinner data
        fetchSubjects();
        fetchGroups();
        fetchPrograms();

        // Save button listener
        buttonSave.setOnClickListener(v -> saveAssignment());
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int defaultYear = calendar.get(Calendar.YEAR);
        int defaultMonth = calendar.get(Calendar.MONTH);
        int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);
        int defaultHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defaultMinute = calendar.get(Calendar.MINUTE);

        // Use selectedDelay if available, otherwise use defaults
        final int year = (selectedDelay != null) ? selectedDelay.getYear() : defaultYear;
        final int month = (selectedDelay != null) ? selectedDelay.getMonthValue() - 1 : defaultMonth;
        final int day = (selectedDelay != null) ? selectedDelay.getDayOfMonth() : defaultDay;
        final int hour = (selectedDelay != null) ? selectedDelay.getHour() : defaultHour;
        final int minute = (selectedDelay != null) ? selectedDelay.getMinute() : defaultMinute;

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
                    ArrayAdapter<SubjectDTO> adapter = new ArrayAdapter<>(EditAssignmentActivity.this,
                            android.R.layout.simple_spinner_item, subjectList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(adapter);
                    // Set the selected subject based on assignmentDTO
                    for (int i = 0; i < subjectList.size(); i++) {
                        if (subjectList.get(i).getId().equals(assignmentDTO.getSubjectId())) {
                            spinnerSubject.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(EditAssignmentActivity.this, "Failed to fetch subjects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubjectDTO>> call, Throwable t) {
                Toast.makeText(EditAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<Group> adapter = new ArrayAdapter<>(EditAssignmentActivity.this,
                            android.R.layout.simple_spinner_item, groupList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGroup.setAdapter(adapter);
                    // Set the selected group based on assignmentDTO
                    for (int i = 0; i < groupList.size(); i++) {
                        if (groupList.get(i).getId().equals(assignmentDTO.getGroupId())) {
                            spinnerGroup.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(EditAssignmentActivity.this, "Failed to fetch groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(EditAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<Program> adapter = new ArrayAdapter<>(EditAssignmentActivity.this,
                            android.R.layout.simple_spinner_item, programList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProgram.setAdapter(adapter);
                    // Set the selected program based on assignmentDTO
                    for (int i = 0; i < programList.size(); i++) {
                        if (programList.get(i).getId().equals(assignmentDTO.getProgramId())) {
                            spinnerProgram.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(EditAssignmentActivity.this, "Failed to fetch programs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Toast.makeText(EditAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Update the existing assignmentDTO with new values
        assignmentDTO.setTitle(title);
        assignmentDTO.setDescription(description);
        assignmentDTO.setDelay(selectedDelay);
        assignmentDTO.setStatus("Not Started"); // Or retain the original status if needed
        assignmentDTO.setSubjectId(selectedSubject.getId());
        assignmentDTO.setGroupId(selectedGroup.getId());
        assignmentDTO.setProgramId(selectedProgram.getId());

        Log.d(TAG, "Updating AssignmentDTO: " + assignmentDTO.toString());

        Call<AssignmentDTO> call = apiService.updateAssignment(assignmentDTO.getId(), assignmentDTO);
        call.enqueue(new Callback<AssignmentDTO>() {
            @Override
            public void onResponse(Call<AssignmentDTO> call, Response<AssignmentDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditAssignmentActivity.this, "Assignment updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Failed to update assignment: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(EditAssignmentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AssignmentDTO> call, Throwable t) {
                Toast.makeText(EditAssignmentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}