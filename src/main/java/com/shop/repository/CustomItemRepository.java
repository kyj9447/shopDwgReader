package com.shop.repository;

import com.shop.entity.CustomItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CustomItemRepository extends JpaRepository<CustomItem, Long>, QuerydslPredicateExecutor<CustomItem>, ItemRepositoryCustom {

    // select  * from Item where ItemNm = itemNm or ItemDetail = itemDetail;
    List<CustomItem> findByPriceLessThan(Integer price);

    List<CustomItem> findByPriceLessThanOrderByPriceDesc(Integer price);
}
