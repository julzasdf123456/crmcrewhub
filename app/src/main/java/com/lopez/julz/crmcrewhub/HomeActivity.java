package com.lopez.julz.crmcrewhub;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonParser;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.ConsumerInfo;
import com.lopez.julz.crmcrewhub.classes.HomeServiceConnectionsQueueAdapter;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.classes.TicketsHomeAdapter;
import com.lopez.julz.crmcrewhub.classes.Towns;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionsDao;
import com.lopez.julz.crmcrewhub.database.TicketRepositories;
import com.lopez.julz.crmcrewhub.database.TicketRepositoriesDao;
import com.lopez.julz.crmcrewhub.database.Tickets;
import com.lopez.julz.crmcrewhub.database.TicketsDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    // MAP
    public MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    public Style style;

    // MENU BUTTONS
    private ImageButton refreshAll, logout, settings, download, upload, archive;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public AppDatabase db;

    /**
     * SERVICE CONNECTIONS
     */
    public RecyclerView recyclerviewScHome;
    public List<ServiceConnections> serviceConnectionsList;
    public HomeServiceConnectionsQueueAdapter homeServiceConnectionsQueueAdapter;
    public TextView scQueueTitle;

    /**
     * TICKETS
     */
    public RecyclerView recyclerviewTicketsHome;
    public List<Tickets> ticketsList;
    public TicketsHomeAdapter ticketsHomeAdapter;
    public TextView ticketsQueueTitle;

    // mapbox markers from active queue
    public List<ServiceConnectionInspections> inspectionsList;

    public BottomSheetBehavior bottomSheetBehavior;

    public SymbolManager symbolManager;

    /**
     * CONFIG VALUES
     */
    public String userId, crew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_home);

        userId = getIntent().getExtras().getString("USERID");
        crew = getIntent().getExtras().getString("CREW");

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        // map
        mapView = (MapView) findViewById(R.id.mapViewForm);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // menu
        logout = (ImageButton) findViewById(R.id.logout);
        settings = (ImageButton) findViewById(R.id.settings);
        download = (ImageButton) findViewById(R.id.download);
        upload = (ImageButton) findViewById(R.id.upload);
        archive = (ImageButton) findViewById(R.id.archive);
        refreshAll = findViewById(R.id.refreshAll);

        /**
         * Service Connections
         */
        recyclerviewScHome = findViewById(R.id.recyclerviewScHome);
        scQueueTitle = findViewById(R.id.scQueueTitle);
        serviceConnectionsList = new ArrayList<>();
        inspectionsList = new ArrayList<>();
        homeServiceConnectionsQueueAdapter = new HomeServiceConnectionsQueueAdapter(serviceConnectionsList, this, userId);
        recyclerviewScHome.setAdapter(homeServiceConnectionsQueueAdapter);
        recyclerviewScHome.setLayoutManager(new LinearLayoutManager(this));


        /**
         * Tickets
         */
        ticketsQueueTitle = findViewById(R.id.ticketsQueueTitle);
        recyclerviewTicketsHome = findViewById(R.id.recyclerviewTicketsHome);
        ticketsList = new ArrayList<>();
        ticketsHomeAdapter = new TicketsHomeAdapter(ticketsList, this);
        recyclerviewTicketsHome.setAdapter(ticketsHomeAdapter);
        recyclerviewTicketsHome.setLayoutManager(new LinearLayoutManager(this));

        selectTab(scQueueTitle, ticketsQueueTitle, recyclerviewScHome, recyclerviewTicketsHome);

        // DOWNLOAD ASSETS
        downloadTownsBackground();
        downloadBarangaysBackground();
        downloadTicketRepos();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DownloadActivity.class);
                intent.putExtra("USERID", userId);
                intent.putExtra("CREW", crew);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        refreshAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchQueuedServiceConnections().execute();
                new FetchTickets().execute();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UploadActivity.class));
            }
        });

        scQueueTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(scQueueTitle, ticketsQueueTitle, recyclerviewScHome, recyclerviewTicketsHome);
            }
        });

        ticketsQueueTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(ticketsQueueTitle, scQueueTitle, recyclerviewTicketsHome, recyclerviewScHome);
            }
        });
    }

    public void selectTab(@NonNull TextView active, TextView previous, View showView, View hideView) {
        active.setTextColor(getResources().getColor(R.color.secondary_500));
        active.setTypeface(active.getTypeface(), Typeface.BOLD);
        previous.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        previous.setTypeface(null, Typeface.NORMAL);

        showView.setVisibility(View.VISIBLE);
        hideView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        showAdminPasswordDialog();
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
                    // initialize queues
                    new FetchQueuedServiceConnections().execute();
                    new FetchTickets().execute();

                    enableLocationComponent(style);

                    /**
                     * UPDATE SERVICE CONNECION LOCATION ON MAP
                     */
                    homeServiceConnectionsQueueAdapter.setShowOnMap(new HomeServiceConnectionsQueueAdapter.ShowOnMap() {
                        @Override
                        public void showLoc(int position) {
                            new MoveCameraMapToServiceConnection().execute(position);
                        }
                    });

                    /**
                     * UPDATE TICKET LOCATION ON MAP
                     */

                    ticketsHomeAdapter.setMapViewListener(new TicketsHomeAdapter.MapViewListener() {
                        @Override
                        public void changeLocation(int position) {
                            if (ticketsList.get(position).getGeoLocation() != null) {
                                Double ticketLat = Double.valueOf(ticketsList.get(position).getGeoLocation().split(",")[0]);
                                Double ticketLong = Double.valueOf(ticketsList.get(position).getGeoLocation().split(",")[1].trim());
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(ticketLat, ticketLong))      // Sets the center of the map to Mountain View
                                        .zoom(13)                      // Sets the tilt of the camera to 30 degrees
                                        .build();

                                if (mapboxMap != null) {
                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1200);
                                } else {
                                    Toast.makeText(HomeActivity.this, "Map is still loading, try again in a couple of seconds", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(HomeActivity.this, "No Geo Location recorded for this ticket", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("ERR_INIT_MAPBOX", e.getMessage());
        }
    }

    public void setStyle(Style style) {
        this.style = style;
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

    public void addTicketMarkers(Style style) {
        try {
            // ADD MARKERS
            symbolManager = new SymbolManager(mapView, mapboxMap, style);

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            if (ticketsList != null) {
                int totalMakers = ticketsList.size();

                for(int i=0; i<totalMakers; i++) {
                    Tickets tickets = ticketsList.get(i);
                    if (tickets.getGeoLocation() != null) {
                        Double ticketLat = Double.valueOf(ticketsList.get(i).getGeoLocation().split(",")[0].trim());
                        Double ticketLong = Double.valueOf(ticketsList.get(i).getGeoLocation().split(",")[1].trim());

                        SymbolOptions symbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(ticketLat, ticketLong))
                                .withData(new JsonParser().parse("{" +
                                        "'id' : '" + tickets.getId() + "'," +
                                        "'ticketId' : '" + tickets.getId() + "'}"))
                                .withIconImage("tw-provincial-2")
                                .withIconSize(.35f);

                        Symbol symbol = symbolManager.create(symbolOptions);
                    }
                }
            }

            symbolManager.addClickListener(new OnSymbolClickListener() {
                @Override
                public boolean onAnnotationClick(Symbol symbol) {
//                    Toast.makeText(HomeActivity.this, symbol.getData().getAsJsonObject().get("scId").getAsString(), Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(HomeActivity.this, UpdateServiceConnectionsActivity.class);
//                    intent.putExtra("SCID", symbol.getData().getAsJsonObject().get("scId").getAsString());
//                    intent.putExtra("INSP_ID", symbol.getData().getAsJsonObject().get("id").getAsString());
//                    intent.putExtra("USERID", userId);
//                    startActivity(intent);
                    return false;
                }
            });

            symbolManager.addLongClickListener(new OnSymbolLongClickListener() {
                @Override
                public boolean onAnnotationLongClick(Symbol symbol) {
                    new FetchTicketInfo().execute(symbol.getData().getAsJsonObject().get("ticketId").getAsString());
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addServiceConnectionMarkers(Style style) {
        try {
            // ADD MARKERS
            symbolManager = new SymbolManager(mapView, mapboxMap, style);

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            if (inspectionsList != null) {
                int totalMakers = inspectionsList.size();

                for(int i=0; i<totalMakers; i++) {
                    ServiceConnectionInspections insp = inspectionsList.get(i);
                    if (!getLatLongFromInspections(insp).equals("")) {
                        String lati = getLatLongFromInspections(insp).split(",")[0];
                        String longi = getLatLongFromInspections(insp).split(",")[1];

                        SymbolOptions symbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(Double.valueOf(lati), Double.valueOf(longi)))
                                .withData(new JsonParser().parse("{" +
                                                                            "'id' : '" + insp.getId() + "'," +
                                                                            "'scId' : '" + insp.getServiceConnectionId() + "'}"))
                                .withIconImage("tw-provincial-expy-2")
                                .withIconSize(.35f);

                        Symbol symbol = symbolManager.create(symbolOptions);
                    }
                }
            }

            symbolManager.addClickListener(new OnSymbolClickListener() {
                @Override
                public boolean onAnnotationClick(Symbol symbol) {
//                    Toast.makeText(HomeActivity.this, symbol.getData().getAsJsonObject().get("scId").getAsString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(HomeActivity.this, UpdateServiceConnectionsActivity.class);
                    intent.putExtra("SCID", symbol.getData().getAsJsonObject().get("scId").getAsString());
                    intent.putExtra("INSP_ID", symbol.getData().getAsJsonObject().get("id").getAsString());
                    intent.putExtra("USERID", userId);
                    startActivity(intent);
                    return false;
                }
            });

            symbolManager.addLongClickListener(new OnSymbolLongClickListener() {
                @Override
                public boolean onAnnotationLongClick(Symbol symbol) {
                    new FetchConsumerInfo().execute(symbol.getData().getAsJsonObject().get("scId").getAsString());
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        new FetchQueuedServiceConnections().execute();
        new FetchTickets().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    /**
     * SERVICE CONNECTIONS
     */
    public void downloadTownsBackground() {
        try {
            Call<List<Towns>> townsCall = requestPlaceHolder.getTowns();

            townsCall.enqueue(new Callback<List<Towns>>() {
                @Override
                public void onResponse(Call<List<Towns>> call, Response<List<Towns>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("ERR_DWNLD_TOWNS", response.message());
                    } else {
                        if (response.code() == 200) {
                            List<Towns> townsList = response.body();

                            new SaveTownsInBackground().execute(townsList);
                        } else {
                            Log.e("ERR_DWNLD_TOWNS", response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Towns>> call, Throwable t) {
                    Log.e("ERR_DWNLD_TOWNS", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_DWNLD_TOWNS", e.getMessage());
        }
    }

    public class SaveTownsInBackground extends AsyncTask<List<Towns>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("TOWN_DL_STARTING", "Town download starting");
        }

        @Override
        protected Void doInBackground(List<Towns>... lists) {
            List<Towns> towns = lists[0];
            int count = towns.size();

            TownsDao townsDao = db.townsDao();

            for (int i=0; i<count; i++) {
                Towns town = townsDao.getOne(towns.get(i).getId());

                if (town == null) {
                    townsDao.insertAll(towns.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("TOWN_DL_FINISHED", "Town download finished");
        }
    }

    public void downloadBarangaysBackground() {
        try {
            Call<List<Barangays>> barangaysCall = requestPlaceHolder.getBarangays();

            barangaysCall.enqueue(new Callback<List<Barangays>>() {
                @Override
                public void onResponse(Call<List<Barangays>> call, Response<List<Barangays>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("ERR_DL_BRGYS", response.message());
                    } else {
                        if (response.code() == 200) {
                            List<Barangays> barangaysList = response.body();

                            new SaveBarangaysInBackground().execute(barangaysList);
                        } else {
                            Log.e("ERR_DL_BRGYS", response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Barangays>> call, Throwable t) {
                    Log.e("ERR_DL_BRGYS", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_DL_BRGYS", e.getMessage());
        }
    }

    public class SaveBarangaysInBackground extends AsyncTask<List<Barangays>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.e("BRGY_DL_STARTED", "Barangays download started");
        }

        @Override
        protected Void doInBackground(List<Barangays>... lists) {
            List<Barangays> barangaysList = lists[0];
            int count = barangaysList.size();

            BarangaysDao barangaysDao = db.barangaysDao();

            for (int i=0; i<count; i++) {
                Barangays brgy = barangaysDao.getOne(barangaysList.get(i).getId());

                if (brgy == null) {
                    barangaysDao.insertAll(barangaysList.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("BRGY_DL_FINISHED", "Barangays download finished");
        }
    }

    public void showAdminPasswordDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Enter Admin Password");

            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            builder.setView(editText);

            builder.setCancelable(false);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editText.getText().toString().equals("2419")) {
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, "Admin password mismatch!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        } catch (Exception e) {
            Log.e("ERR_STRT_PWD_DLG", e.getMessage());
        }
    }

    public class FetchQueuedServiceConnections extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();

                List<ServiceConnections> serviceConnections = serviceConnectionsDao.getQueue();

                for(int i=0; i<serviceConnections.size(); i++) {
                    serviceConnectionsList.add(serviceConnections.get(i));

                    // FETCH INSPECTIONS FROM ACTIVE QUEUE
                    ServiceConnectionInspections inspection = serviceConnectionInspectionsDao.getOneByServiceConnectionId(serviceConnections.get(i).getId());
//                    Log.e("TEST", serviceConnections.get(i).getServiceAccountName());
                    inspectionsList.add(inspection);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serviceConnectionsList.clear();
            inspectionsList.clear();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            homeServiceConnectionsQueueAdapter.notifyDataSetChanged();
            scQueueTitle.setText("Service Connections (" + serviceConnectionsList.size() + ")");
            if (symbolManager != null) {
                symbolManager.deleteAll();
            }
            addServiceConnectionMarkers(style);
        }
    }

    public class FetchConsumerInfo extends AsyncTask<String, Void, Void> {

        private ConsumerInfo consumerInfo;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();

                ServiceConnections consumerData = serviceConnectionsDao.getOne(strings[0]);

                BarangaysDao barangaysDao = db.barangaysDao();
                TownsDao townsDao = db.townsDao();

                consumerInfo = new ConsumerInfo(consumerData.getServiceAccountName(),
                                            barangaysDao.getOne(consumerData.getBarangay()).getBarangay(),
                                            townsDao.getOne(consumerData.getTown()).getTown());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

            builder.setTitle(consumerInfo.getName());
            builder.setMessage(consumerInfo.getBarangay() + ", " + consumerInfo.getTown());

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }

    public class MoveCameraMapToServiceConnection extends AsyncTask<Integer, Void, Void> {

        private ServiceConnectionInspections inspections;

        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                inspections = db.serviceConnectionInspectionsDao().getOneByServiceConnectionId(serviceConnectionsList.get(integers[0]).getId());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (inspections != null) {
                if (getLatLongFromInspections(inspections) != null | !getLatLongFromInspections(inspections).equals("")) {
                    Double lati = Double.valueOf(getLatLongFromInspections(inspections).split(",")[0]);
                    Double longi = Double.valueOf(getLatLongFromInspections(inspections).split(",")[1]);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lati, longi))      // Sets the center of the map to Mountain View
                            .zoom(13)                      // Sets the tilt of the camera to 30 degrees
                            .build();

                    if (mapboxMap != null) {
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1200);
                    } else {
                        Toast.makeText(HomeActivity.this, "Map is still loading, try again in a couple of seconds", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "No Geo Location recorded for this ticket", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * TICKETS
     */
    public void downloadTicketRepos() {
        try {
            Call<List<TicketRepositories>> ticketRepCall = requestPlaceHolder.getTicketTypes();

            ticketRepCall.enqueue(new Callback<List<TicketRepositories>>() {
                @Override
                public void onResponse(Call<List<TicketRepositories>> call, Response<List<TicketRepositories>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("ERR_DWNL_TCKT_REP", response.message() + "\n" + response.errorBody());
                    } else {
                        if (response.code() == 200) {
                            new SaveTicketRepositories().execute(response.body());
                        } else {
                            Log.e("ERR_DWNL_TCKT_REP", response.message() + "\n" + response.errorBody());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<TicketRepositories>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.e("ERR_DWNL_TCKT_REP", e.getMessage());
        }
    }

    public class SaveTicketRepositories extends AsyncTask<List<TicketRepositories>, Void, Void> {

        @Override
        protected Void doInBackground(List<TicketRepositories>... lists) {
            try {
                if (lists != null) {
                    TicketRepositoriesDao ticketRepositoriesDao = db.ticketRepositoriesDao();

                    List<TicketRepositories> ticketRepositoriesList = lists[0];

                    int size = ticketRepositoriesList.size();

                    for (int i=0; i<size; i++) {
                        TicketRepositories ticketRepository = ticketRepositoriesDao.getOne(ticketRepositoriesList.get(i).getId());

                        if (ticketRepository != null) {

                        } else {
                            ticketRepositoriesDao.insertAll(ticketRepositoriesList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SV_TCT_REP", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("TICKT_SVD", "All ticket repositories downloaded");
        }
    }

    public class FetchTickets extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketsList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();

                ticketsList.addAll(ticketsDao.getAll());
            } catch (Exception e) {
                Log.e("ERR_FETCH_TCKTS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ticketsHomeAdapter.notifyDataSetChanged();
            ticketsQueueTitle.setText("Tickets/Complains (" + ticketsList.size() + ")");

            addTicketMarkers(style);
        }
    }

    public class FetchTicketInfo extends AsyncTask<String, Void, Void> {

        private Tickets ticket;
        private String town, barangay, ticketName, parentTicket;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                TicketsDao ticketsDao = db.ticketsDao();

                ticket = ticketsDao.getOne(strings[0]);
                town = db.townsDao().getOne(ticket.getTown()).getTown();
                barangay = db.barangaysDao().getOne(ticket.getBarangay()).getBarangay();

                TicketRepositories ticketRepository = db.ticketRepositoriesDao().getOne(ticket.getTicket());
                ticketName = ticketRepository.getName();
                parentTicket = db.ticketRepositoriesDao().getOne(ticketRepository.getParentTicket()).getName();
            } catch (Exception e) {
                Log.e("ERR_GET_TCKT_INF", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (ticket != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                builder.setTitle(ticket.getConsumerName());
                builder.setMessage(ticket.getSitio() + ", " + barangay + ", " + town + "\n" + parentTicket + " - " + ticketName);

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        }
    }

}