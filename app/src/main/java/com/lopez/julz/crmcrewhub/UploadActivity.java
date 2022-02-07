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
import com.lopez.julz.crmcrewhub.classes.UploadTicketsAdapter;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TimeFrames;
import com.lopez.julz.crmcrewhub.database.TimeFramesDao;
import com.mapbox.mapboxsdk.plugins.annotation.Line;

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

    public RecyclerView uploadRecyclerViewTickets;
    public UploadTicketsAdapter uploadTicketsAdapter;
    public List<Tickets> ticketsList;

    public TextView scCount;

    public AppDatabase db;

    public FloatingActionButton uploadDataBtn;

    public boolean isUploadingServiceConnections = false;
    public boolean isUploadingSCTimeFrames = false;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public CircularProgressIndicator scProgressUpload, ticketsProgressUpload;

    public AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadRecyclerViewServiceConnections = findViewById(R.id.uploadRecyclerViewServiceConnections);
        serviceConnectionsList = new ArrayList<>();
        timeFramesList = new ArrayList<>();
        ticketsList = new ArrayList<>();
        uploadServiceConnectionsAdapter = new UploadServiceConnectionsAdapter(serviceConnectionsList, this);
        uploadRecyclerViewServiceConnections.setAdapter(uploadServiceConnectionsAdapter);
        uploadRecyclerViewServiceConnections.setLayoutManager(new LinearLayoutManager(this));
        scProgressUpload = findViewById(R.id.scProgressUpload);
        uploadRecyclerViewTickets = findViewById(R.id.uploadRecyclerViewTickets);
        ticketsProgressUpload = findViewById(R.id.ticketsProgressUpload);
        uploadTicketsAdapter = new UploadTicketsAdapter(ticketsList, this);
        uploadRecyclerViewTickets.setAdapter(uploadTicketsAdapter);
        uploadRecyclerViewTickets.setLayoutManager(new LinearLayoutManager(this));

        scProgressUpload.setVisibility(View.GONE);
        ticketsProgressUpload.setVisibility(View.GONE);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        scCount = findViewById(R.id.scCount);
        uploadDataBtn = findViewById(R.id.uploadDataBtn);

        new FetchUploadableServiceConnections().execute();
        new FetchSCTimeFrames().execute();
        new FetchUploadableTickets().execute();

        uploadDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scProgressUpload.setVisibility(View.VISIBLE);
                uploadServiceConnectionData();
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

    public void uploadServiceConnectionData() {
        try {
            if (serviceConnectionsList.size() > 0) {
                Call<ServiceConnections> serviceConnectionsCall = requestPlaceHolder.uploadServiceConnection(serviceConnectionsList.get(0));

                serviceConnectionsCall.enqueue(new Callback<ServiceConnections>() {
                    @Override
                    public void onResponse(Call<ServiceConnections> call, Response<ServiceConnections> response) {
                        if (!response.isSuccessful()) {
                            Log.e("ERR_UPLD_SC", response.raw().toString());
                        } else {
                            if (response.code() == 200) {
                                new UpdateServiceConnection().execute(serviceConnectionsList.get(0));
                                serviceConnectionsList.remove(0);
                                uploadServiceConnectionsAdapter.notifyDataSetChanged();
                                uploadServiceConnectionData();
                            } else {
                                Log.e("ERR_UPLD_SC", response.raw().toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServiceConnections> call, Throwable t) {
                        Log.e("ERR_UPLD_SC", t.getMessage());
                    }
                });
            } else {
                // UPLOAD SC TIMEFRAMES
                if (timeFramesList.size() > 0) {
                    uploadSCTimeFrame();
                } else {
                    uploadTickets();
                }
            }

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

            serviceConnectionsList.addAll(serviceConnectionsDao.getUploadableServicConnections());

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            scCount.setText("Service Connections (" + serviceConnectionsList.size() + ")");

            uploadServiceConnectionsAdapter.notifyDataSetChanged();

        }
    }

    public class UpdateServiceConnection extends AsyncTask<ServiceConnections, Void, Void> {

        @Override
        protected Void doInBackground(ServiceConnections... serviceConnections) {
            try {
                if (serviceConnections != null) {
                    ServiceConnections sc = serviceConnections[0];
                    sc.setUploadStatus("UPLOADED");
                    db.serviceConnectionsDao().updateAll(sc);
                }
            } catch (Exception e) {
                Log.e("ERR_UPDT_TICKT", e.getMessage());
            }
            return null;
        }
    }

    public void uploadSCTimeFrame() {
        try {
            if (timeFramesList.size() > 0) {
                Call<TimeFrames> timeFramesCall = requestPlaceHolder.uploadTimeFrames(timeFramesList.get(0));

                timeFramesCall.enqueue(new Callback<TimeFrames>() {
                    @Override
                    public void onResponse(Call<TimeFrames> call, Response<TimeFrames> response) {
                        if (!response.isSuccessful()) {
                            Log.e("ERR_UPLD_SC", response.raw().toString());
                        } else {
                            if (response.code() == 200) {
                                new UpdateSCTimeFrames().execute(timeFramesList.get(0));
                                timeFramesList.remove(0);
                                uploadSCTimeFrame();
                            } else {
                                Log.e("ERR_UPLD_SC", response.raw().toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TimeFrames> call, Throwable t) {
                        Log.e("ERR_UPLD_SC", t.getMessage());
                    }
                });
            } else {
                // UPLOAD TICKETS
                scProgressUpload.setVisibility(View.GONE);
                if (ticketsList != null) {
                    ticketsProgressUpload.setVisibility(View.VISIBLE);
                    uploadTickets();
                } else {
                    ticketsProgressUpload.setVisibility(View.GONE);
                    AlertHelpers.showExitableInfoDialog(UploadActivity.this, UploadActivity.this, "Upload success!", "All data uploaded successfully!");
                }
            }

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

                timeFramesList.addAll(timeFramesDao.getUnuploaded());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

        }
    }

    public class UpdateSCTimeFrames extends AsyncTask<TimeFrames, Void, Void> {

        @Override
        protected Void doInBackground(TimeFrames... timeFrames) {
            try {
                if (timeFrames != null) {
                    TimeFrames tf = timeFrames[0];
                    tf.setIsUploaded("Yes");
                    db.timeFramesDao().updateAll(tf);
                }
            } catch (Exception e) {
                Log.e("ERR_UPDT_SC_TFRME", e.getMessage());
            }
            return null;
        }
    }

    public class FetchUploadableTickets extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ticketsList.addAll(db.ticketsDao().getUploadableTickets());
            } catch (Exception e) {
                Log.e("ERR_GET_TICKETS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            uploadTicketsAdapter.notifyDataSetChanged();
        }
    }

    public void uploadTickets() {
        try {
            if (ticketsList.size() > 0) {
                Call<Tickets> ticketsCall = requestPlaceHolder.uploadTickets(ticketsList.get(0));
                ticketsCall.enqueue(new Callback<Tickets>() {
                    @Override
                    public void onResponse(Call<Tickets> call, Response<Tickets> response) {
                        if (response.isSuccessful()) {
                            new UpdateTicket().execute(ticketsList.get(0));
                            ticketsList.remove(0);
                            uploadTicketsAdapter.notifyDataSetChanged();
                            uploadTickets();
                        } else {
                            Log.e("ERR_UPLD_TICKETS", response.raw() + "");
                        }
                    }

                    @Override
                    public void onFailure(Call<Tickets> call, Throwable t) {
                        Log.e("ERR_UPLD_TICKETS", t.getMessage());
                    }
                });
            } else {
                ticketsProgressUpload.setVisibility(View.GONE);
                AlertHelpers.showExitableInfoDialog(UploadActivity.this, UploadActivity.this, "Upload success!", "All data uploaded successfully!");
            }
        } catch (Exception e) {
            Log.e("ERR_UPLD_TICKETS", e.getMessage());
        }
    }

    public class UpdateTicket extends AsyncTask<Tickets, Void, Void> {

        @Override
        protected Void doInBackground(Tickets... tickets) {
            try {
                if (tickets != null) {
                    Tickets tk = tickets[0];
                    tk.setUploadStatus("UPLOADED");
                    db.ticketsDao().updateAll(tk);
                }
            } catch (Exception e) {
                Log.e("ERR_UPDT_TICKT", e.getMessage());
            }
            return null;
        }
    }
}