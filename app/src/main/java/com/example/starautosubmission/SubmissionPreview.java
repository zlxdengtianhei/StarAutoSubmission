package com.example.starautosubmission;

import java.io.Serializable;

public class SubmissionPreview implements Serializable {
    private String format;
    private String content;
    private String receiverEmail;
    private String subject;
    private String attachment;

    public SubmissionPreview(String format, String content, String receiverEmail, String subject, String attachment) {
        this.format = format;
        this.content = content;
        this.receiverEmail = receiverEmail;
        this.subject = subject;
        this.attachment = attachment;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
