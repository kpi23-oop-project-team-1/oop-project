package com.mkr.datastore.inFile;

import java.util.Spliterator;
import java.util.function.Consumer;

public class InFileCollectionSpliterator<E> implements Spliterator<E> {
    private final CollectionFileController<E> fileController;

    private final long maxIndex;
    private long currentIndex;
    private long currentPos;

    public InFileCollectionSpliterator(CollectionFileController<E> fileController) {
        this.fileController = fileController;

        this.currentPos = fileController.getFirstEntityPos();

        this.currentIndex = 0;
        this.maxIndex = fileController.countEntitiesFromPos(this.currentPos) - 1;
    }

    public InFileCollectionSpliterator(CollectionFileController<E> fileController, long currentPos, long currentIndex, long maxIndex) {
        this.fileController = fileController;

        this.currentPos = currentPos;

        this.currentIndex = currentIndex;
        this.maxIndex = maxIndex;
    }

    public boolean tryAdvance(Consumer<? super E> action) {
        if (currentIndex > maxIndex) return false;

        // Skip not active records
        boolean isActive;
        while (true) {
            isActive = fileController.readEntityIsActiveAtPos(currentPos);

            if (isActive) break;

            goToNext();

            if (currentIndex > maxIndex) return false;
        }

        action.accept(fileController.readEntityAtPos(currentPos));
        goToNext();

        return true;
    }

    public Spliterator<E> trySplit() {
        long currentSize = estimateSize();

        if (currentSize < 2) return null;

        long splitIndex = currentIndex + currentSize / 2;

        var newSpliterator = new InFileCollectionSpliterator<>(
                fileController, currentPos, currentIndex, splitIndex - 1);

        // Make currentIndex equal to splitIndex
        while (currentIndex != splitIndex) {
            goToNext();
        }

        return newSpliterator;
    }

    public long estimateSize() {
        return maxIndex - currentIndex + 1;
    }

    public int characteristics() {
        return IMMUTABLE | NONNULL | ORDERED;
    }

    private void goToNext() {
        currentPos = fileController.findNextEntityPos(currentPos);
        currentIndex++;
    }
}
