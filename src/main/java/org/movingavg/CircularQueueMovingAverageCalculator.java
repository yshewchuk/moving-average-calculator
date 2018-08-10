package org.movingavg;

import java.util.ConcurrentModificationException;

/**
 * Implements a moving average calculation by means of a circular queue
 *
 * Internally, it maintains a running sum, when a new element is added it adds the new value to the running sum.
 * If the queue has been filled (based on the capacity), then the value that is being overwritten is subtracted
 * from the running sum to maintain consistency.
 *
 * This class will perform a best-effort check for concurrent modification (there are some potential race conditions
 * that would cause it to not be properly detected, but this is intended to help developers catch errors in
 * logic during development without a large performance overhead). This class itself is not thread-safe (to
 * maintain optimal performance).
 */
public class CircularQueueMovingAverageCalculator implements MovingAverageCalculator {

    static CircularQueueEnumerationFactory iteratorFactory = new CircularQueueEnumerationFactory();

    private double[] values;

    // sum of all elements in the values array
    private double movingSum = 0;

    // tail points to the index where the next element is about to be added
    private int tail = 0;

    private int size = 0;
    private long nonce = Long.MIN_VALUE;

    public CircularQueueMovingAverageCalculator(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }

        values = new double[capacity];
    }

    private int indexIncremented(int index) {
        return (index + 1) % values.length;
    }

    @Override
    public double movingAverage() {
        // avoid divide by zero
        return movingSum / (double)Math.max(size, 1);
    }

    @Override
    public void add(double element) {
        // increment the nonce
        nonce++;

        // update the moving sum
        movingSum = movingSum - values[tail] + element;

        // set the new element
        values[tail] = element;

        // move the tail pointer
        tail = indexIncremented(tail);

        // update the size;
        size = Math.min(values.length, size + 1);
    }

    @Override
    public NumericEnumeration iterator() {
        int start = (size < values.length) ? 0 : tail;
        return iteratorFactory.create(values, start, size, nonce, nonce -> checkVolatility(nonce));
    }

    void checkVolatility(long nonce) {
        if (nonce != this.nonce) {
            throw new ConcurrentModificationException("Collection has been modified since creation, consider reimplementing with thread safety");
        }
    }

}
