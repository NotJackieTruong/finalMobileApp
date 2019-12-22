package com.example.final_mobile_project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class InboxFragment extends Fragment {
    boolean isColor = true;
    boolean isClicked = false;
    static TextView userAccountName, inboxSubject, inboxContent, inboxDate;
    LinearLayout inboxRow, inboxFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView view = (ScrollView) inflater.inflate(R.layout.inbox_fragment, container, false);
        inboxFragment = (LinearLayout)view.findViewById(R.id.inbox_fragment_layout);

        for(int i=0; i< SplashActivity.mails.size(); i++){
            String emailFrom= SplashActivity.mails.get(i).getFrom();
            String emailSubject=SplashActivity.mails.get(i).getSubject();
            String emailContent= SplashActivity.mails.get(i).getContent();
            String emailDate=SplashActivity.mails.get(i).getDate();
            System.out.println("FROM: "+emailFrom+", SUBJECT:"+emailSubject+", CONTENT: "+emailContent+", DATE: "+emailDate+", LABLE: ");
            displayEmail(SplashActivity.mails.get(i).getFrom(), SplashActivity.mails.get(i).getSubject(), SplashActivity.mails.get(i).getContent(), SplashActivity.mails.get(i).getDate());

        }

        return view;
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

    public void displayEmail(final String from, final String subject, final String content, final String date){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inboxRow=(LinearLayout)inflater.inflate(R.layout.inbox_row, inboxFragment, false);

        //display mail info
        userAccountName = inboxRow.findViewById(R.id.user_account_name);
        userAccountName.setText(from);
//          create first letter icon like Gmail
        String firstLetter = userAccountName.getText().toString();
        firstLetter = firstLetter.substring(0, 1);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(userAccountName);
        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter, color);
        ImageView userImage = inboxRow.findViewById(R.id.user_image);
        userImage.setImageDrawable(textDrawable);

        inboxSubject = inboxRow.findViewById(R.id.inbox_topic);
        inboxSubject.setText(subject);

        inboxContent = inboxRow.findViewById(R.id.inbox_content);
        inboxContent.setText(content);
        inboxDate = inboxRow.findViewById(R.id.date_time);
        inboxDate.setText(date);
        inboxFragment.addView(inboxRow);

        inboxRow.setClickable(true);
        inboxRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDetail(from, subject, content, date);
            }
        });
    }

}
