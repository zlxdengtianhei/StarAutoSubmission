package com.example.starautosubmission;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {

    private static final String TAG = "MailSender";
    private Context context;

    public MailSender(Context context) {
        this.context = context;
    }

    public void sendEmail(String recipient, String subject, String body, String attachmentName, Uri attachmentUri) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EmailPrefs", Context.MODE_PRIVATE);
        String emailService = sharedPreferences.getString("emailService", "");
        String senderEmail = sharedPreferences.getString("senderEmail", "");
        String emailPassword = sharedPreferences.getString("emailPassword", "");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        switch (emailService) {
            case "GMail":
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");
                break;
            case "Yahoo":
                properties.put("mail.smtp.host", "smtp.mail.yahoo.com");
                properties.put("mail.smtp.port", "587");
                break;
            case "Outlook":
                properties.put("mail.smtp.host", "smtp.office365.com");
                properties.put("mail.smtp.port", "587");
                break;
            case "QQ":
                properties.put("mail.smtp.host", "smtp.qq.com");
                properties.put("mail.smtp.port", "465");
                properties.put("mail.smtp.socketFactory.port", "465");
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            default:
                Toast.makeText(context, "未知的邮件服务提供商", Toast.LENGTH_SHORT).show();
                return;
        }

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, emailPassword);
            }
        });

        AsyncTask.execute(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);

                Multipart multipart = new MimeMultipart();

                // 添加邮件正文
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);
                multipart.addBodyPart(textPart);

                // 添加附件
                if (attachmentUri != null) {
                    Log.d(TAG, "Attachment URI: " + attachmentUri.toString());
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    File attachmentFile = new File(context.getCacheDir(), attachmentName);

                    try (InputStream inputStream = context.getContentResolver().openInputStream(attachmentUri);
                         FileOutputStream outputStream = new FileOutputStream(attachmentFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        Log.d(TAG, "Attachment file written: " + attachmentFile.getAbsolutePath());
                    } catch (Exception e) {
                        Log.e(TAG, "Error writing attachment file", e);
                    }

                    DataSource source = new FileDataSource(attachmentFile);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(attachmentName);
                    multipart.addBodyPart(attachmentPart);

                    Log.d(TAG, "Attachment part added to multipart");
                } else {
                    Log.d(TAG, "No attachment URI provided");
                }

                message.setContent(multipart);

                Transport.send(message);
                Log.d(TAG, "邮件发送成功");
                runOnUiThread(() -> Toast.makeText(context, "邮件发送成功", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "邮件发送失败", e);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(context, "邮件发送失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(action);
        }
    }
}
