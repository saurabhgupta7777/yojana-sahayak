package com.example.util

import com.example.data.model.CitizenCategory
import com.example.data.model.Scheme
import com.example.data.model.SchemeSector

object LanguageTranslator {

    fun getLocalizedTitle(scheme: Scheme, language: String): String {
        val lang = language.lowercase()
        return when {
            lang.contains("english") || lang == "en" -> scheme.titleEng.ifBlank { scheme.titleHindi }
            lang.contains("bengali") || lang.contains("বাংলা") || lang == "bn" -> getBengaliTitle(scheme)
            lang.contains("telugu") || lang.contains("తెలుగు") || lang == "te" -> getTeluguTitle(scheme)
            lang.contains("marathi") || lang.contains("मराठी") || lang == "mr" -> getMarathiTitle(scheme)
            lang.contains("tamil") || lang.contains("தமிழ்") || lang == "ta" -> getTamilTitle(scheme)
            lang.contains("gujarati") || lang.contains("ગુજરાતી") || lang == "gu" -> getGujaratiTitle(scheme)
            else -> scheme.titleHindi
        }
    }

    fun getLocalizedShortDesc(scheme: Scheme, language: String): String {
        val lang = language.lowercase()
        return when {
            lang.contains("english") || lang == "en" -> getEnglishDesc(scheme)
            lang.contains("bengali") || lang.contains("বাংলা") || lang == "bn" -> getBengaliDesc(scheme)
            lang.contains("telugu") || lang.contains("తెలుగు") || lang == "te" -> getTeluguDesc(scheme)
            lang.contains("marathi") || lang.contains("मराठी") || lang == "mr" -> getMarathiDesc(scheme)
            lang.contains("tamil") || lang.contains("தமிழ்") || lang == "ta" -> getTamilDesc(scheme)
            lang.contains("gujarati") || lang.contains("ગુજરાતી") || lang == "gu" -> getGujaratiDesc(scheme)
            else -> scheme.shortDescriptionHindi
        }
    }

    fun getLocalizedCategory(category: CitizenCategory, language: String): String {
        val lang = language.lowercase()
        val isEng = lang.contains("english") || lang == "en"
        val isBengali = lang.contains("bengali") || lang.contains("বাংলা") || lang == "bn"
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు") || lang == "te"
        val isMarathi = lang.contains("marathi") || lang.contains("मराठी") || lang == "mr"
        val isTamil = lang.contains("tamil") || lang.contains("தமிழ்") || lang == "ta"
        val isGujarati = lang.contains("gujarati") || lang.contains("ગુજરાતી") || lang == "gu"

        return when (category) {
            CitizenCategory.ALL -> when {
                isEng -> "All Schemes"
                isBengali -> "সকল প্রকল্প"
                isTelugu -> "అన్ని పథకాలు"
                isMarathi -> "सर्व योजना"
                isTamil -> "அனைத்து திட்டங்கள்"
                isGujarati -> "બધી યોજનાઓ"
                else -> category.displayNameHindi
            }
            CitizenCategory.FARMERS -> when {
                isEng -> "Farmers"
                isBengali -> "কৃষক"
                isTelugu -> "రైతులు"
                isMarathi -> "शेतकरी"
                isTamil -> "விவசாயிகள்"
                isGujarati -> "ખેડૂતો"
                else -> category.displayNameHindi
            }
            CitizenCategory.WOMEN -> when {
                isEng -> "Women & Girls"
                isBengali -> "মহিলা ও কন্যা"
                isTelugu -> "మహిళలు"
                isMarathi -> "महिला"
                isTamil -> "பெண்கள்"
                isGujarati -> "મહિલાઓ"
                else -> category.displayNameHindi
            }
            CitizenCategory.STUDENTS -> when {
                isEng -> "Students & Scholarships"
                isBengali -> "ছাত্র ও বৃত্তি"
                isTelugu -> "విద్యార్థులు & స్కాలర్‌షిప్‌లు"
                isMarathi -> "विद्यार्थी व शिष्यवृत्ती"
                isTamil -> "மாணவர்கள் & உதவித்தொகை"
                isGujarati -> "વિદ્યાર્થીઓ અને શિષ્યવૃત્તિ"
                else -> category.displayNameHindi
            }
            CitizenCategory.SENIOR_CITIZENS -> when {
                isEng -> "Senior Citizens (60+)"
                isBengali -> "প্রবীণ নাগরিক"
                isTelugu -> "సీనియర్ సిటిజన్లు"
                isMarathi -> "ज्येष्ठ नागरिक"
                isTamil -> "மூத்த குடிமக்கள்"
                isGujarati -> "વરિષ્ઠ નાગરિકો"
                else -> category.displayNameHindi
            }
            CitizenCategory.LOW_INCOME -> when {
                isEng -> "Low Income / BPL"
                isBengali -> "স্বল্প আয় / BPL"
                isTelugu -> "తక్కువ ఆదాయం / BPL"
                isMarathi -> "कमी उत्पन्न / BPL"
                isTamil -> "குறைந்த வருமானம்"
                isGujarati -> "ઓછી આવક / BPL"
                else -> category.displayNameHindi
            }
            CitizenCategory.ARTISANS -> when {
                isEng -> "Artisans & Workers"
                isBengali -> "কারিগর ও শ্রমিক"
                isTelugu -> "చేతివృత్తులవారు & కార్మికులు"
                isMarathi -> "कारागीर व कामगार"
                isTamil -> "கைவினைஞர்கள்"
                isGujarati -> "કારીગરો અને શ્રમિકો"
                else -> category.displayNameHindi
            }
            CitizenCategory.DIFFERENTLY_ABLED -> when {
                isEng -> "Differently Abled"
                isBengali -> "প্রতিবন্ধী"
                isTelugu -> "దివ్యాంగులు"
                isMarathi -> "दिव्यांग"
                isTamil -> "மாற்றுத்திறனாளிகள்"
                isGujarati -> "દિવ્યાંગ"
                else -> category.displayNameHindi
            }
        }
    }

