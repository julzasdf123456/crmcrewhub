package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.lopez.julz.crmcrewhub.classes.ArchiveAdapter;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.Settings;
import com.lopez.julz.crmcrewhub.database.Tickets;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    Toolbar archivesToolbar;

    RecyclerView archiveRecyclerView;
    List<Tickets> ticketsList;
    ArchiveAdapter archiveAdapter;

    String crew;

    public AppDatabase db;
    public Settings settings;

    public EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        archivesToolbar = findViewById(R.id.archivesToolbar);

        crew = getIntent().getExtras().getString("CREW");

        setSupportActionBar(archivesToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        search = findViewById(R.id.search);
        ticketsList = new ArrayList<>();
        archiveAdapter = new ArchiveAdapter(ticketsList, this, crew);
        archiveRecyclerView.setAdapter(archiveAdapter);
        archiveRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        new GetTickets().execute();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new SearchTickets().execute(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetTickets extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ticketsList.addAll(db.ticketsDao().getArchive());
            } catch (Exception e) {
                Log.e("ERR_GET_TCKTS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            archiveAdapter.notifyDataSetChanged();
        }
    }

    public class SearchTickets extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketsList.clear();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (strings[0] != null) {
                    ticketsList.addAll(db.ticketsDao().getArchiveSearch("%" + strings[0] + "%"));
                } else {
                    ticketsList.addAll(db.ticketsDao().getArchive());
                }

            } catch (Exception e) {
                Log.e("ERR_GET_TCKTS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            archiveAdapter.notifyDataSetChanged();
        }
    }
}