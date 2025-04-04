package com.shop.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {
    private Long id;
    private String itemName;
    private String itemDetail;
    private String imgUrl;
    private Integer price;
    private String itemCategory;


    @QueryProjection
    public MainItemDto(Long id, String itemName, String itemDetail, String imgUrl, Integer price, String itemCategory) {
        this.id = id;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.itemCategory = itemCategory;
    }
}
