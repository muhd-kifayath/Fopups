package com.scm.fopups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.scm.fopups.ui.todo.ToDoFragment;

import java.util.List;

public class DismissHelper extends ToDoFragment {
    public List<ToDo> taskList;
    public ToDoAdapter tasksAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        return v;
    }
}
