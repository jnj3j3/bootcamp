package com.my_api_server.repo;

import com.my_api_server.entity.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class MemberRepo {
    Map<Long, Member> members = new HashMap<>();

    //저장
    public Long saveMember(String email, String password) {
//        long id = Long.parseLong(UUID.randomUUID().toString());
        Random random = new Random();
        Long id = random.nextLong();

        Member member = Member.builder().id(id).email(email).password(password).build();
        members.put(id, member);
        return id;
    }

    //조회
    public Member findMember(Long id) {
        return members.get(id);
    }
    
}