    fun getLocalizedSector(sector: SchemeSector, language: String): String {
        val lang = language.lowercase()
        val isEng = lang.contains("english") || lang == "en"
        val isBengali = lang.contains("bengali") || lang.contains("বাংলা") || lang == "bn"
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు") || lang == "te"
        val isMarathi = lang.contains("marathi") || lang.contains("मराठी") || lang == "mr"
        val isTamil = lang.contains("tamil") || lang.contains("தமிழ்") || lang == "ta"
        val isGujarati = lang.contains("gujarati") || lang.contains("ગુજરાતી") || lang == "gu"

        return when (sector) {
            SchemeSector.ALL -> when {
                isEng -> "All Sectors"
                isBengali -> "সকল ক্ষেত্র"
                isTelugu -> "అన్ని రంగాలు"
                isMarathi -> "सर्व क्षेत्रे"
                isTamil -> "அனைத்து துறைகள்"
                isGujarati -> "બધા ક્ષેત્રો"
                else -> sector.displayNameHindi
            }
            SchemeSector.AGRICULTURE -> when {
                isEng -> "Agriculture"
                isBengali -> "কৃষি"
                isTelugu -> "వ్యవసాయం"
                isMarathi -> "शेती व कृषी"
                isTamil -> "விவசாயம்"
                isGujarati -> "કૃષિ અને ખેતી"
                else -> sector.displayNameHindi
            }
            SchemeSector.HEALTH -> when {
                isEng -> "Healthcare"
                isBengali -> "স্বাস্থ্য পরিষেবা"
                isTelugu -> "ఆరోగ్యం & వైద్యం"
                isMarathi -> "आरोग्य सेवा"
                isTamil -> "சுகாதாரம்"
                isGujarati -> "આરોગ્ય સેવા"
                else -> sector.displayNameHindi
            }
            SchemeSector.EDUCATION -> when {
                isEng -> "Education & Scholarships"
                isBengali -> "শিক্ষা ও স্কলারশিপ"
                isTelugu -> "విద్య & స్కాలర్‌షిప్‌లు"
                isMarathi -> "शिक्षण व शिष्यवृत्ती"
                isTamil -> "கல்வி & உதவித்தொகை"
                isGujarati -> "શિક્ષણ અને શિષ્યવૃત્તિ"
                else -> sector.displayNameHindi
            }
            SchemeSector.HOUSING -> when {
                isEng -> "Housing & Solar"
                isBengali -> "আবাসন ও সৌরশক্তি"
                isTelugu -> "గృహనిర్మాణం & సౌరశక్తి"
                isMarathi -> "गृहनिर्माण व सौर ऊर्जा"
                isTamil -> "வீட்டுவசதி & சூரியசக்தி"
                isGujarati -> "આવાસ અને સૌર ઊર્જા"
                else -> sector.displayNameHindi
            }
            SchemeSector.EMPLOYMENT -> when {
                isEng -> "Employment & Loans"
                isBengali -> "কর্মসংস্থান ও ঋণ"
                isTelugu -> "ఉపాధి & రుణాలు"
                isMarathi -> "रोजगार व व्यवसाय कर्ज"
                isTamil -> "வேலைவாய்ப்பு & கடன்கள்"
                isGujarati -> "રોજગાર અને ધિરાણ"
                else -> sector.displayNameHindi
            }
            SchemeSector.PENSION -> when {
                isEng -> "Pension & Social Welfare"
                isBengali -> "পেনশন ও সামাজিক কল্যাণ"
                isTelugu -> "పెన్షన్ & సంక్షేమం"
                isMarathi -> "पेन्शन व सामाजिक कल्याण"
                isTamil -> "ஓய்வூதியம் & சமூக நலம்"
                isGujarati -> "પેન્શન અને સામાજિક કલ્યાણ"
                else -> sector.displayNameHindi
            }
            SchemeSector.WOMEN_CHILD -> when {
                isEng -> "Women & Child Welfare"
                isBengali -> "মহিলা ও শিশু কল্যাণ"
                isTelugu -> "మహిళా & శిశు సంక్షేమం"
                isMarathi -> "महिला व बाल कल्याण"
                isTamil -> "மகளிர் & குழந்தைகள் நலம்"
                isGujarati -> "મહિલા અને બાળ કલ્યાણ"
                else -> sector.displayNameHindi
            }
        }
    }

