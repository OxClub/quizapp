package com.oxclub.quizapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {

    public static class Question {
        public final String text;
        public final String[] options;
        public final int correct;

        public Question(String text, String a, String b, String c, String d, int correct) {
            this.text = text;
            this.options = new String[]{a, b, c, d};
            this.correct = correct;
        }
    }

    // ADD YOUR QUESTIONS HERE - copy a line, change the text!
    // The last number (0-3) = which option is correct
    private static final Question[] ALL = {
        new Question("What is the capital of France?", "London", "Paris", "Berlin", "Madrid", 1),
        new Question("Which planet is known as the Red Planet?", "Venus", "Mars", "Jupiter", "Saturn", 1),
        new Question("How many continents are on Earth?", "5", "6", "7", "8", 2),
        new Question("What is the largest ocean?", "Atlantic", "Indian", "Arctic", "Pacific", 3),
        new Question("Who painted the Mona Lisa?", "Picasso", "Da Vinci", "Van Gogh", "Michelangelo", 1),
        new Question("Chemical symbol for gold?", "Ag", "Fe", "Au", "Gd", 2),
        new Question("Longest river in the world?", "Amazon", "Nile", "Yangtze", "Ganges", 1),
        new Question("How many players in a football team?", "9", "10", "11", "12", 2),
        new Question("King of the Jungle?", "Tiger", "Lion", "Elephant", "Wolf", 1),
        new Question("Tallest mountain in the world?", "K2", "Everest", "Kilimanjaro", "Mont Blanc", 1),
        new Question("Which country has the pyramids?", "Iraq", "Egypt", "Mexico", "Greece", 1),
        new Question("What do bees make?", "Milk", "Honey", "Silk", "Butter", 1),
        new Question("Which gas do plants absorb?", "Oxygen", "Nitrogen", "Carbon dioxide", "Helium", 2),
        new Question("How many minutes in a full day?", "1200", "1440", "1600", "2400", 1),
        new Question("Smallest planet in our solar system?", "Mars", "Mercury", "Venus", "Moon", 1)
    };

    public static List<Question> getQuiz(int count) {
        List<Question> list = new ArrayList<>();
        Collections.addAll(list, ALL);
        Collections.shuffle(list);
        return list.subList(0, Math.min(count, list.size()));
    }
}
