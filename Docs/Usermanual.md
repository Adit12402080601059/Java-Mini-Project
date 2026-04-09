# User Manual — Java Quiz Application

---

## 1. Installation Guide

### System Requirements

| Requirement | Minimum Version |
|-------------|----------------|
| Java JDK | 11 or higher |
| Operating System | Windows 10 / macOS 10.14 / Ubuntu 18.04 |
| RAM | 256 MB |
| Disk Space | 10 MB |

### Step-by-step Installation

**Step 1 — Install Java JDK**

Download and install Java JDK 11+ from:
- https://adoptium.net (recommended — free and open source)
- https://www.oracle.com/java/technologies/downloads/

After installation, verify it works by opening a terminal and running:
```
java -version
```
You should see output like: `openjdk version "11.0.x"`

**Step 2 — Download the project**

Option A — Clone using Git:
```bash
git clone https://github.com/YOUR_USERNAME/java-quiz-app.git
cd java-quiz-app
```

Option B — Download the ZIP file from the GitHub repository page, then extract it and open a terminal in the extracted folder.

**Step 3 — Compile the source code**

```bash
javac src/Main.java -d out
```

This compiles all Java classes into the `/out` folder.

**Step 4 — Run the application**

```bash
java -cp out Main
```

The main menu will appear on screen.

---

## 2. User Guide

### Navigating the Main Menu

When the application launches, you will see:

```
===== MENU =====
1. Start Quiz
2. Leaderboard
3. Submit!
Enter choice:
```

Type the number for the action you want and press **Enter**.

---

### How to take the quiz (Option 1)

**Step 1** — Type `1` and press Enter.

**Step 2** — Enter your username when prompted:
```
Enter Username: Alice
```

**Step 3** — The quiz begins. Each question is displayed one at a time.

For a Multiple Choice Question:
```
What is Java?
A. Language
B. OS
C. Browser
D. Hardware
Your Answer (15 sec):
```
Type the letter of your answer (e.g., `A`) and press Enter.

For a True/False Question:
```
Java supports multiple inheritance through classes (True/False)
Your Answer (15 sec):
```
Type `True` or `False` and press Enter.

**Important:** You have **15 seconds** per question. If you do not answer in time, the question is skipped and marked incorrect.

**Step 4** — After all 5 questions, your result is displayed:
```
===== RESULT =====
User: Alice
Score: 4/5
Percentage: 80.0%
Grade: A
```

Your result is automatically saved to `results.txt`.

---

### Viewing the Leaderboard (Option 2)

Type `2` and press Enter to view the top 5 players by score:

```
===== LEADERBOARD =====
1. Alice - 5
2. Bob - 4
3. Carol - 3
4. David - 2
5. Eve - 1
```

The leaderboard reads all saved results and displays the highest scores first.

---

### Exiting the Application (Option 3)

Type `3` and press Enter to exit:
```
Thank you!
```

---

### Grading Reference

| Score | Percentage | Grade |
|-------|------------|-------|
| 5/5 | 100% | A |
| 4/5 | 80% | A |
| 3/5 | 60% | B |
| 2/5 | 40% | C |
| 1/5 | 20% | Fail |
| 0/5 | 0% | Fail |

---

## 3. FAQ — Frequently Asked Questions

**Q: The application does not start. What should I do?**
A: Make sure Java JDK (not just JRE) is installed and added to your system PATH. Run `java -version` in the terminal to confirm. If you get "command not found", reinstall the JDK from https://adoptium.net.

---

**Q: I get a compilation error when running `javac src/Main.java`.**
A: Ensure you are running the command from the root of the project folder (where the `/src` folder is visible). Also confirm the filename is exactly `Main.java` (case-sensitive).

---

**Q: My answer seems correct but it was marked wrong.**
A: Answers are case-insensitive but must be entered exactly. For MCQ, type only the letter (e.g., `A` not `A. Language`). For True/False, type exactly `True` or `False`.

---

**Q: The 15-second timer does not work / all answers show "Time Over".**
A: This happens if your terminal is slow to display input. The timer starts as soon as the question is displayed — make sure you type quickly after the prompt appears.

---

**Q: Where are my results saved?**
A: Results are saved in a file called `results.txt` in the same folder where you run the application. Each line contains: `username,score`.

---

**Q: The leaderboard shows "No data available".**
A: This means no quiz has been completed yet, or `results.txt` does not exist in the current directory. Complete at least one quiz session first.

---

**Q: Can two people play with the same username?**
A: Yes. Both entries will be saved separately in `results.txt`, and both will appear on the leaderboard independently.

---

**Q: Can I add more questions to the quiz?**
A: Yes. Open `src/Main.java` and add more `manager.addQuestion(...)` lines in the `main` method before the menu loop. Use `new MCQ(...)` for multiple choice or `new TrueFalse(...)` for true/false questions.

---

**Q: Does the application work on Windows, macOS, and Linux?**
A: Yes. The application is pure Java and runs on any operating system with JDK 11 or higher installed.
