package com.scm.fopups.ui.todo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scm.fopups.ToDo;
import com.scm.fopups.ToDoAdapter;

import java.util.List;

public class ToDoViewModel extends ViewModel {
    private final MutableLiveData<List<ToDo>> taskList = new MutableLiveData<List<ToDo>>();
    private final MutableLiveData<ToDoAdapter> taskAdapter = new MutableLiveData<ToDoAdapter>();

    public void selectToDo(List<ToDo> item) {
        taskList.setValue(item);
    }
    public LiveData<List<ToDo>> getToDoList() {
        return taskList;
    }

    public void selectAdapter(ToDoAdapter item) {
        taskAdapter.setValue(item);
    }
    public LiveData<ToDoAdapter> getToDoAdapter() {
        return taskAdapter;
    }


}
