package com.oxclub.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
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
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);
        String source = getIntent().getStringExtra("source");

        ((TextView) findViewById(R.id.tvResult)).setText(score + " / " + total);

        int percent = total == 0 ? 0 : score * 100 / total;
        TextView msg = findViewById(R.id.tvMessage);
        if (percent >= 80) msg.setText("🏆 Amazing! Quiz master!");
        else if (percent >= 50) msg.setText("👍 Good job! Keep going!");
        else msg.setText("💪 Don't give up - try again!");

        if (source != null && !source.isEmpty()) {
            msg.setText(msg.getText().toString() + "\n" + source);
        }

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
                    @Override public void onAdFailedToLoad(AdError e) { interstitial = null; }
                });
    }
}
