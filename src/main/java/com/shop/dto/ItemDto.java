package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private String itemName;
    private String itemCategory;
    private Integer price;
    private String itemDetail;
    private String itemRequest;
    private String sellStatCd;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
