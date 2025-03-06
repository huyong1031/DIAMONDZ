package com.himedia.spserver.security.service;

import com.himedia.spserver.dto.MemberDTO;
import com.himedia.spserver.entity.Member;
import com.himedia.spserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    final MemberRepository mr;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername-username: " + username + "-----------------------");


        Member member = mr.getWithRoles(username);
        if (member == null) {
            throw new UsernameNotFoundException(username + " -User Not Found");
        }

        MemberDTO memberdto = new MemberDTO(
                member.getMemberId(),
                member.getMemberPwd(),
                member.getMemberName(),
                member.getMemberPhone(),
                member.getMemberEmail(),
                member.getMemberAddress1(),
                member.getMemberAddress2(),
                member.getMemberAddress3(),
                member.getZipNum(),
                member.getMemberPoints(),
//                member.getMemberBirthdate(),
//                member.getInDate(),
                member.getProvider(),
                member.isBirthdateRewarded(),
                member.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        System.out.println("member" + member);
        System.out.println("memberdto" + memberdto);


        return memberdto;
    }
}
