# 🏗️ ARCHITECTURE.md — System Architecture & Component Design

This document details the architectural design, component interactions, state management, database schemas, and AI/Voice pipelines for **योजना सहायक (Yojana Sahayak)**.

---

## 📐 Architecture Overview

The application strictly follows **Android Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** architectural pattern using **Jetpack Compose** for presentation and **Room** for local persistence.

```
       ┌─────────────────────────────────────────────────────────────┐
       │                 UI Layer (Jetpack Compose)                 │
       │  HomeScreen, EligibilityCalculatorScreen, VoiceAssistant UI │
       └──────────────────────────────┬──────────────────────────────┘
                                      │
                                      ▼
       ┌─────────────────────────────────────────────────────────────┐
       │                   ViewModel (MainViewModel)                 │
       │     Manages AppMode, StateFlows, STT & TTS triggers         │
       └──────────────┬──────────────────────────────┬───────────────┘
                      │                              │
                      ▼                              ▼
       ┌──────────────────────────────┐┌─────────────────────────────┐
       │       SchemeRepository       ││    GovDocumentRepository    │
       └──────┬────────────────┬──────┘└──────────────┬──────────────┘
              │                │                      │
              ▼                ▼                      ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│   Room SQLite    │  │  Gemini AI API   │  │ Voice/TTS Utils  │
│  (AppDatabase)   │  │ (GeminiService)  │  │(VoiceRecognizer) │
└──────────────────┘  └──────────────────┘  └──────────────────┘
```

---

## 🔄 Component Connection & Data Flow

### 1. Presentation Layer (UI & Jetpack Compose)
- **Single Activity Architecture**: `MainActivity.kt` acts as the root host container. It initializes the `MainViewModel` and renders a responsive `Scaffold`.
- **Navigation via Enum State (`AppMode`)**: Screen switching is managed reactively via `currentMode: StateFlow<AppMode>`.
  - `AppMode.STANDARD_SCHEMES`: Renders `HomeScreen.kt` (Slider, Search, Scheme List).
  - `AppMode.ELIGIBILITY_CALCULATOR`: Renders `EligibilityCalculatorScreen.kt`.
  - `AppMode.VOICE_ASSISTANT`: Renders `VoiceAssistantScreen.kt`.
  - `AppMode.DOC_VALIDITY_SCANNER`: Renders `DocValidityScannerScreen.kt`.
  - `AppMode.DOCUMENTS_DIRECTORY`: Renders `DirectoryScreen.kt`.
  - `AppMode.CSC_SLIPS`: Renders `CscSlipScreen.kt`.
  - `AppMode.SCHEME_COLLECTOR_DASHBOARD`: Renders `SchemeCollectorDashboardScreen.kt`.
  - `AppMode.OFFICE_LOCATOR`: Renders `OfficeLocatorScreen.kt`.

### 2. ViewModel Layer (`MainViewModel.kt`)
- Serves as the single source of truth for the UI.
- Exposes immutable `StateFlow` streams (`allSchemes`, `savedSchemes`, `citizenProfile`, `voiceAssistantTranscript`, `voiceAssistantResponse`, `isListening`, `showVoiceInputDialog`).
- Executes coroutines (`viewModelScope.launch`) to trigger repository queries, voice recognition events, and Gemini AI advice without blocking the main UI thread.

### 3. Repository Layer (`SchemeRepository.kt` & `GovDocumentRepository.kt`)
- **`SchemeRepository`**: Bridges the UI with the local Room Database (`SchemeDao`, `CscSlipDao`, `QuestionnaireDao`) and external `GeminiService`.
  - Reads DB entities and maps them to clean domain models (`Scheme`, `CscSlip`).
  - Pre-populates default database records on first run.
  - Generates CSC Application Slips with randomized reference IDs.
- **`GovDocumentRepository`**: Manages official government document definitions, live rule fetching, and document validity OCR verification.

### 4. Remote API Layer (`GeminiService.kt`)
- Handles REST calls to Gemini AI using `OkHttp` and `kotlinx.serialization`.
- Formulates tailored system instructions in Hindi and regional languages to ensure accurate, safe, non-hallucinated welfare scheme guidance.

---

## 🎙️ Voice Pipeline Architecture

The application includes an end-to-end voice engine enabling hands-free operation:

```
[User Speaks] ──► [VoiceRecognizer (SpeechRecognizer)]
                           │
                           ▼ (Transcribed Text)
               [MainViewModel / VoiceAssistantScreen]
                           │
                           ▼ (Query Request)
                     [GeminiService]
                           │
                           ▼ (AI Response Text)
                  [TtsManager (TextToSpeech)] ──► [Audio Output]
```

