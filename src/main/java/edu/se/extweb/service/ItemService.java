package edu.se.extweb.service;


/*
  @author   george
  @project   proj-test
  @class  ItemService
  @version  1.0.0 
  @since 09.09.24 - 12.16
*/


import edu.se.extweb.model.Item;
import edu.se.extweb.repository.ItemRepository;
import edu.se.extweb.request.ItemCreateRequest;
import edu.se.extweb.request.ItemPageRequest;
import edu.se.extweb.request.ItemUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.response.PaginationMetaData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private List<Item> items = new ArrayList<>();

    {
        items.add(new Item( "Freddie Mercury", "Queen","vocal, piano"));
        items.add(new Item( "Paul McCartney", "Beatles","guitar"));
        items.add(new Item( "Till Lindemann", "Rammstein","vocal"));
        items.add(new Item( "John Lennon", "Beatles","piano"));
        items.add(new Item( "Brian May", "Queen","solo guitar"));
        items.add(new Item( "Tarja Turunen", "Nightwish","vocal"));
        items.add(new Item( "Roger Waters", "Pink Floyd","poet"));
    }

    //  @PostConstruct
    void init() {
        this.itemRepository.deleteAll();
        for(Item item : items) {
            create(item);
        }

    }
    //  CRUD   - create read update delete

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public Item create(ItemCreateRequest request) {
        return mapToItem(request);
    }

    public  Item update(Item item) {
        return itemRepository.save(item);
    }


    public void delById(String id) {
        itemRepository.deleteById(id);
    }

    private Item mapToItem(ItemCreateRequest request) {
        Item item = new Item(request.name(), request.code(), request.description());
        return item;
    }

    public Item update(ItemUpdateRequest request) {
        Item itemPersisted = itemRepository.findById(request.id()).orElse(null);
        if (itemPersisted != null) {
            Item itemToUpdate =
                    Item.builder()
                            .id(request.id())
                            .name(request.name())
                            .code(request.code())
                            .description(request.description())
                            .build();
            return itemRepository.save(itemToUpdate);

        }
        return null;
    }

    public List<Item> createAll(List<Item> items) {
        return itemRepository.saveAll(items);
    }

    public void deleteAll() {
        itemRepository.deleteAll();
    }

    //------------------------- 12 03 response impl ------------------------------
    public ApiResponse<BaseMetaData, Item> getByIdAsApiResponse(String id) {
        Item itemPersisted = itemRepository.findById(id).orElse(null);
        BaseMetaData baseMetaData = new BaseMetaData();
        if (itemPersisted != null) {
            ApiResponse<BaseMetaData, Item> response = new ApiResponse<>(baseMetaData, itemPersisted);
            return response;
        }

        return null;
    }

    public  ApiResponse<BaseMetaData, Item> getAllAsApiResponse() {
        return null;
    }

    public  ApiResponse<BaseMetaData, Item> updateAsApiResponse(Item item) {
        return null;
    }

    /////////////////   26.03 ////////////////////////////////

    public ApiResponse<PaginationMetaData, Item> getItemsPage(ItemPageRequest request) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(request.page(), request.size(), sort);
        Page<Item> page = itemRepository.findAll(pageable);

        PaginationMetaData metaData = new PaginationMetaData();
        metaData.setCode(200);
        metaData.setSuccess(true);
        metaData.setNumber(page.getNumber());
        metaData.setSize(page.getSize());
        metaData.setTotalElements(page.getTotalElements());
        metaData.setTotalPages(page.getTotalPages());
        metaData.setFirst(page.isFirst());
        metaData.setLast(page.isLast());

        if (page.getTotalElements() == 0) {
            metaData.setErrorMessage("Warning: No items found in the database");
            return new ApiResponse<>(metaData, new ArrayList<>());
        }

        if (page.getNumber() >= page.getTotalPages()) {
            int totalPages = page.getTotalPages();
            long totalElements = page.getTotalElements();
            int lastPageIndex = totalPages - 1;

            log.warn("Out of range: requested page {}, total pages: {}", request.page(), totalPages);

            List<Item> lastItems = buildFullLastPage(request.size(), totalElements, lastPageIndex, sort);

            metaData.setCode(404);
            metaData.setSuccess(false);
            metaData.setErrorMessage("Warning: Maximal page for the size is " + totalPages);
            metaData.setNumber(lastPageIndex);
            metaData.setSize(request.size());
            metaData.setTotalElements(totalElements);
            metaData.setTotalPages(totalPages);
            metaData.setFirst(true);
            metaData.setLast(true);

            return new ApiResponse<>(metaData, lastItems);
        }

        return new ApiResponse<>(metaData, page.getContent());
    }

    private List<Item> buildFullLastPage(int size, long totalElements, int lastPageIndex, Sort sort) {
        if (lastPageIndex == 0 || totalElements % size == 0) {
            return itemRepository.findAll(PageRequest.of(lastPageIndex, size, sort)).getContent();
        }

        List<Item> prevContent = itemRepository.findAll(PageRequest.of(lastPageIndex - 1, size, sort)).getContent();
        List<Item> lastContent = itemRepository.findAll(PageRequest.of(lastPageIndex, size, sort)).getContent();

        List<Item> combined = new ArrayList<>();
        combined.addAll(prevContent);
        combined.addAll(lastContent);

        int fromIndex = Math.max(0, combined.size() - size);
        return new ArrayList<>(combined.subList(fromIndex, combined.size()));
    }



}