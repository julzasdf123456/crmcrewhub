package com.lopez.julz.crmcrewhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.DownloadAdapter;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;

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

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public TextView scCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        downloadProgress = (CircularProgressIndicator) findViewById(R.id.downloadProgress);
        downloadRecyclerView = (RecyclerView) findViewById(R.id.downloadRecyclerViewServiceConnections);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        scCount = (TextView) findViewById(R.id.scCount);

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        serviceConnectionsList = new ArrayList<>();
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
    }

    public void fetchDownloadables() {
        try {
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

                            int count = responseList.size();

                            scCount.setText("Service Connections (" + count + ")");

                            for (int i=0; i<count; i++) {
                                serviceConnectionsList.add(responseList.get(i));
                            }

                            downloadAdapter.notifyDataSetChanged();
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
        } catch (Exception e) {
            Log.e("ERR_GET_DWNLDBLS", e.getMessage());
        }
    }
}