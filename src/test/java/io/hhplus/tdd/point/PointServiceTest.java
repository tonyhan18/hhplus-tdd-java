package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Test
    void getPointById_ReturnsValidUserPoint_WhenIdExists() {
        // Given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 100, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(expectedUserPoint);

        // When
        UserPoint result = new PointService(userPointTable, pointHistoryTable).getPointById(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedUserPoint, result);
    }

//    @Test
//    void getPointById_ReturnsEmptyUserPoint_WhenIdDoesNotExist() {
//        // Given
//        long userId = 2L;
//        when(userPointTable.selectById(userId)).thenReturn(null);
//
//        // When
//        UserPoint result = new PointService(userPointTable, pointHistoryTable).getPointById(userId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(UserPoint.empty(userId), result);
//    }
}
