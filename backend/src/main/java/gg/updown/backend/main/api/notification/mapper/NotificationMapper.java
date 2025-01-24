package gg.updown.backend.main.api.notification.mapper;

import gg.updown.backend.main.api.notification.model.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationEntity notificationEntity);
    List<NotificationEntity> getNotifications(long siteCode);
    void readNotification(@Param("id") String id, @Param("siteCode") long siteCode);
    void readNotifications(@Param("ids") List<String> ids, @Param("siteCode") long siteCode);
}
