package com.scm.fopups.ui.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.scm.fopups.Alarms;
import com.scm.fopups.AppInfo;
import com.scm.fopups.AppInfoActivity;
import com.scm.fopups.AppInfoListAdapter;
import com.scm.fopups.TrackedAppHelper;
import com.scm.fopups.R;
import com.scm.fopups.TrackedAppInfo;
import com.scm.fopups.Utils;
import com.scm.fopups.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TrackedAppHelper dbHelper;
    private ListView appListView;
    public List<AppInfo> appInfoList;
    private static int LAUNCH_SETTINGS_ACTIVITY = 1;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new TrackedAppHelper(getContext());
        appListView = root.findViewById(R.id.app_list);

        Alarms.resetIsUsageExceededData(getContext());
        startBackgroundService();



        openDialog();

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        final List<AppInfo>appInfoList = getAppInfoList();
        AppInfoListAdapter appInfoListAdapter = new AppInfoListAdapter(getContext(), appInfoList);
        appListView.setAdapter(appInfoListAdapter);

        showAppListAndSetClickListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        final List<AppInfo>appInfoList = getAppInfoList();
        AppInfoListAdapter appInfoListAdapter = new AppInfoListAdapter(getContext(), appInfoList);
        appListView.setAdapter(appInfoListAdapter);

    }

    public List<AppInfo> getAppInfoList() {
        PackageManager packageManager = this.getActivity().getPackageManager();
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
                    boolean isUsageExceeded = trackedAppInfo.getIsUsageExceeded() == 1;
                    appInfoList.add(new AppInfo(appName, appIcon, packageName, true, isUsageExceeded));
                }
                else {
                    appInfoList.add(new AppInfo(appName, appIcon, packageName, false, false));
                }
            }
        }
        Collections.sort(appInfoList);
        return appInfoList;
    }

    public void showAppListAndSetClickListener() {
        final List<AppInfo>appInfoList = getAppInfoList();
        AppInfoListAdapter appInfoListAdapter = new AppInfoListAdapter(getContext(), appInfoList);
        appListView.setAdapter(appInfoListAdapter);

        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Intent intent = new Intent(getContext(), AppInfoActivity.class);
                intent.putExtra("packageName", appInfoList.get(i).getPackageName());
                intent.putExtra("appName", appInfoList.get(i).getAppName());
                startActivity(intent);
            }
        });
    }

    private void startBackgroundService() {
        if(Utils.isUsageAccessAllowed(getContext())) {
            Alarms.scheduleNotification(getContext());
        }
    }

    private void openDialog() {
        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("DialogInfo", Context.MODE_PRIVATE);

        View checkBoxView = View.inflate(getContext(), R.layout.checkbox, null);
        CheckBox checkBox = checkBoxView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("openDialog", false);
                editor.apply();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("IMPORTANT!")
                .setMessage("You might not get usage notifications after reboot. To solve this issue, you can either " +
                        "give auto start permission for this app manually from settings or launch " +
                        "this app at least once after reboot. That's all :)")
                .setView(checkBoxView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        if(sharedPreferences.getBoolean("openDialog", true)) {
            builder.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        dbHelper.close();
    }
}