package test.developer.backend.sp.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class PriceRecord {
    private String id;
    private LocalDateTime asOf;
    private Object payload; // Flexible data structure

    public PriceRecord() {
    }

    public PriceRecord(String id, LocalDateTime asOf, Object payload) {
        this.id = id;
        this.asOf = asOf;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getAsOf() {
        return asOf;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "PriceRecord{" +
                "id='" + id + '\'' +
                ", asOf=" + asOf +
                ", payload=" + payload +
                '}';
    }
}

