package com.example.auto_list.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.auto_list.model.Auto;

@Database(entities = {Auto.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AutoDao autoDao();
}
