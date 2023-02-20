package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
    public String userId;
    public AppDatabase db;
    private ShowOnMap showOnMap;
    private String crew;

    public HomeServiceConnectionsQueueAdapter(List<ServiceConnections> serviceConnectionsList, Context context, String userId, String crew) {
        this.serviceConnectionsList = serviceConnectionsList;
        this.context = context;
        this.userId = userId;
        this.crew = crew;
        db = Room.databaseBuilder(context,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();
    }

    public void setShowOnMap(ShowOnMap showOnMap) {
        this.showOnMap = showOnMap;
    }

    public interface ShowOnMap {
        public void showLoc(int position);
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
                intent.putExtra("USERID", userId);
                intent.putExtra("CREW", crew);
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

            downloadableParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(context, downloadableParent);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.home_ticket_recyclerview_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.locate_ticket:
                                    if (showOnMap != null) {
                                        showOnMap.showLoc(getAdapterPosition());
                                    }
                                    return true;
                                case R.id.delete_ticket:
                                    //handle menu2 click
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                    return false;
                }
            });
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
            holder.serviceAccountAddress.setText((serviceConnections.getSitio() != null ? serviceConnections.getSitio() + ", " : "") + (null==barangay ? serviceConnections.getBarangay() : barangay + ", ") + (null==town ? serviceConnections.getTown() : town));

            if (serviceConnections.getStatus().equals("Energized")) {
                holder.serviceAccountName.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_round_check_circle_18), null);
                holder.serviceAccountName.setCompoundDrawablePadding(10);
            } else if (serviceConnections.getStatus().equals("Not Energized")) {
                holder.serviceAccountName.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_round_error_18), null);
                holder.serviceAccountName.setCompoundDrawablePadding(10);
            } else {
                holder.serviceAccountName.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_baseline_info_18), null);
                holder.serviceAccountName.setCompoundDrawablePadding(10);
            }
            super.onPostExecute(s);
        }
    }
}
