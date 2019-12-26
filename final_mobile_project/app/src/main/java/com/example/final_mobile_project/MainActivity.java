package com.example.final_mobile_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.view.ViewGroup;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.final_mobile_project.SplashActivity.mails;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {
    private DrawerLayout drawerLayout;
    static MainActivity instance;

    Boolean isFirst = true;



    static GoogleAccountCredential mCredential;
    static ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    public static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS, GmailScopes.MAIL_GOOGLE_COM};

    MainMakeRequest mainMakeRequest;


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


    }

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ONSRESUME", "THIS IS ON RESUME");
//        if (from.length() > 0 && to.length() > 0 && subject.length() > 0 && content.length() > 0) {
//            new MainActivity.AsyncSend(SplashActivity.mCredential).execute();
//        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String value = bundle.getString("Start");
//            if(value.equals("Start reloading")){
//                Toast.makeText(getApplicationContext(), "Reloading to get newest mail...", Toast.LENGTH_LONG).show();
//                mainMakeRequest = new MainMakeRequest();
//                mainMakeRequest.new MainRequest(SplashActivity.mCredential).execute();
//            }
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
    protected void onPause() {
        super.onPause();
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
        switch (item.getItemId()) {
            case R.id.refresh:
                mainMakeRequest = new MainMakeRequest();



                if(InboxFragment.isIsOpen()==true){
                    System.out.println("INBOX_FRAGMENT");
                    mainMakeRequest.new MainRequest(SplashActivity.mCredential).execute();
//                    InboxFragment inboxFragment = new InboxFragment();
//                    inboxFragment.new MakeRequestTask(SplashActivity.mCredential).execute();

                } else if(SentFragment.isIsOpen()==true){
                    System.out.println("SENT_FRAGMENT");
                    mainMakeRequest.new MainSentRequest(SplashActivity.mCredential).execute();
//                    SentFragment sentFragment = new SentFragment();
//                    sentFragment.new MakeSentRequestTask(SplashActivity.mCredential).execute();

                } else if(DraftsFragment.isIsOpen()==true){
                    System.out.println("DRAFT_FRAGMENT");
                    mainMakeRequest.new MainDraftRequest(SplashActivity.mCredential).execute();
//                    DraftsFragment draftsFragment = new DraftsFragment();
//                    DraftsFragment.MainDraftMakeRequest mainDraftMakeRequest = draftsFragment.new MainDraftMakeRequest();
//                    mainDraftMakeRequest.new MainDraftRequest(SplashActivity.mCredential).execute();
//                    DraftsFragment draftsFragment = new DraftsFragment();
//                    draftsFragment.new MakeDraftRequestTask(SplashActivity.mCredential).execute();

                }




                break;
            case R.id.overflow_button:
                View overflowMenuView = findViewById(R.id.overflow_button);
                PopupMenu popupMenu = new PopupMenu(this, overflowMenuView);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
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

    public class MainMakeRequest extends SplashActivity {

        private class MainRequest extends MakeRequestTask {

            MainRequest(GoogleAccountCredential credential) {
                super(credential);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Please wait...");
                mProgress.show();
            }

            @Override
            protected void onPostExecute(List<Mail> output) {
                mProgress.hide();
                if (output == null || output.size() == 0) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    mails.clear();
                    mails.addAll(output);
                }

//               Toast.makeText(MainActivity.this, "Loaded successfully", Toast.LENGTH_SHORT).show();
            }

        }

        private class MainSentRequest extends MakeSentRequestTask {

            MainSentRequest(GoogleAccountCredential credential) {
                super(credential);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Please wait...");
                mProgress.show();
            }

            @Override
            protected void onPostExecute(List<Mail> output) {
                super.onPostExecute(output);
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                MainActivity.this.startActivity(intent);
                finish();
            }
        }

        private class MainDraftRequest extends MakeDraftRequestTask {
            boolean isFinished;
            MainDraftRequest(GoogleAccountCredential credential) {
                super(credential);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Please wait...");
                mProgress.show();
            }
            @Override
            protected void onPostExecute(List<Mail> output) {
                mProgress.hide();
                if (output == null || output.size() == 0) {
//                    Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    draftMails.clear();
                    draftMails.addAll(output);
                    DraftsFragment draftsFragment = new DraftsFragment();
                    draftsFragment.updateUI(SplashActivity.draftMails);
//                    DraftsFragment draftsFragment = ((DraftsFragment)getSupportFragmentManager().findFragmentById(R.id.inbox_fragment));
//                    ((DraftsFragment)draftsFragment).updateUI(draftMails);
//                    DraftsFragment draftsFragment = new DraftsFragment();
//                    draftsFragment.updateUI(draftMails);

                }

            }
        }


    }





}