    fun getLocalizedText(key: String, language: String): String {
        val lang = language.lowercase()
        val isEng = lang.contains("english") || lang == "en"
        val isBengali = lang.contains("bengali") || lang.contains("বাংলা") || lang == "bn"
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు") || lang == "te"
        val isMarathi = lang.contains("marathi") || lang.contains("मराठी") || lang == "mr"
        val isTamil = lang.contains("tamil") || lang.contains("தமிழ்") || lang == "ta"
        val isGujarati = lang.contains("gujarati") || lang.contains("ગુજરાતી") || lang == "gu"

        return when (key) {
            "app_name" -> when {
                isEng -> "Scheme Assistant"
                isBengali -> "যোজনা সহায়ক"
                isTelugu -> "యోజన సహాయక్"
                isMarathi -> "योजना सहाय्यक"
                isTamil -> "திட்ட உதவியாளர்"
                isGujarati -> "યોજના સહાયક"
                else -> "योजना सहायक"
            }
            "app_tagline" -> when {
                isEng -> "Official Govt Schemes & CSR Guide"
                isBengali -> "সরকারি প্রকল্প ও সিএসআর নির্দেশিকা"
                isTelugu -> "ప్రభుత్వ పథకాలు & CSR మార్గదర్శి"
                isMarathi -> "शासकीय योजना व CSR मार्गदर्शक"
                isTamil -> "அரசு திட்டங்கள் & CSR வழிகாட்டி"
                isGujarati -> "સરકારી યોજનાઓ અને CSR માર્ગદર્શિકા"
                else -> "Sarkari & CSR Assistant"
            }
            "ai_assistant_btn" -> when {
                isEng -> "💬 AI Assistant"
                isBengali -> "💬 এআই সহায়ক"
                isTelugu -> "💬 AI సహాయకుడు"
                isMarathi -> "💬 AI सहाय्यक"
                isTamil -> "💬 AI உதவியாளர்"
                isGujarati -> "💬 AI સહાયક"
                else -> "💬 AI सहायक"
            }
            "nav_schemes" -> when {
                isEng -> "Schemes"
                isBengali -> "প্রকল্পসমূহ"
                isTelugu -> "పథకాలు"
                isMarathi -> "योजना"
                isTamil -> "திட்டங்கள்"
                isGujarati -> "યોજનાઓ"
                else -> "योजनाएं"
            }
            "nav_documents" -> when {
                isEng -> "Documents"
                isBengali -> "নথিপত্র"
                isTelugu -> "పత్రాలు"
                isMarathi -> "कागदपत्रे"
                isTamil -> "ஆவணங்கள்"
                isGujarati -> "દસ્તાવેજો"
                else -> "दस्तावेज"
            }
            "nav_ai" -> when {
                isEng -> "AI Assistant"
                isBengali -> "AI সহায়ক"
                isTelugu -> "AI సహాయకుడు"
                isMarathi -> "AI सहाय्यक"
                isTamil -> "AI உதவியாளர்"
                isGujarati -> "AI સહાયક"
                else -> "AI सहायक"
            }
            "nav_calculator" -> when {
                isEng -> "Eligibility"
                isBengali -> "যোগ্যতা"
                isTelugu -> "అర్హత"
                isMarathi -> "पात्रता"
                isTamil -> "தகுதி"
                isGujarati -> "પાત્રતા"
                else -> "पात्रता जाँच"
            }
            "nav_saved" -> when {
                isEng -> "Saved"
                isBengali -> "সংরক্ষিত"
                isTelugu -> "సేవ్ చేసినవి"
                isMarathi -> "जतन केलेल्या"
                isTamil -> "சேமித்தவை"
                isGujarati -> "સાચવેલ"
                else -> "सेव योजनाएं"
            }
            "citizen_category" -> when {
                isEng -> "👤 Citizen Category:"
                isBengali -> "👤 সুবিধাভোগী শ্রেণী (Citizen Category):"
                isTelugu -> "👤 లబ్ధిదారుల వర్గం (Citizen Category):"
                isMarathi -> "👤 लाभार्थी प्रवर्ग (Citizen Category):"
                isTamil -> "👤 பயனாளி பிரிவு (Citizen Category):"
                isGujarati -> "👤 લાભાર્થી વર્ગ (Citizen Category):"
                else -> "👤 लाभार्थी श्रेणी (Citizen Category):"
            }
            "scheme_sector" -> when {
                isEng -> "🌐 Sector & Category:"
                isBengali -> "🌐 প্রকল্পের ক্ষেত্র (Sector & Category):"
                isTelugu -> "🌐 పథకం రంగం (Sector & Category):"
                isMarathi -> "🌐 योजना क्षेत्र (Sector & Category):"
                isTamil -> "🌐 திட்ட துறை (Sector & Category):"
                isGujarati -> "🌐 યોજના ક્ષેત્ર (Sector & Category):"
                else -> "🌐 योजना क्षेत्र (Sector & Category):"
            }
            "filter_reset" -> when {
                isEng -> "Reset Filters"
                isBengali -> "ফিল্টার রিসেট"
                isTelugu -> "ఫిల్టర్ రీసెట్"
                isMarathi -> "फिल्टर रीसेट करा"
                isTamil -> "வடிகட்டி மீட்டமை"
                isGujarati -> "ફિલ્ટર રીસેટ"
                else -> "फ़िल्टर रीसेट"
            }
            "all_schemes" -> when {
                isEng -> "📋 All Government Schemes & Scholarships"
                isBengali -> "📋 সমস্ত সরকারি প্রকল্প ও বৃত্তি"
                isTelugu -> "📋 అన్ని ప్రభుత్వ పథకాలు & స్కాలర్‌షిప్‌లు"
                isMarathi -> "📋 सर्व सरकारी योजना व शिष्यवृत्ती"
                isTamil -> "📋 அனைத்து அரசு திட்டங்கள் & உதவித்தொகை"
                isGujarati -> "📋 બધી સરકારી યોજનાઓ અને શિષ્યવૃત્તિ"
                else -> "📋 सभी सरकारी योजनाएं व स्कॉलरशिप"
            }
            "central_state" -> when {
                isEng -> "🇮🇳 Central & State Govt"
                isBengali -> "🇮🇳 কেন্দ্র ও রাজ্য সরকার"
                isTelugu -> "🇮🇳 కేంద్ర & రాష్ట్ర ప్రభుత్వం"
                isMarathi -> "🇮🇳 केंद्र व राज्य शासन"
                isTamil -> "🇮🇳 மத்திய & மாநில அரசு"
                isGujarati -> "🇮🇳 કેન્દ્ર અને રાજ્ય સરકાર"
                else -> "🇮🇳 केंद्र व राज्य सरकार"
            }
            "no_schemes" -> when {
                isEng -> "No schemes found"
                isBengali -> "কোনো প্রকল্প পাওয়া যায়নি"
                isTelugu -> "ఎటువంటి పథకాలు కనుగొనబడలేదు"
                isMarathi -> "कोणतीही योजना आढळली नाही"
                isTamil -> "திட்டங்கள் எதுவும் கிடைக்கவில்லை"
                isGujarati -> "કોઈ યોજના મળી નથી"
                else -> "कोई योजना नहीं मिली"
            }
            "no_schemes_sub" -> when {
                isEng -> "Please adjust your search keywords or filter options."
                isBengali -> "অনুগ্রহ করে আপনার অনুসন্ধান বা ফিল্টার পরিবর্তন করুন।"
                isTelugu -> "దయచేసి మీ శోధన లేదా ఫిల్టర్లను మార్చి ప్రయత్నించండి."
                isMarathi -> "कृपया आपला शोध किंवा फिल्टर बदलून पहा."
                isTamil -> "உங்கள் தேடல் அல்லது வடிகட்டியை மாற்றவும்."
                isGujarati -> "કૃપા કરીને તમારી શોધ અથવા ફિલ્ટર્સ બદલીને જુઓ."
                else -> "कृपया अपनी खोज या फ़िल्टर बदलकर देखें।"
            }
            "search_placeholder" -> when {
                isEng -> "Search schemes, scholarships, loans..."
                isBengali -> "প্রকল্প বা স্কলারশিপ খুঁজুন..."
                isTelugu -> "పథకాలు లేదా స్కాలర్‌షిప్‌లను వెతకండి..."
                isMarathi -> "योजना किंवा शिष्यवृत्ती शोधा..."
                isTamil -> "திட்டங்கள் அல்லது உதவித்தொகையைத் தேடுங்கள்..."
                isGujarati -> "યોજના અથવા શિષ્યવૃત્તિ શોધો..."
                else -> "योजना या स्कॉलरशिप खोजें..."
            }
            "listening" -> when {
                isEng -> "🎤 Listening... Speak now"
                isBengali -> "🎤 শুনছি... এখন বলুন"
                isTelugu -> "🎤 వింటున్నాము... ఇప్పుడు మాట్లాడండి"
                isMarathi -> "🎤 ऐकत आहे... आता बोला"
                isTamil -> "🎤 கேட்கிறது... இப்போது பேசுங்கள்"
                isGujarati -> "🎤 સાંભળી રહ્યા છીએ... બોલો"
                else -> "🎤 सुन रहे हैं... (Listening)"
            }
            "read_aloud" -> when {
                isEng -> "🔊 Read Aloud"
                isBengali -> "🔊 শুনে নিন"
                isTelugu -> "🔊 వినండి"
                isMarathi -> "🔊 ऐका"
                isTamil -> "🔊 கேளுங்கள்"
                isGujarati -> "🔊 સાંભળો"
                else -> "🔊 बोलकर सुनें"
            }
            "csc_slip" -> when {
                isEng -> "🖨️ CSC Slip"
                isBengali -> "🖨️ সিএসসি স্লিপ"
                isTelugu -> "🖨️ CSC స్లిప్"
                isMarathi -> "🖨️ CSC पावती"
                isTamil -> "🖨️ CSC சீட்டு"
                isGujarati -> "🖨️ CSC સ્લીપ"
                else -> "🖨️ आवेदन पर्ची"
            }
            "apply_online" -> when {
                isEng -> "🌐 Apply on Official Portal"
                isBengali -> "🌐 অফিসিয়াল পোর্টালে আবেদন করুন"
                isTelugu -> "🌐 అధికారిక పోర్టల్‌లో దరఖాస్తు చేయండి"
                isMarathi -> "🌐 अधिकृत पोर्टलवर अर्ज करा"
                isTamil -> "🌐 அதிகாரப்பூர்வ இணையதளத்தில் விண்ணப்பிக்கவும்"
                isGujarati -> "🌐 સત્તાવાર પોર્ટલ પર અરજી કરો"
                else -> "🌐 आधिकारिक पोर्टल पर आवेदन करें"
            }
            "save" -> when {
                isEng -> "Save"
                isBengali -> "সংরক্ষণ"
                isTelugu -> "సేవ్ చేయండి"
                isMarathi -> "जतन करा"
                isTamil -> "சேமி"
                isGujarati -> "સાચવો"
                else -> "सेव करें"
            }
            "saved" -> when {
                isEng -> "Saved"
                isBengali -> "সংরক্ষিত"
                isTelugu -> "సేవ్ చేయబడింది"
                isMarathi -> "जतन केले"
                isTamil -> "சேமிக்கப்பட்டது"
                isGujarati -> "સાચવેલ છે"
                else -> "सेव्ड (Saved)"
            }
            "verified_schemes" -> when {
                isEng -> "Verified Schemes"
                isBengali -> "যাচাইকৃত প্রকল্পসমূহ"
                isTelugu -> "సరిచూసిన పథకాలు"
                isMarathi -> "सत्यशोधित योजना"
                isTamil -> "சரிபார்க்கப்பட்ட திட்டங்கள்"
                isGujarati -> "ચકાસાયેલ યોજનાઓ"
                else -> "सत्यापित योजनाएं"
            }
            "max_benefit" -> when {
                isEng -> "💰 Max Benefit: "
                isBengali -> "💰 সর্বোচ্চ সুবিধা: "
                isTelugu -> "💰 గరిష్ట ప్రయోజనం: "
                isMarathi -> "💰 कमाल लाभ: "
                isTamil -> "💰 அதிகபட்ச நன்மை: "
                isGujarati -> "💰 મહત્તમ લાભ: "
                else -> "💰 अधिकतम लाभ: "
            }
            "more_details" -> when {
                isEng -> "View Full Details"
                isBengali -> "বিস্তারিত দেখুন"
                isTelugu -> "పూర్తి వివరాలు చూడండి"
                isMarathi -> "पूर्ण तपशील पहा"
                isTamil -> "முழு விவரங்களை காண்க"
                isGujarati -> "વિગતવાર જુઓ"
                else -> "पूरा विवरण देखें (More Details)"
            }
            "less_details" -> when {
                isEng -> "Hide Details"
                isBengali -> "সংক্ষেপ করুন"
                isTelugu -> "వివరాలు దాచండి"
                isMarathi -> "कम तपशील पहा"
                isTamil -> "விவரங்களை மறைக்க"
                isGujarati -> "ઓછી વિગતો"
                else -> "कम जानकारी देखें"
            }
            "scheme_details_title" -> when {
                isEng -> "Scheme Full Details"
                isBengali -> "প্রকল্পের সম্পূর্ণ বিবরণ"
                isTelugu -> "పథకం పూర్తి వివరాలు"
                isMarathi -> "योजनेचा संपूर्ण तपशील"
                isTamil -> "திட்டத்தின் முழு விவரங்கள்"
                isGujarati -> "યોજનાની સંપૂર્ણ વિગતો"
                else -> "योजना संपूर्ण विवरण"
            }
            "overview" -> when {
                isEng -> "📖 Scheme Overview"
                isBengali -> "📖 প্রকল্পের বিবরণ (Overview)"
                isTelugu -> "📖 పథకం అవలోకనం (Overview)"
                isMarathi -> "📖 योजनेचा संक्षिप्त तपशील (Overview)"
                isTamil -> "📖 திட்ட கண்ணோட்டம் (Overview)"
                isGujarati -> "📖 યોજનાની રૂપરેખા (Overview)"
                else -> "📖 योजना का संक्षिप्त विवरण (Overview)"
            }
            "key_benefits" -> when {
                isEng -> "✨ Key Benefits & Subsidies"
                isBengali -> "✨ প্রধান সুবিধা ও ভর্তুকি"
                isTelugu -> "✨ ముఖ్య ప్రయోజనాలు & రాయితీలు"
                isMarathi -> "✨ प्रमुख लाभ व आर्थिक मदत"
                isTamil -> "✨ முக்கிய நன்மைகள் & மானியங்கள்"
                isGujarati -> "✨ મુખ્ય લાભો અને સબસિડી"
                else -> "✨ प्रमुख लाभ व सुविधाएं (Key Benefits)"
            }
            "who_is_eligible" -> when {
                isEng -> "🎯 Eligibility Criteria"
                isBengali -> "🎯 যোগ্যতার মানদণ্ড (Eligibility)"
                isTelugu -> "🎯 అర్హత ప్రమాణాలు (Eligibility)"
                isMarathi -> "🎯 पात्रता निकष (Eligibility)"
                isTamil -> "🎯 தகுதி வரம்புகள் (Eligibility)"
                isGujarati -> "🎯 પાત્રતાના માપદંડ (Eligibility)"
                else -> "🎯 पात्रता मानदंड (Who is Eligible)"
            }
            "documents_required" -> when {
                isEng -> "📄 Required Documents"
                isBengali -> "📄 প্রয়োজনীয় নথিপত্র"
                isTelugu -> "📄 అవసరమైన పత్రాలు"
                isMarathi -> "📄 आवश्यक कागदपत्रे"
                isTamil -> "📄 தேவையான ஆவணங்கள்"
                isGujarati -> "📄 જરૂરી દસ્તાવેજો"
                else -> "📄 आवश्यक दस्तावेज (Documents Required)"
            }
            "how_to_apply" -> when {
                isEng -> "💡 Step-by-Step Application Process"
                isBengali -> "💡 ধাপে ধাপে আবেদন প্রক্রিয়া"
                isTelugu -> "💡 దరఖాస్తు చేసుకునే విధానం"
                isMarathi -> "💡 अर्जाची संपूर्ण पायरीनिहाय प्रक्रिया"
                isTamil -> "💡 விண்ணப்பிக்கும் வழிமுறைகள்"
                isGujarati -> "💡 અરજી કરવાની પ્રક્રિયા"
                else -> "💡 आवेदन की पूरी प्रक्रिया (Step-by-Step)"
            }
            "drawer_voice" -> when {
                isEng -> "🎙️ Voice Assistant Mode"
                isBengali -> "🎙️ ভয়েস সহকারী মোড"
                isTelugu -> "🎙️ వాయిస్ అసిస్టెంట్ మోడ్"
                isMarathi -> "🎙️ व्हॉइस असिस्टंट मोड"
                isTamil -> "🎙️ குரல் வழி உதவியாளர்"
                isGujarati -> "🎙️ વૉઇસ સહાયક મોડ"
                else -> "🎙️ केवल बोलकर चलाएं (Voice Mode)"
            }
            "drawer_home" -> when {
                isEng -> "🏠 Home & Schemes"
                isBengali -> "🏠 হোম ও প্রকল্পসমূহ"
                isTelugu -> "🏠 హోమ్ & పథకాలు"
                isMarathi -> "🏠 होम व योजना"
                isTamil -> "🏠 முகப்பு & திட்டங்கள்"
                isGujarati -> "🏠 હોમ અને યોજનાઓ"
                else -> "🏠 होम एवं योजनाएं"
            }
            "drawer_directory" -> when {
                isEng -> "📁 Schemes & Documents Directory"
                isBengali -> "📁 নথি ও প্রকল্প ডিরেক্টরি"
                isTelugu -> "📁 పత్రాలు & పథకాల డైరెక్టరీ"
                isMarathi -> "📁 कागदपत्रे व योजना निर्देशिका"
                isTamil -> "📁 ஆவணங்கள் மற்றும் திட்டங்கள்"
                isGujarati -> "📁 દસ્તાવેજ અને યોજના ડિરેક્ટરી"
                else -> "📁 Documents & Schemes Directory"
            }
            "drawer_scanner" -> when {
                isEng -> "📷 Document Validity AI Scanner"
                isBengali -> "📷 নথি বৈধতা এআই স্ক্যানার"
                isTelugu -> "📷 పత్ర చెల్లుబాటు AI స్కానర్"
                isMarathi -> "📷 कागदपत्र वैधता AI स्कॅनर"
                isTamil -> "📷 ஆவண சரிபார்ப்பு AI ஸ்கேனர்"
                isGujarati -> "📷 દસ્તાવેજ માન્યતા AI સ્કેનર"
                else -> "📷 दस्तावेज वैधता एआई स्कैनर"
            }
            "drawer_calculator" -> when {
                isEng -> "📑 Scheme Eligibility Calculator"
                isBengali -> "📑 যোগ্যতা ক্যালকুলেটর"
                isTelugu -> "📑 అర్హత కాలిక్యులేటర్"
                isMarathi -> "📑 योजना पात्रता गणना"
                isTamil -> "📑 தகுதி கணக்கீடு"
                isGujarati -> "📑 યોજના પાત્રતા કેલ્ક્યુલેટર"
                else -> "📑 योजना पात्रता गणना (Eligibility)"
            }
            "drawer_whatsapp" -> when {
                isEng -> "💬 WhatsApp AI Assistant"
                isBengali -> "💬 হোয়াটসঅ্যাপ এআই সহায়তা"
                isTelugu -> "💬 WhatsApp AI సహాయం"
                isMarathi -> "💬 WhatsApp AI सहाय्य"
                isTamil -> "💬 WhatsApp AI உதவி"
                isGujarati -> "💬 WhatsApp AI સહાય"
                else -> "💬 WhatsApp AI सहायता"
            }
            "drawer_saved" -> when {
                isEng -> "🔖 Saved Schemes & CSC Slips"
                isBengali -> "🔖 সংরক্ষিত প্রকল্প ও স্লিপ"
                isTelugu -> "🔖 సేవ్ చేసిన పథకాలు & స్లిప్పులు"
                isMarathi -> "🔖 जतन केलेल्या योजना व पावत्या"
                isTamil -> "🔖 சேமிக்கப்பட்ட திட்டங்கள் & சீட்டுகள்"
                isGujarati -> "🔖 સાચવેલી યોજનાઓ અને સ્લિપ"
                else -> "🔖 सेव की गई योजनाएं व CSC पर्चियां"
            }
            "drawer_locator" -> when {
                isEng -> "📍 Find Nearby CSC / Govt Centers"
                isBengali -> "📍 নিকটস্থ সিএসসি কেন্দ্র খুঁজুন"
                isTelugu -> "📍 సమీపంలోని CSC కేంద్రాలను కనుగొనండి"
                isMarathi -> "📍 जवळचे जन सेवा केंद्र (CSC) शोधा"
                isTamil -> "📍 அருகிலுள்ள CSC மையங்களைக் கண்டறியவும்"
                isGujarati -> "📍 નજીકના CSC કેન્દ્રો શોધો"
                else -> "📍 जन सेवा केंद्र खोजें (CSC / Aadhaar)"
            }
            "drawer_privacy" -> when {
                isEng -> "🔒 Privacy Policy"
                isBengali -> "🔒 গোপনীয়তা নীতি"
                isTelugu -> "🔒 గోప్యతా విధానం"
                isMarathi -> "🔒 गोपनीयता धोरण"
                isTamil -> "🔒 தனியுரிமைக் கொள்கை"
                isGujarati -> "🔒 ગોપનીયતા નીતિ"
                else -> "🔒 गोपनीयता नीति (Privacy Policy)"
            }
            else -> key
        }
    }

