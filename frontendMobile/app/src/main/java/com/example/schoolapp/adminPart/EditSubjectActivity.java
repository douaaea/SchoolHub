package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Subject;
import com.example.schoolapp.model.SubjectInputDTO;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSubjectActivity extends AppCompatActivity {
    private EditText editTextSubjectName;
    private Button buttonUpdate, buttonCancel;
    private ApiService apiService;
    private Long subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonCancel = findViewById(R.id.buttonCancel);

        apiService = ApiClient.getClient().create(ApiService.class);
        subjectId = getIntent().getLongExtra("subjectId", -1);

        if (subjectId == -1) {
            Toast.makeText(this, "Invalid subject ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchSubject();
        buttonUpdate.setOnClickListener(v -> updateSubject());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void fetchSubject() {
        Call<Subject> call = apiService.getSubject(subjectId);
        call.enqueue(new Callback<Subject>() {
            @Override
            public void onResponse(Call<Subject> call, Response<Subject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Subject subject = response.body();
                    editTextSubjectName.setText(subject.getName());
                } else {
                    Toast.makeText(EditSubjectActivity.this, "Failed to load subject: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Subject> call, Throwable t) {
                Toast.makeText(EditSubjectActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSubject() {
        String name = editTextSubjectName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        SubjectInputDTO input = new SubjectInputDTO(name, null); // levelId set to null if not provided
        Call<Subject> call = apiService.updateSubject(subjectId, input);
        call.enqueue(new Callback<Subject>() {
            @Override
            public void onResponse(Call<Subject> call, Response<Subject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditSubjectActivity.this, "Subject updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditSubjectActivity.this, "Failed to update subject: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Subject> call, Throwable t) {
                Toast.makeText(EditSubjectActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}