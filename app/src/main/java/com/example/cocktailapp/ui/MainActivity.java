package com.example.cocktailapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cocktailapp.R;
import com.example.cocktailapp.databinding.ActivityMainBinding;
import com.example.cocktailapp.model.CocktailWithIngredients;
import com.example.cocktailapp.model.base.Resource;
import com.example.cocktailapp.model.base.Status;
import com.example.cocktailapp.ui.adapter.CocktailRecyclerViewAdapter;
import com.example.cocktailapp.viewmodel.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private SwipeRefreshLayout pullToRefresh;
    private CocktailRecyclerViewAdapter cocktailAdapter;

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
                if (cocktails.status != Status.LOADING) {
                    pullToRefresh.setRefreshing(false);
                }

                if (cocktails.data != null) {
                    cocktailAdapter.setCocktailsToShow(cocktails.data);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            viewModel.getCocktailsObservable();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getData();
    }
}