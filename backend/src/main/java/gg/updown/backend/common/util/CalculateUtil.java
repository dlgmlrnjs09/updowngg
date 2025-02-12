package gg.updown.backend.common.util;

public class CalculateUtil {

    // private 생성자를 추가하여 인스턴스화 방지
    private CalculateUtil() {
        throw new IllegalStateException("Utility class");
    }
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

    /**
     * Wilson Score 계산 메소드
     *
     * @param upCount 좋아요 수
     * @param downCount 싫어요 수
     * @return 계산된 Wilson Score
     */
    public static double calculateWilsonScore(int upCount, int downCount) {
        int n = upCount + downCount;
        if (n == 0) {
            return 0; // 평가 데이터가 없는 경우 0 반환
        }
        double pHat = (double) upCount / n; // 좋아요 비율
        double z = 1.96; // 신뢰 구간 값 95%

        double wilsonScore = (pHat + z * z / (2 * n) - z * Math.sqrt((pHat * (1 - pHat) + z * z / (4 * n)) / n))
                / (1 + z * z / n);

        return Math.floor(wilsonScore * 1000);
    }


}
