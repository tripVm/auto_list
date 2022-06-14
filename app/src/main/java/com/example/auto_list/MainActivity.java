package com.example.auto_list;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.auto_list.fragment.AddingAutoFragment;
import com.example.auto_list.fragment.AutoListFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ActionBar actionBar;
    private SearchView searchView;
    private Bundle savedInstanceState;
    private boolean stateBackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.savedInstanceState = savedInstanceState;
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.autoListFrame, new AutoListFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            stateBackPress = (boolean) savedInstanceState.get("stateBackPress");
            seToolbarForAddingFragment(stateBackPress);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Start Create Options Menu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_auto);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        if (savedInstanceState != null) {
            Boolean focusable = (Boolean) savedInstanceState.get("focusable");
            Object searchViewText = savedInstanceState.get("searchViewText");
            if (focusable != null && searchViewText != null && focusable) {
                searchView.setQuery(String.valueOf(searchViewText), true);
                searchView.setIconified(false);
            }
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setFilterAutoList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setFilterAutoList(newText);
                return false;
            }
        });
        return true;
    }

    private void setFilterAutoList(String text) {
        Fragment fragment = getAutoFragment();
        if (fragment instanceof AutoListFragment) {
            AutoListFragment autoListFragment = (AutoListFragment) getAutoFragment();
            Filter.FilterListener filterListener = getFilterListener(autoListFragment);
            autoListFragment.filter(text, filterListener);
        }
    }

    private Fragment getAutoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        return fragments.get(0);
    }

    private Filter.FilterListener getFilterListener(AutoListFragment autoListFragment) {
        return count -> {
            if (count == 0) {
                autoListFragment.displayEmptyList();
            } else {
                autoListFragment.setVisibleRecycler();
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_auto) {
            seToolbarForAddingFragment(true);
            startAddingAutoFragment();
            return true;
        } else if (id == R.id.action_exit) {
            finishAndRemoveTask();
            return true;
        } else if (id == android.R.id.home) {
            seToolbarForAddingFragment(false);
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAddingAutoFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.autoListFrame, new AddingAutoFragment())
                .addToBackStack(null)
                .commit();
    }

    private void seToolbarForAddingFragment(boolean state) {
        stateBackPress = state;
        actionBar.setDisplayHomeAsUpEnabled(state);
        actionBar.setDisplayShowHomeEnabled(state);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (searchView != null) {
            outState.putBoolean("focusable", searchView.requestFocus());
            outState.putString("searchViewText", String.valueOf(searchView.getQuery()));
            outState.putBoolean("stateBackPress", stateBackPress);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            seToolbarForAddingFragment(false);
            super.onBackPressed();
        }
    }

}