    private fun getEnglishDesc(scheme: Scheme): String {
        return when (scheme.id) {
            "pm_kisan" -> "Direct income support of ₹6,000 per year paid in 3 installments of ₹2,000 directly to farmer bank accounts via DBT."
            "ayushman_bharat" -> "Free health insurance coverage up to ₹5 Lakh per year for hospital treatment for low income and eligible families."
            "ayushman_vaya_vandana" -> "Universal free health cover up to ₹5 Lakh per year for senior citizens aged 70 years and above, regardless of income."
            "pm_awas_yojana" -> "Financial subsidy assistance up to ₹1.20 Lakh to ₹2.50 Lakh for building permanent concrete house."
            "pm_vishwakarma" -> "Collateral-free loan up to ₹3 Lakh at 5% interest with ₹15,000 toolkit voucher for traditional artisans."
            "pm_surya_ghar" -> "Up to ₹78,000 subsidy and 300 units free electricity per month with rooftop solar installation."
            "pm_poshan" -> "Nutritious free mid-day meal scheme for government school students."
            "ladli_behna" -> "Monthly direct cash financial assistance of ₹1,250 to women aged 21-60 years."
            "pmfby_crop_insurance" -> "100% comprehensive financial protection and fast claim settlement for crop damage caused by drought, flood, pests, and unseasonal rain."
            "soil_health_card" -> "Free soil testing and customized crop-nutrient guidance for farmers to boost farm yields and save fertilizer costs."
            "pm_kusum" -> "Up to 60% government subsidy for installing solar water agriculture pumps and selling surplus solar power."
            "kcc_loan" -> "Low interest agriculture and dairy working capital credit loans up to ₹3 Lakh at effective 4% interest rate."
            "sukanya_samriddhi" -> "High interest small savings scheme (8.2% p.a.) with tax exemption for girl child education and marriage."
            "pm_matru_vandana" -> "Cash maternity benefit of ₹5,000 to ₹6,000 directly transferred to pregnant women and lactating mothers."
            "free_sewing_machine" -> "Free sewing machine or cash subsidy of ₹15,000 for rural and urban women for self-employment."
            "nsp_scholarship" -> "Direct DBT scholarships up to ₹50,000 per year for pre-matric, post-matric, and higher education students."
            "pm_vidyalaxmi" -> "Collateral-free education loans up to ₹10 Lakh with 3% interest subsidy for top higher education institutes."
            "atal_pension" -> "Guaranteed monthly pension of ₹1,000 to ₹5,000 per month for unorganized workers after age 60."
            "pm_street_vendor" -> "Collateral-free working capital loan up to ₹50,000 with 7% interest subsidy for street vendors."
            "e_shram" -> "Universal registration portal for unorganized workers with ₹2 Lakh accidental insurance cover and direct DBT benefits."
            "mudra_loan" -> "Collateral-free business loan up to ₹20 Lakh under Shishu, Kishore, and Tarun categories for micro enterprises."
            "standup_india" -> "Bank loans from ₹10 Lakh to ₹1 Crore for SC, ST, and women entrepreneurs for greenfield enterprises."
            else -> scheme.shortDescriptionHindi.ifBlank { "Government welfare scheme providing direct benefits and subsidies to eligible citizens." }
        }
    }

