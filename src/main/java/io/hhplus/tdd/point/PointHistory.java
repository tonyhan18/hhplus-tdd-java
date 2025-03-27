package io.hhplus.tdd.point;

/* 
 * 포인트 내역 클래스
 * 
 * 포인트 충전/사용 내역을 저장하는 클래스
 * 
 */
public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
