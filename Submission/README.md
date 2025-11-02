# Card Game Test Suite

## Contains JUnit tests and base classes

- `CardTest.java` - tests for the Card class
- `CardDeckTest.java` - tests for the CardDeck class  
- `PlayerTest.java` - tests for the Player class
- `CardGameTest.java` - tests for the CardGame class

`CardGameTestSuite.java` which runs all the tests together in one go.

## Requirements

JUnit 5 and a java environment
Most IDE have this built in or in vsc use the respective java extensions

## Run Tests
In IDE go to testing tab, if JUnit is properly installed all tests will show
Run CardGameTestSuite to run all at once

## Extra
Ensure main class files are compiled
Tests create temporary files which will get cleaned up after completion
multi threading tests can take a few seconds to run