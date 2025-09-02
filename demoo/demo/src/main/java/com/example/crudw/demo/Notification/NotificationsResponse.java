package com.example.crudw.demo.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class NotificationsResponse {
    private String message;
    private LocalDateTime createdAt;
    private List<Notification> notifications;
    public NotificationsResponse(String message, LocalDateTime createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }
    public NotificationsResponse(List<Notification> notifications) {
        this.notifications = notifications;
    }
    public static NotificationsResponse from(Notification notification) {
        return new NotificationsResponse(notification.getMessage(), notification.getCreatedAt());
    }
    public static List<NotificationsResponse> fromList(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationsResponse::from)
                .collect(Collectors.toList());
    }
}
