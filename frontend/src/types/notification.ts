export interface Notification {
    notificationSeq: number;
    notificationId: string;
    notificationType: string;
    targetSiteCode: number;
    content: string;
    readYn: boolean;
    readDt: string;
    regDt: string;
}