    private fun getBengaliDesc(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "সকল কৃষক পরিবারকে প্রতি বছর ₹৬,০০০ সরাসরি ব্যাংক অ্যাকাউন্টে ডিবিটি মাধ্যমে প্রদান।"
        "ayushman_bharat" -> "যোগ্য পরিবারগুলির জন্য প্রতি বছর ₹৫ লাখ পর্যন্ত বিনামূল্যে হাসপাতালে চিকিৎসার কভারেজ।"
        "ayushman_vaya_vandana" -> "৭০ বছর বা তার বেশি বয়সী সকল প্রবীণ নাগরিকদের জন্য ₹৫ লাখের বিনামূল্যে চিকিৎসা।"
        "pm_awas_yojana" -> "পাকা বাড়ি তৈরির জন্য ₹১.২০ লাখ থেকে ₹২.৫০ লাখ পর্যন্ত সরকারি আর্থিক অনুদান।"
        "pm_vishwakarma" -> "কারিগরদের জন্য ₹৩ লাখ পর্যন্ত কম সুদের ঋণ এবং ₹১৫,০০০ মূল্যের আধুনিক টুলকিট ভাউচার।"
        "pm_surya_ghar" -> "ছাদ সৌরবিদ্যুৎ প্রকল্পে ₹৭৮,০০০ পর্যন্ত ভর্তুকি এবং মাসে ৩০০ ইউনিট বিনামূল্যে বিদ্যুৎ।"
        else -> "সরকারি প্রকল্প: ${scheme.shortDescriptionHindi}"
    }

