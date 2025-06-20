package za.co.turbo.code_shield.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.service.NotificationScheduler;
import za.co.turbo.code_shield.service.NotificationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationSchedulerTest {

    private NotificationService notificationService;
    private NotificationScheduler notificationScheduler;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);

        // Create a test subclass to override getUsersWithPendingTasks()
        notificationScheduler = new NotificationScheduler(notificationService) {
            @Override
            protected List<User> getUsersWithPendingTasks() {
                User user1 = new User();
                user1.setUsername("Alice");
                user1.setEmail("alice@example.com");

                User user2 = new User();
                user2.setUsername("Bob");
                user2.setEmail("bob@example.com");

                return List.of(user1, user2);
            }
        };
    }

    @Test
    void sendScheduledReminders_shouldSendNotificationsToUsers() {
        notificationScheduler.sendScheduledReminders();

        // Capture arguments passed to notificationService.sendNotification
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        verify(notificationService, times(2)).sendNotification(userCaptor.capture(), notificationCaptor.capture());

        List<User> notifiedUsers = userCaptor.getAllValues();
        List<Notification> sentNotifications = notificationCaptor.getAllValues();

        assertEquals(2, notifiedUsers.size());
        assertEquals(2, sentNotifications.size());

        assertTrue(notifiedUsers.stream().anyMatch(u -> u.getUsername().equals("Alice")));
        assertTrue(notifiedUsers.stream().anyMatch(u -> u.getUsername().equals("Bob")));

        for (Notification notification : sentNotifications) {
            assertEquals("Reminder", notification.getSubject());
            assertEquals("You have tasks pending!", notification.getMessage());
        }
    }
    @Test
    void sendScheduledReminders_noUsers_shouldNotSendNotifications() {
        // Override to return empty list
        notificationScheduler = new NotificationScheduler(notificationService) {
            @Override
            protected List<User> getUsersWithPendingTasks() {
                return List.of();
            }
        };

        notificationScheduler.sendScheduledReminders();

        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void sendScheduledReminders_nullUserList_shouldNotThrow() {
        notificationScheduler = new NotificationScheduler(notificationService) {
            @Override
            protected List<User> getUsersWithPendingTasks() {
                return null;
            }
        };

        assertDoesNotThrow(() -> notificationScheduler.sendScheduledReminders());

        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void sendScheduledReminders_notificationServiceThrows_shouldContinueSending() {
        notificationScheduler = new NotificationScheduler(notificationService) {
            @Override
            protected List<User> getUsersWithPendingTasks() {
                User user1 = new User();
                user1.setUsername("Alice");
                User user2 = new User();
                user2.setUsername("Bob");
                return List.of(user1, user2);
            }
        };

        // Throw exception for first user
        doThrow(new RuntimeException("Send failure"))
                .when(notificationService).sendNotification(argThat(user -> user.getUsername().equals("Alice")), any());

        assertDoesNotThrow(() -> notificationScheduler.sendScheduledReminders());

        // Should still attempt to send for Bob
        verify(notificationService, times(1)).sendNotification(argThat(user -> user.getUsername().equals("Bob")), any());
    }

    @Test
    void sendScheduledReminders_notificationContentValidation() {
        notificationScheduler = new NotificationScheduler(notificationService) {
            @Override
            protected List<User> getUsersWithPendingTasks() {
                User user = new User();
                user.setUsername("Charlie");
                return List.of(user);
            }
        };

        notificationScheduler.sendScheduledReminders();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).sendNotification(any(), captor.capture());

        Notification sentNotification = captor.getValue();
        assertEquals("Reminder", sentNotification.getSubject());
        assertEquals("You have tasks pending!", sentNotification.getMessage());
    }

}