1. **Speech-to-Text (STT)**:
   - `VoiceRecognizer.kt` wraps Android `android.speech.SpeechRecognizer`.
   - Listens on `hi-IN` (Hindi) by default or selected BCP-47 locale tag.
   - Converts voice input into a clean string callback and updates `_voiceAssistantTranscript`.

2. **AI Processing**:
   - Transcribed string is sent to `SchemeRepository.queryGeminiAssistant()`.
   - Gemini generates a concise, citizen-friendly response in Hindi or target regional language.

3. **Text-to-Speech (TTS)**:
   - `TtsManager.kt` receives the AI response.
   - Strips markdown symbols (*, #, •) for natural speech flow.
   - Synthesizes speech at **0.88x speed** for optimal comprehension by elderly or rural citizens.

---

## 📄 Document Validity Scanner & OCR Engine

The Document Scanner evaluates document expiration and validity periods (critical for Income Certificates valid for 3 years):

```
[Doc Image / Date Input] ──► [DocValidityScannerScreen]
                                       │
                                       ▼ (Base64 + Text)
                           [GovDocumentRepository]
                                       │
                                       ▼
                     [GeminiService.analyzeDocumentValidity]
                                       │
                                       ▼
                   ┌───────────────────────────────────────┐
                   │ Structured Verification Result:       │
                   │ • Status: VALID / EXPIRED / RENEWAL   │
                   │ • Days Remaining                     │
                   │ • Step-by-Step Renewal Instructions   │
                   └───────────────────────────────────────┘
```

---

## 🗄️ Database Architecture & Schemas

The local database uses **Room DB** (`AppDatabase.kt`) with versioning and KSP compiler support.

### 1. `schemes` Table (`SchemeEntity.kt`)
| Column | Type | Description |
|---|---|---|
| `id` | String (Primary Key) | Unique scheme identifier |
| `titleHindi` | String | Scheme name in Hindi |
| `titleEng` | String | Scheme name in English |
| `ministryOrOrganization` | String | Issuing government department |
| `category` | String | Category enum string (FARMERS, WOMEN, STUDENTS, etc.) |
| `sector` | String | Sector enum string (AGRICULTURE, EDUCATION, HEALTH, etc.) |
| `shortDescriptionHindi` | String | Brief summary |
| `detailedDescriptionHindi` | String | Complete scheme details & benefits |
| `maxBenefitAmount` | String | Maximum financial benefit (e.g. ₹6,000/year) |
| `officialUrl` | String | Portal URL (.gov.in) |
| `isSaved` | Boolean | Bookmark status |
| `isAlertSubscribed` | Boolean | Deadline alert subscription |
| `isPIBRecent` | Boolean | Sourced from recent PIB press release |
| `matchPercentage` | Int | Calculated compatibility score |

### 2. `csc_slips` Table (`CscSlipEntity.kt`)
| Column | Type | Description |
|---|---|---|
| `id` | String (Primary Key) | CSC Reference ID (e.g., `CSC-849201`) |
| `applicantName` | String | Citizen name |
| `gender` | String | Citizen gender |
| `age` | Int | Citizen age |
| `annualIncomeRupees` | Long | Annual family income |
| `state` | String | Residence state |
| `generationDate` | String | Timestamp of slip generation |
| `appliedSchemeNames` | String | Comma-separated list of schemes applied for |
| `requiredDocuments` | String | Comma-separated documents checklist |

### 3. `questionnaire_responses` Table (`QuestionnaireResponseEntity.kt`)
| Column | Type | Description |
|---|---|---|
| `id` | Int (Primary Key) | Single row persistence (ID = 1) |
| `applicantName` | String | Saved applicant name |
| `age` | Int | Saved age |
| `income` | Long | Saved income |
| `occupation` | String | Occupation title |
| `state` | String | State |
| `isBpl` | Boolean | BPL card status |
| `recipient` | String | Target recipient enum string |
| `category` | String | Category enum string |

---

## 🎨 UI Theme System

Custom Indian National Theme defined in `com/example/ui/theme/Color.kt`:
- **Saffron Primary (`#FF9933` / `#E65100`)**: Highlights primary CTA buttons and status badges.
- **Emerald Green (`#047857` / `#065F46`)**: Success indicators, verified badges, and top bars.
- **Navy Deep (`#1B2A4A` / `#0F172A`)**: Executive headers, dark cards, and high-contrast display text.
