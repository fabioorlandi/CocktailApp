package com.example.cocktailapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cocktailapp.R;
import com.example.cocktailapp.databinding.ActivityMainBinding;
import com.example.cocktailapp.model.CocktailWithIngredients;
import com.example.cocktailapp.model.base.AppConnection;
import com.example.cocktailapp.model.base.AppConnectionStatus;
import com.example.cocktailapp.model.base.CocktailActivityResult;
import com.example.cocktailapp.model.base.Resource;
import com.example.cocktailapp.model.base.Status;
import com.example.cocktailapp.ui.adapter.CocktailRecyclerViewAdapter;
import com.example.cocktailapp.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_COCKTAIL_RESULT = CocktailActivityResult.UPDATE.getValue();
    private static final int DELETE_COCKTAIL_RESULT = CocktailActivityResult.DELETE.getValue();

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private SwipeRefreshLayout pullToRefresh;
    private CocktailRecyclerViewAdapter cocktailAdapter;

    public ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case 1:
                            Long id = result.getData().getExtras().getLong("ID");
                            String name = result.getData().getExtras().getString("EditedCocktailName");
                            String directions = result.getData().getExtras().getString("EditedDirections");
                            viewModel.updateCocktail(id, name, directions);
                            break;
                        case 2:
                            viewModel.deleteCocktail(result.getData().getExtras().getLong("ID"));
                            break;
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        RecyclerView cocktailRecyclerView = findViewById(R.id.cocktail_recyclerview_id);
        cocktailAdapter = new CocktailRecyclerViewAdapter(this);
        cocktailRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        cocktailRecyclerView.setAdapter(cocktailAdapter);

        pullToRefresh = findViewById(R.id.pull_to_refresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getData();
            }
        });

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        pullToRefresh.setRefreshing(true);

        viewModel.getCocktailsObservable().observe(MainActivity.this, new Observer<Resource<List<CocktailWithIngredients>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<CocktailWithIngredients>> cocktails) {
                if (cocktails.status != Status.LOADING || (cocktailAdapter.getCocktailsShown() != null && cocktailAdapter.getCocktailsShown().size() > 0)) {
                    pullToRefresh.setRefreshing(false);
                }

                if (cocktails.data != null && !compareLists(cocktailAdapter.getCocktailsShown(), cocktails.data)) {
                    cocktails.data.sort(new Comparator<CocktailWithIngredients>() {
                        @Override
                        public int compare(CocktailWithIngredients firstCocktail, CocktailWithIngredients secondCocktail) {
                            return firstCocktail.cocktail.name.compareToIgnoreCase(secondCocktail.cocktail.name);
                        }
                    });

                    cocktailAdapter.setCocktailsToShow(cocktails.data);
                }
            }
        });
    }

    private boolean compareLists(List<CocktailWithIngredients> baseList, List<CocktailWithIngredients> newList) {
        boolean areSame = true;
        if (baseList == null) {
            areSame = false;
        } else {
            for (CocktailWithIngredients item : newList) {
                if (!baseList.contains(item)) {
                    areSame = false;
                    break;
                }
            }
        }
        return areSame;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cocktailAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.app_status) {
            AppConnectionStatus status = AppConnection.getConnectionState(this);

            Toast.makeText(this, getString(R.string.app_is_running) + " " + status.toString().toLowerCase(Locale.ROOT) + "!", Toast.LENGTH_SHORT).show();

            return true;
        }

        if (id == R.id.action_sync_from_API) {
            if (AppConnection.getConnectionState(this) == AppConnectionStatus.OFFLINE) {
                Toast.makeText(this, getString(R.string.unable_to_do_operation) + AppConnectionStatus.OFFLINE.toString() + "!", Toast.LENGTH_SHORT).show();

                return true;
            }

            cocktailAdapter.setCocktailsToShow(new ArrayList<>());
            pullToRefresh.setRefreshing(true);
            viewModel.syncData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getData();
    }
}