package com.scm.fopups;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.scm.fopups.databinding.ActivityMainBinding;
import com.scm.fopups.ui.todo.ToDoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private ActivityMainBinding binding;

    TrackedAppHelper dbHelper;
    ToDoFragment toDoFragment;
    ToDoHandler tdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        tdb = new ToDoHandler(getApplicationContext());
        tdb.openDatabase();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void putAppInfoList() {
        PackageManager packageManager = getPackageManager();

        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        List<AppInfo> appInfoList = new ArrayList<>();

        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                String packageName = packageInfo.packageName;

                TrackedAppInfo trackedAppInfo = dbHelper.getRow(packageName);
                if(trackedAppInfo != null) {
                    boolean isUsageExceeded = trackedAppInfo.getIsUsageExceeded()==1;
                    appInfoList.add(new AppInfo(appName, appIcon, packageName, true, isUsageExceeded));
                }
                else {
                    appInfoList.add(new AppInfo(appName, appIcon, packageName, false, false));
                }
            }
        }

    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        toDoFragment.taskList = tdb.getAllTasks();
        Collections.reverse(toDoFragment.taskList);
        toDoFragment.tasksAdapter.setTasks(toDoFragment.taskList);
        toDoFragment.tasksAdapter.notifyDataSetChanged();
    }
}