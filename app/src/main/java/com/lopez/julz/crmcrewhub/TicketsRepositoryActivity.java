package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.classes.TicketRepositoriesAdapter;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.mapbox.mapboxsdk.plugins.annotation.Line;

import java.util.ArrayList;
import java.util.List;

public class TicketsRepositoryActivity extends AppCompatActivity {

    public RecyclerView ticketRepositoriesRecyclerView;
    public List<TicketRepositories> ticketRepositoriesList;
    public TicketRepositoriesAdapter ticketRepositoriesAdapter;

    public Toolbar ticketRepositoriesToolbar;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_repository);

        ticketRepositoriesToolbar = findViewById(R.id.ticketRepositoriesToolbar);
        setSupportActionBar(ticketRepositoriesToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ticketRepositoriesRecyclerView = findViewById(R.id.ticketRepositoriesRecyclerView);
        ticketRepositoriesList = new ArrayList<>();
        ticketRepositoriesAdapter = new TicketRepositoriesAdapter(ticketRepositoriesList, this);
        ticketRepositoriesRecyclerView.setAdapter(ticketRepositoriesAdapter);
        ticketRepositoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        new GetTicketRepos().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetTicketRepos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketRepositoriesList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketRepositoriesDao ticketRepositoriesDao = db.ticketRepositoriesDao();

                ticketRepositoriesList.addAll(ticketRepositoriesDao.getTicketTypes());
            } catch (Exception e) {
                Log.e("ERR_GET_TCKT_REPS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ticketRepositoriesAdapter.notifyDataSetChanged();
        }
    }
}