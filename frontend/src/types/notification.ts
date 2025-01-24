export interface Notification {
    notificationSeq: number;
    notificationId: string;
    notificationType: string;
    targetSiteCode: number;
    content: string;
    readYn: boolean;
    readDt: string;
    regDt: string;
    gameCreateDt: string;
    gameModeName: string;
}

export interface ReviewNotification {
    notificationSeq: number;
    notificationId: string;
    notificationType: string;
    targetSiteCode: number;
    championIconUrl: string;
    content: string;
    readYn: boolean;
    readDt: string;
    regDt: string;
}