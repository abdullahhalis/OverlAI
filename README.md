# OverlAI — AI Overlay Translator

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)
![Min SDK](https://img.shields.io/badge/minSdk-26-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-purple)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-orange)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-red)
![AI](https://img.shields.io/badge/AI-ML%20Kit-yellow)
![On Device](https://img.shields.io/badge/AI-On--Device-success)
![Overlay](https://img.shields.io/badge/System-Overlay-important)

> Translate Anything on Your Screen without switching apps.

OverlAI is an Android floating overlay app that captures on-screen text, performs OCR, and displays translated results directly over your screen in real-time. Built for manga, manhwa, and webtoon readers who want seamless translation without leaving their reading app.

---

## 🚀 Features

### Core (MVP)
- **Onboarding** : First-launch walkthrough explaining permissions and core concept
- **Floating Bubble** : Draggable overlay bubble with snap-to-edge behavior
- **Screen Capture** : One-tap screen capture via MediaProjection API
- **OCR Recognition** : On-device text recognition for Japanese, Korean, Chinese, and Latin scripts
- **AI Translation** : On-device translation powered by ML Kit (no internet required)
- **Translation Overlay** : Translated text rendered directly over original text with accurate positioning
- **Language Settings** : Configurable source and target language (ID/EN)
- **Translation History** : Paginated history with date grouping, swipe-to-delete, and undo

### Post-MVP (Planned)
- [ ] Overlay translation position adjustment
- [ ] Capture region selection
- [ ] Auto font scaling
- [ ] Auto language detection
- [ ] Custom glossary / translation override
- [ ] Theme & font customization
- [ ] Filter, search and bookmarks in translation history
- [ ] Model management

---

## 🏗️ Architecture

OverlAI follows **Clean Architecture** principles without a domain layer, repositories are injected directly into ViewModels for simplicity.

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  MainActivity · NavGraph · Screens  │
│  ViewModels · Jetpack Compose UI    │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│            Service Layer            │
│  OverlayService · CaptureManager    │
│  OcrManager · TranslationManager    │
│  OverlayServiceState                │
└────────────────-────────────────────┘
                 │
┌────────────────▼────────────────────┐
│              Data Layer             │
│  AppRepository · AppPreferences     │
│  Room Database · DataStore          │
└─────────────────────────────────────┘
```
The service layer isolates long-running overlay and capture operations from UI lifecycle to prevent Activity-related crashes.

### Key Technical Decisions

|    Decision    | Choice                                 | Reason                                 |
|:--------------:|:---------------------------------------|:---------------------------------------|
|       UI       | Jetpack Compose + Material 3           | Modern declarative UI                  |                
|       DI       | Hilt                                   | Industry standard, compile-time safety |
|     Async      | Coroutines + Flow                      | Structured concurrency                 |
|      OCR       | ML Kit Text Recognition                | On-device, free, multi-script support  |
|  Translation   | ML Kit Translate                       | On-device, no API key needed           |
|    Overlay     | WindowManager TYPE_APPLICATION_OVERLAY | System-level floating window           |
| Screen Capture | MediaProjection API                    | Official Android screen capture API    |
|       DB       | Room + Paging 3                        | Efficient paginated history            |
|     Prefs      | DataStore                              | Coroutine-friendly, type-safe          |

---

## 🛠️ Tech Stack

- **Language** : Kotlin
- **UI** : Jetpack Compose + Material 3
- **Architecture** : MVVM + Clean Architecture (no domain layer)
- **DI** : Hilt
- **ML** : ML Kit Text Recognition + ML Kit Translate
- **Screen Capture** : MediaProjection API
- **Overlay** : WindowManager `TYPE_APPLICATION_OVERLAY`
- **Database** : Room + Paging 3
- **Preferences** : DataStore
- **Build** : AGP, Kotlin DSL

---

## 🗺️ How It Works

```
User taps floating bubble
        ↓
Bubble hides (100ms delay for clean capture)
        ↓
CaptureManager captures screen via MediaProjection
        ↓
Bitmap cropped (status bar area removed)
        ↓
OcrManager processes bitmap via ML Kit Text Recognition
        ↓
OCR blocks merged by proximity (vertical/horizontal aware)
        ↓
TranslationManager translates each block via ML Kit Translate
        ↓
Translation results saved to Room database
        ↓
TranslationOverlay renders translated bubbles over original text
        ↓
User taps anywhere to dismiss
```

---

## ⚠️ Known Limitations

- **OCR accuracy** : ML Kit accuracy is ~85-90% for clear Japanese text. Stylized manga fonts may reduce accuracy
- **Vertical text ordering** : Merged OCR blocks for vertical Japanese text may occasionally have incorrect reading order
- **Fullscreen apps** : Apps that hide the status bar may have slight vertical offset in overlay positioning
- **Dense text** : Not optimized for paragraph-heavy content, best suited for manga/manhwa speech bubbles
- **Translation quality** : ML Kit uses English as a pivot language (JP → EN → ID), which may reduce quality slightly vs. direct translation APIs
- **Single VirtualDisplay** : Android restricts MediaProjection to one active VirtualDisplay; app requires restart if projection is interrupted

---

## ⚙️ Setup & Installation

### Prerequisites
- Android Studio Panda 1 (2025.3.1) or newer
- Android device running API 26+ (Android 8.0)
- **Physical device strongly recommended**, overlay behavior is unreliable on emulator

### Steps

1. Clone the repository
```bash
git clone https://github.com/abdullahhalis/OverlAI.git
```

2. Open in Android Studio

3. Sync Gradle, all dependencies download automatically

4. Run on a physical device

### First Launch
On first launch, the onboarding flow will explain and request three permissions:
1. **Display over other apps** : required for the floating overlay
2. **Screen capture** : required for MediaProjection (requested once per session)
3. **Notifications** : required to show the overlay status indicator (Android 13+)

> ⚠️ **First translation may take longer**. ML Kit downloads language models (around 30MB per language pair) on first use. Subsequent translations are near-instant.

---
<p align="center">Built with ❤️ as an Android portfolio project</p>