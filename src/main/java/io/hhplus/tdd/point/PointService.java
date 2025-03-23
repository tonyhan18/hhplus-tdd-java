package io.hhplus.tdd.point;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 클래스의 final 필드와 @NonNull로 표시된 필드에 대한 생성자를 자동으로 생성해 줍니다.
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getPointById(long userId) {
        return userPointTable.selectById(userId);
    }

//    public UserPoint chargePointById(long userId, long chargeAmount) {
//        try{
//            // check for user exists
//            UserPoint userPoint = userPointTable.selectById(userId);
//            userPoint.validate
//
//        }finally {
//
//        }
//
//    }
}