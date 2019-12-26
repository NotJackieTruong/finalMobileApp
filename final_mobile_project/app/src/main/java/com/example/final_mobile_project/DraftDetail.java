package com.example.final_mobile_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DraftDetail extends AppCompatActivity {
    String messFrom, messTo, messSubject, messContent, messId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draft_detail);
        Bundle b = getIntent().getExtras();
        messFrom = b.getString("from");
        messTo = b.getString("to");
        messSubject = b.getString("subject");
        messContent = b.getString("content");
        messId = b.getString("id");


        // display UI
        TextView messtopic= (TextView)findViewById(R.id.subject);
        messtopic.setText(messSubject);
        TextView messcontent = (TextView)findViewById(R.id.message_content);
        messcontent.setText(messContent);

        ImageButton updateDraft = (ImageButton)findViewById(R.id.update_draft);
        updateDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from", messFrom);
                bundle.putString("to", messTo);
                bundle.putString("subject", messSubject);
                bundle.putString("content", messContent);
                bundle.putString("id", messId);
                System.out.println("ID FROM DRAFTDETAIL.JAVA TO COMPOSEMAIL.JAVA: "+messId);
                Intent intent = new Intent(DraftDetail.this, ComposeMail.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

    }
}
