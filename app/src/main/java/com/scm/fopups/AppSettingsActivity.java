package com.scm.fopups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;


public class AppSettingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        recyclerView = findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.

        //recyclerView.setLayoutManager(recyclerViewLayoutManager);
        List<String> allApps = new ApkInfoExtractor(AppSettingsActivity.this).getAllInstalledApkInfo();

        adapter = new AppsAdapter(AppSettingsActivity.this, allApps);
        recyclerView.setAdapter(adapter);
    }
}