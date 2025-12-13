package com.blue.bridge.notification.service;

import com.blue.bridge.notification.dto.NotificationDTO;
import com.blue.bridge.users.entity.User;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);
}
