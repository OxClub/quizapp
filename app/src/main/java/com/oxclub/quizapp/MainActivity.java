package com.oxclub.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends Activity {

    private final Button[] catButtons = new Button[6];
    private final Button[] diffButtons = new Button[3];
    private final String[] DIFF_NAMES = {"easy", "medium", "hard"};
    private TextView tvLevel, tvHigh, tvHints;
    private Button btnTheme, btnSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);
        ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());

        tvLevel = findViewById(R.id.tvLevel);
        tvHigh = findViewById(R.id.tvHigh);
        tvHints = findViewById(R.id.tvHints);
        btnTheme = findViewById(R.id.btnTheme);
        btnSound = findViewById(R.id.btnSound);

        catButtons[0] = findViewById(R.id.btnCat0);
        catButtons[1] = findViewById(R.id.btnCat1);
        catButtons[2] = findViewById(R.id.btnCat2);
        catButtons[3] = findViewById(R.id.btnCat3);
        catButtons[4] = findViewById(R.id.btnCat4);
        catButtons[5] = findViewById(R.id.btnCat5);

        for (int i = 0; i < 6; i++) {
            final int idx = i;
            catButtons[i].setText(QuestionBank.CAT_NAMES[i]);
            catButtons[i].setOnClickListener(v -> {
                Prefs.setCategory(this, idx);
                styleCats();
            });
        }

        diffButtons[0] = findViewById(R.id.btnEasy);
        diffButtons[1] = findViewById(R.id.btnMedium);
        diffButtons[2] = findViewById(R.id.btnHard);

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            diffButtons[i].setOnClickListener(v -> {
                Prefs.setDifficulty(this, DIFF_NAMES[idx]);
                styleDiffs();
            });
        }

        btnTheme.setOnClickListener(v -> {
            Prefs.setDark(this, !Prefs.isDark(this));
            applyTheme();
        });

        btnSound.setOnClickListener(v -> {
            Prefs.setSound(this, !Prefs.isSound(this));
            updateToggles();
        });

        findViewById(R.id.btnStart).setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class)));

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent s = new Intent(Intent.ACTION_SEND);
            s.setType("text/plain");
            s.putExtra(Intent.EXTRA_TEXT, "🧠 I'm playing OxQuiz — my best score is "
                    + Prefs.getHighScore(this) + "/10. Can you beat me? 🔥");
            startActivity(Intent.createChooser(s, "Share OxQuiz"));
        });

        applyTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStats();
        styleCats();
        styleDiffs();
        updateToggles();
    }

    private void refreshStats() {
        int xp = Prefs.getXP(this);
        int level = LevelManager.levelFor(xp);
        tvLevel.setText("Level " + level + " • " + LevelManager.titleFor(level) + " • " + xp + " XP");
        tvHigh.setText("🏆 Best: " + Prefs.getHighScore(this) + "/10");
        tvHints.setText("💡 Hints: " + Prefs.getHints(this));
    }

    private void styleCats() {
        int sel = Prefs.getCategory(this);
        for (int i = 0; i < 6; i++)
            catButtons[i].setBackgroundResource(
                    i == sel ? R.drawable.bg_cat_selected : R.drawable.bg_option);
    }

    private void styleDiffs() {
        String sel = Prefs.getDifficulty(this);
        for (int i = 0; i < 3; i++)
            diffButtons[i].setBackgroundResource(
                    DIFF_NAMES[i].equals(sel) ? R.drawable.bg_cat_selected : R.drawable.bg_option);
    }

    private void updateToggles() {
        btnTheme.setText(Prefs.isDark(this) ? "🌙 Dark" : "☀️ Light");
        btnSound.setText(Prefs.isSound(this) ? "🔊 Sound" : "🔇 Muted");
    }

    private void applyTheme() {
        boolean dark = Prefs.isDark(this);
        findViewById(R.id.root).setBackgroundColor(
                dark ? Color.parseColor("#16213E") : Color.parseColor("#F5F6FA"));
        int c = dark ? Color.parseColor("#B0BEC5") : Color.parseColor("#455A64");
        tvLevel.setTextColor(c);
        tvHigh.setTextColor(c);
        tvHints.setTextColor(c);
        updateToggles();
    }
}
