package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.SubjectInputDTO;
import com.example.schoolapp.model.Subject;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSubjectActivity extends AppCompatActivity {
    private EditText editTextSubjectName;
    private Button buttonSave, buttonCancel;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonSave.setOnClickListener(v -> saveSubject());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveSubject() {
        String name = editTextSubjectName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        SubjectInputDTO input = new SubjectInputDTO(name, null); // levelId set to null if not provided
        Call<Subject> call = apiService.addSubject(input);
        call.enqueue(new Callback<Subject>() {
            @Override
            public void onResponse(Call<Subject> call, Response<Subject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddSubjectActivity.this, "Subject added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddSubjectActivity.this, "Failed to add subject: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Subject> call, Throwable t) {
                Toast.makeText(AddSubjectActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}