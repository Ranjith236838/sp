package test.developer.backend.sp.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.developer.backend.sp.model.PriceRecord;
import test.developer.backend.sp.service.PriceService;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @Operation(summary = "Get the Latest Price")
    @GetMapping("/{id}")
    public ResponseEntity<PriceRecord> getLastPrice(@PathVariable String id) {
        PriceRecord lastPrice = priceService.getLastPrice(id);
        if (lastPrice != null) {
            return ResponseEntity.ok(lastPrice);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Start a batch")
    @PostMapping("/batch/start")
    public ResponseEntity<Long> startBatch() {
        try {
            long batchId = priceService.startBatch();
            return ResponseEntity.ok(batchId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Upload a batch")
    @PostMapping("/batch/{batchId}/upload")
    public ResponseEntity<Void> uploadRecords(@PathVariable long batchId, @RequestBody List<PriceRecord> records) {
        try {
            priceService.uploadRecords(batchId, records);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Complete a batch")
    @PostMapping("/batch/{batchId}/complete")
    public ResponseEntity<Void> completeBatch(@PathVariable long batchId) {
        try {
            priceService.completeBatch(batchId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Cancel a batch")
    @PostMapping("/batch/{batchId}/cancel")
    public ResponseEntity<Void> cancelBatch(@PathVariable long batchId) {
        try {
            priceService.cancelBatch(batchId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

