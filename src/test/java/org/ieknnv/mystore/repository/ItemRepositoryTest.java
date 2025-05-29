package org.ieknnv.mystore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    private static final String SEARCH = "keyword";

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testFindAllBySearchLine() {
        var page = itemRepository.findAllBySearchLine(SEARCH, PageRequest.of(0, 5));
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        var pageContent = page.getContent();
        // Check that only expected items were extracted
        pageContent.forEach(item -> assertTrue(item.getName().equals("Item 1") || item.getName().equals("Item 4")));
    }
}
