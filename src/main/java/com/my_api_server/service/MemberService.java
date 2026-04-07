package com.my_api_server.service;

import com.my_api_server.entity.Member;
import com.my_api_server.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

//
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepo memberRepo;

    @PostMapping
    public Long signUp(String email, String password) {
        log.info(email);
        Long memberId = memberRepo.saveMember(email, password);
        log.info("회원가입한 Member ID: {}", memberId);
        sendNotification();
        return memberId;
    }

    public void sendNotification() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("email 전송 완료");
    }

    public Member findMember(Long id) {
        Member member = memberRepo.findMember(id);
        return member;
    }
}
