package com.my_api_server.service;

import com.my_api_server.entity.Member;
import com.my_api_server.event.MemberSignUpEvent;
import com.my_api_server.repo.MemberDBRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDBService {
    private final MemberDBRepo memberDBRepo;
    private final MemberPointService memberPointService;
    private final ApplicationEventPublisher publisher;

    @Transactional(rollbackFor = IOException.class)
    public Long signUp(String email, String password) throws IOException {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();
        Member savedMember = memberDBRepo.save(member);
//        sendNotification();
        memberPointService.changeAllUserData();
        publisher.publishEvent(new MemberSignUpEvent(savedMember.getId(), savedMember.getEmail()));

//        throw new RuntimeException("something happend");
//        throw new IOException("I/O EXCEPTION OCCURRED");
//        sendNotification();
        return savedMember.getId();
    }

    @Transactional
    public Long signUp2(String email, String password) {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();
        Member savedMember = memberDBRepo.save(member);
        sendNotification();
        return savedMember.getId();
    }

    public void sendNotification() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("email 전송 완료");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeAllUserData() {
        List<Member> members = memberDBRepo.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, timeout = 2)
    public void tx1() {
        List<Member> members = memberDBRepo.findAll();
        members.stream().forEach((m) -> {
            log.info("member id = {}", m.getId());
            log.info("member email = {}", m.getEmail());
        });
        memberPointService.changeAllUserData();
        memberPointService.timeout();
    }
}

