package com.oxclub.quizapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestionBank {

    public static final String[] CAT_NAMES = {
            "All 🌍", "Physics ⚛️", "Chemistry 🧪", "Biology 🧬", "Maths 📐", "Science 🔬",
            "Sports ⚽", "Movies 🎬", "History 🏛️", "Geo 🗺️", "GK 🧠", "Olympiad 🏅"
    };

    public static class Question {
        public final String text;
        public final String[] options;
        public final int correct;
        public String category;
        public int difficulty = 1;

        public Question(String text, String[] options, int correct) {
            this.text = text;
            this.options = options;
            this.correct = correct;
        }
    }

    public interface Callback {
        void onReady(List<Question> questions, String source);
    }

    public static void loadQuiz(int count, int categoryIndex, String difficulty, Callback callback) {
        List<Question> qs = pickLocal(count, categoryIndex, difficulty);
        deliver(callback, qs, "Quiz Mode 🧠");
    }

    private static void deliver(Callback cb, List<Question> qs, String src) {
        new Handler(Looper.getMainLooper()).post(() -> cb.onReady(qs, src));
    }

    private static List<Question> pickLocal(int count, int categoryIndex, String difficulty) {
        List<Question> all = loadLocalBank();
        List<Question> pool = new ArrayList<>();

        if (categoryIndex > 0) {
            String want = CAT_NAMES[categoryIndex].split(" ")[0];
            for (Question q : all) {
                if (want.equalsIgnoreCase(q.category)) pool.add(q);
            }
        } else {
            pool.addAll(all);
        }

        List<Question> filtered = new ArrayList<>();
        for (Question q : pool) {
            if ("easy".equals(difficulty) && q.difficulty <= 1) filtered.add(q);
            else if ("hard".equals(difficulty) && q.difficulty >= 1) filtered.add(q);
            else filtered.add(q);
        }
        if (filtered.size() >= count) pool = filtered;

        Collections.shuffle(pool);
        List<Question> out = new ArrayList<>();
        for (int i = 0; i < Math.min(count, pool.size()); i++) out.add(pool.get(i));
        while (out.size() < count) out.add(makeMathQuestion(difficulty));
        return out;
    }

    private static List<Question> loadLocalBank() {
        List<Question> list = new ArrayList<>();
        try {
            Context ctx = AppCtx.get();
            InputStream is = ctx.getAssets().open("questions.json");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) bos.write(buf, 0, n);
            is.close();

            JSONArray arr = new JSONArray(bos.toString("UTF-8"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                JSONArray opts = o.getJSONArray("o");
                String[] raw = new String[opts.length()];
                for (int j = 0; j < opts.length(); j++) raw[j] = opts.getString(j);

                List<String> shuffled = new ArrayList<>(Arrays.asList(raw));
                Collections.shuffle(shuffled);
                int correct = shuffled.indexOf(raw[o.getInt("a")]);

                Question q = new Question(o.getString("q"),
                        shuffled.toArray(new String[0]), correct);
                q.category = o.optString("c", "GK");
                q.difficulty = o.optInt("d", 1);
                list.add(q);
            }
        } catch (Exception ignored) {}
        return list;
    }

    private static Question makeMathQuestion(String diff) {
        java.util.Random rnd = new java.util.Random();
        boolean easy = "easy".equals(diff);
        boolean hard = "hard".equals(diff);
        int type = rnd.nextInt(4);
        int a, b, answer;
        String text;
        switch (type) {
            case 0:
                if (easy) { a = 2 + rnd.nextInt(18); b = 2 + rnd.nextInt(18); }
                else if (hard) { a = 150 + rnd.nextInt(400); b = 150 + rnd.nextInt(400); }
                else { a = 10 + rnd.nextInt(90); b = 10 + rnd.nextInt(90); }
                answer = a + b; text = a + " + " + b + " = ?"; break;
            case 1:
                if (easy) { a = 10 + rnd.nextInt(18); b = 2 + rnd.nextInt(9); }
                else if (hard) { a = 300 + rnd.nextInt(400); b = 100 + rnd.nextInt(250); }
                else { a = 40 + rnd.nextInt(90); b = 10 + rnd.nextInt(35); }
                answer = a - b; text = a + " − " + b + " = ?"; break;
            case 2:
                if (easy) { a = 2 + rnd.nextInt(6); b = 2 + rnd.nextInt(6); }
                else if (hard) { a = 12 + rnd.nextInt(20); b = 12 + rnd.nextInt(20); }
                else { a = 2 + rnd.nextInt(11); b = 2 + rnd.nextInt(11); }
                answer = a * b; text = a + " × " + b + " = ?"; break;
            default:
                if (easy) { b = 2 + rnd.nextInt(5); answer = 2 + rnd.nextInt(6); }
                else if (hard) { b = 6 + rnd.nextInt(12); answer = 4 + rnd.nextInt(15); }
                else { b = 2 + rnd.nextInt(11); answer = 2 + rnd.nextInt(11); }
                a = b * answer; text = a + " ÷ " + b + " = ?"; break;
        }
        java.util.Set<Integer> opts = new java.util.HashSet<>();
        opts.add(answer);
        while (opts.size() < 4) {
            int cand = answer + (rnd.nextInt(11) - 5);
            if (cand >= 0) opts.add(cand);
        }
        List<Integer> optList = new ArrayList<>(opts);
        Collections.shuffle(optList);
        String[] arr = new String[4];
        for (int i = 0; i < 4; i++) arr[i] = String.valueOf(optList.get(i));
        return new Question(text, arr, optList.indexOf(answer));
    }
}
