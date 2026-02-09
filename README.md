# Ship Hunters (Java Terminal Game)

Java CLI battleship game using Unicode icons and ANSI colors. Run in VS Code Terminal or Windows Terminal (UTF‑8), basic cmd may not render icons.

## Requirements
- Java 11+ (Java 17+ recommended)
- A terminal that supports UTF‑8 and ANSI colors
- A font with good Unicode support (Cascadia Code, Fira Code, JetBrains Mono)

## How to run
1. Save your code as `ShipHunters.java` in the project folder.
2. Open a terminal (VS Code Terminal or Windows Terminal is recommended).
3. Compile and run:

```bash
javac -encoding UTF-8 ShipHunters.java
java -Dfile.encoding=UTF-8 ShipHunters
```

## Notes
- Windows: If using basic `cmd.exe`, try:
```bat
chcp 65001
```
…then use a font that supports Unicode. VS Code Terminal or Windows Terminal is easier.
- Some terminals may not fully render the icons; use a UTF‑8 terminal for best results.
