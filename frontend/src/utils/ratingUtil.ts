// export const getRatingColor = (rating: number): string => {
//     if (!rating) return '#9e9e9e'
//     if (rating < 2.5) return '#4CAF50'  // 2.5 미만: 초록색
//     if (rating < 3.5) return '#FFB300'  // 2.5~3.5 미만: 노란색
//     return '#FF5252'                    // 3.5 이상: 빨간색
// }

export const getRatingColor = (rating: number): string => {
    if (rating < 4.0) return '#2979FF'
    else return '#FF5252'
}