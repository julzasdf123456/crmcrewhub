package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.classes.Towns;
import com.lopez.julz.crmcrewhub.classes.TownsAdapter;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.TownsDao;

import java.util.ArrayList;
import java.util.List;

public class TownsActivity extends AppCompatActivity {

    Toolbar townsToolbar;

    public RecyclerView townsRecyclerView;
    public TownsAdapter townsAdapter;
    public List<Towns> townsList;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_towns);

        townsToolbar = (Toolbar) findViewById(R.id.townsToolbar);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        setSupportActionBar(townsToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        townsRecyclerView = findViewById(R.id.townsRecyclerView);
        townsList = new ArrayList<>();
        townsAdapter = new TownsAdapter(townsList, this);
        townsRecyclerView.setAdapter(townsAdapter);
        townsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchTowns().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchTowns extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TownsDao townsDao = db.townsDao();
            List<Towns> towns = townsDao.getAll();

            for (int i=0; i<towns.size(); i++) {
                townsList.add(towns.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            townsAdapter.notifyDataSetChanged();
        }
    }
}