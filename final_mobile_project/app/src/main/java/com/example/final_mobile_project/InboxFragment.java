package com.example.final_mobile_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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
import java.util.Random;

public class InboxFragment extends Fragment {
    boolean isColor = true;
    boolean isClicked = false;
    static TextView userAccountName, inboxSubject, inboxContent, inboxDate;
    LinearLayout inboxRow, inboxFragment;
    private static boolean isOpen;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpen=true;

    }

    @Override
    public void onResume() {
        super.onResume();
        isOpen=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isOpen=false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isOpen = false;
    }

    public static boolean isIsOpen() {
        return isOpen;
    }

    public static void setIsOpen(boolean isOpen) {
        InboxFragment.isOpen = isOpen;
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
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

//    public class MakeRequestTask extends AsyncTask<Void, Integer, List<Mail>> {
//
//        public com.google.api.services.gmail.Gmail mService = null;
//        public Exception mLastError = null;
//
//        MakeRequestTask(GoogleAccountCredential credential) {
//            HttpTransport transport = AndroidHttp.newCompatibleTransport();
//            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//            mService = new com.google.api.services.gmail.Gmail.Builder(
//                    transport, jsonFactory, credential)
//                    .setApplicationName("Gmail API Android Quickstart")
//                    .build();
//        }
//
//        @Override
//        protected List<Mail> doInBackground(Void... params) {
//            try {
//                return getInboxDataFromApi();
//            } catch (Exception e) {
//                mLastError = e;
//                cancel(true);
//                return null;
//            }
//        }
//
//        public String filterEmail(String text) {
//            String result = "";
//            boolean check = false;
//            for (int i = 0; i < text.length(); i++) {
//                char c = text.charAt(i);
//                if (c == '<') {
//                    check = true;
//                }
//                if (c == '>') {
//                    return result;
//                }
//                if (check && c != '<') {
//                    result = result + c;
//                }
//            }
//            return text;
//        }
//
//        private List<Mail> getInboxDataFromApi() throws IOException {
//            List<Mail> result = new ArrayList<Mail>();
//            try {
//                List<Message> messages = GmailSetup.listAllInboxMessages(mService, "me", 5);
//
//                for (int i = 0; i < messages.size(); i++) {
//                    Message messageDetail = GmailSetup.getMessage(mService, "me", messages.get(i).getId(), "full");
//                    String content = messageDetail.getSnippet();
//                    String subject = "";
//                    String from = "";
//                    String to = "";
//                    String date = "";
//                    String id = messages.get(i).getId();
//                    List<MessagePartHeader> messagePartHeader = messageDetail.getPayload().getHeaders();
//                    for (int j = 0; j < messagePartHeader.size(); j++) {
//                        if (messagePartHeader.get(j).getName().equals("Subject")) {
//                            subject = messagePartHeader.get(j).getValue();
//                        }
//                        if (messagePartHeader.get(j).getName().equals("From")) {
//                            from = messagePartHeader.get(j).getValue();
//                        }
//                        if (messagePartHeader.get(j).getName().equals("To")) {
//                            to = messagePartHeader.get(j).getValue();
//                        }
//                        if (messagePartHeader.get(j).getName().equals("Date")) {
//                            date = messagePartHeader.get(j).getValue();
//                        }
////                        if(messagePartHeader.get(j).getName().equals("Id")){
////                            id = messagePartHeader.get(j).getValue();
////                        }
//                    }
//                    Mail mail = new Mail(subject, content, filterEmail(from), to, date, id);
//                    result.add(mail);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(List<Mail> output) {
//
//            if (output == null || output.size() == 0) {
//                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
//            } else {
//                SplashActivity.mails.clear();
//                SplashActivity.mails.addAll(output);
//                for(int i=0; i< SplashActivity.mails.size(); i++){
//                    String emailFrom= SplashActivity.mails.get(i).getFrom();
//                    String emailSubject=SplashActivity.mails.get(i).getSubject();
//                    String emailContent= SplashActivity.mails.get(i).getContent();
//                    String emailDate=SplashActivity.mails.get(i).getDate();
//                    System.out.println("FROM: "+emailFrom+", SUBJECT:"+emailSubject+", CONTENT: "+emailContent+", DATE: "+emailDate+", LABLE: ");
//                    displayEmail(SplashActivity.mails.get(i).getFrom(), SplashActivity.mails.get(i).getSubject(), SplashActivity.mails.get(i).getContent(), SplashActivity.mails.get(i).getDate());
//
//                }
//            }
//
//        }
//    }

}
