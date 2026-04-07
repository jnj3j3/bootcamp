package com.my_api_server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class MemberSignUpListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxRetries = 3)
    public void sendNotification(MemberSignUpEvent event) {
        log.info("member ID = {}", event.getId());
        log.info("member email = {}", event.getEmail());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("email 전송 완료");
    }
}
