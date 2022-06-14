package com.example.auto_list.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.auto_list.R;
import com.example.auto_list.utils.Utils;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAutoFragment extends DialogFragment {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.photoAuto)
    PhotoView photoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_auto, container, false);
        ButterKnife.bind(this, view);
        photoView.setZoomable(true);
        photoView.setMinimumScale(1.0f);
        photoView.setMaximumScale(2.0f);
        Bundle bundleGallery = getArguments();
        String pathPhoto = Objects.requireNonNull(bundleGallery).getString("pathPhoto");
        Bitmap photo = Utils.getBitmap(pathPhoto);
        photoView.setImageBitmap(photo);
        return view;
    }

}
