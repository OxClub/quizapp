package com.oxclub.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends Activity {

    private List<QuestionBank.Question> questions;
    private String source = "";
    private int index = 0, score = 0, lastSecond = 16;
    private boolean answered = false;

    private TextView tvQuestion, tvProgress, tvScore, tvTimer;
    private ProgressBar progressBar;
    private Button btnHint;
    private final Button[] options = new Button[4];
    private CountDownTimer timer;
    private RewardedAd rewarded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        MobileAds.initialize(this);
        ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());
        loadRewarded();

        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        progressBar = findViewById(R.id.progressBar);
        btnHint = findViewById(R.id.btnHint);

        options[0] = findViewById(R.id.btnOpt1);
        options[1] = findViewById(R.id.btnOpt2);
        options[2] = findViewById(R.id.btnOpt3);
        options[3] = findViewById(R.id.btnOpt4);

        for (int i = 0; i < 4; i++) {
            final int choice = i;
            options[i].setOnClickListener(v -> answer(choice));
        }

        btnHint.setOnClickListener(v -> onHint());

        applyTheme();
        loadQuestions();
    }

    private void loadQuestions() {
        tvQuestion.setText("Loading questions...");
        for (Button b : options) b.setEnabled(false);

        QuestionBank.loadQuiz(10, Prefs.getCategory(this), Prefs.getDifficulty(this), (qs, src) -> {
            questions = qs;
            source = src == null ? "" : src;
            for (Button b : options) b.setEnabled(true);
            showQuestion();
        });
    }

    private void showQuestion() {
        answered = false;
        QuestionBank.Question q = questions.get(index);

        tvQuestion.setText(q.text);
        tvProgress.setText("Q " + (index + 1) + "/" + questions.size());
        tvScore.setText("Score: " + score);
        updateHintLabel();
        btnHint.setEnabled(true);

        for (int i = 0; i < 4; i++) {
            options[i].setText(q.options[i]);
            options[i].setEnabled(true);
            options[i].setAlpha(1f);
            options[i].setScaleX(1f);
            options[i].setScaleY(1f);
            options[i].setBackgroundResource(R.drawable.bg_option);
            options[i].setTextColor(Color.parseColor("#1B2A4A"));

            options[i].setAlpha(0f);
            options[i].animate().alpha(1f).setDuration(200).setStartDelay(60 + i * 60).start();
        }

        tvQuestion.setAlpha(0f);
        tvQuestion.setTranslationY(50f);
        tvQuestion.animate().alpha(1f).translationY(0f).setDuration(250).start();

        startTimer();
    }

    // ---------- TIMER ----------

    private void startTimer() {
        if (timer != null) timer.cancel();
        lastSecond = 16;
        progressBar.setProgress(100);
        tvTimer.setTextColor(Color.parseColor("#FF9800"));
        tvTimer.setText("15s ⏱️");

        timer = new CountDownTimer(15000, 200) {
            @Override
            public void onTick(long ms) {
                progressBar.setProgress((int) (ms * 100 / 15000));
                int sec = (int) ((ms + 999) / 1000);
                tvTimer.setText(sec + "s ⏱️");
                if (sec <= 5) {
                    if (sec != lastSecond) SoundManager.tick(QuizActivity.this);
                    tvTimer.setTextColor(Color.parseColor("#E53935"));
                }
                lastSecond = sec;
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                timeUp();
            }
        }.start();
    }

    private void timeUp() {
        if (answered || questions == null) return;
        answered = true;
        SoundManager.wrong(this);
        SoundManager.vibrate(this, 300);
        paint(questions.get(index).correct, R.drawable.bg_correct);
        lockOptions();
        nextAfterDelay();
    }

    // ---------- ANSWERS ----------

    private void answer(int choice) {
        if (answered) return;
        answered = true;
        if (timer != null) timer.cancel();

        QuestionBank.Question q = questions.get(index);
        pop(options[choice]);

        if (choice == q.correct) {
            score++;
            SoundManager.correct(this);
            paint(choice, R.drawable.bg_correct);
            bumpScore();
        } else {
            SoundManager.wrong(this);
            SoundManager.vibrate(this, 250);
            paint(choice, R.drawable.bg_wrong);
            paint(q.correct, R.drawable.bg_correct);
        }
        lockOptions();
        nextAfterDelay();
    }

    private void nextAfterDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            index++;
            if (index < questions.size()) showQuestion();
            else finishQuiz();
        }, 1100);
    }

    private void finishQuiz() {
        Intent i = new Intent(this, ResultActivity.class);
        i.putExtra("score", score);
        i.putExtra("total", questions.size());
        i.putExtra("source", source);
        startActivity(i);
        finish();
    }

    // ---------- HINT (50/50) ----------

    private void onHint() {
        if (answered) return;
        if (Prefs.getHints(this) > 0) {
            Prefs.setHints(this, Prefs.getHints(this) - 1);
            SoundManager.vibrate(this, 30);
            useHint();
        } else if (rewarded != null) {
            rewarded.show(this, reward -> {
                Prefs.setHints(this, Prefs.getHints(this) + 1);
                Toast.makeText(this, "+1 Hint earned! 💡", Toast.LENGTH_SHORT).show();
                useHint();
            });
        } else {
            Toast.makeText(this, "Video not ready — try again in a moment", Toast.LENGTH_SHORT).show();
            loadRewarded();
        }
        updateHintLabel();
    }

    private void useHint() {
        if (answered || questions == null) return;
        QuestionBank.Question q = questions.get(index);
        List<Integer> wrongIdx = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (i != q.correct) wrongIdx.add(i);
        Collections.shuffle(wrongIdx);
        for (int i = 0; i < 2 && i < wrongIdx.size(); i++) {
            options[wrongIdx.get(i)].setEnabled(false);
            options[wrongIdx.get(i)].setAlpha(0.25f);
        }
        btnHint.setEnabled(false);
    }

    private void updateHintLabel() {
        btnHint.setText("💡 50/50 (" + Prefs.getHints(this) + " left)");
    }

    private void loadRewarded() {
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override public void onAdLoaded(RewardedAd ad) { rewarded = ad; }
                    @Override public void onAdFailedToLoad(LoadAdError e) { rewarded = null; }
                });
    }

    // ---------- HELPERS ----------

    private void paint(int i, int res) {
        options[i].setBackgroundResource(res);
        options[i].setTextColor(Color.WHITE);
    }

    private void lockOptions() {
        for (Button b : options) b.setEnabled(false);
    }

    private void pop(Button b) {
        b.animate().scaleX(1.08f).scaleY(1.08f).setDuration(100)
                .withEndAction(() -> b.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void bumpScore() {
        tvScore.animate().scaleX(1.5f).scaleY(1.5f).setDuration(120)
                .withEndAction(() -> tvScore.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
                .start();
    }

    private void applyTheme() {
        boolean dark = Prefs.isDark(this);
        findViewById(R.id.root).setBackgroundColor(
                dark ? Color.parseColor("#16213E") : Color.parseColor("#F5F6FA"));
        tvQuestion.setTextColor(dark ? Color.WHITE : Color.parseColor("#1B2A4A"));
        int c = dark ? Color.parseColor("#B0BEC5") : Color.parseColor("#455A64");
        tvProgress.setTextColor(c);
        tvScore.setTextColor(c);
        tvTimer.setTextColor(Color.parseColor("#FF9800"));
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}
