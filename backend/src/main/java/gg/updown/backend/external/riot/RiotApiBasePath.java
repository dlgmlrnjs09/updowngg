package gg.updown.backend.external.riot;

import lombok.Getter;

@Getter
public enum RiotApiBasePath {
    API("https://%s.api.riotgames.com"),
    DDRAGON("https://ddragon.leagueoflegends.com");

    private final String url;

    RiotApiBasePath(String url) {
        this.url = url;
    }

    public String getUrl(String param) {
        return String.format(url, param);
    }
}
