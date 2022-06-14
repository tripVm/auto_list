package com.example.auto_list.presenter;

import android.content.Context;

import androidx.room.Room;

import com.example.auto_list.dao.AppDatabase;
import com.example.auto_list.dao.AutoDao;
import com.example.auto_list.model.Auto;
import com.example.auto_list.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EditAutoPresenter {
    private final AutoViewHolder autoViewHolder;
    private final AppDatabase database;

    public EditAutoPresenter(Context context, AutoViewHolder autoViewHolder) {
        this.autoViewHolder = autoViewHolder;
        database = Room.databaseBuilder(context, AppDatabase.class, "auto").build();
    }

    public void updateAuto(Auto editAuto, List<Auto> autoList, int position) {
        boolean validate = validateAuto(editAuto);
        if (validate) {
            Observable.create((ObservableOnSubscribe<Auto>) emitter -> {
                Auto actualAuto = autoList.get(position);
                actualAuto.setModification(editAuto.getModification());
                actualAuto.setPower(editAuto.getPower());
                actualAuto.setWeight(editAuto.getWeight());
                AutoDao autoDao = database.autoDao();
                autoDao.update(actualAuto);
                emitter.onComplete();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        autoViewHolder.displaySuccess();
                        autoViewHolder.notifyAuto();
                    }).subscribe();
        } else {
            autoViewHolder.displayError();
        }
    }

    private boolean validateAuto(Auto editAuto) {
        boolean power = Utils.checkNumeric(editAuto.getPower());
        boolean weight = Utils.checkNumeric(editAuto.getWeight());
        return  !editAuto.getModification().isEmpty() && power && weight;
    }

    public interface AutoViewHolder {
        void displaySuccess();
        void displayError();
        void notifyAuto();
    }

}
