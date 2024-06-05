package test.developer.backend.sp;

import test.developer.backend.sp.model.PriceRecord;
import test.developer.backend.sp.service.PriceService;

import java.time.LocalDateTime;
import java.util.List;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        PriceService priceService = new PriceService();
        A.priceService = priceService;
        B.priceService = priceService;

        A obj1 = new A();
        B obj2 = new B();

        obj1.start();
        obj2.start();
    }

    static void testUpload(){
        PriceService service = new PriceService();

    }

}

class A extends Thread {
    public static PriceService priceService;

    public void run(){
        for (int i = 0; i < 10; i++){
            long id = priceService.startBatch();
            PriceRecord priceRecord = new PriceRecord("1", LocalDateTime.now(), null);
            priceService.uploadRecords(id, List.of(priceRecord));
            priceService.completeBatch(id);
        }
    }
}

class B extends Thread {
    public static PriceService priceService;
    public void run(){
        for (int i = 0; i < 100000; i++){
            System.out.println(PriceService.getLatestPrices());
        }

    }
}
