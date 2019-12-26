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
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.final_mobile_project.SplashActivity.REQUEST_AUTHORIZATION;

public class SentFragment extends Fragment {
    public SentFragment(){};
    TextView tvFrom, tvTO, tvSubject, tvContent, tvDate;
    LinearLayout sentFragment, sentRow;
    ArrayList<Mail> mails = new ArrayList<>();
    List<Mail> result;
    static ProgressDialog mProgress;
    private static boolean isOpen;
    String emailTo, emailSubject, emailContent, emailDate;
    static SentFragment instance;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.sent_fragment, container, false);
        sentFragment = (LinearLayout)view.findViewById(R.id.sent_fragment_layout);
        for(int i=0; i<SplashActivity.sentMails.size(); i++){
            emailTo = SplashActivity.sentMails.get(i).getTo();
            emailSubject = SplashActivity.sentMails.get(i).getSubject();
            emailContent = SplashActivity.sentMails.get(i).getContent();
            emailDate = SplashActivity.sentMails.get(i).getDate();
            displayEmail(emailTo, emailSubject, emailContent, emailDate);

        }

        return view;
    }
//    public class MakeSentRequestTask extends  AsyncTask<Void, Integer, List<Mail>> {
//
//        public com.google.api.services.gmail.Gmail mService = null;
//        public Exception mLastError = null;
//
//        MakeSentRequestTask(GoogleAccountCredential credential) {
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
//                return getSentDataFromApi();
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
//        private List<Mail> getSentDataFromApi() throws IOException {
//            List<Mail> result = new ArrayList<Mail>();
//            try {
//                List<Message> messages = GmailSetup.listAllSentMessages(mService, "me", 5);
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
//                SplashActivity.sentMails.clear();
//                SplashActivity.sentMails.addAll(output);
//                for(int i=0; i<SplashActivity.sentMails.size(); i++){
//                    emailTo = SplashActivity.sentMails.get(i).getTo();
//                    emailSubject = SplashActivity.sentMails.get(i).getSubject();
//                    emailContent = SplashActivity.sentMails.get(i).getContent();
//                    emailDate = SplashActivity.sentMails.get(i).getDate();
//                    displayEmail(emailTo, emailSubject, emailContent, emailDate);
//
//                }
//
//            }
//
//        }
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
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
        isOpen=false;
    }

    public static SentFragment getInstance(){
        return instance;
    }


    public static boolean isIsOpen() {
        return isOpen;
    }

    public static void setIsOpen(boolean isOpen) {
        SentFragment.isOpen = isOpen;
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
