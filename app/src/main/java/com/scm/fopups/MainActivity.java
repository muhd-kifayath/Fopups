package com.scm.fopups;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.scm.fopups.API_Calls.DataModel;
import com.scm.fopups.API_Calls.RetrofitAPI;
import com.scm.fopups.databinding.ActivityMainBinding;
import com.scm.fopups.ui.todo.ToDoFragment;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    TrackedAppHelper dbHelper;
    ToDoHandler tdb;

    private final String FLASK_ENDPOINT = "http://172.23.20.2:5000/askgpt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Fopups-Channel";
            String description = "App Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Fopup-Notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        tdb = new ToDoHandler(getApplicationContext());
        tdb.openDatabase();
        List<ToDo> taskList = tdb.getAllTasks();
        tdb.close();
        List<String> taskStrings = new ArrayList<>();
        for (ToDo task : taskList) {
            taskStrings.add(task.getTask());
        }

        Log.d("Api Call", post_data(taskStrings.toString()));
        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                //post_data(taskStrings.toString());
                Log.d("Notify", taskStrings.toString());
                int max = taskStrings.size();
                if(max>0) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Fopup-Notification");
                    builder.setContentTitle("Limit Exceeded");
                    int b = (int) (Math.random() * (max) + 0);
                    builder.setContentText(taskStrings.get(b));
                    builder.setSmallIcon(R.drawable.warning);
                    builder.setAutoCancel(true);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    managerCompat.notify(1, builder.build());
                }

            }
        };
        timer.schedule(hourlyTask, 0l, 90000);



    };



    public String post_data(String input){
        String gen_message = "";

        if (input!=null) {
            String query = input.toString();
            String prompt = query;

            try
            {
                URL url = new URL(FLASK_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Construct the request body
                String requestBody = "Message=" + prompt;

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Extract the generated message from the JSON response
                    gen_message = response.substring(12, (response.length() - 2));
                    //gen_message = StringEscapeUtils.unescapeJava(gen_message);
                } else {
                    gen_message = "API request failed with response code: " + responseCode;
                }

                connection.disconnect();
            } catch(Exception e)
            {
                e.printStackTrace();
                gen_message = "API request failed: " + e.toString();

            }
        }
        return gen_message;
    }

/*
    private void postData(String tasks) {

        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://task-prioritizor-ai.onrender.com/usage/api/prompt/update/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // below line is to create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // passing data from our text fields to our modal class.
        DataModel model = new DataModel(tasks);

        // calling a method to create a post and passing our modal class.
        Call<DataModel> call = retrofitAPI.createPost(model);

        // on below line we are executing our method.
        call.enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                // this method is called when we get response from our api.
                Toast.makeText(MainActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();

                // we are getting response from our body
                // and passing it to our modal class.
                DataModel responseFromAPI = response.body();

                // on below line we are getting our data from modal class and adding it to our string.
                String responseString = "Response Code : " + response.code() + "\nName : " + responseFromAPI.getPriority();
                Log.d("Response: ",responseString);
            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
                // setting text to our text view when
                // we get error response from API.
                Log.d("Failed to call API","Error found is : " + t.getMessage());
            }
        });
    }
*/

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

}