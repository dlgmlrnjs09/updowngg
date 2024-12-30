import router from "@/router";

export const goSelectedSummonerProfile = (name: string, tag: string) => {
    router.push({
        name: 'summoner',
        params: {
            name: name,
            tag: tag
        }
    })
}