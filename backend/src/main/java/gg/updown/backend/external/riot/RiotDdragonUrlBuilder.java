package gg.updown.backend.external.riot;

public class RiotDdragonUrlBuilder {
    public static String getChampionIconUrl(String gameVersion, String championId) {
        return RiotApiBasePath.DDRAGON.getUrl() + "/cdn/" + gameVersion +"/img/champion/" + championId + ".png";
    }

    public static String getSummonerIconUrl(String gameVersion, String iconId) {
        return RiotApiBasePath.DDRAGON.getUrl() + "/cdn/" + gameVersion +"/img/profileicon/" + iconId + ".png";
    }
}