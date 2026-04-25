package edu.se.extweb;

import edu.se.extweb.model.Item;
import edu.se.extweb.repository.ItemRepository;
import edu.se.extweb.request.ItemPageRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.PaginationMetaData;
import edu.se.extweb.service.ItemService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServicePagingTest {


    @Autowired
    private ItemService underTest;

    @MockitoSpyBean
    private ItemRepository itemRepository;

    List<Item> items = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void setUp() {
    }
    @AfterEach
    void tearsDown(){
    }

    @Test
    @Order(1)
    void whenHappyPathThenOk(){
        // given
        ItemPageRequest request = new ItemPageRequest(0,5);
        // when
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        //then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());

        assertEquals(0, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(6, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(5, response.getData().size());
        assertEquals("69ecf0368db92d68825b4971", response.getData().get(0).getId());
    }

    @Test
    @Order(2)
    void whenSizeIs_7_AndPageIs_4_ThenIsLast_TrueAndSizeEquals_2(){
        // given
        ItemPageRequest request = new ItemPageRequest(4, 7);
        // when
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());
        assertTrue(response.getMeta().isLast());
        assertEquals(2, response.getData().size());
    }

    @Test
    @Order(3)
    void whenPageValueIsOutOfRangeThenErrorMessageHasTheWarning(){
        // given
        ItemPageRequest request = new ItemPageRequest(100, 5);
        // when
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        // then
        assertNotNull(response.getMeta().getErrorMessage());
        assertTrue(response.getMeta().getErrorMessage().startsWith("Warning:"));
    }

    @Test
    @Order(4)
    void whenTheListIsEmptyThenMetadataAndDataAreNotNull(){
        // given
        doReturn(Page.empty()).when(itemRepository).findAll(any(Pageable.class));
        ItemPageRequest request = new ItemPageRequest(0, 5);
        // when
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());
        assertNotNull(response.getData());
        Mockito.reset(itemRepository);
    }

    @Test
    @Order(5)
    void whenTheListIsEmptyThenErrorMessageHasTheWarning(){
        // given
        doReturn(Page.empty()).when(itemRepository).findAll(any(Pageable.class));
        ItemPageRequest request = new ItemPageRequest(0, 5);
        // when
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        // then
        assertNotNull(response.getMeta().getErrorMessage());
        assertTrue(response.getMeta().getErrorMessage().startsWith("Warning:"));
        Mockito.reset(itemRepository);
    }

}
