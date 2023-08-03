package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDataType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDataType) || searchDataType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDataType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDataType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDataType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDataType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("itemName", searchBy)) {
            return QItem.item.itemName.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()),
                        categoryNotLike("custom") // 상품관리 페이지에 custom 카테고리는 제외
                )
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Item> content = results.getResults();
        Long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNameLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemName.like("%"+searchQuery+"%");
    }

    private BooleanExpression categoryLike(String searchCategory){
        return StringUtils.isEmpty(searchCategory) ? null : QItem.item.itemCategory.like("%"+searchCategory+"%");
    }

    private BooleanExpression categoryNotLike(String searchCategory){
        return StringUtils.isEmpty(searchCategory) ? null : QItem.item.itemCategory.notLike("%"+searchCategory+"%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemName, item.itemDetail, itemImg.imgUrl, item.price, item.itemCategory))
                .from(itemImg)
                .join(itemImg.item , item)
                .where(itemImg.repImgYn.eq("Y")) //대표 이미지 가져옴
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getCategoryPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemName, item.itemDetail, itemImg.imgUrl, item.price, item.itemCategory))
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(categoryLike(itemSearchDto.getSearchCategory()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }


}
