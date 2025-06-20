package za.co.turbo.code_shield.sender;

import org.junit.jupiter.api.Test;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.sender.implementation.SmsNotificationSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SmsNotificationSenderTest {

    private SmsNotificationSender smsSender;
    private User user;
    private Notification notification;

    @Test
    void send_shouldNotThrowException() {
        smsSender = new SmsNotificationSender();
        user = new User();
        user.setUsername("johndoe");
        user.setPhoneNumber("0123456789");

        notification = new Notification();
        notification.setSubject("Hello");
        notification.setMessage("This is a test");

        assertDoesNotThrow(() -> smsSender.send(user, notification));
    }
}
