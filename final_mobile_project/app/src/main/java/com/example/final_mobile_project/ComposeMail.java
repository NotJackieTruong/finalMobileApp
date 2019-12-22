package com.example.final_mobile_project;

import android.app.Activity;

import android.os.Bundle;

import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class ComposeMail extends Activity implements EasyPermissions.PermissionCallbacks  {
    EditText editTo, editFrom, editSubject, editContent;
    ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_mail);

        MainActivity mainActivity = MainActivity.getInstance();
        SplashActivity splashActivity = SplashActivity.getInstance();
        editFrom = (EditText)findViewById(R.id.user_email);
        editFrom.setText(splashActivity.getUserEmail(SplashActivity.mCredential.getSelectedAccountName()));
        editTo = (EditText)findViewById(R.id.receiver_email);
        editSubject = (EditText)findViewById(R.id.topic);
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




}




