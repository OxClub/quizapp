package com.oxclub.quizapp;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuestionBank {

    public static final String[] CAT_NAMES = {
            "All 🌍", "Science 🧪", "Sports ⚽", "Movies 🎬", "History 🏛️", "Geo 🗺️"
    };
    public static final int[] CAT_IDS = {0, 17, 21, 11, 23, 22};

    public static class Question {
        public final String text;
        public final String[] options;
        public final int correct;

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
        fetchFromApi(count, CAT_IDS[categoryIndex], difficulty, (questions, source) -> {
            if (questions != null && questions.size() > 0) {
                callback.onReady(questions, source);
            } else {
                callback.onReady(makeMathQuiz(count, difficulty), "Math Mode (offline) 🔢");
            }
        });
    }

    private static void fetchFromApi(int count, int catId, String difficulty, Callback callback) {
        new Thread(() -> {
            try {
                StringBuilder u = new StringBuilder("https://opentdb.com/api.php?amount=")
                        .append(count).append("&type=multiple");
                if (catId > 0) u.append("&category=").append(catId);
                u.append("&difficulty=").append(difficulty);

                HttpURLConnection conn = (HttpURLConnection) new URL(u.toString()).openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONArray results = new JSONObject(sb.toString()).getJSONArray("results");
                List<Question> list = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject o = results.getJSONObject(i);
                    String text = decode(o.getString("question"));
                    String correct = decode(o.getString("correct_answer"));
                    JSONArray wrong = o.getJSONArray("incorrect_answers");

                    List<String> opts = new ArrayList<>();
                    opts.add(correct);
                    for (int j = 0; j < wrong.length(); j++) opts.add(decode(wrong.getString(j)));
                    Collections.shuffle(opts);
                    list.add(new Question(text, opts.toArray(new String[0]), opts.indexOf(correct)));
                }

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onReady(list, "Online Questions 🌍"));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onReady(null, null));
            }
        }).start();
    }

    private static String decode(String s) {
        return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY).toString().trim();
    }

    // ---------- MATH GENERATOR ----------

    private static List<Question> makeMathQuiz(int count, String difficulty) {
        List<Question> list = new ArrayList<>();
        for (int i = 0; i < count; i++) list.add(makeMathQuestion(difficulty));
        return list;
    }

    private static Question makeMathQuestion(String diff) {
        Random rnd = new Random();
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
                answer = a + b;
                text = a + " + " + b + " = ?";
                break;
            case 1:
                if (easy) { a = 10 + rnd.nextInt(18); b = 2 + rnd.nextInt(9); }
                else if (hard) { a = 300 + rnd.nextInt(400); b = 100 + rnd.nextInt(250); }
                else { a = 40 + rnd.nextInt(90); b = 10 + rnd.nextInt(35); }
                answer = a - b;
                text = a + " − " + b + " = ?";
                break;
            case 2:
                if (easy) { a = 2 + rnd.nextInt(6); b = 2 + rnd.nextInt(6); }
                else if (hard) { a = 12 + rnd.nextInt(20); b = 12 + rnd.nextInt(20); }
                else { a = 2 + rnd.nextInt(11); b = 2 + rnd.nextInt(11); }
                answer = a * b;
                text = a + " × " + b + " = ?";
                break;
            default:
                if (easy) { b = 2 + rnd.nextInt(5); answer = 2 + rnd.nextInt(6); }
                else if (hard) { b = 6 + rnd.nextInt(12); answer = 4 + rnd.nextInt(15); }
                else { b = 2 + rnd.nextInt(11); answer = 2 + rnd.nextInt(11); }
                a = b * answer;
                text = a + " ÷ " + b + " = ?";
                break;
        }

        Set<Integer> opts = new HashSet<>();
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
