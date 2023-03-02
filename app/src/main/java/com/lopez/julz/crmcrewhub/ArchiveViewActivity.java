package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.Crew;
import com.lopez.julz.crmcrewhub.database.CrewDao;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TicketsDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;

public class ArchiveViewActivity extends AppCompatActivity {

    Toolbar viewTicketsToolbar;

    public String ticketId;

    public AppDatabase db;

    public Tickets ticket;

    public TextView accountName, ticketNo, ticketType, reason, address, contact, acctNo, poleNumber, entry;

    public MaterialButton markTimeOfArrivalBtn, markTimeOfEnergizationBtn;
    public TextView arrivalDateTime, energizationDateTime, remarks, coordinates, assessment;
    public RadioButton forAveraging;
    public FloatingActionButton editTimeOfArrivalBtn, editTimeOfEnergizationBtn;

    public TextView oldMeterSerial, oldMeterBrand;
    public TextView oldMeterReading, newMeterSerial, newMeterBrand, newMeterReading, oldSeal, newSeal;
    public TextView crewExecuted;

    private String crew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_view);

        viewTicketsToolbar = findViewById(R.id.viewTicketsToolbar);

        setSupportActionBar(viewTicketsToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ticketId = getIntent().getExtras().getString("ID");
        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        crew = getIntent().getExtras().getString("CREW");

        accountName = findViewById(R.id.accountName);
        ticketNo = findViewById(R.id.ticketNo);
        ticketType = findViewById(R.id.ticketType);
        reason = findViewById(R.id.reason);
        address = findViewById(R.id.address);
        contact = findViewById(R.id.contact);
        acctNo = findViewById(R.id.acctNo);
        arrivalDateTime = findViewById(R.id.linemanArrived);
        energizationDateTime = findViewById(R.id.executed);
        remarks = findViewById(R.id.remarks);
        oldMeterSerial = findViewById(R.id.oldMeterSerial);
        oldMeterBrand = findViewById(R.id.oldMeterBrand);
        oldMeterReading = findViewById(R.id.oldMeterReading);
        newMeterSerial = findViewById(R.id.newMeterNumber);
        newMeterBrand = findViewById(R.id.newMeterBrand);
        newMeterReading = findViewById(R.id.newMeterReading);
        coordinates = findViewById(R.id.coordinates);
        poleNumber = findViewById(R.id.poleNumber);
        crewExecuted = findViewById(R.id.crewExecuted);
        oldSeal = findViewById(R.id.oldSeal);
        newSeal = findViewById(R.id.newSeal);
        entry = findViewById(R.id.entry);
        assessment = findViewById(R.id.assessment);

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

        private String ticketName, consumeraddress, accountno, loggedcrew;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();
                TicketRepositoriesDao ticketRepositories = db.ticketRepositoriesDao();
                TownsDao townsDao = db.townsDao();
                BarangaysDao barangaysDao = db.barangaysDao();

                ticket = ticketsDao.getOne(ticketId);
                TicketRepositories ticketSource = ticketRepositories.getOne(ticket.getTicket());

                CrewDao crewDao = db.crewDao();
                Crew crewDetails = crewDao.getOne(crew);
                loggedcrew = crewDetails != null ? crewDetails.getStationName() : "-";

                ticketName = ticketRepositories.getOne(ticketSource.getParentTicket()) != null ? (ticketRepositories.getOne(ticketSource.getParentTicket()).getName() + "-" + ticketSource.getName()) : ticketSource.getName();
                consumeraddress = ticket.getSitio() + ", " + barangaysDao.getOne(ticket.getBarangay()).getBarangay() + ", " + townsDao.getOne(ticket.getTown()).getTown();
                accountno = ticket.getAccountNumber();
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
                ticketType.setText(ticketName);
                reason.setText(ticket.getReason());
                address.setText(consumeraddress);
                contact.setText(ticket.getContactNumber());
                acctNo.setText(accountno);
                oldMeterSerial.setText(ticket.getCurrentMeterNo());
                oldMeterBrand.setText(ticket.getCurrentMeterBrand());
                oldMeterReading.setText(ticket.getCurrentMeterReading());
                newMeterReading.setText(ticket.getNewMeterReading());
                newMeterSerial.setText(ticket.getNewMeterNo());
                newMeterBrand.setText(ticket.getNewMeterBrand());
                energizationDateTime.setText(ticket.getDateTimeLinemanExecuted());
                arrivalDateTime.setText(ticket.getDateTimeLinemanArrived());
                remarks.setText(ticket.getNotes());
                coordinates.setText(ticket.getGeoLocation());
                poleNumber.setText(ticket.getPoleNumber());
                oldSeal.setText(ticket.getCurrentMeterSeal());
                newSeal.setText(ticket.getNewMeterSeal());
                String entryDate = ticket.getCreated_at() != null ? ticket.getCreated_at() : "";
                entry.setText(entryDate.equals("") ? "-" : entryDate.substring(0,10));
                crewExecuted.setText(ticket.getLinemanCrewExecuted());

                assessment.setText(ticket.getStatus());

            }
        }
    }
}