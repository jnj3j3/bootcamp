package com.my_api_server.common;

import com.my_api_server.entity.Member;

public class MemberFixture {
    // email is fixed
    public static Member.MemberBuilder defaultMember() {
        return Member.builder()
                .email("test1@gmail.com");

    }

    ;
}
