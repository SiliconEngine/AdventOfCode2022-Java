package Day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Advent of Code 2022 challenge, Day 2.
 * Link: <a href="https://adventofcode.com/2022/day/2">...</a>
 * <p>
 * Challenge: Simulate elf strategy game and determine total score.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    static class Move {
        char myMove;
        int score;
        public Move(char myMove, int score) {
            this.myMove = myMove;
            this.score = score;
        }
    };

    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    /**
     * Part 1: Calculates the total score by reading the strategy guide and computing the score based
     * on the predetermined outcomes and the scores associated with each shape.
     */
    public void part1() {
        Map<Character, Integer> shapeScores = new HashMap<Character, Integer>() {{
            put('X', 1);
            put('Y', 2);
            put('Z', 3);
        }};

        Map<String, Integer> winScores = new HashMap<>() {{
            put("AX", 3);
            put("AY", 6);
            put("AZ", 0);
            put("BX", 0);
            put("BY", 3);
            put("BZ", 6);
            put("CX", 6);
            put("CY", 0);
            put("CZ", 3);
        }};

        String filePath = "src/Day2/rps_strategy.txt"; // Replace with the path to your file

        int totalScore = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                char om = line.charAt(0);
                char mm = line.charAt(2);
                String key = "" + om + mm;
                System.out.println(key);

                int score = shapeScores.get(mm) + winScores.get(key);
                totalScore += score;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total score is " + totalScore);
    }

    /**
     * Part 2: Calculates the total score, but the strategy guide is interpreted differently: it
     * specifies the desired outcome of each round (win, lose, draw). The method determines the
     * appropriate move to achieve this outcome and calculates the score accordingly.
     */
    public void part2() {
        Map<Character, Integer> shapeScores = new HashMap<Character, Integer>() {{
            put('A', 1);
            put('B', 2);
            put('C', 3);
        }};

        Map<String, Move> winScores = new HashMap<>() {{
            put("AX", new Move('C', 0));
            put("AY", new Move('A', 3));
            put("AZ", new Move('B', 6));
            put("BX", new Move('A', 0));
            put("BY", new Move('B', 3));
            put("BZ", new Move('C', 6));
            put("CX", new Move('B', 0));
            put("CY", new Move('C', 3));
            put("CZ", new Move('A', 6));
        }};

        String filePath = "src/Day2/rps_strategy.txt"; // Replace with the path to your file

        int totalScore = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                char om = line.charAt(0);
                char end = line.charAt(2);
                String key = "" + om + end;

                char mm = winScores.get(key).myMove;
                int score = shapeScores.get(mm) + winScores.get(key).score;
                totalScore += score;

                System.out.println(line + ": om=" + om + ", end = " + end + ", mm=" + mm + ", score is " + score);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total score is " + totalScore);
    }
}