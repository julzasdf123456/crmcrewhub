package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppConfig;
import com.lopez.julz.crmcrewhub.database.AppConfigDao;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.Crew;
import com.lopez.julz.crmcrewhub.database.CrewDao;
import com.lopez.julz.crmcrewhub.database.Settings;

import java.util.ArrayList;
import java.util.List;

public class LoginSettingsActivity extends AppCompatActivity {

    private Toolbar settingsLoginToolbar;
    private Spinner selectCrew;
    public List<Crew> crewList;
    public List<String> crewNames;
    public ArrayAdapter<String> arrayAdapter;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_settings);

        settingsLoginToolbar = findViewById(R.id.settingsLoginToolbar);
        setSupportActionBar(settingsLoginToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        selectCrew = findViewById(R.id.selectCrew);
        crewNames = new ArrayList<>();
        crewList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, crewNames);

        new FetchCrews().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            new SaveAppConfig().execute(crewList.get(selectCrew.getSelectedItemPosition()).getId(), crewList.get(selectCrew.getSelectedItemPosition()).getCrewLeader());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new SaveAppConfig().execute(crewList.get(selectCrew.getSelectedItemPosition()).getId(), crewList.get(selectCrew.getSelectedItemPosition()).getCrewLeader());
        super.onBackPressed();
    }


    public class FetchCrews extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CrewDao crewDao = db.crewDao();

                crewList.addAll(crewDao.getAll());
                for (int i=0; i<crewList.size(); i++) {
                    crewNames.add("Station " + crewList.get(i).getCrewLeader() + " - " + crewList.get(i).getStationName());
                }
            } catch (Exception e) {
                Log.e("ERR_GET_CREW", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            selectCrew.setAdapter(arrayAdapter);
        }
    }

    public class SaveAppConfig extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                AppConfigDao appConfigDao = db.appConfigDao();

                AppConfig appConfig = appConfigDao.getConfig();

                if (appConfig != null) {
                    appConfig.setDeviceStation(strings[0]); // strings[0] = CREW ID
                    appConfig.setCrewLeader(strings[1]); // strings[1] = CREW LEADER
                    appConfigDao.updateAll(appConfig);
                } else {
                    appConfigDao.insertAll(new AppConfig(strings[0], strings[1])); // strings[0] = CREW ID, strings[1] = CREW LEADER
                }
            } catch (Exception e) {
                Log.e("ERR_SV_CONFIG", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            finish();
        }
    }

}