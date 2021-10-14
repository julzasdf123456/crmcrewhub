package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.lopez.julz.crmcrewhub.HomeActivity;
import com.lopez.julz.crmcrewhub.R;
import com.lopez.julz.crmcrewhub.UpdateServiceConnectionsActivity;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.TownsDao;

import java.util.List;

public class HomeServiceConnectionsQueueAdapter extends RecyclerView.Adapter<HomeServiceConnectionsQueueAdapter.ViewHolder> {

    public Context context;
    public List<ServiceConnections> serviceConnectionsList;
    public AppDatabase db;

    public HomeServiceConnectionsQueueAdapter(List<ServiceConnections> serviceConnectionsList, Context context) {
        this.serviceConnectionsList = serviceConnectionsList;
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();
    }

    @NonNull
    @Override
    public HomeServiceConnectionsQueueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.download_recyclerview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeServiceConnectionsQueueAdapter.ViewHolder holder, int position) {
        ServiceConnections serviceConnections = serviceConnectionsList.get(position);

        DisplayDetails displayDetails = new DisplayDetails(serviceConnections, holder);
        displayDetails.execute();

        holder.downloadableParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateServiceConnectionsActivity.class);
                intent.putExtra("SCID", serviceConnections.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceConnectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView serviceAccountName, serviceAccountAddress, serviceAccountId;
        public CoordinatorLayout downloadableParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceAccountName = itemView.findViewById(R.id.serviceAccountName);
            serviceAccountAddress = itemView.findViewById(R.id.serviceAccountAddress);
            serviceAccountId = itemView.findViewById(R.id.serviceAccountId);
            downloadableParent = itemView.findViewById(R.id.downloadableParent);
        }
    }

    class DisplayDetails extends AsyncTask<Void, Void, String> {

        ServiceConnections serviceConnections;
        HomeServiceConnectionsQueueAdapter.ViewHolder holder;

        String town, barangay;

        public DisplayDetails(ServiceConnections serviceConnections, HomeServiceConnectionsQueueAdapter.ViewHolder holder) {
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
