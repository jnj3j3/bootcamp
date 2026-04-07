package com.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Slf4j
public class ReentrantCounter {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 3;
        ReentrantCounter counter = new ReentrantCounter();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(counter::increaseCount);
            thread.start();
            threads.add(thread);
        }

        //thread 종료까지 wait
        threads.forEach(thread ->
        {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("expected: {}", threadCount);
        log.info("actual: {}", counter.getCount());
    }

    // sync 매서드 실행 자체에 대해 락을 얻어 순서 제어
    private void increaseCount() {
        this.lock.lock();
        //while문을 통해 시간 제어
//        while (true) {
//            try {
//                if (this.lock.tryLock(3, TimeUnit.SECONDS)) {
//                    try {
//                        log.info("락 획득 성공!");
//                        this.count++;
//                        Thread.sleep(4000);
//                        break;
//                    } finally {
//                        this.lock.unlock();
//                    }
//                } else {
//                    log.info("락 획득 실패 → 재시도");
//                }
//            } catch (InterruptedException e) {
//                log.info("작업 중단");
//                Thread.currentThread().interrupt();
//                break;
//            }
//
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//    }
        try {
            if (this.lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    log.info("locked start calculating");
                    this.count++;
                    Thread.sleep(4000);
                } finally {
                    this.lock.unlock();
                }
            } else {
                log.info("lock failed");
            }
        } catch (InterruptedException e) {
            log.info("interrupted");
            throw new RuntimeException(e);
        }

    }
}
