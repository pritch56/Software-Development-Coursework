# Card Game Test Suite

## Overview

This test package contains comprehensive JUnit 5 tests for the multi-threaded card game simulation.

## Test Framework

- **JUnit 5 (Jupiter)** - Version 5.x
- **JUnit Platform Suite API** - For test suite organization

## Test Classes

### CardTest.java
Tests the basic Card class functionality:
- Card creation with valid/invalid denominations
- Immutability and thread safety
- Equality and hash code consistency
- toString method

### CardDeckTest.java
Tests the thread-safe CardDeck class:
- FIFO (First In, First Out) operations
- Thread safety with concurrent access
- File output functionality
- Draw and discard operations as atomic actions

### PlayerTest.java
Tests the Player class behavior:
- Player creation and initialization
- Winning hand detection
- Game strategy implementation
- File output generation
- Thread behavior

### CardGameTest.java
Integration tests for the complete game:
- End-to-end game scenarios
- Input validation (players, pack files)
- Error handling for invalid inputs
- Output file generation and format

### CardGameTestSuite.java
Test suite runner that executes all test classes in order using JUnit 5 Suite API.

## Running the Tests

### Prerequisites
1. Java 11 or higher
2. JUnit 5 dependencies in classpath

### Required JUnit 5 Dependencies
```
junit-jupiter-engine-5.x.x.jar
junit-jupiter-api-5.x.x.jar
junit-platform-suite-api-1.x.x.jar
junit-platform-launcher-1.x.x.jar
```

### Command Line Execution

1. **Compile all test classes:**
```bash
javac -cp ".:junit-jupiter-api-5.x.x.jar:junit-platform-suite-api-1.x.x.jar" *.java ../src/*.java
```

2. **Run individual test class:**
```bash
java -cp ".:junit-jupiter-engine-5.x.x.jar:junit-jupiter-api-5.x.x.jar:junit-platform-launcher-1.x.x.jar" org.junit.platform.console.ConsoleLauncher --class-path . --select-class CardTest
```

3. **Run complete test suite:**
```bash
java -cp ".:junit-jupiter-engine-5.x.x.jar:junit-jupiter-api-5.x.x.jar:junit-platform-launcher-1.x.x.jar:junit-platform-suite-api-1.x.x.jar" org.junit.platform.console.ConsoleLauncher --class-path . --select-class CardGameTestSuite
```

### IDE Execution
Most modern IDEs (Eclipse, IntelliJ IDEA, VS Code) have built-in JUnit 5 support:
1. Right-click on any test class or the test suite
2. Select "Run Tests" or "Debug Tests"

## Test Coverage

The test suite provides comprehensive coverage of:

1. **Unit Testing:**
   - All public methods of Card, CardDeck, and Player classes
   - Edge cases and error conditions
   - Thread safety verification

2. **Integration Testing:**
   - Complete game scenarios
   - Input validation and error handling
   - File I/O operations

3. **Concurrency Testing:**
   - Multi-threaded access to shared resources
   - Atomic operations verification
   - Race condition detection

## Test Data Files

The tests create temporary files during execution:
- `test_pack_integration.txt` - Sample pack file for integration tests
- `player*_output.txt` - Player output files (cleaned up after tests)
- `deck*_output.txt` - Deck output files (cleaned up after tests)

All temporary files are automatically cleaned up after test execution.

## Performance Considerations

Some tests include timing-based assertions and thread operations:
- Tests may take longer on slower systems
- Concurrent tests may occasionally fail due to system load
- Timeout values can be adjusted if needed

## Troubleshooting

### Common Issues:

1. **ClassNotFoundException**: Ensure all JUnit 5 JARs are in classpath
2. **File access errors**: Ensure write permissions in test directory
3. **Timing issues**: Increase timeout values in concurrent tests if needed
4. **Path separator issues**: Tests use system-appropriate path separators

### Debug Mode:
Run tests with `-Djunit.jupiter.execution.parallel.enabled=false` to disable parallel execution for debugging.

## Author Information

**Framework Used:** JUnit 5.x  
**Test Categories:** Unit, Integration, Concurrency  
**Coverage:** Classes, Methods, Edge Cases, Error Conditions