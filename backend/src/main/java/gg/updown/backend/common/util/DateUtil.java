package gg.updown.backend.common.util;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 밀리초를 LocalDate로 변환
     * @param milliSecond
     * @return
     */
    public static LocalDateTime msToLocalDateTime(Long milliSecond) {
        return Instant.ofEpochMilli(milliSecond)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
    }

    /**
     * yyyy-MM-dd String값을 밀리초로 변환
     * @param dateTimeStr
     * @return
     */
    public static Long yyyyMMddToMilliseconds(String dateTimeStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Required format: yyyy-MM-dd HH:mm", e);
        }
    }

    /**
     * 현재시간을 밀리초로 변환
     * @return
     */
    public static Long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * localDateTime -> YYYY-MM-DD
     * @param dateTime
     * @return
     */
    public static String formatDateTimeYYYYMMDD(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }
}
