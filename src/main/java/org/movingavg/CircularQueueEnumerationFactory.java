package org.movingavg;

/**
 * Provides an extensibility point for testing purposes
 */
class CircularQueueEnumerationFactory {

    public CircularQueueEnumeration create(double[] values, int start, int size, long id, VolatilityCheck volatilityCheck) {
        return new CircularQueueEnumeration(values, start, size, id, volatilityCheck);
    }
}
