# 🇮🇳 योजना सहायक (Yojana Sahayak) — Government Welfare & Scheme Assistant

**Yojana Sahayak** is an intuitive, accessible Android application designed to help citizens across India—especially in rural and semi-urban communities—discover, evaluate eligibility for, and apply for 50+ central and state government schemes, scholarships, and welfare programs.

Powered by **Jetpack Compose**, **Room Database**, **Gemini 2.5/3 Flash AI**, and **Android Speech Recognition & Speech Synthesis (TTS)**, the app delivers a seamless multilingual experience tailored for Indian citizens.

---

## 🌟 Key Features

### 1. 🖼️ Home Page App Info Image Slider Carousel
- Prominently positioned at the top of the home screen.
- Auto-sliding visual carousel highlighting core app capabilities:
  - 🏛️ **100% Official Information**: Verified central and state schemes.
  - 🎙️ **AI Voice Assistant**: Hands-free voice querying in Hindi and regional languages.
  - 🎯 **Eligibility Assessor**: Smart matching based on age, income, state, and category.
  - 📍 **CSC Office Locator**: Map-integrated Common Service Center guidance.
  - 🔖 **Bookmark & Offline Access**: Save schemes and generate official CSC application slips.

### 2. 🏛️ Scheme Directory & Smart Filtering
- Comprehensive database of central and state welfare schemes (PM-Kisan, PM Awas Yojana, Sukanya Samriddhi, Ladli Behna, Post-Matric Scholarships, etc.).
- Categorized by **Recipient Profile** (*Myself, Family, Parents, Daughter, Son*) and **Occupational Sector** (*Farmers, Students, Women, Senior Citizens, BPL/LIG, Artisans*).
- Instant text search across Hindi and English scheme titles, department names, and benefits.

### 3. 🎯 Eligibility Calculator & Assessor
- Evaluates citizen eligibility using multi-point criteria:
  - Age slider & gender selection
  - Annual household income & BPL Ration Card status
  - Residence state selection
  - Educational qualification & social category (General, OBC, SC, ST, EWS)
- Instant matching algorithm calculates percentage match scores and filters relevant schemes in real-time.

### 4. 🎙️ Multilingual AI Voice Assistant & TTS Audio Narration
- **Voice Recognition (STT)**: Built with Android `SpeechRecognizer` tuned for BCP-47 language codes (`hi-IN`, `en-IN`, `bn-IN`, `te-IN`, `mr-IN`, `ta-IN`, `gu-IN`, `kn-IN`, `ml-IN`, `pa-IN`).
- **Voice Synthesis (TTS)**: `TtsManager` with a 0.88x speech rate for clear, comfortable audio narration suited for elderly and rural users.
- **Dedicated Voice Mode ("केवल बोलकर उपयोग करें")**: Hands-free conversational AI mode powered by Gemini for citizens unable to read or type text.

### 5. 📄 Document Validity Scanner & Gemini OCR
- Verifies government documents required for scheme applications:
  - Aadhaar Card, PAN Card, Voter ID, Ration Card, Driving License, Passport, Birth Certificate, Caste Certificate, Domicile, and Income Certificate.
- **Income Certificate Validity Rules**: Evaluates the mandatory 3-year validity window from issue date and alerts users if renewal is required before applying for scholarships/schemes.
- **Gemini OCR Analysis**: Accepts document image uploads or camera captures to automatically extract issue dates and verify validity status.

### 6. 🖨️ CSC Center Locator & Application Slip Generator
- Finds nearby Common Service Centers (CSC / Jan Seva Kendra) and government block/tehsil offices.
- Generates official **CSC Application Slips** with a unique barcode/reference ID (`CSC-XXXXXX`), citizen profile breakdown, required documents checklist, and official `.gov.in` portal links.

### 7. 📊 Scheme Collector / Admin Dashboard
- Administrative view for tracking scheme updates and Press Information Bureau (PIB) press releases.
- Live PIB sync engine inserts new schemes directly into local Room DB.

---

