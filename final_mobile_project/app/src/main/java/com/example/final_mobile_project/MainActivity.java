package com.example.final_mobile_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {
    private DrawerLayout drawerLayout;
    static MainActivity instance;
    static String from = " ", to = " ", subject = " ", content = "", date = "";
    Boolean isFirst = true;


    static GoogleAccountCredential mCredential;
    static ProgressDialog mProgress, progressDialog;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    public static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS, GmailScopes.MAIL_GOOGLE_COM};

    SplashActivity splashActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ONCREATE", "THIS IS ON CREATE");
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InboxFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inbox);
        }

        //compose button
        FloatingActionButton composeButton = (FloatingActionButton) findViewById(R.id.compose_button);
        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ComposeMail.class);
                startActivity(i);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait...");

    }

    public static MainActivity getInstance(){
        return instance;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ONSRESUME", "THIS IS ON RESUME");
        if(from.length()>0 && to.length()>0 && subject.length()>0 && content.length()>0){
            new MainActivity.AsyncSend(SplashActivity.mCredential).execute();
        }
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String value = bundle.getString("Start");
            if(value.equals("Start reloading")){
                Toast.makeText(getApplicationContext(), "Reloading to get newest mail...", Toast.LENGTH_LONG).show();
                splashActivity=SplashActivity.getInstance();
                splashActivity.getResultsFromApi();
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("ONRESTART", "THIS IS ON RESTART");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ONSTART", "THIS IS ON START");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_inbox:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InboxFragment()).commit();
                break;
            case R.id.nav_sent:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SentFragment()).commit();
                break;
            case R.id.nav_drafts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DraftsFragment()).commit();
                break;
            case R.id.log_in:
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
                break;
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LogIn()).commit();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                splashActivity=SplashActivity.getInstance();
                splashActivity.getResultsFromApi();

                break;
            case R.id.overflow_button:
                View overflowMenuView = findViewById(R.id.overflow_button);
                PopupMenu popupMenu = new PopupMenu(this, overflowMenuView);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.settings:
//                                Intent i = new Intent(getApplicationContext(), PrefActivity.class);
//                                startActivity(i);
                                return true;
                            case R.id.item1:
                                Toast.makeText(getApplicationContext(), "Item 1 is pressed", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.item2:
                                Toast.makeText(getApplicationContext(), "Item 2 is pressed", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    public class AsyncSend extends AsyncTask<Void, Void, Boolean> {
        private Gmail mService = null;

        AsyncSend(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Gmail.Builder(transport, jsonFactory, credential)
                    .setApplicationName("GmailAPI")
                    .build();
//            System.out.println("EMAIL ADDR: "+ mService.Users.GetProfile("me").Execute());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Send mail...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Message message = GmailSetup.sendMessage(mService, "me", GmailSetup.createEmail(to, from, subject, content));
                return (message != null);
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (aBoolean) {
                Toast.makeText(MainActivity.this, "Send OK!", Toast.LENGTH_SHORT).show();
            }
//            else {
//                if (from.length() != 0 && to.length() != 0 && subject.length() != 0 && content.length() != 0) {
//                    Toast.makeText(MainActivity.this, "Send fail!", Toast.LENGTH_SHORT).show();
//                }
//            }
            from = " ";
            to = " ";
            subject = " ";
            content = " ";


        }
    }
}
