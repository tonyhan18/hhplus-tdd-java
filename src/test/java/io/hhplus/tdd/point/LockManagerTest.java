package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class LockManagerTest {
    private final LockManager lockManager = new LockManager();

    @Test
    @DisplayName("동일한 사용자 ID로 동일한 락 객체를 반환하는지 테스트")
    void testSameLockForSameUserId() {
        long userId = 1L;

        // When
        var lock1 = lockManager.getLock(userId);
        var lock2 = lockManager.getLock(userId);

        // Then
        assertSame(lock1, lock2, "락 매니저는 동일한 락 객체의 ID를 반환해야 한다");
    }

    @Test
    @DisplayName("서로 다른 사용자 ID로 다른 락 객체를 반환하는지 테스트")
    void testDifferentLockForDifferentUserId() {
        long userId1 = 1L;
        long userId2 = 2L;

        // When
        var lock1 = lockManager.getLock(userId1);
        var lock2 = lockManager.getLock(userId2);

        // Then
        assertNotSame(lock1, lock2, "락 매니저는 서로 다른 락 객체의 ID를 반환해야 한다");
    }

    @Test
    @DisplayName("동일한 사용자 ID로 여러 스레드에서 동시에 락 객체를 요청하면 같은 객체를 반환하하고 첫 요청이 락을 먼저 획득 하는지 테스트")
    void testConcurrentLockRequestsForSameUserId() throws InterruptedException {
        // Given
        long userId = 1L;
        int threadCount = 10;
        /**
         * CountDownLatch = 모든 스레드를 동시에 실행하여 동시성을 테스트
         * */
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        /**
         * 각 스레드가 반환받은 ReentrantLock 객체를 저장합니다.
         * 나중에 락 객체들이 모두 동일한지 확인한다.
         * */
        ReentrantLock[] locks = new ReentrantLock[threadCount];
        boolean[] hasLockFirst = new boolean[1];
        /**
         * `ExecutorService`는 자바에서 스레드를 효율적으로 관리하고 실행하기 위한 **스레드 풀(Thread Pool)** 구현을 제공
         *      스레드 생성 및 제거 작업을 자동으로 처리하므로, **개별 스레드를 직접 생성하는 부담을 줄여줍니다**
         *      단, 리소스 관리와 종료 관리가 필요함
         * */
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            // Thread로 처리하는 방식 이번에는 ExecutorService를 사용했지만 기본 사용법도 알아놓으면 좋음
            //new Thread(()->{}).start();
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 준비될 때까지 대기
                    locks[index] = lockManager.getLock(userId);

                    if (index == 0) { // 첫번째 스레드가 lock 획득
                        locks[index].lock();
                        try {
                            hasLockFirst[0] = true; // 첫 번째 락 획득시 상태 변경
                        } finally {
                            locks[index].unlock(); // 락 해제
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();  // 스레드 종료
                }
            });
        }
        startLatch.countDown(); // 모든 스레드 시작
        endLatch.await(); // 각 스레드가 작업을 마칠때까지 대기

        // Then
        assertTrue(hasLockFirst[0], "첫 번째 스레드가 락을 먼저 획득해야 합니다."); // Ensure first thread acquired lock first
        for (int i = 1; i < threadCount; i++) {
            assertSame(locks[0], locks[i], "같은 user ID에 대해 모두 같은 스레드 객체를 받아야 한다");
        }
        executorService.shutdown();
    }


    @Test
    @DisplayName("락 객체의 잠금과 해제가 정상적으로 동작하는지 테스트")
    void testLockUnlockMechanism() throws InterruptedException {
        // Given
        long userId = 1L;
        ReentrantLock lock = lockManager.getLock(userId);

        // Ensure the lock is initially unlocked
        assertFalse(lock.isLocked(), "처음 : 락 안 걸려 있어야 함");

        // Locking the lock
        lock.lock();
        assertTrue(lock.isLocked(), "락 걸린거 확인되어야 함");

        // Unlocking the lock
        lock.unlock();
        assertFalse(lock.isLocked(), "종료 : 락 해제 된거 확인해야 함");
    }
    
    
    
}
