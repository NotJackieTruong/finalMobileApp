package com.example.final_mobile_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
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

import java.io.IOException;
import java.io.SerializablePermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DraftsFragment extends Fragment {
    static TextView userAccountName, userImage, inboxSubject, inboxContent, inboxDate;
    LinearLayout draftFragment, draftRow;
    private static boolean isOpen;
    String emailFrom, emailTo, emailSubject, emailContent, emailDate, emailId;
    public static DraftsFragment instance;
    public ProgressDialog mProgress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.drafts_fragment, container, false);
        draftFragment = (LinearLayout)view.findViewById(R.id.draft_fragment_layout);
        for(int i = 0; i<SplashActivity.draftMails.size(); i++){
            emailId = SplashActivity.draftMails.get(i).getId();
            emailFrom = SplashActivity.draftMails.get(i).getFrom();
            emailTo= SplashActivity.draftMails.get(i).getTo();
            emailSubject=SplashActivity.draftMails.get(i).getSubject();
            emailContent= SplashActivity.draftMails.get(i).getContent();
            emailDate=SplashActivity.draftMails.get(i).getDate();
            System.out.println("DRAFT: FROM: "+emailTo+", SUBJECT:"+emailSubject+", CONTENT: "+emailContent+", DATE: "+emailDate+", ID: "+ emailId);
            displayEmail(emailFrom, emailTo, emailSubject, emailContent, emailDate, emailId);

        }

//        updateUI(SplashActivity.draftMails);


        return view;
    }

    public void updateUI(List<Mail> list){
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = (View)inflater.inflate(R.layout.drafts_fragment, (ViewGroup)getView(), false);
//        draftFragment = (LinearLayout)view.findViewById(R.id.draft_fragment_layout);
        for(int i = 0; i<list.size(); i++){
            emailFrom = list.get(i).getFrom();
            emailTo= list.get(i).getTo();
            emailSubject=list.get(i).getSubject();
            emailContent= list.get(i).getContent();
            emailDate=list.get(i).getDate();
            System.out.println("DRAFT: FROM: "+emailTo+", SUBJECT:"+emailSubject+", CONTENT: "+emailContent+", DATE: "+emailDate+", ID: ");
            displayEmail(list.get(i).getFrom(), list.get(i).getTo(), list.get(i).getSubject(), list.get(i).getContent(), list.get(i).getDate(), list.get(i).getId());

        }
        System.out.println(list);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpen=true;
        instance=this;

//        mProgress = new ProgressDialog(getActivity().getApplicationContext());
//        mProgress.setMessage("Please wait...");
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

    public static DraftsFragment getInstance() {
        return instance;
    }


//    public class MakeDraftRequestTask extends  AsyncTask<Void, Integer, List<Mail>> {
//
//        public com.google.api.services.gmail.Gmail mService = null;
//        public Exception mLastError = null;
//
//        MakeDraftRequestTask(GoogleAccountCredential credential) {
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
//                return getDraftDataFromApi();
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
//        private List<Mail> getDraftDataFromApi() throws IOException {
//            List<Mail> result = new ArrayList<Mail>();
//            try {
//                List<Message> messages = GmailSetup.listAllDraftMessages(mService, "me", 5);
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
//                SplashActivity.draftMails.clear();
//                SplashActivity.draftMails.addAll(output);
//                for(int i = 0; i<SplashActivity.draftMails.size(); i++){
//                    emailId = SplashActivity.draftMails.get(i).getId();
//                    emailFrom = SplashActivity.draftMails.get(i).getFrom();
//                    emailTo= SplashActivity.draftMails.get(i).getTo();
//                    emailSubject=SplashActivity.draftMails.get(i).getSubject();
//                    emailContent= SplashActivity.draftMails.get(i).getContent();
//                    emailDate=SplashActivity.draftMails.get(i).getDate();
//                    System.out.println("DRAFT: FROM: "+emailTo+", SUBJECT:"+emailSubject+", CONTENT: "+emailContent+", DATE: "+emailDate+", ID: "+ emailId);
//                    displayEmail(emailFrom, emailTo, emailSubject, emailContent, emailDate, emailId);
//
//                }
//            }
//
//        }
//    }

    public void displayEmail(final String from, final String to, final String subject, final String content, final String date, final String id){
        if(getActivity()!=null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            draftRow=(LinearLayout)inflater.inflate(R.layout.inbox_row, draftFragment, false);
        }


        //display mail info
        userAccountName = draftRow.findViewById(R.id.user_account_name);
        userAccountName.setText("To: "+ to);
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
                messageDetail(from, to, subject, content, date, id);
            }
        });

//        draftRow.setOnLongClickListener();
    }
    public static boolean isIsOpen() {
        return isOpen;
    }

    public static void setIsOpen(boolean isOpen) {
        DraftsFragment.isOpen = isOpen;
    }

    public void messageDetail(String from, String to, String subject, String content, String date, String id){
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        bundle.putString("to", to);
        bundle.putString("subject", subject);
        bundle.putString("content", content);
        bundle.putString("date", date);
        bundle.putString("id", id);
        System.out.println("ID FROM DRAFTSFRAGMENT.JAVA TO DRAFTDETAIL.JAVA: "+id);
        Intent intent = new Intent(getContext(), DraftDetail.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
