package io.hhplus.tdd.point;

import lombok.val;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    // 1억
    private static final long maxPoint = 10000L;
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    /// point 경계값 체크
    public void chkForValidate(TransactionType type, long amount){
        // over 0 is validate
        if(amount <= 0) throw new IllegalArgumentException("요청한 포인트가 0 보다 커야 합니다.");
        switch (type){
            case CHARGE -> {
                if(point + amount > maxPoint)
                {
                    throw new IllegalArgumentException("포인트 한도를 초과했습니다.");
                }
            }
            case USE -> {
                if(point - amount < 0)
                {
                    throw new IllegalArgumentException("포인트가 부족합니다.");
                }
            }
        }
    }
}
