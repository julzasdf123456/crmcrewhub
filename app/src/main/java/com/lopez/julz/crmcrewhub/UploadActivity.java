package com.lopez.julz.crmcrewhub;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.AlertHelpers;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.classes.UploadServiceConnectionsAdapter;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;
import com.lopez.julz.crmcrewhub.database.TimeFrames;
import com.lopez.julz.crmcrewhub.database.TimeFramesDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    public RecyclerView uploadRecyclerViewServiceConnections;
    public UploadServiceConnectionsAdapter uploadServiceConnectionsAdapter;
    public List<ServiceConnections> serviceConnectionsList;
    public List<TimeFrames> timeFramesList;

    public TextView scCount;

    public AppDatabase db;

    public FloatingActionButton uploadDataBtn;

    public boolean isUploadingServiceConnections = false;
    public boolean isUploadingSCTimeFrames = false;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadRecyclerViewServiceConnections = findViewById(R.id.uploadRecyclerViewServiceConnections);
        serviceConnectionsList = new ArrayList<>();
        timeFramesList = new ArrayList<>();
        uploadServiceConnectionsAdapter = new UploadServiceConnectionsAdapter(serviceConnectionsList, this);
        uploadRecyclerViewServiceConnections.setAdapter(uploadServiceConnectionsAdapter);
        uploadRecyclerViewServiceConnections.setLayoutManager(new LinearLayoutManager(this));

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        scCount = findViewById(R.id.scCount);
        uploadDataBtn = findViewById(R.id.uploadDataBtn);

        new FetchUploadableServiceConnections().execute();

        uploadDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUploadingServiceConnections = true;
                new FetchUploadableServiceConnections().execute();
            }
        });
    }

    public void showLoadDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Finishing, please wait...");

            CircularProgressIndicator circularProgressIndicator = new CircularProgressIndicator(this);
            circularProgressIndicator.setIndeterminate(true);

            builder.setView(circularProgressIndicator);

            dialog = builder.create();

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadServiceConnectionData(ServiceConnections serviceConnections) {
        try {
            Call<ServiceConnections> serviceConnectionsCall = requestPlaceHolder.uploadServiceConnection(serviceConnections);

            serviceConnectionsCall.enqueue(new Callback<ServiceConnections>() {
                @Override
                public void onResponse(Call<ServiceConnections> call, Response<ServiceConnections> response) {
                    if (!response.isSuccessful()) {
                        isUploadingServiceConnections = false;
                    } else {
                        if (response.code() == 200) {
                            new FetchUploadableServiceConnections().execute(serviceConnections);
                        } else {
                            isUploadingServiceConnections = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServiceConnections> call, Throwable t) {
                    isUploadingServiceConnections = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FetchUploadableServiceConnections extends AsyncTask<ServiceConnections, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serviceConnectionsList.clear();
        }

        @Override
        protected Void doInBackground(ServiceConnections... serviceConnection) {
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();

            if (serviceConnection.length > 0) {
                serviceConnection[0].setStatus("Uploaded");
                serviceConnectionsDao.updateAll(serviceConnection[0]);
            }
            serviceConnectionsList.addAll(serviceConnectionsDao.getEnergized());

            if (serviceConnectionsList.size() < 1) {
                isUploadingServiceConnections = false;

                if (serviceConnection.length > 0) {
                    isUploadingSCTimeFrames = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            scCount.setText("Service Connections (" + serviceConnectionsList.size() + ")");

            uploadServiceConnectionsAdapter.notifyDataSetChanged();

            // START UPLOADING SERVICE CONNECTIONS
            if (isUploadingServiceConnections == true) {
                uploadServiceConnectionData(serviceConnectionsList.get(0));
            }

            // IF SERVICE CONNECTION UPLOADS ARE DONE, START UPLOADING TIMEFRAMES
            if (isUploadingSCTimeFrames) {
//                showLoadDialog();
                new FetchSCTimeFrames().execute();
            }
        }
    }

    public void uploadSCTimeFrame(TimeFrames timeFrames) {
        try {
            Call<TimeFrames> timeFramesCall = requestPlaceHolder.uploadTimeFrames(timeFrames);

            timeFramesCall.enqueue(new Callback<TimeFrames>() {
                @Override
                public void onResponse(Call<TimeFrames> call, Response<TimeFrames> response) {
                    if (!response.isSuccessful()) {
                        isUploadingSCTimeFrames = false;
                    } else {
                        if (response.code() == 200) {
                            new FetchSCTimeFrames().execute(timeFrames);
                        } else {
                            isUploadingSCTimeFrames = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<TimeFrames> call, Throwable t) {
                    isUploadingSCTimeFrames = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FetchSCTimeFrames extends AsyncTask<TimeFrames, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timeFramesList.clear();
        }

        @Override
        protected Void doInBackground(TimeFrames... timeFrames) {
            try {
                TimeFramesDao timeFramesDao = db.timeFramesDao();

                if (timeFrames.length > 0) {
                    timeFrames[0].setIsUploaded("Yes");
                    timeFramesDao.updateAll(timeFrames[0]);
                }

                timeFramesList.addAll(timeFramesDao.getUnuploaded());

                if (timeFramesList.size() < 1) {
                    isUploadingSCTimeFrames = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (isUploadingSCTimeFrames) {
                uploadSCTimeFrame(timeFramesList.get(0));
            } else {
                // STOP LOAD DIALOG
                if (dialog != null) {
                    dialog.dismiss();
                }
                AlertHelpers.showExitableInfoDialog(UploadActivity.this, UploadActivity.this, "Upload success!", "All data uploaded successfully!");
            }
        }
    }
}