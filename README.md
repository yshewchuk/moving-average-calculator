# Circular Queue Moving Average Calculator [![Build Status](https://travis-ci.org/yshewchuk/moving-average-calculator.svg?branch=master)](https://travis-ci.org/yshewchuk/moving-average-calculator) [![Coverage Status](https://coveralls.io/repos/github/yshewchuk/moving-average-calculator/badge.svg?branch=master)](https://coveralls.io/github/yshewchuk/moving-average-calculator?branch=master)

This repo showcases a streaming moving-average calculator implemented as a circular queue.

## Build

To build, execute:
```
gradlew build
```

## Test

To test, execute:
```
gradlew check
```

## Design Decisions

While implementing this, a few qualities were considered higher importance:
- Performance
- Testability
- Separation of Concerns

This led to a number of trade-offs:
- The interface uses the double type specifically instead of generics for different numeric types
   - Forces the client to perform the unboxing only once when adding a value, rather than during the many calculations
   - Makes processor cache misses less likely, since the values themselves will be in the array
   - May cause floating point errors which could be avoided if some properties were known about the values being averaged
   - Caused the implementation to not rely on Java standard collection types (which would have been much simpler)
- The implementation is not thread safe
   - To avoid the overhead of complex mechanisms or heavy locks, the implementation assumes the client will ensure thread safety
   - Does a best-effort concurrent modification check (based on nonce verification)and will fail fast if it is detected
- Separate components for the calculation and iteration
   - Improves separation of concerns and testability
   - Required some more code than combining those concepts more