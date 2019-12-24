package com.example.final_mobile_project;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.final_mobile_project.SplashActivity.PREF_ACCOUNT_NAME;


public class LogIn extends AppCompatActivity implements View.OnClickListener {
    SplashActivity splashActivity = SplashActivity.getInstance();

    private LinearLayout accSection;
    private Button signOutBtn;
    private TextView tvName, tvEmail;
//    static LogIn instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        instance=this;
        setContentView(R.layout.log_in_fragment);
        accSection = findViewById(R.id.acc_section);
        signOutBtn = findViewById(R.id.btn_change_acc);
        tvName = findViewById(R.id.acc_name);
        tvEmail = findViewById(R.id.acc_email);
        signOutBtn.setOnClickListener(this);
        String userSelectedName = splashActivity.getUserEmail(SplashActivity.mCredential.getSelectedAccountName());
        tvEmail.setText(userSelectedName);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_change_acc:
                changeAccount();

        }
    }

    public void changeAccount(){

        SplashActivity.mCredential.setSelectedAccountName(" ");
        startActivityForResult(
                SplashActivity.mCredential.newChooseAccountIntent(),
                SplashActivity.REQUEST_ACCOUNT_PICKER);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SplashActivity.REQUEST_ACCOUNT_PICKER:
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
                        SplashActivity.mCredential.setSelectedAccountName(accountName);
                        Toast.makeText(getApplicationContext(), "Changed account successfully!", Toast.LENGTH_SHORT).show();
                        String userSelectedName = splashActivity.getUserEmail(SplashActivity.mCredential.getSelectedAccountName());
                        tvEmail.setText(userSelectedName);
//                        Intent intent = new Intent(LogIn.this, MainActivity.class);
//                        intent.putExtra("Start", "Start reloading");
//                        startActivity(intent);
//                        finish();
                    }
                }
                break;
        }
    }
}


