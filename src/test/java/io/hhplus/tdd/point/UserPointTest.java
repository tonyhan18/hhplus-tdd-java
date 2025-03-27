package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * | **기능** | `import` | `import static` |
 * | --- | --- | --- |
 * | **대상** | 클래스 또는 인터페이스 | 정적 필드 또는 정적 메서드 |
 * | **멤버 접근 방식** | 클래스 이름으로 접근 | 클래스 이름 없이 직접 접근 |
 * | **주로 사용하는 경우** | 특정 클래스를 많이 사용할 때 | 정적 유틸리티 메서드나 상수 사용 시 |
 * | **가독성 고려** | 항상 명확하고 직관적임 | 과도하게 사용하면 코드가 모호해질 수 있음 |
 * */

import static io.hhplus.tdd.point.TransactionType.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserPointTest {
    /**
     * `@Nested`는 **JUnit 5**에서 제공하는 어노테이션입니다.
     * `@Nested`를 사용하면 테스트 클래스 내부에 **중첩 클래스**를 정의할 수 있으며,
     * 이 중첩 클래스는 테스트 관련 시나리오를 **보다 구체적으로 구조화**하고 **읽기 쉽게 분리**하는 데 사용됩니다.
     */
    @Nested
    class CommonTest{
        @Test
        @DisplayName("실패_요청_포인트_음수")
        void 실패_요청_포인트_음수_IllegalArgumentException_반환()
        {
            // given
            UserPoint userPoint = new UserPoint(1L, 100, System.currentTimeMillis());
            long chargingAmount = -101;

            // when & then
            assertAll(
                    "요청 포인트 검증",
                    ()->{
                        IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userPoint.chkForValidate(CHARGE, chargingAmount));
                        assertEquals("요청한 포인트가 0 보다 커야 합니다.", exception.getMessage());
                    },
                    ()->{
                        IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userPoint.chkForValidate(USE, chargingAmount));
                        assertEquals("요청한 포인트가 0 보다 커야 합니다.", exception.getMessage());
                    }
            );
        }

        @Test
        @DisplayName("실패_포인트_0을_요청")
        void 실패_포인트_0을_요청_IllegalArgumentException_반환()
        {
            // given
            UserPoint userPoint = new UserPoint(1L, 100, System.currentTimeMillis());
            long chargingAmount =0;

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userPoint.chkForValidate(CHARGE, chargingAmount));
            assertEquals("요청한 포인트가 0 보다 커야 합니다.", exception.getMessage());

            exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userPoint.chkForValidate(USE, chargingAmount));
            assertEquals("요청한 포인트가 0 보다 커야 합니다.", exception.getMessage());
        }
    }

    @Nested
    class ChargeTest {
        @Test
        @DisplayName("성공_요금 충전 테스트")
        void 성공_요금_충전_테스트()
        {
            // given
            UserPoint userPoint = new UserPoint(1L, 100, System.currentTimeMillis());
            long chargingAmount = 500;

            // when & then
            assertDoesNotThrow(() -> userPoint.chkForValidate(CHARGE, chargingAmount));
        }

        @Test
        @DisplayName("실패_포인트_충전_초과")
        void 실패_포인트_충전_초과_IllegalArgumentException_반환()
        {
            // given
            UserPoint userPoint = new UserPoint(1L, 9000, System.currentTimeMillis());
            long chargingAmount = 1001;

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userPoint.chkForValidate(CHARGE, chargingAmount));

            assertEquals("포인트 한도를 초과했습니다.", exception.getMessage());
        }
    }

    @Nested
    class UseTest {
        @Test
        @DisplayName("성공_요금 사용 테스트")
        void 성공_요금_사용_테스트()
        {
            // given
            UserPoint userPoint = new UserPoint(1L, 1000, System.currentTimeMillis());
            long chargingAmount = 500;

            // when & then
            assertDoesNotThrow(() -> userPoint.chkForValidate(USE, chargingAmount));
        }

        @Test
        @DisplayName("실패_포인트_사용_초과")
        void 실패_포인트_사용_초과_IllegalArgumentException_반환()
        {
            // given
            UserPoint userPoint = new UserPoint(1L, 1000, System.currentTimeMillis());
            long chargingAmount = 1001;

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userPoint.chkForValidate(USE, chargingAmount));

            assertEquals("포인트가 부족합니다.", exception.getMessage());
        }
    }
}
