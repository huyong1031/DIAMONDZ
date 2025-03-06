package com.himedia.spserver.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MemberDTO extends User {

    public MemberDTO(
                    String username,
                    String password,
                    String name,
                    String phone,
                    String email,
                    String address1,
                    String address2,
                    String address3,
                    String zip_num,
                    BigDecimal points,
//                    LocalDate memberBirthDate,
//                    Timestamp inDate,
                    String provider,
                    boolean birthdateRewarded,
                    List<String> roleNames ) {
                        super(username, password,
                            roleNames.stream().map(
                                str -> new SimpleGrantedAuthority("ROLE_" + str)
                ).collect(Collectors.toList())   // List의 내용들에  ROLE_ 를 앞에 붙여서 다시 리스트로 구성
        );
        this.memberId = username;
        this.memberPwd = password;
        this.memberName = name;
        this.memberPhone = phone;
        this.memberEmail=email;
        this.memberAddress1=address1;
        this.memberAddress2=address2;
        this.memberAddress3=address3;
        this.zipNum=zip_num;
        this.memberPoints=points;
//        this.memberBirthdate=memberBirthDate;
//        this.inDate=inDate;
        this.provider=provider;
        this.roleNames = roleNames;
        this.birthdateRewarded=birthdateRewarded;

    }



    private String memberId;
    private String memberPwd;
    private String memberName;
    private String memberPhone;
    private String memberEmail;
    private String memberAddress1;
    private String memberAddress2;
    private String memberAddress3;
    private String zipNum;
    private BigDecimal memberPoints = BigDecimal.ZERO;
//    private LocalDate memberBirthdate;
//    private Timestamp inDate;
    private String provider = "local";
    private boolean birthdateRewarded = false;
    private List<String> roleNames = new ArrayList<String>();



    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", memberId);
        dataMap.put("memberPwd", memberPwd);
        dataMap.put("memberName", memberName);
        dataMap.put("memberPhone", memberPhone);
        dataMap.put("memberEmail", memberEmail);
        dataMap.put("memberAddress1", memberAddress1);
        dataMap.put("memberAddress2", memberAddress2);
        dataMap.put("memberAddress3", memberAddress3);
        dataMap.put("zipNum", zipNum);
        dataMap.put("memberPoints", memberPoints);
//        dataMap.put("memberBirthdate", memberBirthdate);
//        dataMap.put("inDate", inDate);
        dataMap.put("provider", provider);
        dataMap.put("birthdateRewarded", birthdateRewarded);
        dataMap.put("roleNames", roleNames);
        return dataMap;
    }

    }

