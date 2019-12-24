package com.example.final_mobile_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity {
    LinearLayout inboxRow;
    static ArrayList<Mail> mails = new ArrayList<>();
    List<Mail> result;
    ProgressBar progressBar;
    LinearLayout inboxFragment;
    static SplashActivity instance;
    MainActivity mainActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        instance = this;
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        mProgress = new ProgressDialog(SplashActivity.this);
        mProgress.setMessage("Please wait ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    public static SplashActivity getInstance(){
        return instance;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getResultsFromApi();
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(SplashActivity.this, "Connection error!", Toast.LENGTH_LONG).show();
        } else {
            String userName = mCredential.getSelectedAccountName();
            System.out.println("Chosen user name(from result api): " + userName);
            new SplashActivity.MakeRequestTask(mCredential).execute();

        }

    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);

                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);

                        System.out.println("Account Name: " + accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        System.out.println("Network Info: " + networkInfo);
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                SplashActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    public String getUserEmail(String name){
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for(Account account: accounts){
            possibleEmails.add(account.name);
        }
        for(int i=0; i<possibleEmails.size(); i++){
            if(possibleEmails.get(i).equals(name)){
                System.out.println("USERNAME: "+possibleEmails.get(i));
                return possibleEmails.get(i);

            }
//            System.out.println("USERNAME: "+possibleEmails.get(i));
        }
        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;
        } else
            return null;
    }


    public class MakeRequestTask extends AsyncTask<Void, Integer, List<Mail>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        @Override
        protected List<Mail> doInBackground(Void... params) {

            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String filterEmail(String text) {
            String result =  "";
            boolean check = false;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '<') {
                    check = true;
                }
                if (c == '>') {
                    return  result;
                }
                if (check && c != '<') {
                    result = result + c;
                }
            }
            return text;
        }

        private List<Mail> getDataFromApi() throws IOException {
            List<Mail> result = new ArrayList<Mail>();
            try {
                List<Message> messages = GmailSetup.listAllInboxMessages(mService, "me", 20);

                for (int i = 0; i < messages.size(); i++) {
                    Message messageDetail = GmailSetup.getMessage(mService, "me", messages.get(i).getId(), "full");
                    String content = messageDetail.getSnippet();
                    String subject = "";
                    String from = "";
                    String to = "";
                    String date = "";
                    List<MessagePartHeader> messagePartHeader = messageDetail.getPayload().getHeaders();
                    for (int j = 0; j < messagePartHeader.size(); j++) {
                        if (messagePartHeader.get(j).getName().equals("Subject")) {
                            subject = messagePartHeader.get(j).getValue();
                        }
                        if (messagePartHeader.get(j).getName().equals("From")) {
                            from = messagePartHeader.get(j).getValue();
                        }
                        if (messagePartHeader.get(j).getName().equals("To")) {
                            to = messagePartHeader.get(j).getValue();
                        }
                        if (messagePartHeader.get(j).getName().equals("Date")) {
                            date = messagePartHeader.get(j).getValue();
                        }
                    }
                    if (subject.length() > 0 && content.length() > 0 && from.length() > 0 && to.length() > 0 && date.length() > 0) {
                        Mail mail = new Mail(subject, content, filterEmail(from), filterEmail(to), date);
                        result.add(mail);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Mail> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_SHORT).show();
            } else {
                mails.clear();
                mails.addAll(output);
            }

            Intent intent =new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            // close this activity
            finish();
            Toast.makeText(getApplicationContext(), "Loaded successfully", Toast.LENGTH_SHORT).show();
        }

    }


}
