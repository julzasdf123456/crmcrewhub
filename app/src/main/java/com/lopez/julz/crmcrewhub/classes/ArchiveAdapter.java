package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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

import com.google.android.material.card.MaterialCardView;
import com.lopez.julz.crmcrewhub.ArchiveActivity;
import com.lopez.julz.crmcrewhub.ArchiveViewActivity;
import com.lopez.julz.crmcrewhub.R;
import com.lopez.julz.crmcrewhub.UpdateTicketActivity;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TownsDao;

import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ViewHolder> {

    private List<Tickets> ticketsList;
    private Context context;
    private String crew;

    private AppDatabase db;

    public ArchiveAdapter(List<Tickets> ticketsList, Context context, String crew) {
        this.ticketsList = ticketsList;
        this.context = context;
        this.crew = crew;
        db = Room.databaseBuilder(context, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();
    }

    @NonNull
    @Override
    public ArchiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.archive_tickets_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveAdapter.ViewHolder holder, int position) {
        Tickets tickets = ticketsList.get(position);

        FetchTicketDetails fetchTicketDetails = new FetchTicketDetails(tickets, holder);
        fetchTicketDetails.execute();
    }

    @Override
    public int getItemCount() {
        return ticketsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ticket, consumerName, consumerAddress, accountNo;
        public MaterialCardView ticketParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ticket = itemView.findViewById(R.id.ticket);
            consumerName = itemView.findViewById(R.id.consumerName);
            consumerAddress = itemView.findViewById(R.id.consumerAddress);
            accountNo = itemView.findViewById(R.id.accountNumber);
            ticketParent = itemView.findViewById(R.id.ticketParent);
        }
    }

    public class FetchTicketDetails extends AsyncTask<Void, Void, Void> {

        private Tickets ticket;
        private ViewHolder holder;

        private String ticketName, consumername, consumeraddress, accountno;

        public FetchTicketDetails(Tickets ticket, ViewHolder holder) {
            this.ticket = ticket;
            this.holder = holder;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (ticket != null) {
                    TicketRepositoriesDao ticketRepositoriesDao = db.ticketRepositoriesDao();
                    TownsDao townsDao = db.townsDao();
                    BarangaysDao barangaysDao = db.barangaysDao();

                    TicketRepositories ticketSource = ticketRepositoriesDao.getOne(ticket.getTicket());

                    ticketName = ticketRepositoriesDao.getOne(ticketSource.getParentTicket()) != null ? (ticketRepositoriesDao.getOne(ticketSource.getParentTicket()).getName() + "-" + ticketSource.getName()) : ticketSource.getName();
                    consumername = ticket.getConsumerName();
                    consumeraddress = ticket.getBarangay() != null ? ((ticket.getSitio() != null ? ticket.getSitio() + ", " : "") + barangaysDao.getOne(ticket.getBarangay()).getBarangay() + ", " + townsDao.getOne(ticket.getTown()).getTown()) : ticket.getSitio();
                    accountno = ticket.getAccountNumber();
                }
            } catch (Exception e) {
                Log.e("ERR_FETCH_TCKT_DETAIL", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            holder.accountNo.setText(accountno);
            holder.consumerAddress.setText(consumeraddress);
            holder.consumerName.setText(consumername);
            holder.ticket.setText(ticketName);

            if (ticket.getUploadStatus() != null && ticket.getUploadStatus().equals("UPLOADABLE")) {
                holder.ticket.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_round_check_circle_18), null, null, null);
            } else {
                holder.ticket.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

            holder.ticketParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ArchiveViewActivity.class);
                    intent.putExtra("ID", ticket.getId());
                    intent.putExtra("CREW", crew);
                    context.startActivity(intent);
                }
            });
        }
    }
}
