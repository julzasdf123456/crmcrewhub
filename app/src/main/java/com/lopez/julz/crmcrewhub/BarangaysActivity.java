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
import android.widget.TextView;

import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.BarangaysAdapter;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.classes.Towns;
import com.lopez.julz.crmcrewhub.classes.TownsAdapter;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;

import java.util.ArrayList;
import java.util.List;

public class BarangaysActivity extends AppCompatActivity {

    Toolbar barangaysToolbar;
    public TextView townTitle;

    public RecyclerView barangaysRecyclerView;
    public BarangaysAdapter barangaysAdapter;
    public List<Barangays> barangaysList;

    public AppDatabase db;

    public String town, townId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barangays);

        barangaysToolbar = (Toolbar) findViewById(R.id.barangaysToolbar);

        town = getIntent().getStringExtra("TOWN");
        townId = getIntent().getStringExtra("ID");
        townTitle = findViewById(R.id.townTitle);

        townTitle.setText("Barangays in " + town);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        setSupportActionBar(barangaysToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        barangaysRecyclerView = findViewById(R.id.barangaysRecyclerView);
        barangaysList = new ArrayList<>();
        barangaysAdapter = new BarangaysAdapter(barangaysList, this);
        barangaysRecyclerView.setAdapter(barangaysAdapter);
        barangaysRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchBarangays().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchBarangays extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            BarangaysDao barangaysDao = db.barangaysDao();
            List<Barangays> barangays = barangaysDao.getAllByTownId(townId);

            for (int i=0; i<barangays.size(); i++) {
                barangaysList.add(barangays.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            barangaysAdapter.notifyDataSetChanged();
        }
    }
}