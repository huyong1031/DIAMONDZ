package com.himedia.spserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Member {

    @Id
    @Column(name = "memberid", length = 50, nullable = false)
    private String memberId;

    @Column(name = "memberpwd", length = 300, nullable = true)
    private String memberPwd;

    @Column(name = "membername", length = 50, nullable = false)
    private String memberName;

    @Column(name = "memberphone", length = 50, nullable = true)
    private String memberPhone;

    @Column(name = "memberemail", length = 100, nullable = true)
    private String memberEmail;

    @Column(name = "memberaddress1", length = 100)
    private String memberAddress1;

    @Column(name = "memberaddress2", length = 100)
    private String memberAddress2;

    @Column(name = "memberaddress3", length = 100)
    private String memberAddress3;

    @Column(name = "zip_num", length = 20)
    private String zipNum;

    @Column(name = "points", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private BigDecimal memberPoints = BigDecimal.ZERO;

    @Column(name = "memberbirthdate", nullable = true)
    private LocalDate memberBirthdate;

    @CreationTimestamp
    @Column(name = "indate", nullable = false, updatable = false)
    private Timestamp inDate; // 가입일 (자동 생성)

    @Column(name = "provider", nullable = false)
    private String provider = "local";

    @Column(name = "birthdate_rewarded", nullable = false)
    private boolean birthdateRewarded = false;

    @ElementCollection(fetch = FetchType.LAZY)  // 테이블의 리스트가 아니라 단순데이터(String, Integer 등)이라고 MySQL 에 알려주는 어너테이션
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<MemberRole>();

    public boolean isBirthdateRewarded() {
        return birthdateRewarded;
    }


}