    private fun getTeluguDesc(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "రైతు కుటుంబాలకు ఏటా ₹6,000 ఆర్థిక సాయం 3 విడతల్లో నేరుగా బ్యాంక్ ఖాతాలో జమ."
        "ayushman_bharat" -> "పేద మరియు అర్హత కలిగిన కుటుంబాలకు ఏడాదికి ₹5 లక్షల వరకు ఉచిత ఆసుపత్రి చికిత్స."
        "ayushman_vaya_vandana" -> "70 ఏళ్లు పైబడిన వృద్ధులందరికీ ఆదాయ పరిమితి లేకుండా ₹5 లక్షల వరకు ఉచిత వైద్యం."
        "pm_awas_yojana" -> "సొంత పక్కా ఇల్లు నిర్మించుకోవడానికి ₹1.20 లక్షల నుండి ₹2.50 లక్షల వరకు ప్రభుత్వ సాయం."
        "pm_vishwakarma" -> "చేతివృత్తుల వారికి ₹3 లక్షల వరకు సులభ రుణం మరియు ₹15,000 టూల్‌కిట్ గ్రాంట్."
        "pm_surya_ghar" -> "రూఫ్‌టాప్ సోలార్ కోసం ₹78,000 వరకు సబ్సిడీ మరియు నెలకు 300 యూనిట్ల ఉచిత విద్యుత్."
        else -> "ప్రభుత్వ పథకం: ${scheme.shortDescriptionHindi}"
    }

    private fun getMarathiDesc(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "शेतकऱ्यांच्या बँक खात्यात थेट ₹६,००० वार्षिक आर्थिक मदत ३ हप्त्यांमध्ये जमा केली जाते."
        "ayushman_bharat" -> "पात्र कुटुंबांना दरवर्षी ₹५ लाखांपर्यंत मोफत कॅशलेस उपचार व शस्त्रक्रिया सुविधा."
        "ayushman_vaya_vandana" -> "७० वर्षे व त्यावरील सर्व ज्येष्ठ नागरिकांना ₹५ लाखांपर्यंत मोफत आरोग्य संरक्षण."
        "pm_awas_yojana" -> "पक्के घर बांधण्यासाठी ₹१.२० लाख ते ₹२.५० लाखांपर्यंत थेट सरकारी अनुदान."
        "pm_vishwakarma" -> "पारंपारिक कारागिरांना ₹३ लाखांपर्यंत स्वस्त कर्ज व ₹१५,००० चे टूलकिट व्हाउचर."
        "pm_surya_ghar" -> "रूफटॉप सोलरसाठी ₹७८,००० पर्यंत सबसिडी आणि दरमहा ३०० युनिट मोफत वीज."
        else -> "सरकारी योजना: ${scheme.shortDescriptionHindi}"
    }

