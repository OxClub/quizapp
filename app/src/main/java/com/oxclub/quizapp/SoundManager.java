package com.oxclub.quizapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class SoundManager {
    private static ToneGenerator tone;

    private static void init() {
        if (tone == null) {
            try { tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 80); } catch (Exception e) { tone = null; }
        }
    }

    public static void correct(Context c) {
        if (!Prefs.isSound(c)) return;
        init();
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_ACK, 150);
    }

    public static void wrong(Context c) {
        if (!Prefs.isSound(c)) return;
        init();
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_NACK, 250);
    }

    public static void tick(Context c) {
        if (!Prefs.isSound(c)) return;
        init();
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_BEEP, 60);
    }

    public static void win(Context c) {
        if (!Prefs.isSound(c)) return;
        init();
        if (tone != null) tone.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 400);
    }

    public static void vibrate(Context c, long ms) {
        try {
            Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            else
                v.vibrate(ms);
        } catch (Exception ignored) { }
    }
}
