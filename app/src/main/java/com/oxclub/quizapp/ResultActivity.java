package com.oxclub.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ResultActivity extends Activity {

    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        MobileAds.initialize(this);
        ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);
        String source = getIntent().getStringExtra("source");

        int percent = total == 0 ? 0 : score * 100 / total;
        boolean perfect = score == total;

        // XP + level
        int oldLevel = LevelManager.levelFor(Prefs.getXP(this));
        int xpEarned = score * 10 + (perfect ? 20 : 0);
        Prefs.addXP(this, xpEarned);
        int newLevel = LevelManager.levelFor(Prefs.getXP(this));

        // High score
        boolean record = score > Prefs.getHighScore(this);
        if (record) Prefs.setHighScore(this, score);

        // Views
        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvHigh = findViewById(R.id.tvHigh);
        TextView tvXP = findViewById(R.id.tvXP);
        TextView msg = findViewById(R.id.tvMessage);

        tvResult.setText(score + " / " + total);

        if (percent >= 100) msg.setText("🎉 PERFECT SCORE! 🏆");
        else if (percent >= 80) msg.setText("🏆 Amazing! Quiz master!");
        else if (percent >= 50) msg.setText("👍 Good job! Keep going!");
        else msg.setText("💪 Don't give up - try again!");

        if (source != null && !source.isEmpty()) {
            msg.setText(msg.getText().toString() + "\n" + source);
        }

        String xpLine = "+" + xpEarned + " XP • Level " + newLevel + " "
                + LevelManager.titleFor(newLevel);
        if (newLevel > oldLevel) xpLine += " ⬆️ LEVEL UP!";
        tvXP.setText(xpLine);

        tvHigh.setText(record ? "🎉 NEW RECORD!" : "🏆 Best: " + Prefs.getHighScore(this) + "/10");

        if (percent >= 80) SoundManager.win(this);

        // Pop-in animation
        tvResult.setScaleX(0f);
        tvResult.setScaleY(0f);
        tvResult.animate().scaleX(1f).scaleY(1f).setDuration(400).start();

        // Buttons
        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent s = new Intent(Intent.ACTION_SEND);
            s.setType("text/plain");
            s.putExtra(Intent.EXTRA_TEXT, "🧠 I scored " + score + "/" + total
                    + " in OxQuiz! Can you beat me? 🔥");
            startActivity(Intent.createChooser(s, "Share your score"));
        });

        findViewById(R.id.btnAgain).setOnClickListener(v -> {
            Intent i = new Intent(this, QuizActivity.class);
            if (interstitial != null) {
                interstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override public void onAdDismissedFullScreenContent() { go(i); }
                    @Override public void onAdFailedToShowFullScreenContent(AdError e) { go(i); }
                });
                interstitial.show(this);
            } else {
                go(i);
            }
        });

        findViewById(R.id.btnMenu).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        loadInterstitial();
    }

    private void go(Intent i) {
        startActivity(i);
        finish();
    }

    private void loadInterstitial() {
        InterstitialAd.load(this,
                "ca-app-pub-3940256099942544/1033173712",
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override public void onAdLoaded(InterstitialAd ad) { interstitial = ad; }
                    @Override public void onAdFailedToLoad(LoadAdError e) { interstitial = null; }
                });
    }
}
