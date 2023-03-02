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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.Crew;
import com.lopez.julz.crmcrewhub.database.CrewDao;
import com.lopez.julz.crmcrewhub.database.StationCrews;
import com.lopez.julz.crmcrewhub.database.StationCrewsDao;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TicketsDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateTicketActivity extends AppCompatActivity {

    public String ticketId;
    public Toolbar updateTicketsToolbar;

    public AppDatabase db;

    public Tickets ticket;

    public TextView accountName, ticketNo, ticketType, reason, address, contact, acctNo, poleNumber, entry;

    public MaterialButton markTimeOfArrivalBtn, markTimeOfEnergizationBtn;
    public EditText arrivalDateTime, energizationDateTime, remarks, coordinates;
    public RadioGroup assessment;
    public RadioButton forAveraging;
    public FloatingActionButton editTimeOfArrivalBtn, editTimeOfEnergizationBtn;

    public EditText oldMeterSerial, oldMeterBrand;
    public EditText oldMeterReading, newMeterSerial, newMeterBrand, newMeterReading, oldSeal, newSeal;
    public Spinner crewExecuted;

    private String crew;

    ArrayAdapter crewAdapter;

    private ExtendedFloatingActionButton savebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ticket);

        ticketId = getIntent().getExtras().getString("ID");
        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        crew = getIntent().getExtras().getString("CREW");

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
        forAveraging = findViewById(R.id.forAveraging);
        coordinates = findViewById(R.id.coordinates);
        poleNumber = findViewById(R.id.poleNumber);
        editTimeOfArrivalBtn = findViewById(R.id.editTimeOfArrivalBtn);
        editTimeOfEnergizationBtn = findViewById(R.id.editTimeOfEnergizationBtn);
        crewExecuted = findViewById(R.id.crewExecuted);
        savebtn = findViewById(R.id.savebtn);
        oldSeal = findViewById(R.id.oldSeal);
        newSeal = findViewById(R.id.newSeal);
        entry = findViewById(R.id.entry);

        new GetCrews().execute();

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

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticket.setDateTimeLinemanArrived(arrivalDateTime.getText().toString());
                ticket.setDateTimeLinemanExecuted(energizationDateTime.getText().toString());
                ticket.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()));
                String status = ticket.getStatus();
                if (status != null) {
                    ticket.setUploadStatus("UPLOADABLE");
                }
                ticket.setNotes(remarks.getText().toString());

                if (forAveraging.isChecked()) {
                    ticket.setPercentError("FOR AVERAGING");
                }

                ticket.setCurrentMeterBrand(oldMeterBrand.getText().toString());
                ticket.setCurrentMeterNo(oldMeterSerial.getText().toString());
                ticket.setCurrentMeterReading(oldMeterReading.getText().toString());
                ticket.setNewMeterBrand(newMeterBrand.getText().toString());
                ticket.setNewMeterNo(newMeterSerial.getText().toString());
                ticket.setNewMeterReading(newMeterReading.getText().toString());
                ticket.setGeoLocation(coordinates.getText().toString());
                ticket.setLinemanCrewExecuted(crewExecuted.getSelectedItem().toString());
                ticket.setCurrentMeterSeal(oldSeal.getText().toString());
                ticket.setNewMeterSeal(newSeal.getText().toString());
                new UpdateTicket().execute(ticket);
                finish();
            }
        });

        editTimeOfArrivalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    /**
                                     * GET SELECTED DAY
                                     */
                                    String time = ObjectHelpers.getDateFromDatePicker(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);

                                    /*
                                     * INITIALIZE TIME
                                     */
                                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                    arrivalDateTime.setText(time + " " + hourOfDay + ":" + minute + ":" + second);
                                                }
                                            },
                                            now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            true
                                    );

                                    tpd.setVersion(TimePickerDialog.Version.VERSION_2);
                                    tpd.setOkText("SELECT");
                                    tpd.setCancelText("CLOSE");
                                    tpd.show(getSupportFragmentManager(), "Select Time");
                                }
                            },
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                    dpd.setOkText("SELECT");
                    dpd.setCancelText("CLOSE");
                    dpd.show(getSupportFragmentManager(), "Select Date");
                } catch (Exception e) {
                    Log.e("ERR_DT_PCKR", e.getMessage());
                }
            }
        });

        editTimeOfEnergizationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    /**
                                     * GET SELECTED DAY
                                     */
                                    String time = ObjectHelpers.getDateFromDatePicker(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);

                                    /*
                                     * INITIALIZE TIME
                                     */
                                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                    energizationDateTime.setText(time + " " + hourOfDay + ":" + minute + ":" + second);
                                                }
                                            },
                                            now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            true
                                    );

                                    tpd.setVersion(TimePickerDialog.Version.VERSION_2);
                                    tpd.setOkText("SELECT");
                                    tpd.setCancelText("CLOSE");
                                    tpd.show(getSupportFragmentManager(), "Select Time");
                                }
                            },
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                    dpd.setOkText("SELECT");
                    dpd.setCancelText("CLOSE");
                    dpd.show(getSupportFragmentManager(), "Select Date");
                } catch (Exception e) {
                    Log.e("ERR_DT_PCKR", e.getMessage());
                }
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
            if (status != null) {
                ticket.setUploadStatus("UPLOADABLE");
            }
            ticket.setNotes(remarks.getText().toString());

            if (forAveraging.isChecked()) {
                ticket.setPercentError("FOR AVERAGING");
            }

            ticket.setCurrentMeterBrand(oldMeterBrand.getText().toString());
            ticket.setCurrentMeterNo(oldMeterSerial.getText().toString());
            ticket.setCurrentMeterReading(oldMeterReading.getText().toString());
            ticket.setNewMeterBrand(newMeterBrand.getText().toString());
            ticket.setNewMeterNo(newMeterSerial.getText().toString());
            ticket.setNewMeterReading(newMeterReading.getText().toString());
            ticket.setGeoLocation(coordinates.getText().toString());
            ticket.setLinemanCrewExecuted(crewExecuted.getSelectedItem().toString());
            ticket.setCurrentMeterSeal(oldSeal.getText().toString());
            ticket.setNewMeterSeal(newSeal.getText().toString());
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

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(UpdateTicketActivity.this, "Ticket updated and saved!", Toast.LENGTH_SHORT).show();
        }
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
                if (ticket.getLinemanCrewExecuted() != null) {
                    crewExecuted.setSelection(crewAdapter.getPosition(ticket.getLinemanCrewExecuted()));
                } else {
//                    crewExecuted.setText(loggedcrew);
                    crewExecuted.setSelection(crewAdapter.getPosition(loggedcrew));
                }

                if (ticket.getStatus() != null) {
                    if (ticket.getStatus().equals("Not Executed")) {
                        assessment.check(R.id.opsNotExecuted);
                    } else if(ticket.getStatus().equals("Executed")) {
                        assessment.check(R.id.opsExecuted);
                    } else if(ticket.getStatus().equals("Acted")) {
                        assessment.check(R.id.opsActed);
                    }
                }

                if (ticket.getPercentError() != null && ticket.getPercentError().equals("FOR AVERAGING")) {
                    forAveraging.setChecked(true);
                } else {
                    forAveraging.setChecked(false);
                }
            }
        }
    }

    public int getAssessmentSelected(String recommendation) {
        if (recommendation != null) {
            if (recommendation.equals("Executed")) {
                return R.id.opsExecuted;
            } else if (recommendation.equals("Acted")) {
                return R.id.opsActed;
            } else {
                return R.id.opsNotExecuted;
            }
        } else {
            return 0;
        }
    }

    public class GetCrews extends AsyncTask<String, Void, Void> {

        public List<StationCrews> stationCrewsList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stationCrewsList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                stationCrewsList.addAll(db.stationCrewsDao().getAll());
            } catch (Exception e) {
                Log.e("ERR_GET_CREWS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            try {
                List<String> crews = new ArrayList<>();
                for(int i=0; i<stationCrewsList.size(); i++) {
                    crews.add(stationCrewsList.get(i).getStationName());
                }

                crewAdapter = new ArrayAdapter(UpdateTicketActivity.this, R.layout.spinner_item, crews.toArray());
                crewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                crewExecuted.setAdapter(crewAdapter);
                new GetTicketDetails().execute();
            } catch (Exception e) {
                Log.e("ERR_DSP_CREWS", e.getMessage());
            }
        }
    }
}