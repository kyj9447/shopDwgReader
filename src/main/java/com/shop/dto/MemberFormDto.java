package com.shop.dto;

import com.shop.constant.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class MemberFormDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;
    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length( min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;
    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;
    @NotEmpty(message = "전화번호는 필수 입력 값입니다.")
    private String number;
    //@NotEmpty(message = "[테스트용] ADMIN, USER 선택")
    private Role role;
}

