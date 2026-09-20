package com.oxclub.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class QuizActivity extends Activity {

    private List<QuestionBank.Question> questions;
    private int index = 0;
    private int score = 0;
    private boolean answered = false;

    private TextView tvQuestion, tvProgress, tvScore;
    private final Button[] options = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        MobileAds.initialize(this);
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        questions = QuestionBank.getQuiz(10);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);

        options[0] = findViewById(R.id.btnOpt1);
        options[1] = findViewById(R.id.btnOpt2);
        options[2] = findViewById(R.id.btnOpt3);
        options[3] = findViewById(R.id.btnOpt4);

        for (int i = 0; i < 4; i++) {
            final int choice = i;
            options[i].setOnClickListener(v -> answer(choice));
        }

        showQuestion();
    }

    private void showQuestion() {
        answered = false;
        QuestionBank.Question q = questions.get(index);

        tvQuestion.setText(q.text);
        tvProgress.setText("Question " + (index + 1) + " / " + questions.size());
        tvScore.setText("Score: " + score);

        for (int i = 0; i < 4; i++) {
            options[i].setText(q.options[i]);
            options[i].setEnabled(true);
            options[i].setBackgroundResource(R.drawable.bg_option);
            options[i].setTextColor(Color.parseColor("#1B2A4A"));
        }
    }

    private void answer(int choice) {
        if (answered) return;
        answered = true;

        QuestionBank.Question q = questions.get(index);

        if (choice == q.correct) {
            score++;
            paint(choice, R.drawable.bg_correct);
        } else {
            paint(choice, R.drawable.bg_wrong);
            paint(q.correct, R.drawable.bg_correct);
        }
        for (Button b : options) b.setEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            index++;
            if (index < questions.size()) {
                showQuestion();
            } else {
                Intent i = new Intent(this, ResultActivity.class);
                i.putExtra("score", score);
                i.putExtra("total", questions.size());
                startActivity(i);
                finish();
            }
        }, 900);
    }

    private void paint(int i, int res) {
        options[i].setBackgroundResource(res);
        options[i].setTextColor(Color.WHITE);
    }
}
