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
import com.example.schoolapp.model.Group;
import com.example.schoolapp.service.ApiClient;
import com.example.schoolapp.service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ManageGroupActivity extends AppCompatActivity {
    private static final String TAG = "ManageGroupsActivity";
    private ListView listViewGroups;
    private Button buttonAddGroup;
    private List<Group> groupList;
    private ArrayAdapter<String> adapter;
    private List<String> groupNames;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);

        listViewGroups = findViewById(R.id.listViewGroups);
        buttonAddGroup = findViewById(R.id.buttonAddGroup);
        groupList = new ArrayList<>();
        groupNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNames);
        listViewGroups.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        buttonAddGroup.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddGroupActivity.class);
            startActivity(intent);
        });

        listViewGroups.setOnItemClickListener((parent, view, position, id) -> {
            Group group = groupList.get(position);
            Intent intent = new Intent(this, EditGroupActivity.class);
            intent.putExtra("groupId", group.getId());
            startActivity(intent);
        });

        listViewGroups.setOnItemLongClickListener((parent, view, position, id) -> {
            Group group = groupList.get(position);
            deleteGroup(group.getId());
            return true;
        });

        fetchGroups();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchGroups();
    }

    private void fetchGroups() {
        Call<List<Group>> call = apiService.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupList.clear();
                    groupList.addAll(response.body());
                    groupNames.clear();
                    for (Group group : groupList) {
                        String displayName = group.getName() + " (" + (group.getLevel() != null ? group.getLevel().getName() : "No Level") + ")";
                        groupNames.add(displayName);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch groups: " + error);
                        Toast.makeText(ManageGroupActivity.this, "Failed to fetch groups: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ManageGroupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteGroup(Long id) {
        Call<Void> call = apiService.deleteGroup(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageGroupActivity.this, "Group deleted", Toast.LENGTH_SHORT).show();
                    fetchGroups();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to delete group: " + error);
                        Toast.makeText(ManageGroupActivity.this, "Failed to delete group: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ManageGroupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}