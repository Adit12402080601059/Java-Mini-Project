# Technical Documentation — Java Quiz Application

---

## 1. Class Diagram

The following describes the relationships between all classes in the system.

```
┌─────────────────────────────┐
│       <<abstract>>          │
│          Question           │
│─────────────────────────────│
│ # questionText : String     │
│─────────────────────────────│
│ + display() : void          │
│ + checkAnswer(String):bool  │
└──────────┬──────────────────┘
           │ extends
     ┌─────┴──────┐
     │            │
┌────▼────┐  ┌───▼──────┐
│   MCQ   │  │TrueFalse │
│─────────│  │──────────│
│options  │  │correct   │
│correct  │  │Answer    │
│Answer   │  │──────────│
│─────────│  │display() │
│display()│  │check     │
│check    │  │Answer()  │
│Answer() │  └──────────┘
└─────────┘

┌──────────────────────────────────────┐
│             QuizManager              │
│──────────────────────────────────────│
│ - questions : List<Question>         │
│ - sc : Scanner                       │
│──────────────────────────────────────│
│ + addQuestion(Question) : void       │
│ + startQuiz(String) : void           │
│ + saveResult(String, int) : void     │
│ + showLeaderboard() : void           │
│──────────────────────────────────────│
│  ┌────────────────────────────────┐  │
│  │   <<inner>> QuizSession        │  │
│  │────────────────────────────────│  │
│  │ - username : String            │  │
│  │────────────────────────────────│  │
│  │ + start() : void               │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘

┌─────────────────────────────┐
│           Main              │
│─────────────────────────────│
│ + main(String[]) : void     │
└─────────────────────────────┘
  Main creates QuizManager and drives the menu loop.
```

### Relationships Summary

| Class | Relationship | With |
|-------|-------------|------|
| `MCQ` | extends | `Question` |
| `TrueFalse` | extends | `Question` |
| `QuizManager` | has-a (composition) | `QuizSession` (inner class) |
| `QuizManager` | aggregates | `List<Question>` |
| `Main` | uses | `QuizManager` |

---

## 2. Sequence Diagram

### 2a. Start Quiz Flow

```
User          Main         QuizManager      QuizSession       File (results.txt)
 │              │               │                │                    │
 │──choice=1───►│               │                │                    │
 │              │──startQuiz()─►│                │                    │
 │              │               │──new Session()─►│                   │
 │              │               │                │                    │
 │              │               │                │──display(Q1)──────►│
 │◄─────────────────────────────────────────────── question text ─────│
 │──answer──────────────────────────────────────►│                    │
 │              │               │                │──checkAnswer()     │
 │◄─────────────────────────────────────────── Correct/Wrong ─────────│
 │              │               │                │  [repeat per Q]    │
 │              │               │                │──saveResult()─────►│
 │◄──────────── Score / Grade displayed ─────────│                    │
```

### 2b. Leaderboard Flow

```
User          Main         QuizManager       results.txt
 │              │               │                │
 │──choice=2───►│               │                │
 │              │──showBoard()─►│                │
 │              │               │──readLines()──►│
 │              │               │◄───────────── data
 │              │               │──parse & sort  │
 │◄────────────────────────── Top 5 printed ─────│
```

---

## 3. API Documentation (Method Reference)

Since this is a console application (no HTTP API), the public method interfaces are documented below.

---

### `Question` (abstract)

#### `display() : void`
Prints the question text and answer options to the console.

#### `checkAnswer(String answer) : boolean`
Checks if the user's input matches the correct answer (case-insensitive).
- **Parameter:** `answer` — the raw string entered by the user
- **Returns:** `true` if correct, `false` otherwise

---

### `MCQ`

#### Constructor: `MCQ(String questionText, List<String> options, String correctAnswer)`
| Parameter | Type | Description |
|-----------|------|-------------|
| `questionText` | String | The question prompt |
| `options` | List\<String\> | Answer choices (A, B, C, D) |
| `correctAnswer` | String | The correct letter, e.g. `"A"` |

---

### `TrueFalse`

#### Constructor: `TrueFalse(String questionText, String correctAnswer)`
| Parameter | Type | Description |
|-----------|------|-------------|
| `questionText` | String | The question prompt |
| `correctAnswer` | String | Either `"True"` or `"False"` |

---

### `QuizManager`

#### `addQuestion(Question q) : void`
Adds a question to the quiz bank.
- **Parameter:** `q` — any subclass of `Question`

#### `startQuiz(String username) : void`
Creates a new `QuizSession` for the given user and starts it.
- **Parameter:** `username` — the player's display name

#### `saveResult(String username, int score) : void`
Appends the result to `results.txt` in CSV format: `username,score`.
- **Parameters:** `username` — player name; `score` — number of correct answers
- **Side effect:** Creates or appends to `results.txt`

#### `showLeaderboard() : void`
Reads `results.txt`, parses all entries, sorts by score descending, and prints the top 5.

---

### `QuizSession` (inner class)

#### `start() : void`
Iterates through all questions, enforces the 15-second timer, scores the session, calls `saveResult()`, and prints the final result with grade.

**Grading logic:**
```
percentage = (score / total) * 100
>= 80  → Grade A
>= 60  → Grade B
>= 40  → Grade C
< 40   → Fail
```

---

## 4. Commented Source Code

Below is the well-commented version of `Main.java`. Place this in your `/src` folder.

