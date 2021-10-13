package com.lopez.julz.crmcrewhub;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.lopez.julz.crmcrewhub.api.RequestPlaceHolder;
import com.lopez.julz.crmcrewhub.api.RetrofitBuilder;
import com.lopez.julz.crmcrewhub.classes.Login;
import com.lopez.julz.crmcrewhub.classes.ObjectHelpers;
import com.lopez.julz.crmcrewhub.database.AppDatabase;
import com.lopez.julz.crmcrewhub.database.Users;
import com.lopez.julz.crmcrewhub.database.UsersDao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public EditText username, password;
    public Button loginBtn;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    // PERFORM ONLINE LOGIN
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
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showAdminPasswordDialog();
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
                Users users = new Users(strings[0], strings[1], strings[2]);
                usersDao.insertAll(users);
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
                    if (editText.getText().toString().equals("2419")) {
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
}