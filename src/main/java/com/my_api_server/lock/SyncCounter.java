package com.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class SyncCounter {
    private int count = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 3;
        SyncCounter counter = new SyncCounter();
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
    private synchronized void increaseCount() {
        Thread.State state = Thread.currentThread().getState();
        log.info("thread state: {}", state);
//        synchronized (this) {
//            log.info("thread state: {}", state);
//            count++;
//        }
        log.info("thread state: {}", state);
    }
}
