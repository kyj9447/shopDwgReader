package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{

    @Id // 기본키
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(name = "category", nullable = false)
    private String itemCategory;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Lob
    private String itemRequest;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    @ManyToMany
    @JoinTable(name = "member_item", joinColumns = @JoinColumn(name = "member_id"),inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Member> member;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemRequest = itemFormDto.getItemRequest();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: "+this.stockNumber+")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}
