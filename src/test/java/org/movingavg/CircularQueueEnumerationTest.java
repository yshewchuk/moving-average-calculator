package org.movingavg;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class CircularQueueEnumerationTest {

    @Mock
    VolatilityCheck volatilityCheck;

    @Test
    public void whenPassedInvalidInput_constructor_doesNotValidate() {
        try {
            new CircularQueueEnumeration(new double[0], -1, -1, 0, null);
            new CircularQueueEnumeration(new double[4], 2, 5, 0, null);
        } catch (Exception e) {
            fail("The CircularQueueEnumeration is a package private class that expects to be used by trusted elements that pass valid input");
        }
    }

    @Test
    public void whenValidityCheckFails_hasMoreElements_throwsException() {
        RuntimeException expected = new RuntimeException();
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[4], 0, 3, 1, volatilityCheck);
        doThrow(expected).when(volatilityCheck).checkVolatility(eq(1l));

        assertThatThrownBy(() -> sut.hasMoreElements())
            .isSameAs(expected);
    }

    @Test
    public void whenSizeZero_hasMoreElements_returnsFalse() {
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[3], 0, 0, 2, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(2l));

        assertThat(sut.hasMoreElements()).isFalse();
    }

    @Test
    public void whenSizeLessThanCapacity_hasMoreElements_returnsCorrectResult() {
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[3], 0, 2, 2, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(2l));

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isFalse();
    }

    @Test
    public void whenSizeEqualToCapacity_hasMoreElements_returnsCorrectResult() {
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[3], 0, 3, 3, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(3l));

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isFalse();
    }

    @Test
    public void whenSizeLessThanCapacityAndAtOffset_hasMoreElements_returnsCorrectResult() {
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[3], 2, 2, 4, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(4l));

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isFalse();
    }

    @Test
    public void whenSizeEqualToCapacityAndAtOffset_hasMoreElements_returnsCorrectResult() {
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[3], 1, 3, 5, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(5l));

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isTrue();
        sut.nextElement();

        assertThat(sut.hasMoreElements()).isFalse();
    }

    @Test
    public void whenValidityCheckFails_nextElement_throwsException() {
        RuntimeException expected = new RuntimeException();
        CircularQueueEnumeration sut = new CircularQueueEnumeration(new double[4], 0, 4, 1, volatilityCheck);
        doThrow(expected).when(volatilityCheck).checkVolatility(eq(1l));

        assertThatThrownBy(() -> sut.nextElement())
            .isSameAs(expected);
    }

    @Test
    public void whenSizeLessThanCapacity_nextElement_returnsCorrectResult() {
        double[] vals = { 1d, 2d, 3d };

        CircularQueueEnumeration sut = new CircularQueueEnumeration(vals, 0, 2, 2, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(2l));

        assertThat(sut.nextElement()).isEqualTo(1d);
        assertThat(sut.nextElement()).isEqualTo(2d);
    }

    @Test
    public void whenSizeEqualToCapacity_nextElement_returnsCorrectResult() {
        double[] vals = { 1d, 2d, 3d };

        CircularQueueEnumeration sut = new CircularQueueEnumeration(vals, 0, 3, 3, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(3l));

        assertThat(sut.nextElement()).isEqualTo(1d);
        assertThat(sut.nextElement()).isEqualTo(2d);
        assertThat(sut.nextElement()).isEqualTo(3d);
    }

    @Test
    public void whenSizeLessThanCapacityAndAtOffset_nextElement_returnsCorrectResult() {
        double[] vals = { 1d, 2d, 3d };

        CircularQueueEnumeration sut = new CircularQueueEnumeration(vals, 2, 2, 4, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(4l));

        assertThat(sut.nextElement()).isEqualTo(3d);
        assertThat(sut.nextElement()).isEqualTo(1d);
    }

    @Test
    public void whenSizeEqualToCapacityAndAtOffset_nextElement_returnsCorrectResult() {
        double[] vals = { 1d, 2d, 3d };

        CircularQueueEnumeration sut = new CircularQueueEnumeration(vals, 1, 3, 5, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(5l));

        assertThat(sut.nextElement()).isEqualTo(2d);
        assertThat(sut.nextElement()).isEqualTo(3d);
        assertThat(sut.nextElement()).isEqualTo(1d);
    }

    @Test
    public void whenIterationBeyondSizeOccursFromBeginning_nextElement_throwsException() {
        double[] vals = { 1d, 2d, 3d };

        CircularQueueEnumeration sut = new CircularQueueEnumeration(vals, 0, 2, 6, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(6l));

        assertThat(sut.nextElement()).isEqualTo(1d);
        assertThat(sut.nextElement()).isEqualTo(2d);
        assertThatThrownBy(() -> sut.nextElement())
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void whenIterationBeyondSizeOccursFromOffset_nextElement_throwsException() {
        double[] vals = { 1d, 2d, 3d };

        CircularQueueEnumeration sut = new CircularQueueEnumeration(vals, 2, 1, 6, volatilityCheck);
        doNothing().when(volatilityCheck).checkVolatility(eq(6l));

        assertThat(sut.nextElement()).isEqualTo(3d);
        assertThatThrownBy(() -> sut.nextElement())
            .isExactlyInstanceOf(NoSuchElementException.class);
    }
}
