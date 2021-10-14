package com.lopez.julz.crmcrewhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.DownloadAdapter;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadActivity extends AppCompatActivity {

    public CircularProgressIndicator downloadProgress;
    public SwipeRefreshLayout refresh;

    public RecyclerView downloadRecyclerView;
    public DownloadAdapter downloadAdapter;
    public List<ServiceConnections> serviceConnectionsList;
    public List<ServiceConnectionInspections> serviceConnectionInspectionsList;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    private FloatingActionButton downloadDataBtn;

    public TextView scCount;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        downloadProgress = (CircularProgressIndicator) findViewById(R.id.downloadProgress);
        downloadRecyclerView = (RecyclerView) findViewById(R.id.downloadRecyclerViewServiceConnections);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        scCount = (TextView) findViewById(R.id.scCount);
        downloadDataBtn = findViewById(R.id.downloadDataBtn);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        serviceConnectionsList = new ArrayList<>();
        serviceConnectionInspectionsList = new ArrayList<>();
        downloadAdapter = new DownloadAdapter(serviceConnectionsList, this);
        downloadRecyclerView.setAdapter(downloadAdapter);
        downloadRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchDownloadables();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDownloadables();
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
                }
            }
        });
    }

    public void fetchDownloadables() {
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
                publishProgress(progress);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("DOWNLOAD_FINISHED", "Download finished");
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
}