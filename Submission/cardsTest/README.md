# Card Game Test Suite

## Contains JUnit tests and base classes


`CardGameTestSuite.java` which runs all the tests together in one go.

## Requirements

JUnit 5 and a java environment
Most IDE have this built in or in vsc use the respective java extensions

## Run Tests
This folder contains the test sources and compiled test classes used to validate
the card-game implementation. The tests were written against JUnit 5 but the
compiled test classes include small main runners so they can be executed from a
terminal without an IDE. Below are a few alternative ways to run the tests.

Prerequisites
- Java 11+ installed
- The repository root checked out 

Alternative run options

1) Run tests in your IDE 

- Import the project or open the folder in your IDE. Ensure JUnit 5 support is
	enabled for test discovery. You should see the four test classes:
	`CardTest`, `CardDeckTest`, `PlayerTest` and `CardGameTest` and a suite
	`CardGameTestSuite`.

2) Run the bundled test mains from the terminal

These test classes include `main` methods that act as self-contained runners.
From the repository root you can run the test mains directly. Example (adjust
paths if you moved files):

```bash
java -cp Submission/cardsTest:Submission/Card.jar:Submission/CardDeck.jar:Submission/Player.jar:Submission/CardGame.jar CardGameTest
java -cp Submission/cardsTest:Submission/Card.jar:Submission/CardDeck.jar:Submission/Player.jar:Submission/CardGame.jar CardTest
```

The first command runs the integration suite (and other tests bundled in that
class). The second runs the card unit tests. Both should print human-readable
pass/fail summaries.

3) (Optional) Run with JUnit ConsoleLauncher

If you prefer the JUnit platform ConsoleLauncher you can download the
standalone JAR and run a scan. Note: the ConsoleLauncher may not discover the
tests if the compiled classes rely on custom main-based runners; use the above
explicit mains for a reproducible run.


