package com.example.final_mobile_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SentFragment extends Fragment {
    public SentFragment(){};
    TextView tvFrom, tvTO, tvSubject, tvContent, tvDate;
    LinearLayout sentFragment, sentRow;
    ArrayList<Mail> mails = new ArrayList<>();
    List<Mail> result;
    static ProgressDialog mProgress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.sent_fragment, container, false);
        sentFragment = (LinearLayout)view.findViewById(R.id.sent_fragment_layout);
        for(int i=0; i<SplashActivity.sentMails.size(); i++){
            String emailTo = SplashActivity.sentMails.get(i).getTo();
            String emailSubject = SplashActivity.sentMails.get(i).getSubject();
            String emailContent = SplashActivity.sentMails.get(i).getContent();
            String emailDate = SplashActivity.sentMails.get(i).getDate();
            displayEmail(emailTo, emailSubject, emailContent, emailDate);
        }

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

    }


    public void messageDetail(String to, String subject, String content, String date){
        Bundle bundle = new Bundle();
        bundle.putString("to", to);
        bundle.putString("subject", subject);
        bundle.putString("content", content);
        bundle.putString("date", date);
        Intent intent = new Intent(getContext(), MessageDetail.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void displayEmail(final String to, final String subject, final String content, final String date){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        sentRow=(LinearLayout)inflater.inflate(R.layout.inbox_row, sentFragment, false);

        //display mail info
        tvTO = sentRow.findViewById(R.id.user_account_name);
        tvTO.setText(to);
//          create first letter icon like Gmail
        String firstLetter = tvTO.getText().toString();
        firstLetter = firstLetter.substring(0, 1);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(tvTO);
        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter, color);
        ImageView userImage = sentRow.findViewById(R.id.user_image);
        userImage.setImageDrawable(textDrawable);

        tvSubject = sentRow.findViewById(R.id.inbox_topic);
        tvSubject.setText(subject);

        tvContent = sentRow.findViewById(R.id.inbox_content);
        tvContent.setText(content);
        tvDate = sentRow.findViewById(R.id.date_time);
        tvDate.setText(date);
        sentFragment.addView(sentRow);

        sentRow.setClickable(true);
        sentRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDetail(to, subject, content, date);
            }
        });
    }
}