## 📁 Project Folder & File Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                      # Single activity host with Jetpack Compose Scaffold
│
├── data/                                # Data Layer (Database, Repositories, API, Sync)
│   ├── api/
│   │   └── GeminiService.kt             # REST API client for Gemini AI advice, eligibility & OCR
│   ├── local/
│   │   ├── AppDatabase.kt               # Room Database configuration
│   │   ├── SchemeDao.kt                 # Data Access Object for Schemes
│   │   ├── SchemeEntity.kt              # Room Entity for Scheme records
│   │   ├── QuestionnaireDao.kt          # DAO for saved eligibility questionnaire responses
│   │   ├── QuestionnaireResponseEntity.kt # Entity for persisted questionnaire state
│   │   ├── CscSlipDao.kt                # DAO for Common Service Center application slips
│   │   └── CscSlipEntity.kt             # Entity for generated CSC slips
│   ├── model/
│   │   ├── Scheme.kt                    # Domain model for Welfare Schemes
│   │   ├── CitizenProfile.kt            # Model for citizen age, income, state & category
│   │   ├── GovDocument.kt               # Model for government identity documents
│   │   ├── CscSlip.kt                   # Model for CSC Application Slip
│   │   └── ChatMessage.kt               # Model for AI conversation messages
│   ├── repository/
│   │   ├── SchemeRepository.kt          # Main Repository for Schemes, Gemini AI & CSC Slips
│   │   └── GovDocumentRepository.kt     # Repository for Gov Documents & Validity OCR
│   └── sync/
│       └── SchemeSyncManager.kt         # Background sync manager for PIB news releases
│
├── ui/                                  # Presentation / UI Layer (Compose & ViewModel)
│   ├── MainViewModel.kt                 # Central ViewModel managing state, modes & voice
│   ├── AppMode.kt                       # Screen navigation enum modes
│   ├── components/
│   │   ├── AppInfoImageSlider.kt        # Top visual carousel introducing app features
│   │   ├── SchemeCard.kt                # Custom M3 card for individual scheme display
│   │   ├── CategoryFilterChips.kt       # Scrollable chip row for citizen category filtering
│   │   ├── RecipientProfileSelector.kt  # Profile avatar row (Myself, Family, Daughter, etc.)
│   │   └── VoiceInputDialog.kt          # Popup voice recording dialog with pulse animation
│   ├── screens/
│   │   ├── HomeScreen.kt                # Primary screen combining slider, search & scheme list
│   │   ├── EligibilityCalculatorScreen.kt # Interactive age, income & state eligibility form
│   │   ├── VoiceAssistantScreen.kt      # Dedicated voice-only assistant UI
│   │   ├── DocValidityScannerScreen.kt # Document scanner & 3-year validity checker
│   │   ├── DirectoryScreen.kt           # All government documents & application guides
│   │   ├── OfficeLocatorScreen.kt       # CSC Center map & address locator
│   │   ├── CscSlipScreen.kt             # Printable CSC Application Slip view
│   │   ├── SchemeCollectorDashboardScreen.kt # PIB updates & collector dashboard
│   │   ├── QuestionnaireScreen.kt       # Guided step-by-step eligibility wizard
│   │   ├── SavedSchemesScreen.kt        # Bookmarked schemes and saved CSC slips
│   │   └── WhatsAppChatScreen.kt        # Chat interface with AI scheme assistant
│   └── theme/
│       ├── Color.kt                     # Indian National Theme Colors (Saffron, Emerald Green, Navy Deep)
│       ├── Theme.kt                     # Material 3 Light/Dark Theme specification
│       └── Type.kt                      # Typography configurations
│
└── util/                                # Utility Utilities
    ├── VoiceRecognizer.kt               # SpeechRecognizer manager for STT
    ├── TtsManager.kt                    # TextToSpeech manager for TTS narration
    └── LanguageTranslator.kt            # Language option definitions & state helpers
```

---

## 🛠️ How to Build & Run Locally

### Prerequisites
1. **Android Studio**: Ladybug (2024.2.1+) or Hedgehog (2023.1.1+)
2. **JDK Version**: Java 17 or higher
3. **Android SDK**: API Level 34 (Android 14) compiled, Minimum SDK 24 (Android 7.0)

### Steps to Run
1. **Clone or Extract the Project**:
   Open the root directory in Android Studio.

2. **Configure API Key (Optional for AI Features)**:
   The app uses `BuildConfig.GEMINI_API_KEY` for AI features. Pass your key via environment variables or `.env` file:
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Build the Project via Gradle**:
   In the terminal or Android Studio command line:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on Device or Emulator**:
   Select your target connected Android device or emulator and press **Run (`Shift + F10`)**.

5. **Grant Permissions**:
   On first launch, grant **Microphone (`RECORD_AUDIO`)** permission when activating the Voice Assistant.

---

## 📱 Tech Stack & Libraries

- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Repository Pattern
- **State Management**: Kotlin Coroutines, `StateFlow`, `collectAsStateWithLifecycle`
- **Database**: Room Database with KSP (Kotlin Symbol Processing)
- **Networking & AI**: OkHttp, KotlinX Serialization, Gemini REST API
- **Voice Capabilities**: Android Native `SpeechRecognizer` & `TextToSpeech`
