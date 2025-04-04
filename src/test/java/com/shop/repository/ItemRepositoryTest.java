package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setItemCategory("TEST");
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        log.info("결과 : " + savedItem.toString());
    }

    public void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setItemCategory("TEST");
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNameOrItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNameOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item : itemList) {
            log.info("결과");
            log.info("아이템 이름 :" + item.getItemName());
            log.info("아이템 가격 :" + item.getPrice());
            log.info("아이템 재고 :" + item.getStockNumber());
            log.info("아이템 설명 :" + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThan() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item : itemList) {
            log.info("결과");
            log.info("아이템 이름 :" + item.getItemName());
            log.info("아이템 가격 :" + item.getPrice());
            log.info("아이템 재고 :" + item.getStockNumber());
            log.info("아이템 설명 :" + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList) {
            log.info("결과");
            log.info("아이템 이름 :" + item.getItemName());
            log.info("아이템 가격 :" + item.getPrice());
            log.info("아이템 재고 :" + item.getStockNumber());
            log.info("아이템 설명 :" + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList) {
            log.info("결과");
            log.info("아이템 이름 :" + item.getItemName());
            log.info("아이템 가격 :" + item.getPrice());
            log.info("아이템 재고 :" + item.getStockNumber());
            log.info("아이템 설명 :" + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("@nativeQuery를 이용한 상품 조회 테스트")
    public void findByItemDetailByNativeTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList) {
            log.info("결과");
            log.info("아이템 이름 :" + item.getItemName());
            log.info("아이템 가격 :" + item.getPrice());
            log.info("아이템 재고 :" + item.getStockNumber());
            log.info("아이템 설명 :" + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("Querydsl 조회테스트 1")
    public void queryDslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();

        for (Item item : itemList) {
            log.info("결과");
            log.info("아이템 이름 :" + item.getItemName());
            log.info("아이템 가격 :" + item.getPrice());
            log.info("아이템 재고 :" + item.getStockNumber());
            log.info("아이템 설명 :" + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 1")
    public void createItemList2() {
        for (int i = 1; i <= 5; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setItemCategory("TEST");
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i = 6; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setItemCategory("TEST");
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 1")
    public void queryDslTest2() {
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // 불리언 추가용 객체
        QItem item = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }
        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder,pageable);

        log.info("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem : resultItemList) {
            log.info("결과");
            log.info("아이템 이름 :" + resultItem.getItemName());
            log.info("아이템 가격 :" + resultItem.getPrice());
            log.info("아이템 재고 :" + resultItem.getStockNumber());
            log.info("아이템 설명 :" + resultItem.getItemDetail());
        }
    }



}