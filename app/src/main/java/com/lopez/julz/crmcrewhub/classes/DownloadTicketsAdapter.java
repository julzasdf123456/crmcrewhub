package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.lopez.julz.crmcrewhub.R;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TownsDao;

import java.util.List;

public class DownloadTicketsAdapter extends RecyclerView.Adapter<DownloadTicketsAdapter.ViewHolder> {

    private List<Tickets> ticketsList;
    private Context context;

    private AppDatabase db;

    public DownloadTicketsAdapter(List<Tickets> ticketsList, Context context) {
        this.ticketsList = ticketsList;
        this.context = context;
        db = Room.databaseBuilder(context, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();
    }

    @NonNull
    @Override
    public DownloadTicketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.download_tickets_recyclerview_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadTicketsAdapter.ViewHolder holder, int position) {
        Tickets ticket = ticketsList.get(position);

        FetchTicketDetails fetchTicketDetails = new FetchTicketDetails(holder, ticket);
        fetchTicketDetails.execute();
    }

    @Override
    public int getItemCount() {
        return ticketsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ticket, consumerName, consumerAddress, accountNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ticket = itemView.findViewById(R.id.ticket);
            consumerName = itemView.findViewById(R.id.consumerName);
            consumerAddress = itemView.findViewById(R.id.consumerAddress);
            accountNo = itemView.findViewById(R.id.accountNumber);
        }
    }

    public class FetchTicketDetails extends AsyncTask<Void, Void, Void> {

        private ViewHolder holder;
        private Tickets tickets;

        private String ticket, consumername, consumeraddress, accountno;

        public FetchTicketDetails(ViewHolder holder, Tickets tickets) {
            this.holder = holder;
            this.tickets = tickets;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketRepositoriesDao ticketRepositoriesDao = db.ticketRepositoriesDao();
                TownsDao townsDao = db.townsDao();
                BarangaysDao barangaysDao = db.barangaysDao();

                if (tickets != null) {
                    ticket = ticketRepositoriesDao.getOne(tickets.getTicket()).getName();
                    consumeraddress = tickets.getBarangay() != null ? (barangaysDao.getOne(tickets.getBarangay()).getBarangay() + ", " + townsDao.getOne(tickets.getTown()).getTown()) : tickets.getSitio();
                    consumername = tickets.getConsumerName();
                    accountno = tickets.getAccountNumber();
                }

            } catch (Exception e) {
                e.printStackTrace();;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            holder.accountNo.setText(accountno);
            holder.consumerAddress.setText(consumeraddress);
            holder.consumerName.setText(consumername);
            holder.ticket.setText(ticket);
        }
    }
}
