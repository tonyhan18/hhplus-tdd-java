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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * 통합테스트 : 진짜 객체를 가지고 테스트 진행
 * */
@SpringBootTest
public class PointServiceItTest {
    @Autowired
    private PointService pointService;

    @Nested
    class ChargePointTest {

        @Test
        @DisplayName("포인트 충전시 DB에 정상적으로 반영되고 충전된 포인트 정보를 반환함")
        public void testChargePointById_shouldUpdateDbAndReturnUpdatedUserPoint() {
            // Given
            long userId = 1L;
            long initialPoint = 100L;
            long chargeAmount = 50L;

            UserPoint userPoint = pointService.chargePointById(userId, chargeAmount);

            // Then
            assertNotNull(userPoint);
            assertEquals(userId, userPoint.id());
            assertEquals(initialPoint + chargeAmount, userPoint.point());

            // Verifies the database reflects the updated points through another retrieval
            UserPoint updatedPoint = pointService.selectPointById(userId);
            assertNotNull(updatedPoint);
            assertEquals(initialPoint + chargeAmount, updatedPoint.point());
        }
    }
}
