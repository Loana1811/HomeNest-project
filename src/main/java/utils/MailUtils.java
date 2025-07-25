package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailUtils {

    public static void send(String to, String subject, String content) {
        final String from = "dungbthce181851@fpt.edu.vn";
        final String password = "acfrcbjrqlnbqcwb"; // App password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setContent(content, "text/html; charset=utf-8");
            Transport.send(msg);
            System.out.println("✅ Đã gửi mail tới: " + to);
        } catch (Exception e) {
            System.out.println("❌ Lỗi gửi mail:");
            e.printStackTrace(); // QUAN TRỌNG: gửi cho mình nếu bạn thấy lỗi ở đây
        }
    }
}