    private fun getTamilDesc(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "விவசாயிகளுக்கு ஆண்டுதோறும் ₹6,000 நிதி உதவி 3 தவணைகளில் நேரடியாக வங்கி கணக்கில் செலுத்தப்படுகிறது."
        "ayushman_bharat" -> "தகுதியான குடும்பங்களுக்கு ஆண்டுக்கு ₹5 லட்சம் வரை இலவச மருத்துவ சிகிச்சை வசதி."
        "ayushman_vaya_vandana" -> "70 வயதுக்கு மேற்பட்ட அனைத்து முதியவர்களுக்கும் ₹5 லட்சம் வரை இலவச மருத்துவ காப்பீடு."
        "pm_awas_yojana" -> "நிரந்தர கான்கிரீட் வீடு கட்ட ₹1.20 லட்சம் முதல் ₹2.50 லட்சம் வரை அரசு மானியம்."
        "pm_vishwakarma" -> "பாரம்பரிய கைவினைஞர்களுக்கு ₹3 லட்சம் வரை குறைந்த வட்டி கடன் மற்றும் ₹15,000 கருவி மானியம்."
        "pm_surya_ghar" -> "வீட்டு சோலார் திட்டத்திற்கு ₹78,000 வரை மானியம் மற்றும் மாதம் 300 யூனிட் இலவச மின்சாரம்."
        else -> "அரசு திட்டம்: ${scheme.shortDescriptionHindi}"
    }

    private fun getGujaratiDesc(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "ખેડૂતોને વાર્ષિક ₹6,000 ની નાણાકીય સહાય 3 સમાન હપ્તામાં સીધી બેંક ખાતામાં DBT દ્વારા મળે છે."
        "ayushman_bharat" -> "પાત્ર પરિવારોને હોસ્પિટલમાં સારવાર માટે વાર્ષિક ₹5 લાખ સુધીની મફત કેશલેસ સેવા."
        "ayushman_vaya_vandana" -> "70 વર્ષ કે તેથી વધુ ઉંમરના તમામ વરિષ્ઠ નાગરિકો માટે ₹5 લાખ સુધીનું મફત આયુષ્માન કવર."
        "pm_awas_yojana" -> "પાકું મકાન બનાવવા માટે ₹1.20 લાખથી ₹2.50 લાખ સુધીની સરકારી આર્થિક સહાય."
        "pm_vishwakarma" -> "કારીગરો માટે ₹3 લાખ સુધીનું સસ્તું લોન અને ₹15,000 નું ટૂલકિટ વાઉચર."
        "pm_surya_ghar" -> "રૂફટોપ સોલાર માટે ₹78,000 સુધીની સબસિડી અને દર મહિને 300 યુનિટ મફત વીજળી."
        else -> "સરકારી યોજના: ${scheme.shortDescriptionHindi}"
    }

    private fun getBengaliTitle(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "পিএম কিষাণ সম্মান নিধি (PM-Kisan ₹6,000)"
        "ayushman_bharat" -> "আয়ুষ্মান ভারত প্রধান মন্ত্রী জন আরোগ্য যোজনা (₹5 লাখ চিকিৎসা)"
        "ayushman_vaya_vandana" -> "আয়ুষ্মান বয় বন্দনা কার্ড (৭০+ প্রবীণদের ফ্রী চিকিৎসা)"
        "pm_awas_yojana" -> "পিএম আবাসন যোজনা (পাকা বাড়ির জন্য আর্থিক সাহায্য)"
        "pm_vishwakarma" -> "পিএম বিশ্বকর্মা যোজনা (কারিগরদের লোন ও টুলকিট)"
        "pm_surya_ghar" -> "পিএম সূর্য ঘর মুফতি বিজলি যোজনা (বিনামূল্যে সৌর বিদ্যুৎ)"
        "ladli_behna" -> "মুখ্যমন্ত্রী লাডলি বহনা যোজনা (নারীদের মাসিক সহায়তা)"
        "pmfby_crop_insurance" -> "প্রধানমন্ত্রী ফসল বিমা যোজনা (PMFBY)"
        "soil_health_card" -> "মৃত্তিকা স্বাস্থ্য কার্ড যোজনা (Soil Health Card)"
        "pm_kusum" -> "পিএম কুসুম সৌর পাম্প যোজনা (PM-KUSUM)"
        "kcc_loan" -> "কিষাণ ক্রেডিট কার্ড ঋণ যোজনা (KCC 4% Loan)"
        "sukanya_samriddhi" -> "সুকন্যা সমৃদ্ধি যোজনা (SSY 8.2% Interest)"
        "nsp_scholarship" -> "জাতীয় বৃত্তি পোর্টাল স্কলারশিপ (NSP DBT)"
        "atal_pension" -> "অটল পেনশন যোজনা (APY ₹5,000 Pension)"
        "pm_street_vendor" -> "পিএম স্বনিধি স্ট্রিট ভেন্ডর লোন (PM SVANidhi)"
        "e_shram" -> "ই-শ্রম কার্ড পোর্টাল যোজনা (e-Shram ₹2 Lakh Cover)"
        "mudra_loan" -> "প্রধানমন্ত্রী মুদ্রা যোজনা (MUDRA ₹20 Lakh Loan)"
        else -> scheme.titleEng.ifBlank { scheme.titleHindi }
    }

    private fun getTeluguTitle(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "పీఎం కిసాన్ సమ్మాన్ నిధి (PM-Kisan ₹6,000)"
        "ayushman_bharat" -> "ఆయుష్మాన్ భారత్ యోజన (₹5 లక్షల ఉచిత వైద్యం)"
        "ayushman_vaya_vandana" -> "ఆయుష్మాన్ వయ వందన కార్డ్ (70+ వృద్ధులకు ఉచిత వైద్యం)"
        "pm_awas_yojana" -> "పీఎం ఆవాస్ యోజన (సొంతింటి నిర్మాణం)"
        "pm_vishwakarma" -> "పీఎం విశ్వకర్మ యోజన (చేతివృత్తుల రుణ సాయం)"
        "pm_surya_ghar" -> "పీఎం సూర్య ఘర్ ఉచిత విద్యుత్ యోజన"
        "pmfby_crop_insurance" -> "ప్రధానమంత్రి ఫసల్ బీమా యోజన (PMFBY)"
        "soil_health_card" -> "సాయిల్ హెల్త్ కార్డ్ పథకం (Soil Health Card)"
        "pm_kusum" -> "పీఎం కుసుమ్ సోలార్ పంప్ పథకం (PM-KUSUM)"
        "kcc_loan" -> "కిసాన్ క్రెడిట్ కార్డ్ లోన్ (KCC 4% వడ్డీ)"
        "sukanya_samriddhi" -> "సుకున్య సమృద్ధి యోజన (SSY 8.2% వడ్డీ)"
        "nsp_scholarship" -> "నేషనల్ స్కాలర్‌షిప్ పోర్టల్ (NSP DBT)"
        "atal_pension" -> "అటల్ పెన్షన్ యోజన (APY ₹5,000 పెన్షన్)"
        "pm_street_vendor" -> "పీఎం స్వనిధి వీధి వ్యాపారుల రుణం"
        "e_shram" -> "ఈ-శ్రమ్ కార్డ్ పథకం (e-Shram ₹2 లక్షల బీమా)"
        "mudra_loan" -> "ప్రధానమంత్రి ముద్రా లోన్ (MUDRA ₹20 లక్షలు)"
        else -> scheme.titleEng.ifBlank { scheme.titleHindi }
    }

