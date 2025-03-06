package com.himedia.spserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    @Id
    @Column(name = "adminid", length = 50, nullable = false, unique = true)
    private String adminId;

    @Column(name = "adminpwd", length = 300, nullable = false)
    private String adminPwd;

    @Column(name = "adminname", length = 50, nullable = false)
    private String adminName;

    @Column(name = "adminphone", length = 20, nullable = false)
    private String adminPhone;

    @CreationTimestamp
    @Column(name = "registerdate", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp registerDate;

}