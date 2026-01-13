package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.model.Notification;

public interface NotificationService {
    void sendNotification(String username, Notification notification);
}
