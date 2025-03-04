package gg.updown.backend.main.enums;

public enum SiteNotificationContent {
    PARTY_PARTICIPATION("%s 님이 파티에 참가했습니다.", SiteNotificationType.PARTY_COMMUNITY),
    PARTY_SELF_PARTICIPANT("파티에 가입되었습니다.", SiteNotificationType.PARTY_COMMUNITY),
    PARTY_REJECT("파티 가입 거절되었습니다.", SiteNotificationType.PARTY_COMMUNITY),
    PARTY_LEAVE("%s 님이 파티에서 나갔습니다.", SiteNotificationType.PARTY_COMMUNITY),
    PARTY_APPLICANT("%s 님이 %s 포지션에 파티참가 신청하였습니다.", SiteNotificationType.PARTY_COMMUNITY),
    PARTY_BREAKUP("파티가 해체되었습니다.", SiteNotificationType.PARTY_COMMUNITY),
    PARTY_KICK("파티에서 강퇴되었습니다.", SiteNotificationType.PARTY_COMMUNITY),
    REVIEW_RECEIVE("%s 에 플레이한 %s 게임의 평가가 도착했어요!", SiteNotificationType.SITE_REVIEW);

    private final String template;
    private final SiteNotificationType type;

    SiteNotificationContent(String template, SiteNotificationType type) {
        this.template = template;
        this.type = type;
    }

    public String format(Object... args) {
        // "**" 같은 마크다운 스타일을 자동으로 적용
        String[] formattedArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            formattedArgs[i] = "**" + args[i] + "**";
        }
        return String.format(template, (Object[]) formattedArgs);
    }

    public SiteNotificationType getType() {
        return type;
    }
}