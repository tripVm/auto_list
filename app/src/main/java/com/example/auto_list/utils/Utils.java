package com.example.auto_list.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.regex.Pattern;

public class Utils {

    public static boolean checkNumeric(String text) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        return pattern.matcher(text).matches();
    }

    public static Bitmap getBitmap(String path) {
        File imgFile = new  File(path);
        return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
    }

}
