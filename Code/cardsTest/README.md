# Card Game Test Suite# Card Game Test Suite



## OverviewThis directory contains comprehensive JUnit 5 tests for the multi-threaded card game simulation.

This test package contains comprehensive unit tests, integration tests, and concurrency tests for the multi-threaded card game simulation.

## Test Framework

## Test Structure- **JUnit 5 (Jupiter)** - Version 5.x

- **JUnit Platform Suite API** - For test suite organization

### Test Classes

- **CardTest.java** - Unit tests for Card class (immutability, equality, thread safety)## Test Classes

- **CardDeckTest.java** - Tests for CardDeck FIFO operations and concurrency

- **PlayerTest.java** - Player behavior, threading, and game strategy tests### CardTest.java

- **CardGameTest.java** - Integration tests and complete game scenariosTests the basic Card class functionality:

- **CardGameTestSuite.java** - Main test runner that executes all test classes- Card creation with valid/invalid denominations

- Immutability and thread safety

### Test Coverage- Equality and hash code consistency

- **Unit Testing**: Individual class functionality and edge cases- toString method

- **Integration Testing**: Complete game scenarios with file I/O

- **Concurrency Testing**: Thread safety and multi-threading behavior### CardDeckTest.java

- **Input Validation**: Invalid pack files, player counts, and error handlingTests the thread-safe CardDeck class:

- FIFO (First In, First Out) operations

## How to Run Tests- Thread safety with concurrent access

- File output functionality

### Option 1: Run Complete Test Suite- Draw and discard operations as atomic actions

```cmd

java -cp "src;test" CardGameTestSuite### PlayerTest.java

```Tests the Player class behavior:

- Player creation and initialization

### Option 2: Run Individual Test Classes- Winning hand detection

```cmd- Game strategy implementation

java -cp "src;test" CardTest- File output generation

java -cp "src;test" CardDeckTest  - Thread behavior

java -cp "src;test" PlayerTest

java -cp "src;test" CardGameTest### CardGameIntegrationTest.java

```Integration tests for the complete game:

- End-to-end game scenarios

### Option 3: Compile and Run (if .class files not included)- Input validation (players, pack files)

```cmd- Error handling for invalid inputs

# Compile all source and test files- Output file generation and format

javac -cp "src;test" src\*.java test\*.java

### CardGameTestSuite.java

# Run the test suiteTest suite runner that executes all test classes in order.

java -cp "src;test" CardGameTestSuite

```## Running the Tests



## Test Framework### Prerequisites

- **Manual Testing**: Custom assertion methods (no external dependencies)1. Java 11 or higher

- **No JUnit Required**: Self-contained test implementation2. JUnit 5 dependencies in classpath

- **Cross-Platform**: Works with Java 8+ on Windows/Linux/Mac

### Required JUnit 5 Dependencies

## Test Features```

junit-jupiter-engine-5.x.x.jar

### Card Class Testsjunit-jupiter-api-5.x.x.jar

- Valid denomination creationjunit-platform-suite-api-1.x.x.jar

- Zero denomination handlingjunit-platform-launcher-1.x.x.jar

- Negative denomination exception```

- Equality and hashCode consistency

- String representation### Command Line Execution

- Immutability verification

1. **Compile all test classes:**

### CardDeck Class Tests```bash

- FIFO (First-In-First-Out) behaviorjavac -cp ".:junit-jupiter-api-5.x.x.jar:junit-platform-suite-api-1.x.x.jar" *.java ../src/*.java

- Thread-safe concurrent operations```

- Empty deck handling

- Null card rejection2. **Run individual test class:**

- Deck size management```bash

java -cp ".:junit-jupiter-engine-5.x.x.jar:junit-jupiter-api-5.x.x.jar:junit-platform-launcher-1.x.x.jar" org.junit.platform.console.ConsoleLauncher --class-path . --select-class CardTest

### Player Class Tests```

- Player creation and initialization

- Hand management (4 cards)3. **Run complete test suite:**

- Winning condition detection```bash

- Card preference strategyjava -cp ".:junit-jupiter-engine-5.x.x.jar:junit-jupiter-api-5.x.x.jar:junit-platform-launcher-1.x.x.jar:junit-platform-suite-api-1.x.x.jar" org.junit.platform.console.ConsoleLauncher --class-path . --select-class CardGameTestSuite

- Thread lifecycle management```



### Integration Tests### IDE Execution

- Complete game scenariosMost modern IDEs (Eclipse, IntelliJ IDEA, VS Code) have built-in JUnit 5 support:

- Invalid pack file handling1. Right-click on any test class or the test suite

- Input validation testing2. Select "Run Tests" or "Debug Tests"

- File output verification

- Multi-player game simulation## Test Coverage



### Concurrency TestsThe test suite provides comprehensive coverage of:

- Thread safety under high contention

- Atomic operations verification1. **Unit Testing:**

- Race condition detection   - All public methods of Card, CardDeck, and Player classes

- Deadlock prevention testing   - Edge cases and error conditions

   - Thread safety verification

## Expected Test Results

- **Total Tests**: ~50+ individual test methods2. **Integration Testing:**

- **Success Rate**: 100% for correct implementation   - Complete game scenarios

- **Test Duration**: ~5-10 seconds for complete suite   - Input validation and error handling

   - File I/O operations

## Test Output

Tests provide detailed console output showing:3. **Concurrency Testing:**

- Individual test progress (✓ PASSED / ✗ FAILED)   - Multi-threaded access to shared resources

- Assertion failure details   - Atomic operations verification

- Final statistics (passed/failed/success rate)   - Race condition detection



## Dependencies## Test Data Files

- **Java Version**: Java 8 or higher

- **Source Files**: All src/*.java files must be in classpathThe tests create temporary files during execution:

- **Test Data**: Tests create temporary files for validation- `test_pack_integration.txt` - Sample pack file for integration tests

- `player*_output.txt` - Player output files (cleaned up after tests)

## Troubleshooting- `deck*_output.txt` - Deck output files (cleaned up after tests)



### Common IssuesAll temporary files are automatically cleaned up after test execution.

1. **ClassNotFoundException**: Ensure src directory is in classpath

2. **File Permission Errors**: Run with write permissions for test files## Performance Considerations

3. **Path Issues**: Use absolute paths or run from project root directory

Some tests include timing-based assertions and thread operations:

### Test File Cleanup- Tests may take longer on slower systems

Tests automatically clean up temporary files including:- Concurrent tests may occasionally fail due to system load

- test_pack_integration.txt- Timeout values can be adjusted if needed

- player*_output.txt

- deck*_output.txt## Troubleshooting



## Assignment Compliance### Common Issues:

This test suite meets assignment requirements for:

- ✅ Thread-safe Card class testing1. **ClassNotFoundException**: Ensure all JUnit 5 JARs are in classpath

- ✅ Thread-safe Player class testing  2. **File access errors**: Ensure write permissions in test directory

- ✅ Multi-threaded game simulation3. **Timing issues**: Increase timeout values in concurrent tests if needed

- ✅ Input validation and error handling4. **Path separator issues**: Tests use system-appropriate path separators

- ✅ File output verification

- ✅ Complete integration testing### Debug Mode:

Run tests with `-Djunit.jupiter.execution.parallel.enabled=false` to disable parallel execution for debugging.

## Authors

[Your Names] - Pair Programming Team## Author Information

Framework: Manual Testing (No JUnit dependencies)

Version: 1.0**Framework Used:** JUnit 5.x  
**Test Categories:** Unit, Integration, Concurrency  
**Coverage:** Classes, Methods, Edge Cases, Error Conditions