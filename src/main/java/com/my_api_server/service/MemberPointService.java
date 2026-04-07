package com.my_api_server.service;

import com.my_api_server.entity.Member;
import com.my_api_server.repo.MemberDBRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberPointService {
    private final MemberDBRepo memberDBRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeAllUserData() {
        List<Member> members = memberDBRepo.findAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void supportTxTest() {
        //db를 사용하지 않는, 단순 자바 코드 실행하거나 혹인 readyonly=true주로 최적화된 읽기를 사용할때 가끔
        memberDBRepo.findAll();
    }

    @Transactional(timeout = 2) //-1이면 무한(기본))
    public void timeout() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        memberDBRepo.findAll();
    }
}
