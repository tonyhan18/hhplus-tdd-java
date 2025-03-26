package io.hhplus.tdd.database;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserPointTable 단위 테스트")
public class UserPointTableTest {

    private final UserPointTable userPointTable = new UserPointTable();

    @Nested
    @DisplayName("통합 테스트")
    class UserPointTableIntegrationTest {
        @Test
        @DisplayName("업데이트된 포인트를 selectById로 조회할 수 있는지 테스트")
        void testSelectByIdAfterUpdate() {
            // Given
            long userId = 100L;
            long initialPoint = 250L;
            long updatedPoint = 500L;

            // When
            userPointTable.insertOrUpdate(userId, initialPoint);
            userPointTable.insertOrUpdate(userId, updatedPoint);
            UserPoint result = userPointTable.selectById(userId);

            // Then
            assertNotNull(result);
            assertEquals(userId, result.id());
            assertEquals(updatedPoint, result.point());
        }
    }

    @Nested
    @DisplayName("SelectById 단위 테스트")
    class SelectByIdTest {
        @Test
        @DisplayName("저장되지 않은 사용자의 selectById 테스트")
        void testSelectByIdForUnregisteredUser() {
            // Given
            long userId = 12345L;

            // When
            UserPoint result = userPointTable.selectById(userId);

            // Then
            assertNotNull(result);
            assertEquals(userId, result.id());
            assertEquals(0L, result.point());
        }
    }

    @Nested
    @DisplayName("InsertOrUpdate 단위 테스트")
    class InsertOrUpdateTest {
        @Test
        @DisplayName("신규 사용자의 InsertOrUpdate 성공 테스트")
        void testInsertOrUpdateForNewUser() {
            // Given
            long newUserId = 1L;
            long newUserPoint = 500L;

            // When
            UserPoint newUser = userPointTable.insertOrUpdate(newUserId, newUserPoint);

            // Then
            assertNotNull(newUser);
            assertEquals(newUserId, newUser.id());
            assertEquals(newUserPoint, newUser.point());
        }

        @Test
        @DisplayName("기존 사용자의 InsertOrUpdate 성공 테스트")
        void testInsertOrUpdateForExistingUser() {
            // Given
            long existingUserId = 2L;
            long initialPoint = 300L;
            long updatedPoint = 200L;
            userPointTable.insertOrUpdate(existingUserId, initialPoint);

            // When
            UserPoint updatedUser = userPointTable.insertOrUpdate(existingUserId, updatedPoint);

            // Then
            assertNotNull(updatedUser);
            assertEquals(existingUserId, updatedUser.id());
            assertEquals(updatedPoint, updatedUser.point());
        }
    }
}
