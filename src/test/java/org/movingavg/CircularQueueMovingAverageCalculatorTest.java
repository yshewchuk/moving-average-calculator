package org.movingavg;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ConcurrentModificationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CircularQueueMovingAverageCalculatorTest {

    @Spy
    CircularQueueEnumerationFactory iteratorFactory;

    @Before
    public void init() {
        CircularQueueMovingAverageCalculator.iteratorFactory = iteratorFactory;
    }

    @Test
    public void whenCapacityIsZero_constructor_throwsException() {
        assertThatThrownBy(() -> new CircularQueueMovingAverageCalculator(0))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void whenCapacityIsNegative_constructor_throwsException() {
        assertThatThrownBy(() -> new CircularQueueMovingAverageCalculator(0))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void whenElementsAddedBelowCapacity_movingAverage_returnsAverageOfAddedElements() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        assertThat(sut.movingAverage()).isEqualTo(0d);

        sut.add(2d);
        assertThat(sut.movingAverage()).isEqualTo(2d);

        sut.add(4d);
        assertThat(sut.movingAverage()).isEqualTo(3d);

        sut.add(6d);
        assertThat(sut.movingAverage()).isEqualTo(4d);
    }

    @Test
    public void whenElementsAddedBeyondCapacity_movingAverage_returnsMovingAverage() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        sut.add(2d);
        sut.add(4d);
        sut.add(6d);
        assertThat(sut.movingAverage()).isEqualTo(4d);

        sut.add(8d);
        assertThat(sut.movingAverage()).isEqualTo(6d);

        sut.add(10d);
        assertThat(sut.movingAverage()).isEqualTo(8d);

        sut.add(12d);
        assertThat(sut.movingAverage()).isEqualTo(10d);

        sut.add(14d);
        assertThat(sut.movingAverage()).isEqualTo(12d);
    }

    @Test
    public void whenEmpty_iterator_returnsAnIteratorWithNoNext() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        assertThat(sut.iterator().hasMoreElements()).isFalse();

        verify(iteratorFactory).create(any(), eq(0), eq(0), eq(Long.MIN_VALUE), any());
    }

    @Test
    public void whenNonEmpty_iterator_returnsAnIteratorWithItems() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        sut.add(1d);

        assertThat(sut.iterator().hasMoreElements()).isTrue();

        verify(iteratorFactory).create(any(), eq(0), eq(1), eq(Long.MIN_VALUE + 1), any());
    }

    @Test
    public void whenOverfilled_iterator_returnsAnIteratorWithItems() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        sut.add(1d);
        sut.add(1d);
        sut.add(1d);
        sut.add(1d);
        sut.add(1d);

        assertThat(sut.iterator().hasMoreElements()).isTrue();

        verify(iteratorFactory).create(any(), eq(2), eq(3), eq(Long.MIN_VALUE + 5), any());
    }

    @Test
    public void whenCollectionChangedWhileIteratorOutstanding_iterator_isInvalidated() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        sut.add(1d);
        sut.add(1d);

        NumericEnumeration iterator = sut.iterator();

        iterator.nextElement();

        sut.add(2d);

        assertThatThrownBy(() -> iterator.hasMoreElements())
            .isExactlyInstanceOf(ConcurrentModificationException.class);
        assertThatThrownBy(() -> iterator.nextElement())
            .isExactlyInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void whenNonceMatches_volatilityCheck_doesNothing() {
        try {
            CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);
            sut.checkVolatility(Long.MIN_VALUE);
        } catch (Exception e) {
            fail("Should not have thrown");
        }
    }

    @Test
    public void whenNonceDoesNotMatch_volatilityCheck_throwsException() {
        CircularQueueMovingAverageCalculator sut = new CircularQueueMovingAverageCalculator(3);

        assertThatThrownBy(() -> sut.checkVolatility(Long.MIN_VALUE + 1))
            .isExactlyInstanceOf(ConcurrentModificationException.class);
    }
}
