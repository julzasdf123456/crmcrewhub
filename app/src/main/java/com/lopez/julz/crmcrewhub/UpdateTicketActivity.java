package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TicketsDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;

public class UpdateTicketActivity extends AppCompatActivity {

    public String ticketId;
    public Toolbar updateTicketsToolbar;

    public AppDatabase db;

    public Tickets ticket;

    public TextView accountName, ticketNo, ticketType, reason, address, contact, acctNo;

    public MaterialButton markTimeOfArrivalBtn, markTimeOfEnergizationBtn;
    public EditText arrivalDateTime, energizationDateTime, remarks;
    public RadioGroup assessment;

    public TextView oldMeterSerial, oldMeterBrand;
    public EditText oldMeterReading, newMeterSerial, newMeterBrand, newMeterReading;

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
        ticketType = findViewById(R.id.ticketType);
        reason = findViewById(R.id.reason);
        address = findViewById(R.id.address);
        contact = findViewById(R.id.contact);
        acctNo = findViewById(R.id.acctNo);
        markTimeOfArrivalBtn = findViewById(R.id.markTimeOfArrivalBtn);
        markTimeOfEnergizationBtn = findViewById(R.id.markTimeOfEnergizationBtn);
        arrivalDateTime = findViewById(R.id.arrivalDateTime);
        energizationDateTime = findViewById(R.id.energizationDateTime);
        remarks = findViewById(R.id.remarks);
        assessment = findViewById(R.id.assessment);
        oldMeterSerial = findViewById(R.id.oldMeterSerial);
        oldMeterBrand = findViewById(R.id.oldMeterBrand);
        oldMeterReading = findViewById(R.id.oldMeterReading);
        newMeterSerial = findViewById(R.id.newMeterNumber);
        newMeterBrand = findViewById(R.id.newMeterBrand);
        newMeterReading = findViewById(R.id.newMeterReading);

        new GetTicketDetails().execute();

        markTimeOfEnergizationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                energizationDateTime.setText(ObjectHelpers.getDateTime());
            }
        });

        markTimeOfArrivalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrivalDateTime.setText(ObjectHelpers.getDateTime());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ticket.setDateTimeLinemanArrived(arrivalDateTime.getText().toString());
            ticket.setDateTimeLinemanExecuted(energizationDateTime.getText().toString());
            ticket.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()));
            String status = ticket.getStatus();
            if (status != null && status.equals("Executed")) {
                ticket.setUploadStatus("UPLOADABLE");
            }
            ticket.setNotes(remarks.getText().toString());

            ticket.setCurrentMeterReading(oldMeterReading.getText().toString());
            ticket.setNewMeterBrand(newMeterBrand.getText().toString());
            ticket.setNewMeterNo(newMeterSerial.getText().toString());
            ticket.setNewMeterReading(newMeterReading.getText().toString());
            new UpdateTicket().execute(ticket);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class UpdateTicket extends AsyncTask<Tickets, Void, Void> {

        @Override
        protected Void doInBackground(Tickets... tickets) {
            try {
                if (tickets != null) {
                    Tickets ticketX = tickets[0];
                    db.ticketsDao().updateAll(ticketX);
                }
            } catch (Exception e) {
                Log.e("ERR_UPDT_TCKT", e.getMessage());
            }
            return null;
        }
    }

    public class GetTicketDetails extends AsyncTask<Void, Void, Void> {

        private String ticketName, consumeraddress, accountno;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();
                TicketRepositoriesDao ticketRepositories = db.ticketRepositoriesDao();
                TownsDao townsDao = db.townsDao();
                BarangaysDao barangaysDao = db.barangaysDao();

                ticket = ticketsDao.getOne(ticketId);
                TicketRepositories ticketSource = ticketRepositories.getOne(ticket.getTicket());

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

                if (ticket.getStatus() != null) {
                    if (ticket.getStatus().equals("Not Executed")) {
                        assessment.check(R.id.opsNotExecuted);
                    } else if(ticket.getStatus().equals("Executed")) {
                        assessment.check(R.id.opsExecuted);
                    }
                }
            }
        }
    }

    public int getAssessmentSelected(String recommendation) {
        if (recommendation != null) {
            if (recommendation.equals("Executed")) {
                return R.id.opsExecuted;
            } else {
                return R.id.opsNotExecuted;
            }
        } else {
            return 0;
        }
    }
}