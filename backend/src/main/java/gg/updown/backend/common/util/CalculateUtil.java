package gg.updown.backend.common.util;

public class CalculateUtil {
    /**
     * 백분율 계산
     * @param source
     * @param target
     * @return
     */
    public static double getRatio(Long source, Long target) {
        if (source == 0) return 0.0;
        return Math.round((double) target / source * 100 * 10) / 10.0;
    }


}
