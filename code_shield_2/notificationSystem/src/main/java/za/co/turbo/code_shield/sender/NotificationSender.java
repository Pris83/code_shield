package za.co.turbo.code_shield.sender;

import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;

public interface NotificationSender {
    void send(User user, Notification notification);

}
