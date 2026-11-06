# 📱 AIUB Portal App

An unofficial Android app for the [AIUB student portal](https://portal.aiub.edu/) —
the full portal in a polished shell, with a built-in dark theme, automatic captcha
solving, saved login, AIUB notices with phone notifications, a downloads manager,
faculty-upload alerts, a GPA dashboard and exam-routine countdowns.

[![Download latest release](https://img.shields.io/badge/⬇%20Download-APK-0b4a9e?style=for-the-badge)](https://github.com/Shishir-ip/Aiub-Portal-App/releases/latest)

## ✨ Features

- 🌙 **Themes — Default / Night / Light / Auto** — the Night theme is a carefully
  tuned navy palette applied to every portal page (same theme system as the
  AIUB Portal Dark Mode extension).
- 🤖 **Captcha auto-solver** — on-device OCR (bundled Tesseract + English data)
  reads the dotted-font login equation and fills the answer. Never fills a
  guessed answer; unreadable captchas refresh automatically. Toggle in Settings.
- 💾 **Saved login details** — store your ID/password once; the login page shows
  your saved account and fills everything for you.
- 🔄 **Auto re-login** — the portal logs you out after a while. When the app
  detects the login page, it silently logs back in with your saved credentials
  (captcha included) and reloads where you were. You never see the login screen.
- 📢 **AIUB notices** — polls www.aiub.edu/category/notices and posts a phone
  notification when a new notice appears (15/30/60-minute polling, toggle in
  Settings), plus a native notices screen.
- 📞 **Faculty-upload alerts** — teachers upload notes/files all the time; the
  app watches the portal's own notification center with your session and pushes
  a phone alert the moment one appears.
- 📥 **Downloads manager** — every file you download from the portal lands in
  **Documents/Aiub Portal** on your phone (visible in your Files app) and in the
  app's Downloads screen: open, rename, delete, share, view details.
- 📊 **GPA dashboard** — your CGPA and every course grade parsed into a native
  screen with a per-semester average chart.
- 📅 **Exam routine + countdown** — exam schedule with a live countdown to your
  next exam.
- 🌐 **Official website** — a plain in-app browser tab for www.aiub.edu.

## 📦 Install

### ⚡ Install the APK (recommended, 1 minute)

1. **[Download Aiub-Portal-App-v1.0.0.apk](https://github.com/Shishir-ip/Aiub-Portal-App/releases/download/v1.0.0/Aiub-Portal-App-v1.0.0.apk)**
   from the Releases page.
2. Open it on your phone (allow "install from unknown sources" if asked).
3. Open the app → log in once → Settings → enable **Save my login details**
   and **Auto re-login**. Done.

Requires Android 10+.

### 🔧 Build from source

## ⚙️ First launch

1. Open the app → the **Portal** tab opens the login page.
2. Log in once. Then go to **Settings → Login details** → enable **Save my
   login details** and enter your ID/password → enable **Auto re-login**.
3. From then on: when the portal logs you out, the app logs you back in
   silently. On the manual login page, tap the saved-account chip and the
   captcha is solved for you.

## ⚙️ Settings overview

| Setting | What it does |
| --- | --- |
| Appearance | Default / Night / Light / Auto theme |
| Captcha solver | Auto-solve the login captcha (on-device OCR) |
| Login details | Save / edit / clear your ID + password, auto re-login |
| AIUB notices | On/off + poll interval (15/30/60 min) |
| Portal alerts | Faculty uploads & portal notifications as phone alerts |

## 🗂 Project structure

```
app/src/main/java/com/shishir/aiubportal/
├── MainActivity.kt          # shell: bottom navigation + screens
├── portal/                  # WebView, theme injector, captcha solver,
│                            # session keeper (auto re-login), downloads
├── notices/                 # AIUB notices screen, poller, faculty alerts
├── downloads/               # Downloads manager screen + MediaStore repo
├── gpa/                     # GPA dashboard (parses the grade report)
├── exam/                    # Exam routine + countdown
├── settings/                # Settings screen + DataStore
└── util/                    # cookie-aware HTTP client
app/src/main/assets/
├── dark.css                 # the dark theme stylesheet (injected per page)
└── tessdata/eng.traineddata.gz
```

## 🛠 Technical notes

- **Hybrid design** — the portal is a session-based website without a public
  API, so the app renders it in an enhanced WebView and adds native powers:
  theme injection, captcha OCR, credential autofill, download interception.
  All native screens (notices, GPA, exam, downloads) parse the same pages the
  website uses.
- **Session expiry** — the portal invalidates sessions periodically. The app
  detects the login redirect and auto re-logs-in; background alert workers
  simply skip until the next successful login.
- **Captcha OCR** runs 100% on-device (tesseract4android); the login captcha
  never leaves your phone.
- **Downloads** go to the public `Documents/Aiub Portal` folder via MediaStore
  (Android 10+), so they are visible in your Files app.
- If AIUB changes their HTML routes, the parsers in
  `NoticesRepository/GpaRepository/ExamRepository` are the only files to update.

## 🧪 Tested

- Login + auto re-login flow, captcha solving
- Dashboard, Courses & Results (with Upload Document links), Registration,
  Print page, Grade Report, Financials, Section/POC schedule
- Notices screen + notifications, GPA dashboard, exam countdown
- Downloads to Documents/Aiub Portal, rename/delete/details
- Night/Light/Auto theme switching

## 📄 License

[MIT](LICENSE)

---

Made with 💙 for AIUB students. Not affiliated with or endorsed by
American International University-Bangladesh.
