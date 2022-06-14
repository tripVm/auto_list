package com.example.auto_list.presenter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.room.Room;

import com.example.auto_list.dao.AppDatabase;
import com.example.auto_list.dao.AutoDao;
import com.example.auto_list.model.Auto;
import com.example.auto_list.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class AddingAutoPresenter {
    private static final String TAG = AddingAutoFragment.class.getSimpleName();

    private final Context context;
    private final AddingAutoFragment addingAutoFragment;
    private final AppDatabase database;

    private String name;
    private String modification;
    private String power;
    private String weight;
    private Uri uriPhotoPath;

    public AddingAutoPresenter(Context context, AddingAutoFragment addingAutoFragment) {
        this.addingAutoFragment = addingAutoFragment;
        database = Room.databaseBuilder(context, AppDatabase.class, "auto").build();
        this.context = context;
    }

    public void addAuto() {
        boolean validate = validateAuto();
        if (validate) {
            Observable.create((ObservableOnSubscribe<Auto>) emitter -> {
                Auto auto = new Auto();
                auto.setName(name);
                auto.setModification(modification);
                auto.setPower(power);
                auto.setWeight(weight);
                Bitmap photo = getBitmap(context.getContentResolver(), this.uriPhotoPath);
                String photoPath = saveToInternalStorage(photo);
                auto.setPhoto(photoPath);
                AutoDao autoDao = database.autoDao();
                autoDao.insert(auto);
                emitter.onComplete();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(addingAutoFragment::displaySuccess)
                    .subscribe();
        } else {
            addingAutoFragment.displayError();
        }
    }

    private boolean validateAuto() {
        boolean power = Utils.checkNumeric(this.power);
        boolean weight = Utils.checkNumeric(this.weight);
        String photoPath = uriPhotoPath.getPath();
        return !name.isEmpty() && !modification.isEmpty() && !photoPath.isEmpty()
                && power && weight;
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File path = new File(directory + "/" + System.currentTimeMillis());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return path.getAbsolutePath();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Uri getUriPhotoPath() {
        return uriPhotoPath;
    }

    public void setUriPhotoPath(Uri uriPhotoPath) {
        this.uriPhotoPath = uriPhotoPath;
    }

    public interface AddingAutoFragment {
        void displaySuccess();
        void displayError();
    }

}
