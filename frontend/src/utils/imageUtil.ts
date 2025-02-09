export function useImageUrl() {
    const cdnUrl = import.meta.env.VITE_CDN_URL;

    const getPositionImage = (positionName: string) => `${cdnUrl}/position/position_${positionName.toLowerCase()}.svg`;
    /*const getProfileImage = (fileName: string) => `${cdnUrl}/profiles/${fileName}`;
    const getItemImage = (itemId: string) => `${cdnUrl}/items/${itemId}.png`;*/

    return {
        getPositionImage
    };
}