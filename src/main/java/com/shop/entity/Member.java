package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "member", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "loginType"}))
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member extends BaseEntity{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String address;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String number;
    @Column
    private String picture;
    @Column(nullable = false)
    private String loginType;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setNumber(memberFormDto.getNumber());
        member.setRole(memberFormDto.getRole());
        member.setLoginType("normal");

        return member;
    }

    @Builder
    public Member(String name, String email, String picture, Role role, String loginType){
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.loginType = loginType;
    }

    public Member update(String name, String picture){
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey(){
        return this.role.toString();
    }

}
