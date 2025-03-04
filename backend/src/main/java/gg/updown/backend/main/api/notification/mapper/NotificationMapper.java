package gg.updown.backend.main.api.notification.mapper;

import gg.updown.backend.main.api.notification.model.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationEntity notificationEntity);
    List<NotificationEntity> getNotifications(String puuid);
    void readNotification(@Param("id") String id, @Param("puuid") String puuid);
    void readNotifications(@Param("ids") List<String> ids, @Param("puuid") String puuid);
}
