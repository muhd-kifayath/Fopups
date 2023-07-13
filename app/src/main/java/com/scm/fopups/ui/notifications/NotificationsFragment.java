package com.scm.fopups.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.scm.fopups.MainActivity;
import com.scm.fopups.R;
import com.scm.fopups.ToDo;
import com.scm.fopups.ToDoHandler;
import com.scm.fopups.databinding.FragmentNotificationsBinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    Button send;
    TextView responseView;
    ToDoHandler tdb;
    private final String FLASK_ENDPOINT = "http://172.23.20.2:5000/askgpt";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        send = root.findViewById(R.id.sendButton);
        responseView = root.findViewById(R.id.response);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Fopups-Channel";
            String description = "App Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Fopup-Notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        tdb = new ToDoHandler(getContext());
        tdb.openDatabase();
        List<ToDo> taskList = tdb.getAllTasks();
        tdb.close();
        List<String> taskStrings = new ArrayList<>();
        for (ToDo task : taskList) {
            taskStrings.add(task.getTask());
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = post_data(taskStrings.toString());
                int max = taskStrings.size();
                int b = (int)(Math.random()*(max)+0);
                responseView.setText(taskStrings.get(b));
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "Fopup-Notification");
                builder.setContentTitle("Limit Exceeded");
                builder.setContentText(taskStrings.get(0));
                builder.setSmallIcon(R.drawable.warning);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        });

        return root;
    }

    public String post_data(String input){
        String gen_message = "";
        if (input!=null) {
            String query = input.toString();
            String prompt = query;

            try {
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
                } else {
                    gen_message = "API request failed with response code: " + responseCode;
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                gen_message = "API request failed: " + e.getMessage();

            }
        }
        return gen_message;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}