package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonParser;
import com.lopez.julz.crmcrewhub.classes.AlertHelpers;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.Crew;
import com.lopez.julz.crmcrewhub.database.CrewDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;
import com.lopez.julz.crmcrewhub.database.TimeFrames;
import com.lopez.julz.crmcrewhub.database.TimeFramesDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolLongClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;

public class UpdateServiceConnectionsActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    // MAP
    public MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    public Style style;

    public Toolbar updateToolbar;
    public TextView accountName; // title

    public String scId, inspId, userId;

    public ServiceConnections serviceConnections;
    public ServiceConnectionInspections serviceConnectionInspections;

    public AppDatabase db;

    public MaterialButton markTimeOfArrivalBtn, markTimeOfEnergizationBtn;
    public EditText arrivalDateTime, energizationDateTime, remarks, crewExecuted;
    public RadioGroup assessment;
    public FloatingActionButton editTimeOfArrivalBtn, editTimeOfEnergizationBtn;
    public ExtendedFloatingActionButton savebtn;

    public TextView id, address, meterNumber, meterBrand, meterSeal, verifier, acctType;

    private String crew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_connections);

        scId = getIntent().getExtras().getString("SCID");
        inspId = getIntent().getExtras().getString("INSP_ID");
        userId = getIntent().getExtras().getString("USERID");
        crew = getIntent().getExtras().getString("CREW");

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        // map
        mapView = (MapView) findViewById(R.id.mapViewUpdate);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        updateToolbar = findViewById(R.id.updateToolbar);
        setSupportActionBar(updateToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountName = findViewById(R.id.accountName);
        markTimeOfArrivalBtn = findViewById(R.id.markTimeOfArrivalBtn);
        markTimeOfEnergizationBtn = findViewById(R.id.markTimeOfEnergizationBtn);
        arrivalDateTime = findViewById(R.id.arrivalDateTime);
        energizationDateTime = findViewById(R.id.energizationDateTime);
        remarks = findViewById(R.id.remarks);
        assessment = findViewById(R.id.assessment);
        editTimeOfArrivalBtn = findViewById(R.id.editTimeOfArrivalBtn);
        editTimeOfEnergizationBtn = findViewById(R.id.editTimeOfEnergizationBtn);
        crewExecuted = findViewById(R.id.crewExecuted);
        savebtn = findViewById(R.id.savebtn);
        id = findViewById(R.id.id);
        address = findViewById(R.id.address);
        meterBrand = findViewById(R.id.meterBrand);
        meterNumber = findViewById(R.id.meterNumber);
        meterSeal = findViewById(R.id.meterSeal);
        verifier = findViewById(R.id.verifier);
        acctType = findViewById(R.id.acctType);

        markTimeOfArrivalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrivalDateTime.setText(ObjectHelpers.getDateTime());
            }
        });

        markTimeOfEnergizationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                energizationDateTime.setText(ObjectHelpers.getDateTime());
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

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()) != null ) {
                    if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()).equals("Energized") & (arrivalDateTime.getText().toString().isEmpty() | energizationDateTime.getText().toString().isEmpty())) {
                        AlertHelpers.showInfoDialog(UpdateServiceConnectionsActivity.this, "No Timestamp Provided", "You must provide 'TIME OF ARRIVAL' and 'TIME OF ENERGIZATION' since you selected the 'ENERGIZED OPTION'. " +
                                "To continue saving without the timestamps, select 'NOT ENERGIZED'.");
                    } else {
                        new SaveData().execute();
                    }
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()) != null ) {
                if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()).equals("Energized") & (arrivalDateTime.getText().toString().isEmpty() | energizationDateTime.getText().toString().isEmpty())) {
                    AlertHelpers.showInfoDialog(UpdateServiceConnectionsActivity.this, "No Timestamp Provided", "You must provide 'TIME OF ARRIVAL' and 'TIME OF ENERGIZATION' since you selected the 'ENERGIZED OPTION'. " +
                            "To continue saving without the timestamps, select 'NOT ENERGIZED'.");
                } else {
                    new SaveData().execute();
                }
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        try {
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(new Style.Builder()
                    .fromUri(getResources().getString(R.string.mapbox_style)), new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    setStyle(style);

                    new FetchDetails().execute(scId, inspId);

                    enableLocationComponent(style);
                }
            });
        } catch (Exception e) {
            Log.e("ERR_INIT_MAPBOX", e.getMessage());
        }
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        try {
            // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(this)) {

                // Get an instance of the component
                locationComponent = mapboxMap.getLocationComponent();

                // Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

                // Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);

                // Set the component's render mode
                locationComponent.setRenderMode(RenderMode.COMPASS);

            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        } catch (Exception e) {
            Log.e("ERR_LOAD_MAP", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()) != null ) {
            if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()).equals("Energized") & (arrivalDateTime.getText().toString().isEmpty() | energizationDateTime.getText().toString().isEmpty())) {
                AlertHelpers.showInfoDialog(UpdateServiceConnectionsActivity.this, "No Timestamp Provided", "You must provide 'TIME OF ARRIVAL' and 'TIME OF ENERGIZATION' since you selected the 'ENERGIZED OPTION'. " +
                        "To continue saving without the timestamps, select 'NOT ENERGIZED'.");
            } else {
                new SaveData().execute();
            }
        } else {
            finish();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public String getLatLongFromInspections(ServiceConnectionInspections inspections) {
        if (inspections.getGeoBuilding() != null) {
            return inspections.getGeoBuilding();
        } else if (inspections.getGeoMeteringPole() != null) {
            return inspections.getGeoMeteringPole();
        } else if (inspections.getGeoSEPole() != null) {
            return inspections.getGeoSEPole();
        } else if (inspections.getGeoTappingPole() != null) {
            return inspections.getGeoTappingPole();
        } else {
            return "";
        }
    }

    public void addMarkers(Style style, ServiceConnectionInspections insp) {
        try {
            // ADD MARKERS
            SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            if (!getLatLongFromInspections(insp).equals("")) {
                String lati = getLatLongFromInspections(insp).split(",")[0];
                String longi = getLatLongFromInspections(insp).split(",")[1];

                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(new LatLng(Double.valueOf(lati), Double.valueOf(longi)))
                        .withIconImage("place-black-24dp")
                        .withIconSize(1.3f);

                Symbol symbol = symbolManager.create(symbolOptions);

                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(Double.valueOf(lati), Double.valueOf(longi)))
                    .zoom(15)
                    .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FetchDetails extends AsyncTask<String, Void, Void> {

        private String loggedcrew, town, barangay;

        @Override
        protected Void doInBackground(String... strings) { // strings[0] = scId, strings[1] = inspId
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();

            serviceConnections = serviceConnectionsDao.getOne(strings[0]);
            serviceConnectionInspections = serviceConnectionInspectionsDao.getOneByServiceConnectionId(strings[0]);

            BarangaysDao barangaysDao = db.barangaysDao();
            TownsDao townsDao = db.townsDao();

            town = townsDao.getOne(serviceConnections.getTown()).getTown();
            barangay = barangaysDao.getOne(serviceConnections.getBarangay()).getBarangay();

            CrewDao crewDao = db.crewDao();
            Crew crewDetails = crewDao.getOne(crew);
            loggedcrew = crewDetails != null ? crewDetails.getStationName() : "-";

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            //set title
            accountName.setText(serviceConnections.getServiceAccountName());

            id.setText(serviceConnections.getId());
            address.setText((serviceConnections.getSitio() != null ? serviceConnections.getSitio() + ", " : "") + (barangay != null ? barangay + ", " : "") + (town != null ? town : ""));
            meterNumber.setText(serviceConnections.getMeterSerialNumber());
            meterBrand.setText(serviceConnections.getMeterBrand());
            meterSeal.setText(serviceConnections.getMeterSealNumber());
            verifier.setText(serviceConnections.getVerifier());
            acctType.setText(serviceConnections.getAccountTypeWord());
            if (serviceConnections.getLinemanCrewExecuted() != null) {
                crewExecuted.setText(serviceConnections.getLinemanCrewExecuted());
            } else {
                crewExecuted.setText(loggedcrew);
            }

            //set values
            arrivalDateTime.setText(serviceConnections.getDateTimeLinemenArrived());
            energizationDateTime.setText(serviceConnections.getDateTimeOfEnergization());

            if (serviceConnections.getStatus() != null) {
                if (serviceConnections.getStatus().equals("") | serviceConnections.getStatus().equals("Approved")) {

                } else {
                    assessment.check(getRecommendationSelected(serviceConnections.getStatus()));
                }
            }

            remarks.setText(serviceConnections.getNotes());

            // input marker
            addMarkers(style, serviceConnectionInspections);
        }
    }

    public int getRecommendationSelected(String recommendation) {
        if (recommendation != null) {
            if (recommendation.equals("Energized")) {
                return R.id.opsEnergized;
            } else {
                return R.id.opsNotEnergized;
            }
        } else {
            return 0;
        }
    }

    public class SaveData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();

                serviceConnections.setDateTimeLinemenArrived(arrivalDateTime.getText().toString());
                serviceConnections.setDateTimeOfEnergization(energizationDateTime.getText().toString());
                serviceConnections.setLinemanCrewExecuted(crewExecuted.getText().toString());
                if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()) != null) {
                    serviceConnections.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()));

                    if (serviceConnections.getStatus() != null && serviceConnections.getStatus().equals("Energized")) {
                        serviceConnections.setUploadStatus("UPLOADABLE");
                    }
                }
                serviceConnections.setNotes(remarks.getText().toString());

                serviceConnectionsDao.updateAll(serviceConnections);

                // CREATE TIMEFRAMES
                if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()) != null) {
                    TimeFramesDao timeFramesDao = db.timeFramesDao();

                    TimeFrames timeFrames = new TimeFrames();
                    timeFrames.setId(ObjectHelpers.getTimeInMillis());
                    timeFrames.setServiceConnectionId(serviceConnections.getId());
                    timeFrames.setUser(userId);
                    timeFrames.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()));
                    timeFrames.setCreated_at(ObjectHelpers.getDateTime());
                    timeFrames.setUpdated_at(ObjectHelpers.getDateTime());
                    timeFrames.setArrivalDate(serviceConnections.getDateTimeLinemenArrived());
                    timeFrames.setEnergizationDate(serviceConnections.getDateTimeOfEnergization());
                    timeFrames.setReason(serviceConnections.getNotes());
                    timeFrames.setIsUploaded("No");

                    timeFramesDao.insertAll(timeFrames);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            finish();
        }
    }
}