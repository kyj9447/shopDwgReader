package com.shop.service;


import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.CustomItem;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.CustomItemRepository;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final CustomItemRepository customItemRepository;


    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i == 0)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            if (itemImgFileList.get(i).getSize() != 0) { // 내용이 있을때만 등록
                log.info("이미지 사이즈 " + itemImgFileList.get(i).getSize());
                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            }
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for (ItemImg itemimg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getCategoryPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getCategoryPage(itemSearchDto, pageable);
    }

    public Long saveCustomItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        CustomItem item = itemFormDto.createCustomItem();
        customItemRepository.save(item);

        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setCustomItem(item);
            if (i == 0)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveCustomItemImg(itemImg, itemImgFileList.get(i));
        }
        return item.getId();
    }

    //Transactional(readOnly = true)
    public List<String> getItemImgsByItemId(Long id) {
        List<ItemImg> itemImgList = itemImgRepository.findImgUrlByItemId(id);
        List<String> itemImgUrlList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            itemImgUrlList.add(itemImg.getImgUrl());
        }

        return itemImgUrlList;
    }

    // public void deleteItemByItemId(Long id) throws Exception{
    //     FileService fileService = new FileService(); // 파일 서비스 호출용
    //     List<ItemImg> itemImgList = itemImgRepository.findImgUrlByItemId(id); // 해당 id의 아이템 이미지 리스트

    //     // 1. 해당 id 아이템의 이미지 백업
    //     for (ItemImg itemImg : itemImgList) {
    //         String imgurl = itemImg.getImgUrl();
    //         String backupFolder = "backup/"+id;
    //         fileService.moveFile(imgurl,backupFolder);
    //     }
        
    //     // 2. 원본 이미지 삭제
    //     for (ItemImg itemImg : itemImgList) {
    //         String imgurl = itemImg.getImgUrl();
    //         fileService.deleteFile(imgurl);
    //     }

    //     // 3. db의 itemImg 삭제, item 삭제

    //     itemRepository.deleteItemImgById(id);
    //     itemRepository.deleteItemById(id);

    // }
}
