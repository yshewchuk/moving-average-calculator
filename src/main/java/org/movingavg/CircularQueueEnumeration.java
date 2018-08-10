package org.movingavg;

import java.util.NoSuchElementException;

/**
 * Provides iteration functionality for a circular queue
 *
 * Accepts a volatility check to provide best effort fail-fast behaviour if
 * there is concurrent modification to the underlying data source
 */
class CircularQueueEnumeration implements NumericEnumeration {

    private double[] values;
    private int current;
    private int remaining;

    private long id;
    private VolatilityCheck volatilityCheck;

    CircularQueueEnumeration(double[] values, int start, int size, long id, VolatilityCheck volatilityCheck) {
        this.values = values;
        this.current = start;
        this.remaining = size;

        this.id = id;
        this.volatilityCheck = volatilityCheck;
    }

    @Override
    public boolean hasMoreElements() {
        volatilityCheck.checkVolatility(id);
        return remaining > 0;
    }

    @Override
    public double nextElement() {
        if (!hasMoreElements()) {
            throw new NoSuchElementException("Iterating past the end of the collection, ensure you call hasMoreElements each iteration");
        }
        double value = values[current];
        current = indexIncremented(current);
        remaining--;
        return value;
    }

    private int indexIncremented(int index) {
        return (index + 1) % values.length;
    }
}