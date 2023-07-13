package com.scm.fopups.API_Calls;

public class DataModel {
    private String prioritize_tasks;
    public DataModel(String prioritize_tasks) {this.prioritize_tasks = prioritize_tasks;};

    public String getPriority() {
        return prioritize_tasks;
    }

    public void setPriority(String prioritize_tasks) {
        this.prioritize_tasks = prioritize_tasks;
    }
}
