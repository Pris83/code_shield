package za.co.turbo.code_shield.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.model.UserPreferences;
import za.co.turbo.code_shield.sender.implementation.EmailNotificationSender;
import za.co.turbo.code_shield.sender.implementation.SmsNotificationSender;
import za.co.turbo.code_shield.service.NotificationService;
import za.co.turbo.code_shield.service.UserPreferencesService;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailNotificationSender emailSender;

    @MockBean
    private SmsNotificationSender smsSender;

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private UserPreferencesService userPreferencesService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setup(){
        user = new User();
        user.setUsername("Jane");
        user.setEmail("jane@example.com");
        user.setPhoneNumber("+123456789");

        // When a task is created (simulate this via controller or service directly)
        notification = new Notification("New Task", "Check your dashboard");
    }

    @Test
    void shouldSendBothEmailAndSmsWhenBothPreferencesEnabled() {
        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(true, true)); // both true

        notificationService.sendNotification(user, notification);

        verify(emailSender, times(1)).send(user, notification);
        verify(smsSender, times(1)).send(user, notification);
    }


    @Test
    void shouldSendNotificationBasedOnUserPreference() throws Exception {
        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(false, true)); // only SMS

        notificationService.sendNotification(user, notification);

        // Then only SMS is sent
        verify(smsSender, times(1)).send(user, notification);
        verify(emailSender, never()).send(any(), any());
    }

    @Test
    void shouldSendOnlyEmailWhenOnlyEmailPreferenceEnabled() {
        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(true, false)); // only email

        notificationService.sendNotification(user, notification);

        verify(emailSender, times(1)).send(user, notification);
        verify(smsSender, never()).send(any(), any());
    }

    @Test
    void shouldNotSendAnyNotificationWhenAllPreferencesDisabled() {
        when(userPreferencesService.getPreferences(user))
                .thenReturn(new UserPreferences(false, false)); // none

        notificationService.sendNotification(user, notification);

        verify(emailSender, never()).send(any(), any());
        verify(smsSender, never()).send(any(), any());
    }

    @Test
    void shouldNotFailIfUserPreferencesAreNull() {
        when(userPreferencesService.getPreferences(user)).thenReturn(null);

        notificationService.sendNotification(user, notification);

        // Nothing should be sent
        verify(emailSender, never()).send(any(), any());
        verify(smsSender, never()).send(any(), any());
    }

}

