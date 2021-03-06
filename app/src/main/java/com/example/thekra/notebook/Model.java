package com.example.thekra.notebook;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class Model extends RealmObject {
    @PrimaryKey
    private int id;
    private String title;
    private String des;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDes() {
        return des;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
