# 🧠 OxQuiz

A free offline-ready quiz app for Android. Test your knowledge with thousands of questions — or infinite auto-generated math puzzles when you're offline!

Built 100% from Termux on a phone 📱 → compiled by GitHub Actions 🤖 → zero PC needed.

---

## ✨ Features

- 🌍 Online mode — fresh questions from the Open Trivia Database (opentdb.com)
- 🔢 Math mode — infinite auto-generated math questions when offline. The app never breaks!
- 🎲 Random question & option order every play
- 🏆 Score screen with motivational messages
- 📲 Small APK, works on Android 7.0+

## 🎮 How to play

1. Tap START QUIZ
2. Answer 10 questions — green = correct ✅ red = wrong ❌
3. See your score and try to beat it!

## 🛠️ Build it yourself

This repo builds automatically with GitHub Actions:

1. Fork or clone this repo
2. Push any change
3. Open the Actions tab → download OxQuiz-APK from Artifacts

No Android Studio needed. 😎

## ❓ Adding your own questions

Offline mode auto-generates math questions. To add fixed trivia, edit:
app/src/main/java/com/oxclub/quizapp/QuestionBank.java

    new Question("Your question?", "Option A", "Option B", "Option C", "Option D", correctIndex)
    // correctIndex: 0, 1, 2 or 3 (which option is right)

## 💰 Monetization (AdMob)

The app currently shows Google test ads (safe for development, they pay nothing).

To earn real money:

1. Create an account at admob.google.com
2. Create a Banner + Interstitial ad unit
3. Replace all test IDs starting with ca-app-pub-3940256099942544 with your real IDs
4. Push → GitHub Actions builds the money version 💵

⚠️ Golden rule: never click your own real ads — Google bans accounts for that.

## 🙏 Credits

- Questions: Open Trivia Database (CC BY-SA 4.0)
- Built with ❤️, Termux and GitHub Actions

---

OxQuiz — a project by OxClub 🚀
