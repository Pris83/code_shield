package za.co.turbo.code_shield.sender;

import org.junit.jupiter.api.Test;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.sender.implementation.EmailNotificationSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class EmailNotificationSenderTest {

    private EmailNotificationSender emailSender;
    private User user;
    private Notification notification;

    @Test
    void send_shouldNotThrowException() {
        emailSender = new EmailNotificationSender();
        user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");

        notification = new Notification();
        notification.setSubject("Hello");
        notification.setMessage("This is a test");

        assertDoesNotThrow(() -> emailSender.send(user, notification));
    }

}
