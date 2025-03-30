package com.shop.config;

public enum SocialType {
    KAKAO("kakao");

    private String name;

    SocialType(String name) { this.name = name; }

    public String getRoleType() { return "ROLE_KAKAO"; }

    public String getValue() { return name; }

    public boolean isEquals(String authority) { return this.getRoleType().equals(authority);}
}