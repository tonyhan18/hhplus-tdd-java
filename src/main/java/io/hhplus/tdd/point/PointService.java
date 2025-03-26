package io.hhplus.tdd.point;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor // 클래스의 final 필드와 @NonNull로 표시된 필드에 대한 생성자를 자동으로 생성해 줍니다.
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint selectPointById(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> selectHistoryById(long id)
    {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint chargePointById(long userId, long chargeAmount) {
        // check for user exists
        UserPoint userPoint = userPointTable.selectById(userId);
        if(userPoint == null){
            userPoint = UserPoint.empty(userId);
        }
        userPoint.chkForValidate(TransactionType.CHARGE, chargeAmount);

        // updating
        long updatedPoint = userPoint.point();
        updatedPoint += chargeAmount;
        userPoint = userPointTable.insertOrUpdate(userId, updatedPoint);

        // recording
        pointHistoryTable.insert(userId, chargeAmount, TransactionType.CHARGE, System.currentTimeMillis());
        return userPoint;
    }

    public UserPoint usePointById(long userId, long useAmount) {
        // check for user exists
        UserPoint userPoint = userPointTable.selectById(userId);
        if(userPoint == null){
            return UserPoint.empty(userId);
        }
        userPoint.chkForValidate(TransactionType.USE, useAmount);

        // updating
        long updatedPoint = userPoint.point();
        updatedPoint -= useAmount;
        userPoint = userPointTable.insertOrUpdate(userId, updatedPoint);

        // recording
        pointHistoryTable.insert(userId, useAmount, TransactionType.USE, System.currentTimeMillis());
        return userPoint;
    }
}