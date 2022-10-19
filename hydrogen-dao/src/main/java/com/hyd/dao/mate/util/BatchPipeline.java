package com.hyd.dao.mate.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

public class BatchPipeline<T> {

    private final List<T> buffer = new ArrayList<>();

    private Consumer<List<T>> batchOperation;

    private int batchSize = 1;

    private boolean ignoreNullItem = true;

    public BatchPipeline<T> setBatchSize(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size " + batchSize + " should be positive.");
        }
        this.batchSize = batchSize;
        return this;
    }

    public BatchPipeline<T> setBatchOperation(Consumer<List<T>> batchOperation) {
        this.batchOperation = batchOperation;
        return this;
    }

    public BatchPipeline<T> setIgnoreNullItem(boolean ignoreNullItem) {
        this.ignoreNullItem = ignoreNullItem;
        return this;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isIgnoreNullItem() {
        return ignoreNullItem;
    }

    public void flush() {
        if (buffer.isEmpty()) {
            return;
        }

        var list = List.copyOf(buffer);
        buffer.clear();
        if (batchOperation != null) {
            batchOperation.accept(list);
        }
    }

    public synchronized void feed(T item) {
        if (item == null && ignoreNullItem) {
            return;
        }

        buffer.add(item);

        if (buffer.size() >= this.batchSize) {
            flush();
        }
    }

    @SafeVarargs
    public final void feed(T... items) {
        for (var item : items) {
            feed(item);
        }
    }

    public void feed(Stream<T> itemStream) {
        itemStream.forEach(this::feed);
    }

    public void feed(BaseStream<T, ?> itemStream) {
        var iterator = itemStream.iterator();
        while (iterator.hasNext()) {
            var item = iterator.next();
            feed(item);
        }
    }

    public void feed(Collection<T> itemCollection) {
        itemCollection.forEach(this::feed);
    }
}
