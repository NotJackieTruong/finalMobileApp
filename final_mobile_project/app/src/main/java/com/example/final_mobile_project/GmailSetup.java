package com.example.final_mobile_project;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailSetup {
    public static boolean isValidEmail(String email) {
//        if (target == null) {
//            return false;
//        } else {
//            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
//        }
        boolean isValid = false;
        try{
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e){
            e.printStackTrace();
        }
        return isValid;
    }

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        if(isValidEmail(to)==true){
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        } else{
            email.setRecipients(javax.mail.Message.RecipientType.TO, to);
        }

        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        return message;
    }

    public static Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();
        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    // TODO: Get show detail email
    public static Message getMessage(Gmail service, String userId, String messageId, String format) throws IOException {
        Message message = null;
        if(format != null && !format.isEmpty()) {
            message = service.users().messages().get(userId, messageId).setFormat(format).execute();
        } else {
            message = service.users().messages().get(userId, messageId).execute();
        }
        return message;
    }

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {
        System.out.println("listMessagesMatchingQuery");
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
        List<Message> messages = new ArrayList<Message>();

        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messages;
    }

    public static List<Message> listMessagesWithLabels(Gmail service, String userId, List<String> labelIds) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId)
                .setLabelIds(labelIds).execute();
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setLabelIds(labelIds)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messages;
    }

    public static List<Message> listAllSentMessages(Gmail service, String userId, long max) throws IOException {
        ListMessagesResponse response;
        List labelIds = new ArrayList();
        labelIds.add("SENT");
        if (max < 1) {
            response = service.users().messages().list(userId).setLabelIds(labelIds).execute();
        } else {
            response = service.users().messages().list(userId).setLabelIds(labelIds).setMaxResults(max).execute();
        }
        List<Message> messages = new ArrayList<Message>();
        if (response.getMessages() != null) {
            messages.addAll(response.getMessages());
        }
        return messages;
    }

    public static List<Message> listAllInboxMessages(Gmail service, String userId, long max) throws IOException {
        ListMessagesResponse response;
        List labelIds = new ArrayList();
        labelIds.add("INBOX");
        if (max < 1) {
            response = service.users().messages().list(userId).setLabelIds(labelIds).execute();
        } else {
            response = service.users().messages().list(userId).setLabelIds(labelIds).setMaxResults(max).execute();
        }
        List<Message> messages = new ArrayList<Message>();
        if (response.getMessages() != null) {
            messages.addAll(response.getMessages());
        }
        return messages;
    }

    public static List<Message> listAllDraftMessages(Gmail service, String userId, long max) throws IOException {
        ListMessagesResponse response;
        List labelIds = new ArrayList();
        labelIds.add("DRAFT");
        if (max < 1) {
            response = service.users().messages().list(userId).setLabelIds(labelIds).execute();
        } else {
            response = service.users().messages().list(userId).setLabelIds(labelIds).setMaxResults(max).execute();
        }
        List<Message> messages = new ArrayList<Message>();
        if (response.getMessages() != null) {
            messages.addAll(response.getMessages());
        }
        return messages;
    }


    public static List<String> getLabels(Gmail service, String userId){
        ListLabelsResponse listResponse = null;
        List<String> labelsStr = new ArrayList();

        try {
            listResponse = service.users().labels().list(userId).execute();
            List<Label> labels = listResponse.getLabels();
            if (labels.size() == 0) {
                System.out.println("No labels found.");
            } else {
                System.out.println("Labels:");
                for (Label label : labels) {
                    System.out.printf("- %s\n", label.getName());
                    labelsStr.add(label.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return labelsStr;
    }

    public static Draft createDraft(Gmail service,
                                    String userId,
                                    MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = service.users().drafts().create(userId, draft).execute();

        System.out.println("Draft id: " + draft.getId());
        System.out.println(draft.toPrettyString());
        return draft;
    }
}
