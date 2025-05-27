package com.example.schoolapp.teacherPart;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Assignment;
import com.example.schoolapp.model.Program;
import com.example.schoolapp.model.Student;
import com.example.schoolapp.model.WorkReturn;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class GradeWorkReturnsActivity extends AppCompatActivity implements OnActionListener {
    private static final String TAG = "GradeWorkReturnsActivity";
    private static final String PREF_TEACHER_ID = "teacher_id";
    private ApiService apiService;
    private ListView listViewWorkReturns;
    private List<WorkReturn> workReturnList = new ArrayList<>();
    private WorkReturnAdapter adapter;
    private Long teacherId;
    private Set<Long> groupIds = new HashSet<>();
    private List<Student> students = new ArrayList<>();
    private List<com.example.schoolapp.model.Subject> subjects = new ArrayList<>();
    private List<Assignment> assignments = new ArrayList<>();
    private boolean isLoading = false;
    private String errorMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_submissions);

        apiService = ApiClient.getClient().create(ApiService.class);

        teacherId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getLong(PREF_TEACHER_ID, -1);
        Log.d(TAG, "Retrieved teacherId from SharedPreferences: " + teacherId);
        if (teacherId == -1) {
            Log.e(TAG, "Teacher ID not found in SharedPreferences");
            Toast.makeText(this, "Teacher ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        listViewWorkReturns = findViewById(R.id.listViewSubmissions);
        adapter = new WorkReturnAdapter(this, workReturnList, this);
        listViewWorkReturns.setAdapter(adapter);

        fetchReferenceData();
    }

    private void fetchReferenceData() {
        isLoading = true;
        updateUI();

        Call<List<Student>> studentsCall = apiService.getStudents();
        Call<List<com.example.schoolapp.model.Subject>> subjectsCall = apiService.getSubjects();
        Call<List<Assignment>> assignmentsCall = apiService.getAssignmentsAsAssignment();

        studentsCall.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    students = response.body();
                    Log.d(TAG, "Fetched " + students.size() + " students");
                } else {
                    Log.e(TAG, "Failed to fetch students: " + response.code());
                }
                checkReferenceDataLoaded();
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                Log.e(TAG, "Network error fetching students: " + t.getMessage());
                checkReferenceDataLoaded();
            }
        });

        subjectsCall.enqueue(new Callback<List<com.example.schoolapp.model.Subject>>() {
            @Override
            public void onResponse(Call<List<com.example.schoolapp.model.Subject>> call, Response<List<com.example.schoolapp.model.Subject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subjects = response.body();
                    Log.d(TAG, "Fetched " + subjects.size() + " subjects");
                } else {
                    Log.e(TAG, "Failed to fetch subjects: " + response.code());
                }
                checkReferenceDataLoaded();
            }

            @Override
            public void onFailure(Call<List<com.example.schoolapp.model.Subject>> call, Throwable t) {
                Log.e(TAG, "Network error fetching subjects: " + t.getMessage());
                checkReferenceDataLoaded();
            }
        });

        assignmentsCall.enqueue(new Callback<List<Assignment>>() {
            @Override
            public void onResponse(Call<List<Assignment>> call, Response<List<Assignment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    assignments = response.body();
                    Log.d(TAG, "Fetched " + assignments.size() + " assignments");
                } else {
                    Log.e(TAG, "Failed to fetch assignments: " + response.code());
                }
                checkReferenceDataLoaded();
            }

            @Override
            public void onFailure(Call<List<Assignment>> call, Throwable t) {
                Log.e(TAG, "Network error fetching assignments: " + t.getMessage());
                checkReferenceDataLoaded();
            }
        });
    }

    private void checkReferenceDataLoaded() {
        if (!students.isEmpty() && !subjects.isEmpty() && !assignments.isEmpty()) {
            fetchProgramsAndWorkReturns();
        }
    }

    private void fetchProgramsAndWorkReturns() {
        Call<List<Program>> call = apiService.getProgramsByTeacher(teacherId);
        call.enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Program> programs = response.body();
                    Log.d(TAG, "Fetched " + programs.size() + " programs for teacher ID: " + teacherId);
                    groupIds.clear();
                    for (Program program : programs) {
                        if (program.getGroup() != null && program.getGroup().getId() != null) {
                            groupIds.add(program.getGroup().getId());
                        }
                    }
                    Log.d(TAG, "Extracted group IDs: " + groupIds);
                    if (groupIds.isEmpty()) {
                        Log.w(TAG, "No groups found for teacher ID: " + teacherId);
                        Toast.makeText(GradeWorkReturnsActivity.this, "No groups assigned to this teacher.", Toast.LENGTH_SHORT).show();
                        fetchAllWorkReturnsAsFallback();
                        return;
                    }
                    fetchWorkReturnsForGroups();
                } else {
                    String errorBody = "N/A";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to fetch programs: " + response.code() + ", Body: " + errorBody);
                    errorMessage = "Failed to fetch programs for teacher: " + response.code() + " (" + errorBody + ")";
                    isLoading = false;
                    updateUI();
                    Toast.makeText(GradeWorkReturnsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    fetchAllWorkReturnsAsFallback();
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Log.e(TAG, "Network error fetching programs: " + t.getMessage());
                errorMessage = "Network error: " + t.getMessage();
                isLoading = false;
                updateUI();
                Toast.makeText(GradeWorkReturnsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                fetchAllWorkReturnsAsFallback();
            }
        });
    }

    private void fetchWorkReturnsForGroups() {
        workReturnList.clear();
        int groupCount = groupIds.size();
        final int[] completedRequests = {0};

        for (Long groupId : groupIds) {
            Call<List<WorkReturn>> call = apiService.getAllWorkReturns(groupId, null);
            call.enqueue(new Callback<List<WorkReturn>>() {
                @Override
                public void onResponse(Call<List<WorkReturn>> call, Response<List<WorkReturn>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<WorkReturn> fetchedWorkReturns = response.body();
                        for (WorkReturn wr : fetchedWorkReturns) {
                            Student student = students.stream()
                                    .filter(s -> s.getId().equals(wr.getStudent() != null ? wr.getStudent().getId() : -1))
                                    .findFirst()
                                    .orElse(null);
                            Assignment assignment = assignments.stream()
                                    .filter(a -> a.getId().equals(wr.getAssignment() != null ? wr.getAssignment().getId() : -1))
                                    .findFirst()
                                    .orElse(null);
                            com.example.schoolapp.model.Subject subject = assignment != null
                                    ? subjects.stream()
                                    .filter(s -> s.getId().equals(assignment.getSubjectId()))
                                    .findFirst()
                                    .orElse(null)
                                    : null;

                            wr.setStudentName(student != null
                                    ? student.getFirstname() + " " + student.getLastname()
                                    : "Unknown Student");
                            wr.setAssignmentName(assignment != null ? assignment.getTitle() : "Unknown Assignment");
                            wr.setSubjectName(subject != null ? subject.getName() : "Unknown Subject");
                            Log.d(TAG, "WorkReturn ID: " + wr.getId() + ", File Path: " + wr.getFilePath() + ", Student: " + wr.getStudent() + ", Assignment: " + wr.getAssignment());
                        }
                        workReturnList.addAll(fetchedWorkReturns);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Fetched " + fetchedWorkReturns.size() + " work returns for group ID: " + groupId + ". Total: " + workReturnList.size());
                    } else {
                        Log.e(TAG, "Failed to fetch work returns for group ID " + groupId + ": " + response.code() + ", Message: " + response.message());
                    }
                    completedRequests[0]++;
                    checkIfAllRequestsCompleted(completedRequests[0], groupCount);
                }

                @Override
                public void onFailure(Call<List<WorkReturn>> call, Throwable t) {
                    Log.e(TAG, "Network error fetching work returns for group ID " + groupId + ": " + t.getMessage());
                    completedRequests[0]++;
                    checkIfAllRequestsCompleted(completedRequests[0], groupCount);
                }
            });
        }
    }

    private void checkIfAllRequestsCompleted(int completed, int total) {
        if (completed == total) {
            if (workReturnList.isEmpty()) {
                Log.w(TAG, "No submissions found for any groups.");
                errorMessage = "No submissions found for your groups.";
                isLoading = false;
                updateUI();
                Toast.makeText(GradeWorkReturnsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                fetchAllWorkReturnsAsFallback();
            } else {
                isLoading = false;
                errorMessage = null;
                updateUI();
            }
        }
    }

    private void fetchAllWorkReturnsAsFallback() {
        Call<List<WorkReturn>> call = apiService.getWorkReturns();
        call.enqueue(new Callback<List<WorkReturn>>() {
            @Override
            public void onResponse(Call<List<WorkReturn>> call, Response<List<WorkReturn>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    workReturnList.clear();
                    List<WorkReturn> fetchedWorkReturns = response.body();
                    for (WorkReturn wr : fetchedWorkReturns) {
                        Student student = students.stream()
                                .filter(s -> s.getId().equals(wr.getStudent() != null ? wr.getStudent().getId() : -1))
                                .findFirst()
                                .orElse(null);
                        Assignment assignment = assignments.stream()
                                .filter(a -> a.getId().equals(wr.getAssignment() != null ? wr.getAssignment().getId() : -1))
                                .findFirst()
                                .orElse(null);
                        com.example.schoolapp.model.Subject subject = assignment != null
                                ? subjects.stream()
                                .filter(s -> s.getId().equals(assignment.getSubjectId()))
                                .findFirst()
                                .orElse(null)
                                : null;

                        wr.setStudentName(student != null
                                ? student.getFirstname() + " " + student.getLastname()
                                : "Unknown Student");
                        wr.setAssignmentName(assignment != null ? assignment.getTitle() : "Unknown Assignment");
                        wr.setSubjectName(subject != null ? subject.getName() : "Unknown Subject");
                        Log.d(TAG, "WorkReturn ID: " + wr.getId() + ", File Path: " + wr.getFilePath() + ", Student: " + wr.getStudent() + ", Assignment: " + wr.getAssignment());
                    }
                    workReturnList.addAll(fetchedWorkReturns);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Fetched " + workReturnList.size() + " work returns as fallback. Total: " + workReturnList.size());
                    if (workReturnList.isEmpty()) {
                        errorMessage = "No submissions found in the system.";
                        Toast.makeText(GradeWorkReturnsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        errorMessage = null;
                    }
                } else {
                    Log.e(TAG, "Failed to fetch all work returns: " + response.code() + ", Message: " + response.message());
                    errorMessage = "Failed to fetch submissions: " + response.code();
                    Toast.makeText(GradeWorkReturnsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
                updateUI();
            }

            @Override
            public void onFailure(Call<List<WorkReturn>> call, Throwable t) {
                Log.e(TAG, "Network error fetching all work returns: " + t.getMessage());
                errorMessage = "Network error: " + t.getMessage();
                isLoading = false;
                updateUI();
                Toast.makeText(GradeWorkReturnsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (isLoading) {
            listViewWorkReturns.setVisibility(View.GONE);
            Toast.makeText(this, "Loading submissions...", Toast.LENGTH_SHORT).show();
        } else if (errorMessage != null) {
            listViewWorkReturns.setVisibility(View.GONE);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        } else {
            listViewWorkReturns.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAction(Long workReturnId, OnActionListener.ActionType actionType) {
        switch (actionType) {
            case DOWNLOAD:
                downloadFile(workReturnId);
                break;
            case GRADE:
                showGradeDialog(workReturnId);
                break;
        }
    }

    private void downloadFile(Long workReturnId) {
        // Find the corresponding WorkReturn to get filePath
        WorkReturn workReturn = workReturnList.stream()
                .filter(wr -> wr.getId().equals(workReturnId))
                .findFirst()
                .orElse(null);
        if (workReturn == null) {
            Log.e(TAG, "WorkReturn not found for ID: " + workReturnId);
            Toast.makeText(this, "WorkReturn not available", Toast.LENGTH_SHORT).show();
            return;
        }
        String filePath = workReturn.getFilePath();
        if (filePath == null) {
            Log.e(TAG, "FilePath is null for WorkReturn ID: " + workReturnId);
            Toast.makeText(this, "File not available for download", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting to download file for WorkReturn ID: " + workReturnId + ", File Path: " + filePath);
        String fullUrl = "http://yourserver:8080/api/workreturns/" + workReturnId + "/download"; // Replace with your server URL
        Log.d(TAG, "Download URL: " + fullUrl);
        Call<ResponseBody> call = apiService.downloadWorkReturnFile(workReturnId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                        if (fileName.isEmpty()) fileName = "submission_" + workReturnId + ".pdf";
                        File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                        java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destinationFile);
                        outputStream.write(response.body().bytes());
                        outputStream.close();
                        Toast.makeText(GradeWorkReturnsActivity.this, "File downloaded to: " + destinationFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Error saving file: " + e.getMessage());
                        Toast.makeText(GradeWorkReturnsActivity.this, "Error downloading file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to download file: " + response.code() + ", Message: " + response.message() + ", URL: " + call.request().url());
                    String errorBody = "N/A";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Log.e(TAG, "Error Body: " + errorBody);
                    Toast.makeText(GradeWorkReturnsActivity.this, "Failed to download file: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Network error downloading file: " + t.getMessage() + ", URL: " + call.request().url());
                Toast.makeText(GradeWorkReturnsActivity.this, "Network error downloading file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGradeDialog(Long workReturnId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Grade");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String grade = input.getText().toString();
            if (!grade.isEmpty()) {
                WorkReturn workReturn = workReturnList.stream()
                        .filter(wr -> wr.getId().equals(workReturnId))
                        .findFirst()
                        .orElse(null);
                if (workReturn != null) {
                    workReturn.setGrade(grade);
                    updateGrade(workReturnId, grade);
                }
            } else {
                Toast.makeText(GradeWorkReturnsActivity.this, "Please enter a grade", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateGrade(Long workReturnId, String grade) {
        WorkReturn workReturn = new WorkReturn();
        workReturn.setId(workReturnId);
        workReturn.setGrade(grade);
        Call<WorkReturn> call = apiService.updateWorkReturnGrade(workReturnId, workReturn);
        call.enqueue(new Callback<WorkReturn>() {
            @Override
            public void onResponse(Call<WorkReturn> call, Response<WorkReturn> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkReturn updatedWorkReturn = response.body();
                    workReturnList.stream()
                            .filter(wr -> wr.getId().equals(workReturnId))
                            .findFirst()
                            .ifPresent(wr -> wr.setGrade(updatedWorkReturn.getGrade()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(GradeWorkReturnsActivity.this, "Grade updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update grade: " + response.code());
                    Toast.makeText(GradeWorkReturnsActivity.this, "Failed to update grade: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkReturn> call, Throwable t) {
                Log.e(TAG, "Network error updating grade: " + t.getMessage());
                Toast.makeText(GradeWorkReturnsActivity.this, "Network error updating grade", Toast.LENGTH_SHORT).show();
            }
        });
    }
}