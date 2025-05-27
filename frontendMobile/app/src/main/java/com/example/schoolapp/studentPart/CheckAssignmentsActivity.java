package com.example.schoolapp.studentPart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.LoginActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.AssignmentDTO;
import com.example.schoolapp.model.Student;
import com.example.schoolapp.model.StudentDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckAssignmentsActivity extends AppCompatActivity {
    private static final String TAG = "CheckAssignmentsActivity";
    private static final String PREF_STUDENT_ID = "student_id";
    private static final String PREF_GROUP_ID = "group_id";
    private static final String PREF_EMAIL = "email";
    private ApiService apiService;
    private Long studentId;
    private Long studentGroupId;
    private String studentEmail;
    private ListView listViewAssignments;
    private List<AssignmentDTO> assignmentList = new ArrayList<>();
    private AssignmentAdapter adapter;
    private Button buttonSelectFile, buttonSubmit;
    private TextView textSelectedFile;
    private AssignmentDTO selectedAssignment;
    private Uri selectedFileUri;
    private String selectedFileName; // Store the display name with extension
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting CheckAssignmentsActivity");
        setContentView(R.layout.activity_check_assignments);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get student ID, group ID, and email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        studentId = prefs.getLong(PREF_STUDENT_ID, -1);
        studentGroupId = prefs.getLong(PREF_GROUP_ID, -1);
        studentEmail = prefs.getString(PREF_EMAIL, null);
        Log.d(TAG, "Initial Student ID: " + studentId + ", Group ID: " + studentGroupId + ", Email: " + studentEmail);

        // Fallback: Fetch student details if not in SharedPreferences
        if (studentId == -1 || studentGroupId == -1 || studentEmail == null) {
            fetchStudentDetails();
        } else {
            Log.d(TAG, "Using cached Student ID: " + studentId + ", Group ID: " + studentGroupId);
            initializeUI();
        }
    }

    private void fetchStudentDetails() {
        // If studentId is available, use it; otherwise, fall back to email
        Call<Student> call;
        if (studentId != -1) {
            call = apiService.getStudent(studentId);
            Log.d(TAG, "Fetching student by ID: " + studentId);
        } else if (studentEmail != null) {
            call = apiService.getStudentByEmail(studentEmail);
            Log.d(TAG, "Fetching student by email: " + studentEmail);
        } else {
            Log.e(TAG, "No student ID or email available to fetch details");
            Toast.makeText(this, "User not logged in. Please log in again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Student student = response.body();
                    Log.d(TAG, "Raw student response: " + student.toString());
                    studentId = student.getId() != null ? student.getId() : -1L;
                    studentGroupId = (student.getGroup() != null && student.getGroup().getId() != null) ? student.getGroup().getId() : -1L;
                    studentEmail = student.getEmail() != null ? student.getEmail() : "";
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(PREF_STUDENT_ID, studentId);
                    editor.putLong(PREF_GROUP_ID, studentGroupId);
                    editor.putString(PREF_EMAIL, studentEmail);
                    editor.apply();
                    Log.d(TAG, "Fetched Student ID: " + studentId + ", Group ID: " + studentGroupId + ", Email: " + studentEmail);
                    initializeUI();
                } else {
                    Log.e(TAG, "Failed to fetch student details: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + (response.errorBody() != null ? response.errorBody().string() : "No error body"));
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(CheckAssignmentsActivity.this, "Failed to load user details: " + response.code(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.e(TAG, "Network error fetching student details: " + t.getMessage(), t);
                Toast.makeText(CheckAssignmentsActivity.this, "Network error loading user details", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void initializeUI() {
        if (studentId == -1) {
            Log.e(TAG, "Student ID not found after fetch");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (studentGroupId == -1) {
            Log.w(TAG, "Group ID not found after fetch, proceeding without group");
            Toast.makeText(this, "No group assigned. Some features may be limited.", Toast.LENGTH_LONG).show();
            // Allow proceeding without a group for now
        }

        // Initialize UI components
        listViewAssignments = findViewById(R.id.listViewAssignments);
        buttonSelectFile = findViewById(R.id.buttonSelectFile);
        textSelectedFile = findViewById(R.id.textSelectedFile);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        adapter = new AssignmentAdapter();
        listViewAssignments.setAdapter(adapter);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting work...");
        progressDialog.setCancelable(false);

        // Set up file picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedFileUri = result.getData().getData();
                        selectedFileName = "No file selected";
                        String mimeType = null;
                        Cursor cursor = getContentResolver().query(selectedFileUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (nameIndex != -1) {
                                selectedFileName = cursor.getString(nameIndex);
                            }
                            mimeType = getContentResolver().getType(selectedFileUri);
                            cursor.close();
                        }
                        Log.d(TAG, "Selected file: " + selectedFileName + ", MIME Type: " + mimeType + ", URI: " + selectedFileUri + ", LastPathSegment: " + selectedFileUri.getLastPathSegment());
                        textSelectedFile.setText(selectedFileName);
                        if (!isValidFileType(selectedFileName, mimeType)) {
                            Toast.makeText(this, "Only PDF, DOC, or DOCX files are allowed", Toast.LENGTH_SHORT).show();
                            selectedFileUri = null;
                            selectedFileName = null;
                            textSelectedFile.setText("No file selected");
                        } else if (!selectedFileName.toLowerCase().endsWith(".pdf") &&
                                !selectedFileName.toLowerCase().endsWith(".doc") &&
                                !selectedFileName.toLowerCase().endsWith(".docx")) {
                            // Fallback: Append extension based on MIME type if display name lacks it
                            if (mimeType != null) {
                                if (mimeType.equals("application/pdf")) {
                                    selectedFileName = selectedFileName + ".pdf";
                                } else if (mimeType.equals("application/msword")) {
                                    selectedFileName = selectedFileName + ".doc";
                                } else if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                                    selectedFileName = selectedFileName + ".docx";
                                }
                            }
                            Log.d(TAG, "Adjusted file name with extension: " + selectedFileName);
                            textSelectedFile.setText(selectedFileName);
                        }
                    }
                }
        );

        // Add click listener to select an assignment
        listViewAssignments.setOnItemClickListener((parent, view, position, id) -> {
            selectedAssignment = assignmentList.get(position);
            Long assignmentGroupId = selectedAssignment.getGroupId();
            Log.d(TAG, "Assignment Group ID: " + assignmentGroupId + ", Student Group ID: " + studentGroupId);
            Toast.makeText(CheckAssignmentsActivity.this, "Selected: " + selectedAssignment.getTitle(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Selected assignment: " + selectedAssignment.getTitle() + " (ID: " + selectedAssignment.getId() + ")");
        });

        // Set file selection button listener
        buttonSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                filePickerLauncher.launch(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching file picker: " + e.getMessage(), e);
                Toast.makeText(this, "Error selecting file", Toast.LENGTH_SHORT).show();
            }
        });

        // Set submit button listener
        buttonSubmit.setOnClickListener(v -> {
            if (selectedAssignment == null) {
                Toast.makeText(this, "Please select an assignment to submit", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedFileUri == null || selectedFileName == null) {
                Toast.makeText(this, "Please select a file to submit", Toast.LENGTH_SHORT).show();
                return;
            }
            submitWork(selectedAssignment.getId());
        });

        // Fetch assignments
        fetchAssignments();
    }

    private void fetchAssignments() {
        Call<List<AssignmentDTO>> call = apiService.getAssignments();
        call.enqueue(new Callback<List<AssignmentDTO>>() {
            @Override
            public void onResponse(Call<List<AssignmentDTO>> call, Response<List<AssignmentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    assignmentList.clear();
                    assignmentList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Fetched " + assignmentList.size() + " assignments");
                } else {
                    Log.e(TAG, "Failed to fetch assignments: " + response.code());
                    Toast.makeText(CheckAssignmentsActivity.this, "Failed to load assignments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AssignmentDTO>> call, Throwable t) {
                Log.e(TAG, "Network error fetching assignments: " + t.getMessage(), t);
                Toast.makeText(CheckAssignmentsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getMimeTypeFromExtension(String fileName) {
        if (fileName == null) return "application/octet-stream";
        if (fileName.toLowerCase().endsWith(".pdf")) return "application/pdf";
        if (fileName.toLowerCase().endsWith(".doc")) return "application/msword";
        if (fileName.toLowerCase().endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

    private void submitWork(Long assignmentId) {
        progressDialog.show();
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
            // Use a temporary file name; the actual file name with extension will be used in the multipart request
            File file = new File(getCacheDir(), "temp_" + System.currentTimeMillis());
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            // Use extension-based MIME type to ensure server compatibility
            String mimeType = getMimeTypeFromExtension(selectedFileName);
            MediaType mediaType = MediaType.parse(mimeType);
            Log.d(TAG, "Submitting file: " + selectedFileName + ", MediaType: " + mediaType + ", Assignment ID: " + assignmentId + ", Student ID: " + studentId);
            RequestBody requestFile = RequestBody.create(mediaType, file);
            // Use selectedFileName (with extension) instead of file.getName()
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", selectedFileName, requestFile);
            RequestBody assignmentIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(assignmentId));
            RequestBody studentIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(studentId));

            Call<Map<String, Object>> call = apiService.createWorkReturn(assignmentIdPart, studentIdPart, filePart);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(CheckAssignmentsActivity.this, "Work submitted successfully", Toast.LENGTH_SHORT).show();
                        textSelectedFile.setText("No file selected");
                        selectedFileUri = null;
                        selectedFileName = null;
                        selectedAssignment = null;
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Failed to submit work: " + response.code() + ", Error: " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage(), e);
                        }
                        Toast.makeText(CheckAssignmentsActivity.this, "Failed to submit work: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Network error submitting work: " + t.getMessage(), t);
                    Toast.makeText(CheckAssignmentsActivity.this, "Network error submitting work", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e(TAG, "Error submitting work: " + e.getMessage(), e);
            Toast.makeText(this, "Error submitting work: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidFileType(String fileName, String mimeType) {
        if (fileName == null) return false;

        boolean isValidExtension = fileName.toLowerCase().endsWith(".pdf") ||
                fileName.toLowerCase().endsWith(".doc") ||
                fileName.toLowerCase().endsWith(".docx");

        boolean isValidMime = mimeType == null ||
                mimeType.equals("application/pdf") ||
                mimeType.equals("application/msword") ||
                mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        Log.d(TAG, "File: " + fileName + ", Extension Valid: " + isValidExtension + ", MIME Valid: " + isValidMime);
        return isValidExtension && isValidMime;
    }

    private class AssignmentAdapter extends BaseAdapter {
        private static final int VIEW_TYPE_COUNT = 1;

        @Override
        public int getCount() {
            return assignmentList.size();
        }

        @Override
        public Object getItem(int position) {
            return assignmentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(CheckAssignmentsActivity.this)
                        .inflate(R.layout.list_item_assignment, parent, false);
                holder = new ViewHolder();
                holder.textTitle = convertView.findViewById(R.id.textAssignmentTitle);
                holder.textDescription = convertView.findViewById(R.id.textAssignmentDescription);
                holder.textDelay = convertView.findViewById(R.id.textAssignmentDelay);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AssignmentDTO assignment = assignmentList.get(position);
            holder.textTitle.setText(assignment.getTitle() != null ? assignment.getTitle() : "No title");
            holder.textDescription.setText(assignment.getDescription() != null ? assignment.getDescription() : "No description");
            holder.textDelay.setText(assignment.getDelay() != null ? assignment.getDelay().toString() : "No deadline");

            return convertView;
        }

        private class ViewHolder {
            TextView textTitle;
            TextView textDescription;
            TextView textDelay;
        }
    }
}