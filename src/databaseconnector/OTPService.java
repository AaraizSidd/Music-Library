package databaseconnector;

import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OTPService {

    private static final String FROM_EMAIL = ""; // Replace with your email
    private static final String FROM_PASSWORD = ""; // Replace with your email password
    private static final String HOST = "smtp.gmail.com";
    private static final HashMap<String, String> otpStorage = new HashMap<>();
    private static final Random random = new Random();

    // Generate a random 6-digit OTP
    public static String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    // Send OTP to the specified email
    public static void sendOtp(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Error: Email cannot be null or empty.");
            return;
        }

        String otp = generateOtp();
        otpStorage.put(email, otp); // Store OTP for validation
        String subject = "Your OTP Code";
        String message = "Your OTP is: " + otp + "\nPlease use this to verify your identity. The OTP is valid for 5 minutes.";

        // Send the email
        sendEmail(email, subject, message);
        System.out.println("OTP generated for " + email + ": " + otp);
    }

    // Validate OTP
    public static boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email); // Remove OTP after successful verification
            return true;
        }
        return false;
    }

    // Email sending logic
    private static void sendEmail(String toEmail, String subject, String message) {
        Properties pr = System.getProperties();
        pr.put("mail.smtp.host", HOST);
        pr.put("mail.smtp.port", "587"); // Use TLS port 587
        pr.put("mail.smtp.auth", "true");
        pr.put("mail.smtp.starttls.enable", "true"); // Enable TLS encryption

        // Set up session with email authentication
        Session session = Session.getInstance(pr, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(FROM_EMAIL));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            // Send email
            Transport.send(mimeMessage);
            System.out.println("OTP sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println("Error sending OTP email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
