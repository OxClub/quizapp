package com.oxclub.quizapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static SharedPreferences get(Context c) {
        return c.getSharedPreferences("oxquiz", Context.MODE_PRIVATE);
    }
    public static boolean isDark(Context c) { return get(c).getBoolean("dark", true); }
    public static void setDark(Context c, boolean v) { get(c).edit().putBoolean("dark", v).apply(); }
    public static boolean isSound(Context c) { return get(c).getBoolean("sound", true); }
    public static void setSound(Context c, boolean v) { get(c).edit().putBoolean("sound", v).apply(); }
    public static int getHighScore(Context c) { return get(c).getInt("high", 0); }
    public static void setHighScore(Context c, int v) { get(c).edit().putInt("high", v).apply(); }
    public static int getXP(Context c) { return get(c).getInt("xp", 0); }
    public static void addXP(Context c, int v) { get(c).edit().putInt("xp", getXP(c) + v).apply(); }
    public static int getHints(Context c) { return get(c).getInt("hints", 1); }
    public static void setHints(Context c, int v) { get(c).edit().putInt("hints", v).apply(); }
    public static int getCategory(Context c) { return get(c).getInt("cat", 0); }
    public static void setCategory(Context c, int v) { get(c).edit().putInt("cat", v).apply(); }
    public static String getDifficulty(Context c) { return get(c).getString("diff", "medium"); }
    public static void setDifficulty(Context c, String v) { get(c).edit().putString("diff", v).apply(); }
}
