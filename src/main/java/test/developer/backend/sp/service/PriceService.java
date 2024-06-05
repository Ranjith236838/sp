package test.developer.backend.sp.service;

import org.springframework.stereotype.Service;
import test.developer.backend.sp.model.PriceRecord;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/*
    All methods that modify the state of the batch processing (startBatch,
    uploadRecords, completeBatch, and cancelBatch) are synchronized to ensure
    that only one thread can execute these methods at a time. This avoids
    concurrent modifications and ensures thread safety.

    The variables latestPrices, batchProcess, batchRecords, and currentBatchId are
    static because they represent the shared state of the batch processing across all instances
    of the PriceService class.

    Since Spring will use a singleton bean, these static variables are not strictly necessary.
    However, making them static ensures that the state is shared consistently, even if multiple
    instances were somehow created.

    The volatile keyword is used for the latestPrices map to ensure visibility of changes to
    this variable across threads. This is important because latestPrices may be read and written
    by different threads.
 */
@Service
public class PriceService{
    private static volatile Map<String, PriceRecord> latestPrices = new HashMap<>();
    private static boolean batchProcess = false;
    private static Map<Long, List<PriceRecord>> batchRecords = new HashMap<>();
    // to store all the batch runs made by producer
    private static Map<Long, List<PriceRecord>> batchRecordsAll = new HashMap<>();
    private final Lock batchLock = new ReentrantLock();
    private static long currentBatchId = 0;

    public synchronized long startBatch(){
        batchLock.lock();
        try {
            if (batchProcess){
                throw new IllegalStateException("Batch is already in progress");
            }
            batchProcess = true;
            batchRecords.put(++currentBatchId, new ArrayList<>());
            batchRecordsAll.put(currentBatchId, new ArrayList<>());
            return currentBatchId;
        } finally {
            batchLock.unlock();
        }
    }

    public synchronized void uploadRecords(long batchId, List<PriceRecord> records){
        batchLock.lock();
        try {
            if (!batchProcess || currentBatchId != batchId){
                throw new IllegalStateException("No batch in progress or incorrect batch ID");
            }
            batchRecords.get(currentBatchId).addAll(records);
            batchRecordsAll.get(currentBatchId).addAll(records);
        } finally {
            batchLock.unlock();
        }
    }

    public synchronized void completeBatch(long batchId) {
        batchLock.lock();
        try {
            if (!batchProcess || currentBatchId != batchId){
                throw new IllegalStateException("No batch in progress or incorrect batch ID");
            }
            for (PriceRecord record : batchRecords.get(batchId)) {
                latestPrices.merge(record.getId(), record, (oldRecord, newRecord) ->
                        newRecord.getAsOf().isAfter(oldRecord.getAsOf()) ? newRecord : oldRecord
                );
            }
            batchRecords.remove(batchId);
            batchProcess = false;
        } finally {
            batchLock.unlock();
        }
    }

    public synchronized void cancelBatch(long batchId){
        batchLock.lock();
        try {
            if (!batchProcess || currentBatchId != batchId){
                throw new IllegalStateException("No batch in progress or incorrect batch ID");
            }
            batchRecords.remove(currentBatchId);
            batchProcess = false;
        } finally {
            batchLock.unlock();
        }
    }

    // This method does not need to be synchronized since it is read-only
    public PriceRecord getLastPrice(String id){
        return latestPrices.get(id);
    }

    public static Map<String, PriceRecord> getLatestPrices() {
        return latestPrices;
    }

    public static Map<Long, List<PriceRecord>> getBatchRecords() {
        return batchRecords;
    }
}
