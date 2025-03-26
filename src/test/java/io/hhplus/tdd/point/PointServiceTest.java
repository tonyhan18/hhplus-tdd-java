package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static io.hhplus.tdd.point.TransactionType.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;
    
    /**
     * InjectMocks : **테스트 대상 클래스에 Mock 객체를 주입**
     * - 테스트하려는 클래스 인스턴스를 생성하고, 
     * 클래스의 필드 또는 생성자에 필요한 의존성을 **테스트에서 생성된 Mock 객체로 주입**합니다.
     * */
    @InjectMocks
    private PointService pointService;

    /* */
    @Nested
    @DisplayName("포인트 충전 검증 단위 테스트")
    class ChargingPointTest {

        @Test
        @DisplayName("chargePointById가 포인트 충전 요청을 성공적으로 처리하는지 단위 테스트")
        void testChargePointById_success() {
            // Given
            long userId = 1L;
            long initialPoint = 100L;
            long chargeAmount = 50L;
            UserPoint originalPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());
            UserPoint updatedPoint = new UserPoint(userId, initialPoint + chargeAmount, System.currentTimeMillis());

            when(userPointTable.selectById(eq(userId))).thenReturn(originalPoint);
            when(userPointTable.insertOrUpdate(eq(userId), eq(initialPoint + chargeAmount))).thenReturn(updatedPoint);

            // When
            UserPoint result = pointService.chargePointById(userId, chargeAmount);

            // Then
            assertNotNull(result);
            assertEquals(userId, result.id());
            assertEquals(initialPoint + chargeAmount, result.point());

            verify(userPointTable).selectById(eq(userId));
            verify(userPointTable).insertOrUpdate(eq(userId), eq(initialPoint + chargeAmount));
            verify(pointHistoryTable).insert(eq(userId), eq(chargeAmount), eq(CHARGE), anyLong());
        }
    }
}
