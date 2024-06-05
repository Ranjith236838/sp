package test.developer.backend.sp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.developer.backend.sp.model.PriceRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceServiceTest {

    private PriceService priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceService();
    }

    @Test
    void testStartBatch() {
        long batchId = priceService.startBatch();
        priceService.completeBatch(batchId);
        assertTrue(batchId > 0);
    }

    @Test
    void testUploadRecords() {
        long batchId = priceService.startBatch();
        List<PriceRecord> records = Arrays.asList(
                new PriceRecord("A", LocalDateTime.now(), null),
                new PriceRecord("B", LocalDateTime.now(), null)
        );
        priceService.uploadRecords(batchId, records);
        assertThrows(IllegalStateException.class, () -> {priceService.startBatch();});
        priceService.completeBatch(batchId);
    }

    @Test
    void testCompleteBatch() {
        long batchId = priceService.startBatch();
        List<PriceRecord> records = Arrays.asList(
                new PriceRecord("A", LocalDateTime.now(), null),
                new PriceRecord("B", LocalDateTime.now(), null)
        );
        priceService.uploadRecords(batchId, records);
        priceService.completeBatch(batchId);
        assertNotNull(priceService.getLastPrice("A"));
        assertNotNull(priceService.getLastPrice("B"));
    }

    @Test
    void testCancelBatch() {
        long batchId = priceService.startBatch();
        priceService.cancelBatch(batchId);
        long batchId1 = priceService.startBatch();
        assertEquals(batchId + 1, batchId1);
        priceService.cancelBatch(batchId1);

    }

    @Test
    void testGetLastPrice() {
        PriceRecord record = new PriceRecord("A", LocalDateTime.now(), null);
        long id = priceService.startBatch();
        priceService.uploadRecords(id, List.of(record));
        priceService.completeBatch(id);
        assertEquals(record, priceService.getLastPrice("A"));
    }
}
