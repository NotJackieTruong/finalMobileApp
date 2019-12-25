package com.example.final_mobile_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DraftsFragment extends Fragment {
    static TextView userAccountName, userImage, inboxSubject, inboxContent, inboxDate;
    LinearLayout draftFragment, draftRow;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.drafts_fragment, container, false);
        draftFragment = (LinearLayout)view.findViewById(R.id.draft_fragment_layout);

        for(int i = 0; i<SplashActivity.draftMails.size(); i++){
            String emailFrom= SplashActivity.draftMails.get(i).getFrom();
            String emailSubject=SplashActivity.draftMails.get(i).getSubject();
            String emailContent= SplashActivity.draftMails.get(i).getContent();
            String emailDate=SplashActivity.draftMails.get(i).getDate();
            System.out.println("DRAFT: FROM: "+emailFrom+", SUBJECT:"+emailSubject+", CONTENT: "+emailContent+", DATE: "+emailDate+", LABLE: ");
            displayEmail(SplashActivity.draftMails.get(i).getFrom(), SplashActivity.draftMails.get(i).getSubject(), SplashActivity.draftMails.get(i).getContent(), SplashActivity.draftMails.get(i).getDate());

        }


        return view;
    }

    public void displayEmail(final String from, final String subject, final String content, final String date){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        draftRow=(LinearLayout)inflater.inflate(R.layout.inbox_row, draftFragment, false);

        //display mail info
        userAccountName = draftRow.findViewById(R.id.user_account_name);
        userAccountName.setText(from);
//          create first letter icon like Gmail
        String firstLetter = userAccountName.getText().toString();
        firstLetter = firstLetter.substring(0, 1);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(userAccountName);
        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter, color);
        ImageView userImage = draftRow.findViewById(R.id.user_image);
        userImage.setImageDrawable(textDrawable);

        inboxSubject = draftRow.findViewById(R.id.inbox_topic);
        inboxSubject.setText(subject);

        inboxContent = draftRow.findViewById(R.id.inbox_content);
        inboxContent.setText(content);
        inboxDate = draftRow.findViewById(R.id.date_time);
        inboxDate.setText(date);
        draftFragment.addView(draftRow);

        draftRow.setClickable(true);
        draftRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDetail(from, subject, content, date);
            }
        });
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
}
