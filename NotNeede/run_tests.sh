#!/usr/bin/env bash
set -euo pipefail

# run_tests.sh - runs the bundled test mains in Submission/cardsTest
# Usage: ./run_tests.sh

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
CP="$ROOT_DIR/Submission/cardsTest:$ROOT_DIR/Submission/Card.jar:$ROOT_DIR/Submission/CardDeck.jar:$ROOT_DIR/Submission/Player.jar:$ROOT_DIR/Submission/CardGame.jar"

echo "Running CardGameTest..."
if ! java -cp "$CP" CardGameTest; then
  echo "CardGameTest failed" >&2
  exit 1
fi

echo "Running CardTest..."
if ! java -cp "$CP" CardTest; then
  echo "CardTest failed" >&2
  exit 1
fi

echo "All tests passed."