package za.co.turbo.code_shield.service;

import org.springframework.stereotype.Service;
import za.co.turbo.code_shield.model.*;
import za.co.turbo.code_shield.sender.implementation.EmailNotificationSender;
import za.co.turbo.code_shield.sender.implementation.SmsNotificationSender;

@Service
public class NotificationService {
    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;
    private final UserPreferencesService userPreferencesService;

    public NotificationService(EmailNotificationSender emailSender, SmsNotificationSender smsSender, UserPreferencesService userPreferencesService) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.userPreferencesService = userPreferencesService;
    }

    public void sendNotification(User user, Notification notification) {
        UserPreferences preferences = userPreferencesService.getPreferences(user);

        if (preferences == null) {
            preferences = new UserPreferences(false, false);
        }

        if (preferences.isEmailEnabled()) {
            emailSender.send(user, notification);
        }

        if (preferences.isSmsEnabled()) {
            smsSender.send(user, notification);
        }
    }
}
