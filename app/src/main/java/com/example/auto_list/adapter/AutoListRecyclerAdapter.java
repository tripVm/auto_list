package com.example.auto_list.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.auto_list.MainActivity;
import com.example.auto_list.R;
import com.example.auto_list.fragment.GalleryAutoFragment;
import com.example.auto_list.model.Auto;
import com.example.auto_list.presenter.EditAutoPresenter;
import com.example.auto_list.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoListRecyclerAdapter extends RecyclerView.Adapter<AutoListRecyclerAdapter.AutoViewHolder> implements Filterable {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private List<Auto> autoList;
    private final List<Auto> filterAutoList;

    public AutoListRecyclerAdapter(Context context, List<Auto> qrDataList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.autoList = qrDataList;
        this.filterAutoList = qrDataList;
    }

    @NonNull
    @Override
    public AutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.auto, parent, false);
        return new AutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutoViewHolder holder, int position) {
        Auto auto = autoList.get(position);
        Bitmap photo = Utils.getBitmap(auto.getPhoto());
        holder.photo.setImageBitmap(photo);
        holder.name.setText(auto.getName());
        holder.modification.setText(auto.getModification());
        holder.power.setText(auto.getPower());
        holder.weight.setText(auto.getWeight());
    }

    @Override
    public int getItemCount() {
        return autoList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    constraint = constraint.toString().toLowerCase();
                    final List<Auto> filters = new ArrayList<>();
                    for (Auto auto : filterAutoList) {
                        if (auto.getName().toLowerCase().contains(constraint)) {
                            filters.add(auto);
                        }
                    }
                    results.count = filters.size();
                    results.values = filters;
                } else {
                    results.count = filterAutoList.size();
                    results.values = filterAutoList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence,
                                          FilterResults filterResults) {
                autoList = (List<Auto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Auto[] getAlbumDataArray() {
        return autoList.toArray(new Auto[autoList.size()]);
    }

    public class AutoViewHolder extends RecyclerView.ViewHolder implements EditAutoPresenter.AutoViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.photo)
        ImageView photo;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.name)
        TextView name;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.modification)
        TextView modification;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.power)
        TextView power;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.weight)
        TextView weight;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.edit)
        ImageView edit;

        private EditText changeModification;
        private EditText changePower;
        private EditText changeWeight;

        private View customEditLayout;
        private final EditAutoPresenter editAutoPresenter;
        private AlertDialog alertDialog;

        @SuppressLint("InflateParams")
        AutoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editAutoPresenter = new EditAutoPresenter(context, this);
            edit.setOnClickListener(view -> {
                customEditLayout = View.inflate(context, R.layout.alert_dialog_edit, null);
                changeModification = getEditText(R.id.changeModification, modification);
                changePower = getEditText(R.id.changePower, power);
                changeWeight = getEditText(R.id.changeWeight, weight);
                alertDialog = getAlertDialog();
                setOnShowListenerDialog(alertDialog);
                alertDialog.show();
            });
            photo.setOnClickListener(view -> {
                final DialogFragment galleryFragment = new GalleryAutoFragment();
                Bundle bundleGallery = new Bundle();
                Auto auto = autoList.get(getAdapterPosition());
                final String pathOfPhoto = auto.getPhoto();
                bundleGallery.putString("pathPhoto", pathOfPhoto);
                galleryFragment.setArguments(bundleGallery);
                final FragmentManager fragmentManager = ((MainActivity) itemView.getContext()).getSupportFragmentManager();
                galleryFragment.show(fragmentManager, "gallery_of_auto");
            });
        }

        private EditText getEditText(int id, TextView textView) {
            EditText changeEditText = customEditLayout.findViewById(id);
            CharSequence changeText = textView.getText();
            changeEditText.setText(changeText);
            changeEditText.setSelection(changeText.length());
            return changeEditText;
        }

        private AlertDialog getAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Rounded_MaterialComponents_MaterialAlertDialog);
            builder.setView(customEditLayout)
                    .setOnCancelListener(DialogInterface::dismiss)
                    .setPositiveButton(R.string.change, null);
            return builder.create();
        }

        private void setOnShowListenerDialog(AlertDialog alertDialog) {
            alertDialog.setOnShowListener(dialog -> {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(view -> updateAuto());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(changeModification, InputMethodManager.SHOW_IMPLICIT);
                imm.showSoftInput(changePower, InputMethodManager.SHOW_IMPLICIT);
                imm.showSoftInput(changeWeight, InputMethodManager.SHOW_IMPLICIT);
            });
        }

        private void updateAuto() {
            final int position = getAdapterPosition();
            Editable editableModification = changeModification.getText();
            Editable editablePower = changePower.getText();
            Editable editableWeight = changeWeight.getText();
            Auto editAuto = new Auto(editableModification.toString(), editablePower.toString(), editableWeight.toString());
            editAutoPresenter.updateAuto(editAuto, autoList, position);
        }

        @Override
        public void displaySuccess() {
            Toast toast = Toast.makeText(context, "Данные успешно изменены", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        public void displayError() {
            Toast toast = Toast.makeText(context, "Проверьте правильность данных", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        public void notifyAuto() {
            notifyDataSetChanged();
            alertDialog.dismiss();
        }
    }

}
