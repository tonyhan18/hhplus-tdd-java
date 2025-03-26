package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Point History Table 단위 테스트")
public class PointHistoryTableTest {
    /**
     * 언제 Mock 객체를 쓰고 언제 진짜 객체를 써야할까?
     * 단위 테스트는 **코드의 개별 단위를 고립시켜 검증**하는 것을 목표로 합니다.
     * - 외부 의존성을 제거하고, 테스트 대상 클래스 **자체의 로직**이 잘 동작하는지 확인하는 것이 핵심
     *
     * ### Mock 객체는 **외부 의존성을 대체**하여 클래스의 **행위를 고립**시켜 테스트하기 위해 사용됩니다.
     * 따라서, 클래스가 다른 객체(의존성)를 사용하는 경우 해당 객체를 Mock으로 주입하는 것이 일반적
     *
     * ### **진짜 객체(실제 의존성)를 써야 할 경우**
     * 1. **PointHistoryTable이 의존성이 없는 순수한 객체**라면
     * 2. **통합 테스트가 목적**인 경우
     * */
    private final PointHistoryTable pointHistoryTable = new PointHistoryTable();

    @Nested
    @DisplayName("Insert 함수 단위 테스트")
    class PointHistoryTableInsertTest {
        @Test
        @DisplayName("성공_PointHistory_삽입_테스트")
        void insertChargeHistory_ReturnsValidPointHistory_WhenValidInput() {
            // given
            long userId = 1L;
            long amount = 100L;
            TransactionType type = TransactionType.CHARGE;
            long updateMillis = System.currentTimeMillis();

            // when
            PointHistory result = pointHistoryTable.insert(userId, amount, type, updateMillis);

            // then
            assertNotNull(result);
            assertEquals(userId, result.userId());
            assertEquals(amount, result.amount());
            assertEquals(type, result.type());
            assertEquals(updateMillis, result.updateMillis());
        }

        @Test
        @DisplayName("성공_사용_이력_테스트")
        void insertUseHistory_ReturnsValidPointHistory_WhenValidInput() {
            // given
            long userId = 1L;
            long amount = 100L;
            TransactionType type = TransactionType.USE;
            long updateMillis = System.currentTimeMillis();

            // when
            PointHistory result = pointHistoryTable.insert(userId, amount, type, updateMillis);

            // then
            assertNotNull(result);
            assertEquals(userId, result.userId());
            assertEquals(amount, result.amount());
            assertEquals(type, result.type());
        }

        @Test
        @DisplayName("성공_ID_증가_테스트")
        void insertIncrementsIdSequentially_WhenMultipleHistoriesInserted() {
            // given
            long userId = 1L;
            TransactionType type = TransactionType.CHARGE;

            // when
            PointHistory firstInsert = pointHistoryTable.insert(userId, 100L, type, System.currentTimeMillis());
            PointHistory secondInsert = pointHistoryTable.insert(userId, 200L, type, System.currentTimeMillis());
            PointHistory thirdInsert = pointHistoryTable.insert(userId, 300L, type, System.currentTimeMillis());

            // then
            assertEquals(firstInsert.id() + 1, secondInsert.id());
            assertEquals(secondInsert.id() + 1, thirdInsert.id());
        }
    }

    @Nested
    @DisplayName("selectAllByUserId 단위 테스트")
    class PointHistoryTableSelectAllByUserIdTest {
        @Test
        @DisplayName("성공_UserId_기반_PointHistory_조회_테스트")
        void selectAllByUserId_ReturnsValidPointHistoryList_WhenValidInput() {
            // given
            long userId = 1L;
            pointHistoryTable.insert(userId, 100L, TransactionType.CHARGE, System.currentTimeMillis());
            pointHistoryTable.insert(userId, 200L, TransactionType.USE, System.currentTimeMillis());
            pointHistoryTable.insert(2L, 50L, TransactionType.CHARGE, System.currentTimeMillis()); // 다른 userId 데이터

            // when
            var result = pointHistoryTable.selectAllByUserId(userId);

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(
                    result.stream()
                            .allMatch(pointHistory -> pointHistory.userId() == userId)
            );
        }


        @Test
        @DisplayName("실패_존재하지_않는_UserId_조회시_빈_리스트_반환_테스트")
        void selectAllByUserId_ReturnsEmptyList_WhenUserIdNotExists() {
            // given
            long nonExistentUserId = 999L;

            // when
            var result = pointHistoryTable.selectAllByUserId(nonExistentUserId);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty(), "Result should be an empty list for non-existent userId");
        }

        @Test
        @DisplayName("성공_이력_삽입_순서대로_조회되는_테스트")
        void selectAllByUserId_ReturnsHistoriesInInsertionOrder_WhenMultipleHistoriesInserted() {
            // given
            long userId = 1L;

            PointHistory firstInsert = pointHistoryTable.insert(userId, 100L, TransactionType.CHARGE, System.currentTimeMillis());
            PointHistory secondInsert = pointHistoryTable.insert(userId, 200L, TransactionType.USE, System.currentTimeMillis());
            PointHistory thirdInsert = pointHistoryTable.insert(userId, 300L, TransactionType.CHARGE, System.currentTimeMillis());

            // when
            var result = pointHistoryTable.selectAllByUserId(userId);

            // then
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(firstInsert.id(), result.get(0).id());
            assertEquals(secondInsert.id(), result.get(1).id());
            assertEquals(thirdInsert.id(), result.get(2).id());
        }
        
        
    }
}
