import java.io.*;
import java.util.*;

abstract class Question {
    protected String questionText;

    public Question(String questionText) {
        this.questionText = questionText;
    }

public abstract void display();
    public abstract boolean checkAnswer(String answer);
}

class MCQ extends Question {
    private List<String> options;
    private String correctAnswer;

    public MCQ(String questionText, List<String> options, String correctAnswer) {
        super(questionText);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public void display() {
        System.out.println("\n" + questionText);
        char ch = 'A';
        for (String opt : options) {
            System.out.println(ch + ". " + opt);
            ch++;
        }
    }

    public boolean checkAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }
}

class TrueFalse extends Question {
    private String correctAnswer;

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

class QuizManager {
   private List<Question> questions = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);

       class QuizSession {
        private String username;

        public QuizSession(String username) {
            this.username = username;
        }

        public void start() {
            int score = 0;

            System.out.println("\nQUIZ STARTED");

            for (Question q : questions) {
                q.display();

                long startTime = System.currentTimeMillis();

                System.out.print("Your Answer (15 sec): ");
                String ans = sc.nextLine();

                long endTime = System.currentTimeMillis();
                if ((endTime - startTime) > 15000) {
                   
 System.out.println("Time Over ");
                    continue;
                }
                if (q.checkAnswer(ans)) {
                    System.out.println("Correct ");
                    score++;
                } else {
                  
  System.out.println("Wrong ");
                }
            }

            saveResult(username, score);

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

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public void startQuiz(String username) {
        new QuizSession(username).start();
    }

    public void saveResult(String username, int score) {
        try (FileWriter fw = new FileWriter("results.txt", true)) {
            fw.write(username + "," + score + "\n");
        } catch (IOException e) {
            System.out.println("File Error!");
        }
    }

        public void showLeaderboard() {
        
List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("results.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

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
                    continue;
                }
           
 }

            data.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

            System.out.println("\n===== LEADERBOARD =====");

            int rank = 1;
            for (String[] entry : data) {
                System.out.println(rank + ". " + entry[0] + " - " + entry[1]);
                rank++;
                if (rank > 5) break;
            }

            if (data.isEmpty()) {
                System.out.println("No valid data found.");
            }

        } catch (IOException e) {
            System.out.println("No data available.");
        }
    }
}

public class Main {
    public static void main(String[] args) {

        QuizManager manager = new QuizManager();
        Scanner sc = new Scanner(System.in);

        // ONLY 5 QUESTIONS
        manager.addQuestion(new MCQ("What is Java?",
                Arrays.asList("Language", "OS", "Browser", "Hardware"), "A"));

        manager.addQuestion(new MCQ("Which is not OOP concept?",
                Arrays.asList("Encapsulation", "Polymorphism", "Compilation", "Inheritance"), "C"));

        manager.addQuestion(new MCQ("Which company developed Java?",
                Arrays.asList("Microsoft", "Sun Microsystems", "Google", "Apple"), "B"));

        manager.addQuestion(new MCQ("Which memory stores objects?",
                Arrays.asList("Stack", "Heap", "Register", "Cache"), "B"));

        manager.addQuestion(new TrueFalse("Java supports multiple inheritance through classes", "False"));

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
