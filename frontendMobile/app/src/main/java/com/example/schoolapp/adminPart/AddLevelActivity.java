package com.example.schoolapp.adminPart;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.R;
import com.example.schoolapp.model.Level;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLevelActivity extends AppCompatActivity {
    private static final String TAG = "AddLevelActivity";
    private EditText editTextName;
    private Button buttonAddLevel, buttonReturn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_level);

        editTextName = findViewById(R.id.editTextName);
        buttonAddLevel = findViewById(R.id.buttonAddLevel);
        buttonReturn = findViewById(R.id.buttonReturn);

        apiService = ApiClient.getClient().create(ApiService.class); // Corrected line

        buttonAddLevel.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter level name", Toast.LENGTH_SHORT).show();
                return;
            }

            Level level = new Level(name);
            Log.d(TAG, "Level payload: name=" + level.getName());

            Call<Level> call = apiService.addLevel(level);
            Log.d(TAG, "Sending add level request to: " + call.request().url());
            call.enqueue(new Callback<Level>() {
                @Override
                public void onResponse(Call<Level> call, Response<Level> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Level added: " + response.body().getName());
                        Toast.makeText(AddLevelActivity.this, "Level added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error: " + error);
                            Toast.makeText(AddLevelActivity.this, "Failed to add level: " + error, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Level> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    Toast.makeText(AddLevelActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonReturn.setOnClickListener(v -> finish());
    }
}