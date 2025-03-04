export interface Notification {
    notificationSeq: number;
    notificationId: string;
    notificationType: string;
    subSeq: number;
    targetSiteCode: number;
    targetPuuid: string;
    content: string;
    iconUrl: string;
    actionDt: string;
    readYn: boolean;
    readDt: string;
    regDt: string;
}
