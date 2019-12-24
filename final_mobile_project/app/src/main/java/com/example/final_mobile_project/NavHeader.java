package com.example.final_mobile_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NavHeader extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header);
        SplashActivity splashActivity = SplashActivity.getInstance();
        String userSelectedName = splashActivity.getUserEmail(SplashActivity.mCredential.getSelectedAccountName());
        System.out.println("FROM NAV HEADER: "+userSelectedName);
        TextView tvUserEmail = findViewById(R.id.user_email);
        tvUserEmail.setText(userSelectedName);
    }
}
