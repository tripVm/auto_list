package com.example.auto_list.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.auto_list.model.Auto;

import java.util.List;

@Dao
public interface AutoDao {
    @Query("SELECT *FROM auto")
    List<Auto> findAll();

    @Insert
    void insert(Auto auto);

    @Update
    void update(Auto auto);
}
