package com.lopez.julz.crmcrewhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.classes.Towns;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.BarangaysDao;
import com.lopez.julz.crmcrewhub.database.TownsDao;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;


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

    // MENU BUTTONS
    private ImageButton logout, settings, download, upload, archive;

    public List<Barangays> barangaysList;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_home);

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

        // DOWNLOAD ASSETS
        downloadTownsBackground();
        downloadBarangaysBackground();

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
                startActivity(new Intent(HomeActivity.this, DownloadActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });
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
            mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } catch (Exception e) {
            Log.e("ERR_INIT_MAPBOX", e.getMessage());
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
}