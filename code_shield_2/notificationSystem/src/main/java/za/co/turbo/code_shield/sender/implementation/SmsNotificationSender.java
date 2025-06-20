package za.co.turbo.code_shield.sender.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.sender.NotificationSender;

@Component
public class SmsNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationSender.class);

    @Override
    public void send(User user, Notification notification) {
        logger.info("Sending SMS to {} ({}): [{}] {}",
                user.getUsername(), user.getEmail(),
                notification.getSubject(), notification.getMessage());
    }

}
