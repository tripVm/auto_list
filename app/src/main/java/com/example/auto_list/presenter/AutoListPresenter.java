package com.example.auto_list.presenter;

import android.content.Context;
import android.os.Build;
import android.widget.Filter;

import androidx.annotation.RequiresApi;
import androidx.room.Room;

import com.example.auto_list.dao.AppDatabase;
import com.example.auto_list.dao.AutoDao;
import com.example.auto_list.model.Auto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AutoListPresenter {
    private AutoListFragment autoListFragment;
    private final AppDatabase database;
    private final List<Auto> autoList = new ArrayList<>();

    public AutoListPresenter(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "auto").build();
    }

    public void attachAutoFragment(AutoListFragment autoListFragment) {
        this.autoListFragment = autoListFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Disposable searchAutoList() {
        autoList.clear();
        int threadCt = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadCt);
        return Observable.create((ObservableOnSubscribe<Auto>) emitter -> {
            AutoDao autoDao = database.autoDao();
            List<Auto> autoList = autoDao.findAll();
            sortAutoListByName(autoList);
            this.autoList.addAll(autoList);
            emitter.onComplete();
        }).subscribeOn(Schedulers.from(executor))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    autoListFragment.displayAutoList(this.autoList);
                }).subscribe();
    }

    private void sortAutoListByName(List<Auto> autoList) {
        Comparator<Auto> compareByName = (Auto auto1, Auto auto2) -> auto1.getName().compareTo(auto2.getName());
        Collections.sort(autoList, compareByName);
    }

    public interface AutoListFragment {
        void displayAutoList(List<Auto> autoList);
        void filter(String text, Filter.FilterListener filterListener);
    }

}
