package za.co.turbo.code_shield.service;

import org.springframework.stereotype.Service;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.model.UserPreferences;

@Service
public class TaskNotificationService {
    private final NotificationService notificationService;
    private final UserPreferencesService userPreferencesService;

    public TaskNotificationService(NotificationService notificationService, UserPreferencesService userPreferencesService) {
        this.notificationService = notificationService;
        this.userPreferencesService = userPreferencesService;
    }

    public void notifyUser(User user) {
        //UserPreferences prefs = userPreferencesService.getPreferences(user);
        UserPreferences prefs = new UserPreferences(true, false);

        Notification notification = new Notification("Notification:","New task added!");
        if (prefs.isEmailEnabled() || prefs.isSmsEnabled()) {
            notificationService.sendNotification(user, notification);
            System.out.println("Notification sent: " + notification);
        } else {
            System.out.println("Notification skipped due to user preferences.");
        }
    }
}