```java
import java.io.*;
import java.util.*;

/**
 * Abstract base class for all question types.
 * Defines the common interface: display and answer checking.
 */
abstract class Question {
    protected String questionText;

    public Question(String questionText) {
        this.questionText = questionText;
    }

    /** Displays the question and options to the console. */
    public abstract void display();

    /**
     * Checks whether the given answer is correct.
     * @param answer The raw user input string.
     * @return true if correct, false otherwise.
     */
    public abstract boolean checkAnswer(String answer);
}

/**
 * Multiple Choice Question with lettered options (A, B, C, D).
 */
class MCQ extends Question {
    private List<String> options;
    private String correctAnswer; // e.g., "A", "B"

    public MCQ(String questionText, List<String> options, String correctAnswer) {
        super(questionText);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    /** Prints the question followed by lettered choices. */
    public void display() {
        System.out.println("\n" + questionText);
        char ch = 'A';
        for (String opt : options) {
            System.out.println(ch + ". " + opt);
            ch++;
        }
    }

    /** Case-insensitive match against the correct letter. */
    public boolean checkAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }
}

/**
 * True/False question type.
 */
class TrueFalse extends Question {
    private String correctAnswer; // "True" or "False"

    public TrueFalse(String questionText, String correctAnswer) {
        super(questionText);
        this.correctAnswer = correctAnswer;
    }

    public void display() {
        System.out.println("\n" + questionText + " (True/False)");
    }

    public boolean checkAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }
}

/**
 * Manages the quiz bank, sessions, file I/O, and leaderboard.
 */
class QuizManager {
    private List<Question> questions = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);

    /**
     * Inner class that runs a single quiz session for one user.
     */
    class QuizSession {
        private String username;

        public QuizSession(String username) {
            this.username = username;
        }

        /**
         * Iterates questions, enforces 15-sec timer, calculates score and grade.
         */
        public void start() {
            int score = 0;
            System.out.println("\nQUIZ STARTED");

            for (Question q : questions) {
                q.display();

                long startTime = System.currentTimeMillis();
                System.out.print("Your Answer (15 sec): ");
                String ans = sc.nextLine();
                long endTime = System.currentTimeMillis();

                // Check if answer was given within 15 seconds
                if ((endTime - startTime) > 15000) {
                    System.out.println("Time Over");
                    continue;
                }

                if (q.checkAnswer(ans)) {
                    System.out.println("Correct");
                    score++;
                } else {
                    System.out.println("Wrong");
                }
            }

            // Save result to file before showing output
            saveResult(username, score);

            // Calculate percentage and assign grade
            double percentage = (score * 100.0) / questions.size();
            String grade;
            if (percentage >= 80) grade = "A";
            else if (percentage >= 60) grade = "B";
            else if (percentage >= 40) grade = "C";
            else grade = "Fail";

            System.out.println("\n===== RESULT =====");
            System.out.println("User: " + username);
            System.out.println("Score: " + score + "/" + questions.size());
            System.out.println("Percentage: " + percentage + "%");
            System.out.println("Grade: " + grade);
        }
    }

    /** Adds a question to the quiz bank. */
    public void addQuestion(Question q) {
        questions.add(q);
    }

    /** Creates and starts a new quiz session for the given username. */
    public void startQuiz(String username) {
        new QuizSession(username).start();
    }

    /**
     * Saves a result entry to results.txt in CSV format.
     * @param username Player name
     * @param score    Number of correct answers
     */
    public void saveResult(String username, int score) {
        try (FileWriter fw = new FileWriter("results.txt", true)) {
            fw.write(username + "," + score + "\n");
        } catch (IOException e) {
            System.out.println("File Error!");
        }
    }

    /**
     * Reads results.txt, parses entries, sorts by score descending,
     * and displays the top 5 players.
     */
    public void showLeaderboard() {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("results.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Support both comma and colon delimiters
                String[] parts;
                if (line.contains(",")) {
                    parts = line.split(",");
                } else if (line.contains(":")) {
                    parts = line.split(":");
                } else {
                    continue;
                }

                String name = parts[0].trim();
                String scoreStr = parts[1].trim();

                try {
                    int score = Integer.parseInt(scoreStr);
                    data.add(new String[]{name, String.valueOf(score)});
                } catch (NumberFormatException e) {
                    continue; // Skip malformed lines
                }
            }

            // Sort descending by score
            data.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

            System.out.println("\n===== LEADERBOARD =====");
            int rank = 1;
            for (String[] entry : data) {
                System.out.println(rank + ". " + entry[0] + " - " + entry[1]);
                rank++;
                if (rank > 5) break; // Show top 5 only
            }

            if (data.isEmpty()) {
                System.out.println("No valid data found.");
            }

        } catch (IOException e) {
            System.out.println("No data available.");
        }
    }
}

/**
 * Entry point. Sets up the quiz bank and runs the main menu loop.
 */
public class Main {
    public static void main(String[] args) {
        QuizManager manager = new QuizManager();
        Scanner sc = new Scanner(System.in);

        // --- Add quiz questions here ---
        manager.addQuestion(new MCQ("What is Java?",
                Arrays.asList("Language", "OS", "Browser", "Hardware"), "A"));
        manager.addQuestion(new MCQ("Which is not OOP concept?",
                Arrays.asList("Encapsulation", "Polymorphism", "Compilation", "Inheritance"), "C"));
        manager.addQuestion(new MCQ("Which company developed Java?",
                Arrays.asList("Microsoft", "Sun Microsystems", "Google", "Apple"), "B"));
        manager.addQuestion(new MCQ("Which memory stores objects?",
                Arrays.asList("Stack", "Heap", "Register", "Cache"), "B"));
        manager.addQuestion(new TrueFalse(
                "Java supports multiple inheritance through classes", "False"));

        // Main menu loop
        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Start Quiz");
            System.out.println("2. Leaderboard");
            System.out.println("3. Submit!");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input!");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Username: ");
                    String name = sc.nextLine();
                    manager.startQuiz(name);
                    break;
                case 2:
                    manager.showLeaderboard();
                    break;
                case 3:
                    System.out.println("Thank you!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
```
