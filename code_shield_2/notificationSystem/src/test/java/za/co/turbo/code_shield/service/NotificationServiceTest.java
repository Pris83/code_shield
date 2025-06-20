package za.co.turbo.code_shield.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.model.UserPreferences;
import za.co.turbo.code_shield.sender.implementation.EmailNotificationSender;
import za.co.turbo.code_shield.sender.implementation.SmsNotificationSender;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    EmailNotificationSender emailSender;
    @Mock
    SmsNotificationSender smsSender;
    @Mock
    UserPreferencesService userPreferencesService;

    @InjectMocks
    NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setUsername("Jane");
        user.setEmail("jane@example.com");
        user.setPhoneNumber("+123456789");

        // When a task is created (simulate this via controller or service directly)
         notification = new Notification("Greetings:", "Hello World!");
    }

    @Test
    void shouldSendEmailAndSmsAccordingToUserPreferences() {

        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(true, true));

        notificationService.sendNotification(user, notification);

        verify(emailSender).send(user, notification);
        verify(smsSender).send(user, notification);
    }

    @Test
    void shouldSendEmailAndSmsAccordingToUserPreferences_OnlyEmail() {

        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(true, false));

        notificationService.sendNotification(user, notification);

        verify(emailSender).send(user, notification);
    }

    @Test
    void shouldSendEmailAndSmsAccordingToUserPreferences_OnlySMS() {

        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(false, true));

        notificationService.sendNotification(user, notification);

        verify(smsSender).send(user, notification);
    }
}