    private fun getMarathiTitle(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "पीएम किसान सन्मान निधी (PM-Kisan ₹6,000)"
        "ayushman_bharat" -> "आयुष्मान भारत योजना (₹५ लाख मोफत उपचार)"
        "ayushman_vaya_vandana" -> "आयुष्मान वय वंदना कार्ड (७०+ ज्येष्ठांना मोफत उपचार)"
        "pm_awas_yojana" -> "पीएम आवास योजना (घरकुल अनुदान)"
        "pm_vishwakarma" -> "पीएम विश्वकर्मा योजना (कारागिरांना कर्ज व साधनसामग्री)"
        "pm_surya_ghar" -> "पीएम सूर्य घर मोफत वीज योजना"
        "ladli_behna" -> "मुख्यमंत्री माझी लाडकी बहीण योजना"
        "pmfby_crop_insurance" -> "प्रधानमंत्री पीक विमा योजना (PMFBY)"
        "soil_health_card" -> "मृदा आरोग्य पत्रिका योजना (Soil Health Card)"
        "pm_kusum" -> "पीएम कुसुम सौर कृषी पंप योजना"
        "kcc_loan" -> "किसान क्रेडिट कार्ड कर्ज योजना (KCC)"
        "sukanya_samriddhi" -> "सुकन्या समृद्धी योजना (SSY 8.2%)"
        "nsp_scholarship" -> "राष्ट्रीय शिष्यवृत्ती पोर्टल (NSP Scholarships)"
        "atal_pension" -> "अटल पेन्शन योजना (APY ₹५,००० पेन्शन)"
        "pm_street_vendor" -> "पीएम स्वनिधी पथविक्रेता कर्ज योजना"
        "e_shram" -> "ई-श्रम कार्ड नोंदणी योजना (e-Shram)"
        "mudra_loan" -> "प्रधानमंत्री मुद्रा कर्ज योजना (MUDRA Loan)"
        else -> scheme.titleHindi
    }

    private fun getTamilTitle(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "பிரதம மந்திரி கிசான் சம்மான் நிதி (PM-Kisan ₹6,000)"
        "ayushman_bharat" -> "ஆயுஷ்மான் பாரத் திட்டம் (₹5 லட்சம் இலவச சிகிச்சை)"
        "ayushman_vaya_vandana" -> "ஆயுஷ்மான் வய வந்தனா கார்டு (70+ முதியோர் சிகிச்சை)"
        "pm_awas_yojana" -> "பிரதம மந்திரி ஆவாஸ் திட்டம் (வீடு கட்டும் உதவி)"
        "pm_vishwakarma" -> "பிஎம் விஸ்வகர்மா திட்டம் (கைவினைஞர்கள் உதவி)"
        "pm_surya_ghar" -> "பிஎம் சூர்ய கர் இலவச மின்சாரத் திட்டம்"
        "pmfby_crop_insurance" -> "பிரதம மந்திரி பயிர் காப்பீட்டுத் திட்டம் (PMFBY)"
        "soil_health_card" -> "மண் வள அட்டை திட்டம் (Soil Health Card)"
        "pm_kusum" -> "பிஎம் குசும் சோலார் பம்ப் திட்டம்"
        "kcc_loan" -> "கிசான் கடன் அட்டை திட்டம் (KCC Loan)"
        "sukanya_samriddhi" -> "சுகன்யா சம்ரிதி யோஜனா (SSY சேமிப்புத் திட்டம்)"
        "nsp_scholarship" -> "தேசிய கல்வி உதவித்தொகை போர்டல் (NSP)"
        "atal_pension" -> "அடல் ஓய்வூதியத் திட்டம் (APY ₹5,000)"
        "pm_street_vendor" -> "பிஎம் ஸ்வநிதி சாலையோர வியாபாரிகள் கடன்"
        "e_shram" -> "இ-ஷ்ராம் கார்டு திட்டம் (e-Shram ₹2 லட்சம்)"
        "mudra_loan" -> "பிரதம மந்திரி முத்ரா கடன் திட்டம்"
        else -> scheme.titleEng.ifBlank { scheme.titleHindi }
    }

    private fun getGujaratiTitle(scheme: Scheme): String = when (scheme.id) {
        "pm_kisan" -> "પીએમ કિસાન સન્માન નિધિ (PM-Kisan ₹6,000)"
        "ayushman_bharat" -> "આયુષ્માન ભારત યોજના (₹5 લાખ મફત સારવાર)"
        "ayushman_vaya_vandana" -> "આયુષ્માન વય વંદના કાર્ડ (70+ વડીલોને મફત સારવાર)"
        "pm_awas_yojana" -> "પીએમ આવાસ યોજના (પાકા ઘરની સહાય)"
        "pm_vishwakarma" -> "પીએમ વિશ્વકર્મા યોજના (કારીગરો માટે લોન સહાય)"
        "pm_surya_ghar" -> "પીએમ સૂર્ય ઘર મફત વીજળી યોજના"
        "pmfby_crop_insurance" -> "પ્રધાનમંત્રી પાક વીમા યોજના (PMFBY)"
        "soil_health_card" -> "જમીન આરોગ્ય કાર્ડ યોજના (Soil Health Card)"
        "pm_kusum" -> "પીએમ કુસુમ સોલાર પંપ યોજના"
        "kcc_loan" -> "કિસાન ક્રેડિટ કાર્ડ લોન યોજના (KCC)"
        "sukanya_samriddhi" -> "સુકન્યા સમૃદ્ધિ યોજના (SSY 8.2%)"
        "nsp_scholarship" -> "રાષ્ટ્રીય શિષ્યવૃત્તિ પોર્ટલ (NSP Scholarships)"
        "atal_pension" -> "અટલ પેન્શન યોજના (APY ₹5,000 પેન્શન)"
        "pm_street_vendor" -> "પીએમ સ્વનિધિ શેરી ફેરિયા લોન યોજના"
        "e_shram" -> "ઈ-શ્રમ કાર્ડ યોજના (e-Shram ₹2 લાખ સુરક્ષા)"
        "mudra_loan" -> "પ્રધાનમંત્રી મુદ્રા લોન યોજના (MUDRA Loan)"
        else -> scheme.titleHindi
    }
}

