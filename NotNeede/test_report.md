# Test Report — Card Game Submission

Date: 2025-11-01

Summary
- I executed the provided test mains in `/Submission/cardsTest` and verified the compiled tests and production classes run in this environment (OpenJDK 11).
- All bundled tests passed when run via their mains.

What I ran
- `java -cp Submission/cardsTest:Submission/Card.jar:Submission/CardDeck.jar:Submission/Player.jar:Submission/CardGame.jar CardGameTest`
  - Output: "Total Tests: 18 / Passed: 18 / Failed: 0 — ALL TESTS PASSED!"
- `java -cp Submission/cardsTest:Submission/Card.jar:Submission/CardDeck.jar:Submission/Player.jar:Submission/CardGame.jar CardTest`
  - Output: "Card tests completed: 8/8 passed"

Artifacts created
- `run_tests.sh` (repo root) — runs both test mains and returns a non-zero exit code on failure.
- `Submission/cardsTest/README.md` — updated with explicit alternative run instructions (IDE, mains, ConsoleLauncher).
- `Submission/cardsTest.zip` — (created) archive of the `Submission/cardsTest` folder containing test sources and compiled classes.

Notes & recommendations
- The tests include built-in `main` runners; while they were originally written with JUnit 5, the ConsoleLauncher may not discover them in this form. Use `run_tests.sh` or run the mains explicitly for reliable runs.
- Ensure the final submission includes `cards.jar` (executable jar with .class and .java) and `cardsTest.zip` with sources and classes. I created `cardsTest.zip` here for convenience.
- The `WriteUp.pdf` must meet the page limits specified by the coursework. I inspected `NotNeede/WriteUp.txt` which contains the expected sections; please ensure the PDF matches the page constraints before final submission.

Next steps I can take
- Inspect and, if needed, repackage `cards.jar` to ensure it contains both `.class` and `.java` files inside.
- Create a submission-ready ZIP that combines `cards.jar` and `cardsTest.zip` along with the `WriteUp.pdf` if you want me to prepare the final upload bundle.

If you want the final submission ZIP prepared now, tell me and I'll package everything into one file named `submission_bundle.zip`.
