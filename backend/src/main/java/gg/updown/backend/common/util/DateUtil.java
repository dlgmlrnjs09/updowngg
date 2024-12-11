package gg.updown.backend.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {
    public static LocalDateTime msToLocalDateTime(Long milliSecond) {
        return Instant.ofEpochMilli(milliSecond)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
    }
}
