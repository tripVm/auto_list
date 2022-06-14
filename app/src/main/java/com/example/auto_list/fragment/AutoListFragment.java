package com.example.auto_list.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.auto_list.R;
import com.example.auto_list.adapter.AutoListRecyclerAdapter;
import com.example.auto_list.model.Auto;
import com.example.auto_list.presenter.AutoListPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoListFragment extends Fragment implements AutoListPresenter.AutoListFragment {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.autoRecyclerView)
    RecyclerView autoRecyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.emptyList)
    TextView emptyList;

    private Context context;
    private AutoListPresenter autoListPresenter;
    private AutoListRecyclerAdapter autoListRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint({"NewApi", "UseRequireInsteadOfGet"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_list, container, false);
        ButterKnife.bind(this, view);
        context = getContext();
        createAutoListRecyclerAdapter(new ArrayList<>());
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoListPresenter = new AutoListPresenter(context);
        if (savedInstanceState == null) {
            progressBar.setVisibility(View.VISIBLE);
            autoListPresenter.attachAutoFragment(this);
            autoListPresenter.searchAutoList();
        } else {
            setByInstanceStateState(savedInstanceState);
        }
    }

    private void setByInstanceStateState(Bundle savedInstanceState) {
        Boolean loading = (Boolean) savedInstanceState.get("loading");
        if (loading != null && loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else  {
            Auto[] autoArray = (Auto[]) savedInstanceState.get("autoArray");
            displayAutoList(Arrays.asList(autoArray));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (progressBar.getVisibility() == View.VISIBLE) {
            outState.putBoolean("loading", true);
        }
        if (autoListRecyclerAdapter != null) {
            outState.putParcelableArray("autoArray", autoListRecyclerAdapter.getAlbumDataArray());
        }
        autoListPresenter.searchAutoList().dispose();
    }

    public void setVisibleRecycler() {
        emptyList.setVisibility(View.GONE);
        autoRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayAutoList(List<Auto> autoList) {
        if (autoList.isEmpty()) {
            displayEmptyList();
        } else {
            createAutoListRecyclerAdapter(autoList);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void createAutoListRecyclerAdapter(List<Auto> autoList) {
        autoRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        autoListRecyclerAdapter = new AutoListRecyclerAdapter(context, autoList);
        autoRecyclerView.setAdapter(autoListRecyclerAdapter);
        emptyList.setVisibility(View.GONE);
        autoRecyclerView.setVisibility(View.VISIBLE);
    }

    public void displayEmptyList() {
        autoRecyclerView.setVisibility(View.GONE);
        emptyList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void filter(String text, Filter.FilterListener filterListener) {
        autoListRecyclerAdapter
                .getFilter()
                .filter(text, filterListener);
    }
}
