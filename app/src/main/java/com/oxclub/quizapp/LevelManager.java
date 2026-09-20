package com.oxclub.quizapp;

public class LevelManager {
    private static final String[] TITLES = {
            "Rookie 🐣", "Explorer 🧭", "Thinker 🤔", "Pro 🎯",
            "Expert 🧠", "Master 👑", "Legend 🌟"
    };

    public static int levelFor(int xp) { return (xp / 100) + 1; }
    public static String titleFor(int level) {
        return TITLES[Math.min((level - 1) / 2, TITLES.length - 1)];
    }
}
