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
import com.google.gson.JsonParser;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;
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

    public String scId, inspId;

    public ServiceConnections serviceConnections;
    public ServiceConnectionInspections serviceConnectionInspections;

    public AppDatabase db;

    public MaterialButton markTimeOfArrivalBtn, markTimeOfEnergizationBtn;
    public EditText arrivalDateTime, energizationDateTime, remarks;
    public RadioGroup assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_connections);

        scId = getIntent().getExtras().getString("SCID");
        inspId = getIntent().getExtras().getString("INSP_ID");

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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            new SaveData().execute();
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
        new SaveData().execute();
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

        @Override
        protected Void doInBackground(String... strings) { // strings[0] = scId, strings[1] = inspId
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();

            serviceConnections = serviceConnectionsDao.getOne(strings[0]);
            serviceConnectionInspections = serviceConnectionInspectionsDao.getOneByServiceConnectionId(strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            //set title
            accountName.setText(serviceConnections.getServiceAccountName());

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
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();

                serviceConnections.setDateTimeLinemenArrived(arrivalDateTime.getText().toString());
                serviceConnections.setDateTimeOfEnergization(energizationDateTime.getText().toString());
                if (ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()) != null) {
                    serviceConnections.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(assessment, getWindow().getDecorView()));
                }

                serviceConnections.setNotes(remarks.getText().toString());

                serviceConnectionsDao.updateAll(serviceConnections);
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