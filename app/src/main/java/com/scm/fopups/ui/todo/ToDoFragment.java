package com.scm.fopups.ui.todo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scm.fopups.AddNewTask;
import com.scm.fopups.DialogCloseListener;
import com.scm.fopups.MainActivity;
import com.scm.fopups.R;
import com.scm.fopups.TaskActions;
import com.scm.fopups.ToDo;
import com.scm.fopups.ToDoAdapter;
import com.scm.fopups.ToDoHandler;
import com.scm.fopups.databinding.FragmentTodoBinding;

import java.util.Collections;
import java.util.List;

public class ToDoFragment extends Fragment implements DialogCloseListener {

    private FragmentTodoBinding binding;


    private ToDoHandler tdb;

    private RecyclerView tasksRecyclerView;
    public ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    public List<ToDo> taskList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentTodoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tdb = new ToDoHandler(getContext());
        tdb.openDatabase();

        tasksRecyclerView = root.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new ToDoAdapter(tdb, (MainActivity) getActivity());
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new TaskActions(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = root.findViewById(R.id.fab);

        taskList = tdb.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getActivity().getSupportFragmentManager(), AddNewTask.TAG);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = tdb.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}