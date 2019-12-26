package com.example.final_mobile_project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MessageDetail extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        Bundle b = getIntent().getExtras();

        String messTopic = b.getString("subject");
        TextView messtopic= (TextView)findViewById(R.id.subject);
        messtopic.setText(messTopic);

        String messContent = b.getString("content");
        TextView messcontent = (TextView)findViewById(R.id.message_content);
        messcontent.setText(messContent);
    }
}
