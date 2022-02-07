package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lopez.julz.crmcrewhub.R;
import com.lopez.julz.crmcrewhub.database.Tickets;

import java.util.List;

public class UploadTicketsAdapter extends RecyclerView.Adapter<UploadTicketsAdapter.ViewHolder> {

    List<Tickets> ticketsList;
    Context context;

    public UploadTicketsAdapter(List<Tickets> ticketsList, Context context) {
        this.ticketsList = ticketsList;
        this.context = context;
    }

    @NonNull
    @Override
    public UploadTicketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.download_tickets_recyclerview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadTicketsAdapter.ViewHolder holder, int position) {
        Tickets ticket = ticketsList.get(position);

        holder.ticket.setText("Ticket No. " + ticket.getId());
        holder.consumerName.setText(ticket.getConsumerName());
        holder.consumerAddress.setVisibility(View.GONE);
        holder.accountNumber.setText(ticket.getAccountNumber());
    }

    @Override
    public int getItemCount() {
        return ticketsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ticket, consumerName, consumerAddress, accountNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ticket = itemView.findViewById(R.id.ticket);
            consumerName = itemView.findViewById(R.id.consumerName);
            consumerAddress = itemView.findViewById(R.id.consumerAddress);
            accountNumber = itemView.findViewById(R.id.accountNumber);
        }
    }
}
