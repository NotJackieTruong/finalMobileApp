package com.example.final_mobile_project;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import pub.devrel.easypermissions.EasyPermissions;

import static com.example.final_mobile_project.SplashActivity.REQUEST_AUTHORIZATION;


public class ComposeMail extends Activity implements EasyPermissions.PermissionCallbacks  {
    EditText editTo, editFrom, editSubject, editContent;
    ImageButton sendButton;
    MainActivity mainActivity;

    static ComposeMail instance;
    static String from = " ", to = " ", subject = " ", content = "", date = "", id= " ";
    boolean isMatch=false;
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
        //      update draft
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            from = bundle.getString("from");
            to = bundle.getString("to");
            subject = bundle.getString("subject");
            content = bundle.getString("content");
            id = bundle.getString("id");
            editTo.setText(to);
            editSubject.setText(subject);
            editContent.setText(content);
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get string from text view
//                String sFrom = editFrom.getText().toString();
//                String sTo = editTo.getText().toString();
//                String sSubject = editSubject.getText().toString();
//                String sContent = editContent.getText().toString();

                from=editFrom.getText().toString();
                to=editTo.getText().toString();
                subject = editSubject.getText().toString();
                content = editContent.getText().toString();
                if(from.length()>0 && to.length()>0 && subject.length()>0 && content.length()>0){
                    new AsyncSend(SplashActivity.mCredential).execute();
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
        System.out.println("ID RECEIVED FROM DRAFTDETAIL.JAVA: "+id);

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
        for(int i=0; i<SplashActivity.draftMails.size(); i++){
            if(id.equals(SplashActivity.draftMails.get(i).getId())){
                isMatch=true;

            }
        }
        if(isMatch==true){
            System.out.println("TRUE");
            new UpdateDraft(SplashActivity.mCredential).execute();
        } else{
            System.out.println("FALSE");
            new CreateDraft(SplashActivity.mCredential).execute();
        }


//        if(isMatch==true){
//
//        } else {
//            new CreateDraft(SplashActivity.mCredential).execute();
//        }

//        mainActivity = MainActivity.getInstance();
//
//        mainActivity.new CreateDraft(SplashActivity.mCredential).execute();
        finish();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public class AsyncSend extends AsyncTask<Void, Void, Boolean> {
        public Gmail mService = null;
        public ProgressDialog mProgress;

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


            from=editFrom.getText().toString();
            to=editTo.getText().toString();
            subject = editSubject.getText().toString();
            content = editContent.getText().toString();

        }

//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//            mProgress = new ProgressDialog(getApplicationContext());
//            mProgress.setMessage("Sending mail...");
//            mProgress.show();
//        }

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
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Send OK!", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(ComposeMail.this, MainActivity.class);
            startActivity(intent);
            finish();


        }
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

            from=editFrom.getText().toString();
            to=editTo.getText().toString();
            subject = editSubject.getText().toString();
            content = editContent.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                MimeMessage mimeMessage = GmailSetup.createEmail(to, from, subject, content);

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

            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Sent to Drafts", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class UpdateDraft extends AsyncTask<Void, Void, Boolean> {
        public Gmail mService = null;

        UpdateDraft(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Gmail.Builder(transport, jsonFactory, credential)
                    .setApplicationName("GmailAPI")
                    .build();
//            System.out.println("EMAIL ADDR: "+ mService.Users.GetProfile("me").Execute());
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                MimeMessage mimeMessage = GmailSetup.createEmail(to, from, subject, content);
                Message updatedMessage = new Message();
                updatedMessage.setRaw(encodeEmail(mimeMessage));
                System.out.println("UPDATED MESSAGE: "+ updatedMessage.getId());
                Draft updatedDraft = new Draft();
                updatedDraft.setMessage(updatedMessage);

                updatedDraft = mService.users().drafts().update("me", "16f43069583f1c03", updatedDraft)
                        .execute();
                System.out.println("UPDATED DRAFT ID: "+ updatedDraft.getId());
            } catch (UserRecoverableAuthIOException e) {

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public String encodeEmail(MimeMessage email) throws MessagingException, IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            email.writeTo(baos);
            return Base64.encodeBase64URLSafeString(baos.toString().getBytes());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
//            mProgress = new ProgressDialog(getApplicationContext());
//            mProgress.dismiss();
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Sent to Drafts", Toast.LENGTH_SHORT).show();
            }

        }

    }

}




