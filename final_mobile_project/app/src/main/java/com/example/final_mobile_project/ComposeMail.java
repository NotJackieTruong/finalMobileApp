package com.example.final_mobile_project;

import android.app.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;

import java.util.List;

import javax.mail.internet.MimeMessage;

import pub.devrel.easypermissions.EasyPermissions;


public class ComposeMail extends Activity implements EasyPermissions.PermissionCallbacks  {
    EditText editTo, editFrom, editSubject, editContent;
    ImageButton sendButton;
    MainActivity mainActivity;
    static ProgressDialog mProgress;
    static ComposeMail instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_mail);
        instance = this;
        mainActivity = MainActivity.getInstance();
        SplashActivity splashActivity = SplashActivity.getInstance();
        editFrom = (EditText)findViewById(R.id.user_email);
        editFrom.setText(splashActivity.getUserEmail(SplashActivity.mCredential.getSelectedAccountName()));
        editTo = (EditText)findViewById(R.id.receiver_email);
        editSubject = (EditText)findViewById(R.id.subject);
        editContent = (EditText)findViewById(R.id.content);
        sendButton = (ImageButton)findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get string from text view
//                String sFrom = editFrom.getText().toString();
//                String sTo = editTo.getText().toString();
//                String sSubject = editSubject.getText().toString();
//                String sContent = editContent.getText().toString();

                MainActivity.from=editFrom.getText().toString();
                MainActivity.to=editTo.getText().toString();
                MainActivity.subject = editSubject.getText().toString();
                MainActivity.content = editContent.getText().toString();
                if(MainActivity.from.length()>0 && MainActivity.to.length()>0 && MainActivity.subject.length()>0 && MainActivity.content.length()>0){
                    finish();
                } else{
                    Toast.makeText(ComposeMail.this, "Cannot send!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public static ComposeMail getInstance() {
        return instance;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new CreateDraft(SplashActivity.mCredential).execute();
//        mainActivity = MainActivity.getInstance();
//
//        mainActivity.new CreateDraft(SplashActivity.mCredential).execute();
        finish();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public class CreateDraft extends AsyncTask<Void, Void, Boolean> {
        public Gmail mService = null;

        CreateDraft(GoogleAccountCredential credential) {
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
//            mProgress = new ProgressDialog(ComposeMail.this);
//            mProgress.setMessage("Sending to Drafts...");
//            mProgress.show();

            MainActivity.from=editFrom.getText().toString();
            MainActivity.to=editTo.getText().toString();
            MainActivity.subject = editSubject.getText().toString();
            MainActivity.content = editContent.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                MimeMessage mimeMessage = GmailSetup.createEmail(MainActivity.to, MainActivity.from, MainActivity.subject, MainActivity.content);
                Draft messageDraft = GmailSetup.createDraft(mService, "me", mimeMessage);
                System.out.println("DRAFT: "+messageDraft);
//                return (messageDraft != null);
            } catch (UserRecoverableAuthIOException e) {
//                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
//            mProgress = new ProgressDialog(getApplicationContext());
//            mProgress.dismiss();
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Sent to Drafts", Toast.LENGTH_SHORT).show();
            }
//            else {
//                if (from.length() != 0 && to.length() != 0 && subject.length() != 0 && content.length() != 0) {
//                    Toast.makeText(MainActivity.this, "Send fail!", Toast.LENGTH_SHORT).show();
//                }
//            }
//            from = " ";
//            to = " ";
//            subject = " ";
//            content = " ";


        }

    }

}




