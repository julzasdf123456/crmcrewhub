package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TicketsDao;

public class UpdateTicketActivity extends AppCompatActivity {

    public String ticketId;
    public Toolbar updateTicketsToolbar;

    public AppDatabase db;

    public Tickets ticket;

    public TextView accountName, ticketNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ticket);

        ticketId = getIntent().getExtras().getString("ID");
        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        updateTicketsToolbar = findViewById(R.id.updateTicketsToolbar);
        setSupportActionBar(updateTicketsToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountName = findViewById(R.id.accountName);
        ticketNo = findViewById(R.id.ticketNo);

        new GetTicketDetails().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetTicketDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();
                TicketRepositoriesDao ticketRepositories = db.ticketRepositoriesDao();

                ticket = ticketsDao.getOne(ticketId);
            } catch (Exception e) {
                Log.e("ERR_GET_DETAILS_TCKT", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (ticket != null) {
                accountName.setText(ticket.getConsumerName());
                ticketNo.setText("Ticket No: " + ticket.getId());
            }
        }
    }
}