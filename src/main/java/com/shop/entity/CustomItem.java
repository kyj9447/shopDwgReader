package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "customItem")
@Getter
@Setter
@ToString
public class CustomItem extends Item{

    @Id // 기본키
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(name = "price", nullable = false)
    private int price;

    @Column
    private int thick;

    @OneToMany
    @JoinTable(name = "member_item", joinColumns = @JoinColumn(name = "member_id"),inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Member> member;

}
