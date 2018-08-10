package org.movingavg;

/**
 * Interface for a class that will maintain a moving average of the last 'N' elements, and provide
 * access to those elements through the use of an iterator.
 */
public interface MovingAverageCalculator {

    double movingAverage();

    void add(double element);

    NumericEnumeration iterator();
}
