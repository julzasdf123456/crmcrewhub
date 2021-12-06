package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;

import java.util.List;

public class TicketRepositoriesAdapter extends RecyclerView.Adapter<TicketRepositoriesAdapter.ViewHolder> {

    public List<TicketRepositories> ticketRepositoriesList;
    public Context context;

    public AppDatabase db;

    public TicketRepositoriesAdapter(List<TicketRepositories> ticketRepositoriesList, Context context) {
        this.ticketRepositoriesList = ticketRepositoriesList;
        this.context = context;
        db = Room.databaseBuilder(context, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.ticket_repositories_recyclerview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketRepositories ticketRepositories = ticketRepositoriesList.get(position);

        FetchAllDetails details = new FetchAllDetails(holder, ticketRepositories);
        details.execute();
    }

    @Override
    public int getItemCount() {
        return ticketRepositoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CoordinatorLayout parent;
        public TextView type, ticket, parentTicket;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.ticketRepositoriesParent);
            type = itemView.findViewById(R.id.ticketType);
            ticket = itemView.findViewById(R.id.ticket);
            parentTicket = itemView.findViewById(R.id.parentTicket);
        }
    }

    public class FetchAllDetails extends AsyncTask<Void, Void, Void> {

        String type = "", parent = "", ticket = "";

        public ViewHolder holder;
        public TicketRepositories ticketRepositories;

        public FetchAllDetails(ViewHolder holder, TicketRepositories ticketRepositories) {
            this.holder = holder;
            this.ticketRepositories = ticketRepositories;
        }

        @Override
        protected Void doInBackground(Void...voids) {
            try {
                TicketRepositoriesDao ticketRepositoriesDao = db.ticketRepositoriesDao();

                if (ticketRepositories != null) {
                    TicketRepositories ticketRepository = ticketRepositories;

                    type = ticketRepository.getType();
                    ticket = ticketRepository.getName();
                    parent = ticketRepositoriesDao.getOne(ticketRepository.getParentTicket()).getName() != null ? ticketRepositoriesDao.getOne(ticketRepository.getParentTicket()).getName() : "";
                }

            } catch (Exception e) {
                Log.e("ERR_FETCH_DETAILS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            holder.type.setText(type);;
            holder.parentTicket.setText(parent);
            holder.ticket.setText(ticket);
            Log.e("TST", ticket);
            super.onPostExecute(unused);
        }
    }
}
