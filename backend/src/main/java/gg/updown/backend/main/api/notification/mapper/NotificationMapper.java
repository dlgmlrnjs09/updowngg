package gg.updown.backend.main.api.notification.mapper;

import gg.updown.backend.main.api.notification.model.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationEntity notificationEntity);
    List<NotificationEntity> getNotifications(long siteCode);
}
