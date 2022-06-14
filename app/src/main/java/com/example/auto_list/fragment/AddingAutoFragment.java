package com.example.auto_list.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.auto_list.R;
import com.example.auto_list.presenter.AddingAutoPresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddingAutoFragment extends Fragment implements AddingAutoPresenter.AddingAutoFragment {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nameOfAddingAuto)
    EditText nameOfAddingAuto;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.modificationOfAddingAuto)
    EditText modificationOfAddingAuto;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.powerOfAddingAuto)
    EditText powerOfAddingAuto;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.weightOfAddingAuto)
    EditText weightOfAddingAuto;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.photoOfAddingAuto)
    ImageView photoOfAddingAuto;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pathOfPhoto)
    TextView pathOfPhoto;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.addAuto)
    Button addAuto;

    private AddingAutoPresenter addingAutoPresenter;
    private Uri uriPhoto = Uri.EMPTY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_of_auto, container, false);
        ButterKnife.bind(this, view);
        if(savedInstanceState != null) {
            String pathOfPhoto = savedInstanceState.getString("pathOfPhoto");
            this.pathOfPhoto.setText(pathOfPhoto);
        }
        addAuto.setOnClickListener(v -> {
            String name = nameOfAddingAuto.getText().toString();
            String modification = modificationOfAddingAuto.getText().toString();
            String power = powerOfAddingAuto.getText().toString();
            String weight = weightOfAddingAuto.getText().toString();

            addingAutoPresenter = new AddingAutoPresenter(getContext(), this);
            addingAutoPresenter.setName(name);
            addingAutoPresenter.setModification(modification);
            addingAutoPresenter.setPower(power);
            addingAutoPresenter.setWeight(weight);
            addingAutoPresenter.setUriPhotoPath(uriPhoto);
            addingAutoPresenter.addAuto();
        });

        ActivityResultLauncher<Intent> photoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uriPhoto = Objects.requireNonNull(data).getData();
                        pathOfPhoto.setText(uriPhoto.getPath());
                    }
                });

        photoOfAddingAuto.setOnClickListener(view12 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            photoActivityResultLauncher.launch(intent);
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence pathOfPhoto = this.pathOfPhoto.getText();
        outState.putString("pathOfPhoto", pathOfPhoto.toString());
    }

    @Override
    public void displaySuccess() {
        clearFields();
        Toast toast = Toast.makeText(getContext(), "Данные успешно добавлены", Toast.LENGTH_LONG);
        toast.show();
    }

    private void clearFields() {
        nameOfAddingAuto.setText("");
        modificationOfAddingAuto.setText("");
        powerOfAddingAuto.setText("");
        weightOfAddingAuto.setText("");
        pathOfPhoto.setText("");
    }

    @Override
    public void displayError() {
        Toast toast = Toast.makeText(getContext(), "Проверьте правильность данных", Toast.LENGTH_LONG);
        toast.show();
    }

}
