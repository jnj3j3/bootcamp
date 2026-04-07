package com.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class Counter {
    private int count = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 200000;
        Counter counter = new Counter();
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

    private void increaseCount() {
        count++;
    }
}
