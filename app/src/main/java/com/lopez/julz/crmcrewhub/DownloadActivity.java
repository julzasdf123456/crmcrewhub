package com.lopez.julz.crmcrewhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.AlertHelpers;
import com.lopez.julz.crmcrewhub.classes.DownloadAdapter;
import com.lopez.julz.crmcrewhub.classes.DownloadTicketsAdapter;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;
import com.lopez.julz.crmcrewhub.database.Settings;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TicketsDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadActivity extends AppCompatActivity {

    public CircularProgressIndicator downloadProgress;
    public SwipeRefreshLayout refresh;

    /**
     * SERVICE CONNECTIONS
     */
    public RecyclerView downloadRecyclerView;
    public DownloadAdapter downloadAdapter;
    public List<ServiceConnections> serviceConnectionsList;
    public List<ServiceConnectionInspections> serviceConnectionInspectionsList;

    /**
     * TICKETS
     */
    public RecyclerView downloadRecyclerViewTickets;
    public List<Tickets> ticketsList;
    public DownloadTicketsAdapter downloadTicketsAdapter;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    private FloatingActionButton downloadDataBtn;

    public TextView scCount;

    public AppDatabase db;
    public Settings settings;

    public String userId, crew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        userId = getIntent().getExtras().getString("USERID");
        crew = getIntent().getExtras().getString("CREW");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchSettings().execute();
    }

    /**
     * _________________________________________________
     * SERVICE CONNECTIONS
     * __________________________________________________
     */
    public void fetchDownloadableServiceConnections() {
        try {
            /**
             * Call downloadables from ServiceConnections
             */
            Call<List<ServiceConnections>> serviceConnectionDownloadables = requestPlaceHolder.getForEnergizationData();
            serviceConnectionDownloadables.enqueue(new Callback<List<ServiceConnections>>() {
                @Override
                public void onResponse(Call<List<ServiceConnections>> call, Response<List<ServiceConnections>> response) {
                    serviceConnectionsList.clear();
                    if (!response.isSuccessful()) {
                        Log.e("FETCH_ERROR", response.message());
                    } else {
                        if (response.code() == 200) {
                            List<ServiceConnections> responseList = response.body();

                            new AssessDownloadableServiceConnections().execute(responseList);
                        } else {
                            Log.e("FETCH_ERROR", response.message());
                        }
                    }
                    downloadProgress.setVisibility(View.GONE);
                    refresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<List<ServiceConnections>> call, Throwable t) {
                    Log.e("FETCH_ERROR", t.getMessage());
                    downloadProgress.setVisibility(View.GONE);
                }
            });

            /**
             * Call downloadables from ServiceConnectionInspections
             */
            Call<List<ServiceConnectionInspections>> serviceInspectionCall = requestPlaceHolder.getInspectionsForEnergizationData();
            serviceInspectionCall.enqueue(new Callback<List<ServiceConnectionInspections>>() {
                @Override
                public void onResponse(Call<List<ServiceConnectionInspections>> call, Response<List<ServiceConnectionInspections>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("ERR_GET_DWNLDBLS", response.message());
                    } else {
                        if (response.code() == 200) {
                            List<ServiceConnectionInspections> responseList = response.body();

                            new AssessDownloadableInspections().execute(responseList);
                        } else {
                            Log.e("ERR_GET_DWNLDBLS", response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ServiceConnectionInspections>> call, Throwable t) {
                    Log.e("ERR_GET_DWNLDBLS", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_GET_DWNLDBLS", e.getMessage());
        }
    }

    public void notifyDownloaded(String name, String number, String id) {
        try {
            Call<Void> notifyDownloaded = requestPlaceHolder.notifyDownloaded(name, number, id);

            notifyDownloaded.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.e("NOTIF_SUCCESS", "Notification sent");
                    } else {
                        Log.e("NOTIF_ERROR", response.message() + "\n" + response.raw() + "\n" + response.body());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("NOTIF_ERROR", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_NOTIFS", e.getMessage());
        }
    }

    public class AssessDownloadableServiceConnections extends AsyncTask<List<ServiceConnections>, Void, Void> {

        @Override
        protected Void doInBackground(List<ServiceConnections>... lists) {
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();

            List<ServiceConnections> serviceConnections = lists[0];

            for (int i=0; i<serviceConnections.size(); i++) {
                ServiceConnections serviceConnection = serviceConnectionsDao.getOne(serviceConnections.get(i).getId());

                if (serviceConnection == null) {
                    serviceConnectionsList.add(serviceConnections.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            downloadAdapter.notifyDataSetChanged();
            downloadProgress.setVisibility(View.GONE);

            scCount.setText("Service Connections (" + serviceConnectionsList.size() + ")");
        }
    }

    public class AssessDownloadableInspections extends AsyncTask<List<ServiceConnectionInspections>, Void, Void> {

        @Override
        protected Void doInBackground(List<ServiceConnectionInspections>... lists) {
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
            List<ServiceConnectionInspections> serviceConnectionInspections = lists[0];

            for(int i=0; i<serviceConnectionInspections.size(); i++) {
                ServiceConnectionInspections inspection = serviceConnectionInspectionsDao.getOneById(serviceConnectionInspections.get(i).getId());
                if (inspection == null) {
                    serviceConnectionInspectionsList.add(serviceConnectionInspections.get(i));
                }
            }
            return null;
        }
    }

    public class DownloadServiceConnections extends AsyncTask<List<ServiceConnections>, Integer, Void> {

        int progress = 0;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.e("PROGRESS", "Downloading item " + (progress));
        }

        @Override
        protected Void doInBackground(List<ServiceConnections>... lists) {
            List<ServiceConnections> serviceConnections = lists[0];

            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();

            int count = serviceConnections.size();

            for (int i=0; i<count; i++) {
                progress = i;
                ServiceConnections serviceConnection = serviceConnections.get(i);
                serviceConnectionsDao.insertAll(serviceConnection);
                notifyDownloaded(serviceConnection.getServiceAccountName(), serviceConnection.getContactNumber(), serviceConnection.getId());
                publishProgress(progress);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("DOWNLOAD_FINISHED", "Download finished");

            Call<Void> updateCall = requestPlaceHolder.updateDownloadedServiceConnectionStatus(crew, userId, "Downloaded by Crew");
            updateCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.e("UPDTED_DWNLDED_SC", response.message());
                    } else {
                        Log.e("ERR_UPDT_DWNLDED_SC", response.raw() + "");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("ERR_UPDT_DWNLDED_SC", t.getMessage());
                }
            });

            // show alert dialog
            AlertHelpers.showExitableInfoDialog(DownloadActivity.this, DownloadActivity.this, "Download finished!", "All data downloaded successfully!");
        }
    }

    public class DownloadInspections extends AsyncTask<List<ServiceConnectionInspections>, Void, Void> {

        @Override
        protected Void doInBackground(List<ServiceConnectionInspections>... lists) {
            List<ServiceConnectionInspections> serviceConnectionInspections = lists[0];
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();

            for(int i=0; i<serviceConnectionInspections.size(); i++) {
                serviceConnectionInspectionsDao.insertAll(serviceConnectionInspections.get(i));
            }
            return null;
        }
    }

    /**
     * ______________________________________
     * TICKETS
     * ___________________________________
     */
    public void fetchDownloadableTickets() {
        try {
            Call<List<Tickets>> ticketsCall = requestPlaceHolder.getDownloadbleTickets(crew);

            ticketsCall.enqueue(new Callback<List<Tickets>>() {
                @Override
                public void onResponse(Call<List<Tickets>> call, Response<List<Tickets>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("ERR_GET_DWNDBL_TCKTS", response.message() + ", " + response.errorBody());
                    } else {
                        if (response.code() == 200) {
                            new AssessDownloadableTickets().execute(response.body());
                        } else {
                            Log.e("ERR_GET_DWNDBL_TCKTS", response.message() + ", " + response.errorBody());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Tickets>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.e("ERR_GET_DWNDBL_TCKTS", e.getMessage());
        }
    }

    public class AssessDownloadableTickets extends AsyncTask<List<Tickets>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketsList.clear();
        }

        @Override
        protected Void doInBackground(List<Tickets>... lists) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();

                if (lists != null) {
                    List<Tickets> tickets = lists[0];

                    int size = tickets.size();

                    for (int i=0; i<size; i++) {
                        Tickets dbTicket = ticketsDao.getOne(tickets.get(i).getId());

                        if (dbTicket != null) {
                            // skip
                        } else {
                            ticketsList.add(tickets.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_ASSESS_DWNDBL_TCKT", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            downloadTicketsAdapter.notifyDataSetChanged();
        }
    }

    public class DownloadTickets extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();

                int size = ticketsList.size();

                for (int i=0; i<size; i++) {
                    ticketsDao.insertAll(ticketsList.get(i));
                }
            } catch (Exception e) {
                Log.e("ERR_SAVE_TICKETS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ticketsList.clear();
            downloadTicketsAdapter.notifyDataSetChanged();
            updateDownloadStatus();
        }
    }

    public void updateDownloadStatus() {
        try {
            Call<Void> updateCall = requestPlaceHolder.updateDownloadedStatus(crew, userId);

            updateCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e("ERR_SET_UPLD_STS", response.raw().toString());
                    } else {
                        if (response.code() == 200) {
                            Toast.makeText(DownloadActivity.this, "All tickets downloaded!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ERR_SET_UPLD_STS", response.raw().toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("ERR_SET_UPLD_STS", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_SET_UPLD_STS", e.getMessage());
        }
    }

    public class FetchSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                settings = db.settingsDao().getSettings();
            } catch (Exception e) {
                Log.e("ERR_FETCH_SETTINGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (settings != null) {
                retrofitBuilder = new RetrofitBuilder(settings.getDefaultServer());
                requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

                /**
                 * SERVICE CONNECTIONS
                 */
                downloadProgress = (CircularProgressIndicator) findViewById(R.id.downloadProgress);
                downloadRecyclerView = (RecyclerView) findViewById(R.id.downloadRecyclerViewServiceConnections);
                refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
                scCount = (TextView) findViewById(R.id.scCount);
                downloadDataBtn = findViewById(R.id.downloadDataBtn);

                serviceConnectionsList = new ArrayList<>();
                serviceConnectionInspectionsList = new ArrayList<>();
                downloadAdapter = new DownloadAdapter(serviceConnectionsList, DownloadActivity.this);
                downloadRecyclerView.setAdapter(downloadAdapter);
                downloadRecyclerView.setLayoutManager(new LinearLayoutManager(DownloadActivity.this));

                /**
                 * TICKETS
                 */
                downloadRecyclerViewTickets = findViewById(R.id.downloadRecyclerViewTickets);
                ticketsList = new ArrayList<>();
                downloadTicketsAdapter = new DownloadTicketsAdapter(ticketsList, DownloadActivity.this);
                downloadRecyclerViewTickets.setAdapter(downloadTicketsAdapter);
                downloadRecyclerViewTickets.setLayoutManager(new LinearLayoutManager(DownloadActivity.this));

                fetchDownloadableServiceConnections();
                fetchDownloadableTickets();

                refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchDownloadableServiceConnections();
                        fetchDownloadableTickets();
                    }
                });

                downloadDataBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (serviceConnectionsList == null) {
                            Toast.makeText(DownloadActivity.this, "No downloadable service connection data for the moment.", Toast.LENGTH_LONG).show();
                        } else {
                            new DownloadServiceConnections().execute(serviceConnectionsList);
                            new DownloadInspections().execute(serviceConnectionInspectionsList);
                            new DownloadTickets().execute();
                        }
                    }
                });
            } else {
                startActivity(new Intent(DownloadActivity.this, ConnectionSettingsActivity.class));
            }
        }
    }
}