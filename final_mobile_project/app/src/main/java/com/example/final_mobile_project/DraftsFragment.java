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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.drafts_fragment, container, false);
        LinearLayout setFragment = (LinearLayout)view.findViewById(R.id.draft_fragment_layout);

        for(int i = 0; i<5; i++){
            LinearLayout draftRow = (LinearLayout)inflater.inflate(R.layout.inbox_row, container, false);

            //receive string from ComposeMail activity
//        String messReceiver = this.getArguments().getString("receiver").toString();
//        String messTopic = getArguments().getString("topic");
//        String messContent = getArguments().getString("content");


            final TextView userAccountName = draftRow.findViewById(R.id.user_account_name);
            userAccountName.setText("UsernameABCD");

//          create first letter icon like Gmail
            String firstLetter = userAccountName.getText().toString();
            firstLetter=firstLetter.substring(0,1);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(userAccountName);
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter, color);

            ImageView userImage = draftRow.findViewById(R.id.user_image);
            userImage.setImageDrawable(textDrawable);

            final TextView inboxTopic = draftRow.findViewById(R.id.inbox_topic);
            inboxTopic.setText("MobileAppDev");

            final TextView inboxContent = draftRow.findViewById(R.id.inbox_content);
            inboxContent.setText("On progress ...");

            TextView inboxDate = draftRow.findViewById(R.id.date_time);
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = simpleDateFormat.format(date);
            inboxDate.setText(dateString);
            setFragment.addView(draftRow);
            draftRow.setClickable(true);
            draftRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //transfer the text to the message detail
                    String messTopic = inboxTopic.getText().toString();
                    String messContent = inboxContent.getText().toString();
                    Intent i = new Intent(getActivity().getApplicationContext(), MessageDetail.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle b = new Bundle();
                    b.putString("topic", messTopic);
                    b.putString("content", messContent);
                    i.putExtras(b);
                    startActivity(i);

                }
            });
        }


        return view;
    }
}
