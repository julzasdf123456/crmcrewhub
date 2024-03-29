package com.lopez.julz.crmcrewhub;

import static com.lopez.julz.crmcrewhub.classes.ObjectHelpers.hasPermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.Login;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppConfig;
import com.lopez.julz.crmcrewhub.database.AppConfigDao;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.Crew;
import com.lopez.julz.crmcrewhub.database.CrewDao;
import com.lopez.julz.crmcrewhub.database.Settings;
import com.lopez.julz.crmcrewhub.database.StationCrews;
import com.lopez.julz.crmcrewhub.database.StationCrewsDao;
import com.lopez.julz.crmcrewhub.database.Users;
import com.lopez.julz.crmcrewhub.database.UsersDao;

import java.lang.reflect.Method;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public EditText username, password;
    public Button loginBtn;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    private MaterialButton settingsBtn, connectionSettingsBtn;

    public AppDatabase db;

    public TextView configErrorMessage;

    public Settings settings;

    private String CREW = null, STATION = null;

    private static final int WIFI_PERMISSION = 100;
    private static final int STORAGE_PERMISSION_READ = 101;
    private static final int STORAGE_PERMISSION_WRITE = 102;
    private static final int CAMERA = 103;
    private static final int LOCATION = 104;
    private static final int PHONE = 105;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        settingsBtn = findViewById(R.id.settingsBtn);
        connectionSettingsBtn = findViewById(R.id.connectionSettingsBtn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, LoginSettingsActivity.class));
            }
        });

        connectionSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ConnectionSettingsActivity.class));
            }
        });
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) == PackageManager.PERMISSION_DENIED) {             // Requesting the permission
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] { permission }, requestCode);
        } else {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new FetchSettings().execute();
    }

    private void login() {
        Login login = new Login(username.getText().toString(), password.getText().toString());

        Call<Login> call = requestPlaceHolder.login(login);

//        login_progressbar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (!response.isSuccessful()) {
//                    login_progressbar.setVisibility(View.INVISIBLE);
                    if (response.code() == 401) {
                        Snackbar.make(username, "The username and password you entered doesn't match our records. Kindly review and try again.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(username,  "Failed to login. Try again later.", Snackbar.LENGTH_LONG).show();
                    }
                    Log.e("LOGIN_ERR", "Code: " + response.code() + "\nMessage: " + response.message());
                } else {
                    if (response.code() == 200) {
                        new SaveUser().execute(response.body().getId(), username.getText().toString(), password.getText().toString());
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("USERID", response.body().getId());
                        intent.putExtra("CREW", CREW);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("LOGIN_FAILED", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
//                login_progressbar.setVisibility(View.INVISIBLE);
//                AlertBuilders.infoDialog(LoginActivity.this, "Internal Server Error", "Failed to login. Try again later.");
                Log.e("ERR", t.getLocalizedMessage());
            }
        });
    }

    public class SaveUser extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) { // 0 = id, 1 = username, 2 = password

            UsersDao usersDao = db.usersDao();
            Users existing = usersDao.getOne(strings[1], strings[2]);

            if (existing == null) {
                Users users = new Users(strings[0], strings[1], strings[2], "YES");
                users.setLoggedIn("YES");
                usersDao.insertAll(users);
            } else {
                existing.setLoggedIn("YES");
                usersDao.updateAll(existing);
            }

            return null;
        }
    }

    public class LoginOffline extends AsyncTask<String, Void, Void> {

        boolean doesUserExists = false;
        String userid = "";

        @Override
        protected Void doInBackground(String... strings) {
            UsersDao usersDao = db.usersDao();
            Users existing = usersDao.getOne(strings[0], strings[1]);

            if (existing == null) {
                doesUserExists = false;
            } else {
                doesUserExists = true;
                userid = existing.getId();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (doesUserExists) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("USERID", userid);
                intent.putExtra("CREW", CREW);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "User not found on this device!", Toast.LENGTH_LONG).show();
            }
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
                    if (editText.getText().toString().equals(getResources().getString(R.string.admin_pw))) {
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Admin password mismatch!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        } catch (Exception e) {
            Log.e("ERR_STRT_PWD_DLG", e.getMessage());
        }
    }

    /**
     * Download Crew
     */
    public void getAllCrew() {
        try {
            Call<List<Crew>> crewCall = requestPlaceHolder.getAllCrew();

            crewCall.enqueue(new Callback<List<Crew>>() {
                @Override
                public void onResponse(Call<List<Crew>> call, Response<List<Crew>> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            new SaveCrew().execute(response.body());
                        } else {
                            Log.e("ER_GET_CREW", response.errorBody() + "" + response.raw());
                        }
                    } else {
                        Log.e("ER_GET_CREW", response.errorBody() + "" + response.raw());
                    }
                }

                @Override
                public void onFailure(Call<List<Crew>> call, Throwable t) {
                    Log.e("ER_GET_CREW", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ER_GET_CREW", e.getMessage());
        }
    }

    public void getStationCrews(String station) {
        try {
            Call<List<StationCrews>> listCall = requestPlaceHolder.getCrewsFromStation(station);

            listCall.enqueue(new Callback<List<StationCrews>>() {
                @Override
                public void onResponse(Call<List<StationCrews>> call, Response<List<StationCrews>> response) {
                    if(response.isSuccessful()) {
                        new SaveCrewFromStations().execute(response.body());
                    } else {
                        Log.e("ERR_GET_ST_CRW", response.message() + " , " + response.raw());
                    }
                }

                @Override
                public void onFailure(Call<List<StationCrews>> call, Throwable t) {
                    Log.e("ERR_GET_ST_CRW", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_GET_ST_CRW", e.getMessage());
        }
    }

    public class SaveCrew extends AsyncTask<List<Crew>, Void, Void> {

        @Override
        protected Void doInBackground(List<Crew>... lists) {
            try {
                CrewDao crewDao = db.crewDao();

                if (lists != null) {
                    List<Crew> crewList = lists[0];

                    for (int i=0; i<crewList.size(); i++) {
                        Crew crew = crewDao.getOne(crewList.get(i).getId());

                        if (crew != null) {
                            // skip
                        } else {
                            crewDao.insertAll(crewList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SV_CREW", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("ALL_CREW_DWNLDD", "All crew downloaded");
        }
    }

    public class SaveCrewFromStations extends AsyncTask<List<StationCrews>, Void, Void> {

        @Override
        protected Void doInBackground(List<StationCrews>... lists) {
            try {
                StationCrewsDao stationCrewsDao = db.stationCrewsDao();

                if (lists != null) {
                    List<StationCrews> crewList = lists[0];

                    for (int i=0; i<crewList.size(); i++) {
                        StationCrews crew = stationCrewsDao.getOne(crewList.get(i).getId());

                        if (crew != null) {
                            // skip
                        } else {
                            stationCrewsDao.insertAll(crewList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SV_CREW", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("ALL_CREW_ST_DWNLD", "All crew stations downloaded");
        }
    }

    /**
     * FETCH APP CONFIG
     */
    public class GetAppConfig extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                AppConfigDao appConfigDao = db.appConfigDao();

                AppConfig appConfig = appConfigDao.getConfig();

                CREW = appConfig.getDeviceStation();
                STATION = appConfig.getCrewLeader();
            } catch (Exception e) {
                Log.e("ERR_GET_CONFIG", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (CREW != null) {
                loginBtn.setEnabled(true);
                configErrorMessage.setVisibility(View.GONE);

                getStationCrews(STATION);
            } else {
                loginBtn.setEnabled(false);
                configErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public class FetchSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                settings = db.settingsDao().getSettings();
            } catch (Exception e) {
                Log.e("ERR_FETCH_SETTINGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (settings != null) {
                retrofitBuilder = new RetrofitBuilder(settings.getDefaultServer());
                requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

                configErrorMessage = findViewById(R.id.configErrorMessage);
                username = (EditText) findViewById(R.id.username);
                password = (EditText) findViewById(R.id.password);
                loginBtn = (Button) findViewById(R.id.login);
                loginBtn.setEnabled(false);
                configErrorMessage.setVisibility(View.GONE);

                getAllCrew();

                new GetAppConfig().execute();

                new CommenceAutoLogin().execute();
            } else {
                startActivity(new Intent(LoginActivity.this, ConnectionSettingsActivity.class));
            }
        }
    }

    public class CommenceAutoLogin extends AsyncTask<Void, Void, Void> {

        boolean doesUserExists = false;
        String userid = "";
        String usernameT, passwordT;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                UsersDao usersDao = db.usersDao();
                Users existing = usersDao.getFirst();

                AppConfigDao appConfigDao = db.appConfigDao();

                AppConfig appConfig = appConfigDao.getConfig();

                CREW = appConfig.getDeviceStation();

                if (existing == null) {
                    doesUserExists = false;
                } else {
                    if (existing.getLoggedIn() != null && existing.getLoggedIn().equals("YES")) {
                        doesUserExists = true;
                        userid = existing.getId();
                        usernameT = existing.getUsername();
                        passwordT = existing.getPassword();
                    } else {
                        doesUserExists = false;
                    }

                }
            } catch (Exception e) {
                Log.e("ERR_AUTO_LGN", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (doesUserExists) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("CREW", CREW);
                intent.putExtra("USERID", userid);
                startActivity(intent);
                finish();
            } else {
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        // CHECK MOBILE DATA
                        boolean mobileDataEnabled = false;
                        try {
                            Class cmClass = Class.forName(connManager.getClass().getName());
                            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                            method.setAccessible(true); // Make the method callable
                            // get the setting for "mobile data"
                            mobileDataEnabled = (Boolean)method.invoke(connManager);
                        } catch (Exception e) {
                            // Some problem accessible private API
                            // TODO do whatever error handling you want here
                        }

                        if (mWifi.isConnected()) {
                            // PERFORM ONLINE LOGIN USING WIFI
                            if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                                Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                            } else {
                                login();
                            }
                        } else {
                            if (mobileDataEnabled) {
                                // PERFORM ONLINE LOGIN USING MOBILE DATA
                                if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                                    Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                                } else {
                                    login();
                                }
                            } else {
                                // PERFORM OFFLINE LOGIN
                                if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                                    Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                                } else {
                                    new LoginOffline().execute(username.getText().toString(), password.getText().toString());
                                }
                            }

                        }
                    }
                });
            }
        }
    }
}