package edu.se.extweb;


/*
  @author   george
  @project   ext-web
  @class  LoggingTest
  @version  1.0.0 
  @since 02.04.26 - 21.31
*/

import edu.se.extweb.model.Item;
import edu.se.extweb.request.ItemPageRequest;
import java.util.List;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.PaginationMetaData;
import edu.se.extweb.service.ItemService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoggingTest {

    @Autowired
    private ItemService underTest;


    @Test
    void testLoggingOutputBeforeMethodGetById(CapturedOutput output) {
        // given
        String id = "69ecf0368db92d68825b4971";
        // when
        Item item = underTest.getById(id);
        // then
        assertNotNull(item);
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("ItemService.getById"));
        assertTrue(output.toString().contains("69ecf0368db92d68825b4971"));
    }

    @Test
    void testLoggingOutputAfterMethodGetById(CapturedOutput output) {
        // given
        String id = "69ecf0368db92d68825b4971";
        // when
        Item item = underTest.getById(id);
        // then
        assertNotNull(item);
        assertTrue(output.toString().contains("ItemService.getById"));
        assertTrue(output.toString().contains("completed successfully"));
        assertTrue(output.toString().contains("69ecf0368db92d68825b4971"));
        assertTrue(output.toString().contains("Iggy")); // Name of the item with the given ID
    }


    @Test
    void testLoggingOutputBeforeMethodGetItemsPage(CapturedOutput output) {
        // given
        ItemPageRequest request = new ItemPageRequest(0, 5);
        // when
        ApiResponse<PaginationMetaData, Item> page = underTest.getItemsPage(request);
        // then
        assertNotNull(page);
        assertTrue(output.toString().contains("ItemService.getItemsPage"));
        assertTrue(output.toString().contains("0"));
        assertTrue(output.toString().contains("5"));
    }

    @Test
    void testLoggingOutputAfterMethodGetItemsPage(CapturedOutput output) {
        // given
        ItemPageRequest request = new ItemPageRequest(0, 5);
        // when
        ApiResponse<PaginationMetaData, Item> page = underTest.getItemsPage(request);
        // then
        assertNotNull(page);
        assertTrue(output.toString().contains("ItemService.getItemsPage"));
        assertTrue(output.toString().contains("completed successfully"));
        assertTrue(output.toString().contains("0"));
        assertTrue(output.toString().contains("5"));
    }

    @Test
    void testLoggingOutputBeforeMethodGetAll(CapturedOutput output) {
        // given / when
        List<Item> items = underTest.getAll();
        // then
        assertNotNull(items);
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("ItemService.getAll"));
    }

    @Test
    void testLoggingOutputAfterMethodGetAll(CapturedOutput output) {
        // given / when
        List<Item> items = underTest.getAll();
        // then
        assertNotNull(items);
        assertTrue(output.toString().contains("ItemService.getAll"));
        assertTrue(output.toString().contains("completed successfully"));
    }





}
