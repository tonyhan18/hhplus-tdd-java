package io.hhplus.tdd.point;

import lombok.val;
import org.apache.catalina.User;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    // TODO : 테스트를 위해 10000으로 고정, 이후 수정 가능
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

    /// Charging
    public UserPoint charge(UserPoint userPoint, long amount)
    {
        chkForValidate(TransactionType.CHARGE, amount);
        return new UserPoint(userPoint.id, userPoint.point + amount, System.currentTimeMillis());
    }

    /// Using
    public UserPoint use(UserPoint userPoint, long amount)
    {
        chkForValidate(TransactionType.USE, amount);
        return new UserPoint(userPoint.id, userPoint.point - amount, System.currentTimeMillis());
    }
}
