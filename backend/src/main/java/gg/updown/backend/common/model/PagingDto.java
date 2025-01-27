package gg.updown.backend.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PagingDto<T> {
    private List<T> items;          // 페이지 데이터
    private int currentPage;        // 현재 페이지
    private int itemsPerPage;       // 페이지당 아이템 수
    private int totalItems;         // 전체 아이템 수
    private int totalPages;         // 전체 페이지 수
    private int displayPageCount;   // 화면에 표시할 페이지 수
    private int startPage;          // 현재 페이지 그룹의 시작 페이지
    private int endPage;           // 현재 페이지 그룹의 마지막 페이지
    private boolean hasPrevious;    // 이전 페이지 그룹 존재 여부
    private boolean hasNext;        // 다음 페이지 그룹 존재 여부

    public PagingDto(List<T> items, int currentPage, int itemsPerPage, int totalItems, int displayPageCount) {
        this.items = items;
        this.currentPage = currentPage;
        this.itemsPerPage = itemsPerPage;
        this.totalItems = totalItems;
        this.displayPageCount = displayPageCount;

        // 전체 페이지 수 계산
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지 그룹의 시작과 끝 페이지 계산
        int pageGroup = (int) Math.ceil((double) currentPage / displayPageCount);
        this.startPage = (pageGroup - 1) * displayPageCount + 1;
        this.endPage = Math.min(pageGroup * displayPageCount, totalPages);

        // 이전/다음 페이지 그룹 존재 여부 계산
        this.hasPrevious = startPage > 1;
        this.hasNext = endPage < totalPages;
    }
}