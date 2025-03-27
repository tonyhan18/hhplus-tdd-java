package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/* Spring 컨테이너에 클래스를 등록하는 부분 -> 이제 AutoWired로 끌어올 수 있음*/
@Component
public class LockManager {
    /**
     *  Long -> 사용자 ID, ReentrantLock -> 사용자에 해당하는 Thread-Safe한 ReentrantLock 객체 할당
     *  ReentrantLock : 다중 스레드 환경에서 **임계 구역(Critical Section)**에 대한 안전한 접근을 보장하기 위해 사용,
     *          단, 명시적으로 **unlock()**을 호출, 복잡성 증가, 성능 오버헤드
     * ConcurrentHashMap은 다중 스레드 환경에서도 안전하게 데이터를 삽입하고 접근할 수 있도록 동시성 처리가 보장
     * */
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public ReentrantLock getLock(long userId) {
        /**
         * 주어진 userId가 맵에 없을 경우, 해당 ID를 키로 새로운 락을 생성하여 삽입 후 반환
         * */
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }
}
