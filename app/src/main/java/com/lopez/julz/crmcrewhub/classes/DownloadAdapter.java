package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.lopez.julz.crmcrewhub.R;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.TownsDao;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    public List<ServiceConnections> serviceConnectionsList;
    public Context context;
    public AppDatabase db;

    public DownloadAdapter(List<ServiceConnections> serviceConnectionsList, Context context) {
        this.serviceConnectionsList = serviceConnectionsList;
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.download_recyclerview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceConnections serviceConnections = serviceConnectionsList.get(position);

        DisplayDetails displayDetails = new DisplayDetails(serviceConnections, holder);
        displayDetails.execute();
    }

    @Override
    public int getItemCount() {
        return serviceConnectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView serviceAccountName, serviceAccountAddress, serviceAccountId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceAccountName = itemView.findViewById(R.id.serviceAccountName);
            serviceAccountAddress = itemView.findViewById(R.id.serviceAccountAddress);
            serviceAccountId = itemView.findViewById(R.id.serviceAccountId);
        }
    }

    class DisplayDetails extends AsyncTask<Void, Void, String> {

        ServiceConnections serviceConnections;
        ViewHolder holder;

        String town, barangay;

        public DisplayDetails(ServiceConnections serviceConnections, ViewHolder holder) {
            this.serviceConnections = serviceConnections;
            this.holder = holder;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                BarangaysDao barangaysDao = db.barangaysDao();
                TownsDao townsDao = db.townsDao();

                town = townsDao.getOne(serviceConnections.getTown()).getTown();
                barangay = barangaysDao.getOne(serviceConnections.getBarangay()).getBarangay();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            holder.serviceAccountName.setText(serviceConnections.getServiceAccountName());
            holder.serviceAccountId.setText(serviceConnections.getId());
            holder.serviceAccountAddress.setText((null==barangay ? serviceConnections.getBarangay() : barangay) + ", " + (null==town ? serviceConnections.getTown() : town));
            super.onPostExecute(s);
        }
    }
}
