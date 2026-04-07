package com.my_api_server.controller;

import ch.qos.logback.core.util.StringUtil;
import com.my_api_server.service.MemberDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vi/member")
public class MemberController {
    private final MemberDBService memberService;

    @PostMapping
    public Long signUp(@Validated @RequestBody MemberSignUpDto dto) {
        if (StringUtil.isNullOrEmpty(dto.email()) || StringUtil.isNullOrEmpty(dto.password())) {
            new RuntimeException("email or password가 빈값이 되면 안됩니다");
        }
        Long memberId = null;
        try {
            memberId = memberService.signUp(dto.email(), dto.password());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return memberId;
    }

//    @GetMapping("/{id}")
//    public Member findMember(@PathVariable Long id) {
//        Member member = memberService.findMember(id);
//        return member;
//
//    }

    @GetMapping("/test")
    public void test() {
        memberService.tx1();
    }
}
