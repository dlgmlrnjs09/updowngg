export interface PagingDTO<T> {
    items: T[];
    currentPage: number;
    itemsPerPage: number;
    totalItems: number;
    totalPages: number;
    displayPageCount: number;
    startPage: number;
    endPage: number;
    hasPrevious: boolean;
    hasNext: boolean;
}