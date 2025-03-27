package io.hhplus.tdd.point;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor // 클래스의 final 필드와 @NonNull로 표시된 필드에 대한 생성자를 자동으로 생성해 줍니다.
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final LockManager lockManager;

    public UserPoint selectPointById(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> selectHistoryById(long id)
    {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint chargePointById(long userId, long chargeAmount) {
        ReentrantLock lock = lockManager.getLock(userId);
        lock.lock();
        try{
            // check for user exists
            UserPoint userPoint = userPointTable.selectById(userId);
            if(userPoint == null){
                userPoint = UserPoint.empty(userId);
            }
            ///  DDD work
            userPoint = userPoint.charge(userPoint, chargeAmount);
            userPoint = userPointTable.insertOrUpdate(userId, userPoint.point());

            // recording
            pointHistoryTable.insert(userId, chargeAmount, TransactionType.CHARGE, System.currentTimeMillis());
            return userPoint;
        } finally {
            lock.unlock();
        }
    }

    public UserPoint usePointById(long userId, long useAmount) {
        ReentrantLock lock = lockManager.getLock(userId);
        lock.lock();
        try{
            // check for user exists
            UserPoint userPoint = userPointTable.selectById(userId);
            if(userPoint == null){
                return UserPoint.empty(userId);
            }
            userPoint.chkForValidate(TransactionType.USE, useAmount);

            // DDD
            userPoint = userPoint.use(userPoint, useAmount);
            userPoint = userPointTable.insertOrUpdate(userId, userPoint.point());

            // recording
            pointHistoryTable.insert(userId, useAmount, TransactionType.USE, System.currentTimeMillis());
            return userPoint;
        } finally {
            lock.unlock();
        }
    }
}