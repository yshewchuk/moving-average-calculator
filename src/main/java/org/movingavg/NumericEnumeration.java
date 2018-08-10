package org.movingavg;

/**
 * Interface for an iterator over a collection of doubles
 */
public interface NumericEnumeration {

    boolean hasMoreElements();

    double nextElement();
}
