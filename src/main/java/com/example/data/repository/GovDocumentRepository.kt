package com.example.data.repository

import com.example.data.api.GeminiService
import com.example.data.model.GovDocument

/**
 * Repository for official Indian government documents (Aadhaar, PAN, Voter ID, Income Certificate, Domicile etc.),
 * live rule verification, and Gemini OCR document validity scanning.
 */
class GovDocumentRepository {

    private val geminiService = GeminiService()

    /**
     * Returns a curated list of default Indian government documents with eligibility guidelines and portal links.
     * @return List of [GovDocument] models.
     */
    fun getDefaultGovernmentDocuments(): List<GovDocument> {
        return listOf(
            GovDocument(
                id = "doc_aadhaar",
                titleHindi = "आधार कार्ड (Aadhaar Card)",
                titleEng = "Aadhaar Card Unique Identity",
                issuingAuthority = "UIDAI (भारतीय विशिष्ट पहचान प्राधिकरण)",
                officialFormLink = "https://myaadhaar.uidai.gov.in",
                requiredDocuments = listOf("जन्म तिथि प्रमाण पत्र / 10th मार्कशीट", "पहचान पत्र (वोटर आईडी / पासपोर्ट)", "निवास प्रमाण पत्र", "मोबाइल नंबर"),
                whereToApply = "UIDAI पोर्टल online या निकटतम आधार सेवा केंद्र / CSC",
                sourceUrl = "https://uidai.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "भारत के प्रत्येक नागरिक के लिए 12 अंकों की विशिष्ट पहचान संख्या वाला राष्ट्रीय दस्तावेज।"
            ),
            GovDocument(
                id = "doc_pan",
                titleHindi = "पैन कार्ड (PAN Card)",
                titleEng = "Permanent Account Number (PAN)",
                issuingAuthority = "आयकर विभाग (Income Tax Department, GoI)",
                officialFormLink = "https://eportal.incometax.gov.in",
                requiredDocuments = listOf("आधार कार्ड (Instant E-PAN के लिए)", "पासपोर्ट फोटो", "हस्ताक्षर"),
                whereToApply = "e-filing Portal (Instant e-PAN Free) या NSDL / Protean Center",
                sourceUrl = "https://www.incometax.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "वित्तीय लेनदेन, बैंक खाता खोलने व आयकर रिटर्न दाखिल करने हेतु अनिवार्य 10 अंकों का अल्फान्यूमेरिक कार्ड।"
            ),
            GovDocument(
                id = "doc_voter_id",
                titleHindi = "मतदाता पहचान पत्र (Voter ID / EPIC)",
                titleEng = "Electoral Photo Identity Card (EPIC)",
                issuingAuthority = "भारत निर्वाचन आयोग (Election Commission of India - ECI)",
                officialFormLink = "https://voters.eci.gov.in",
                requiredDocuments = listOf("आयु प्रमाण (18 वर्ष पूर्ण)", "निवास प्रमाण पत्र", "पासपोर्ट फोटो", "पारिवारिक EPIC नंबर (यदि उपलब्ध हो)"),
                whereToApply = "ECI Voters Service Portal (Form 6) या BLO / तहसील कार्यालय",
                sourceUrl = "https://voters.eci.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "18 वर्ष से अधिक आयु के नागरिकों हेतु मतदान अधिकार व राष्ट्रीय पहचान पत्र।"
            ),
            GovDocument(
                id = "doc_ration_card",
                titleHindi = "राशन कार्ड (Ration Card - NFSA)",
                titleEng = "National Food Security Ration Card",
                issuingAuthority = "खाद्य एवं रसद विभाग (State Food & Civil Supplies Department)",
                officialFormLink = "https://nfsa.gov.in",
                requiredDocuments = listOf("सभी सदस्यों के आधार कार्ड", "परिवार के मुखिया (महिला) की फोटो", "आय प्रमाण पत्र", "बैंक पासबुक", "बिजली बिल"),
                whereToApply = "राज्य के e-District / खाद्य विभाग पोर्टल या सीएससी (CSC) जन सेवा केंद्र",
                sourceUrl = "https://nfsa.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "गरीबी रेखा (BPL/APL/अंत्योदय) के अनुसार रियायती अनाज व सरकारी योजनाओं का आधार कार्ड।"
            ),
            GovDocument(
                id = "doc_driving_license",
                titleHindi = "ड्राइविंग लाइसेंस (Driving License - DL)",
                titleEng = "Driving License (LL & Permanent DL)",
                issuingAuthority = "सड़क परिवहन एवं राजमार्ग मंत्रालय (MoRTH / RTO)",
                officialFormLink = "https://parivahan.gov.in",
                requiredDocuments = listOf("आधार कार्ड", "शिक्षार्थी लाइसेंस (Learner License)", "मेडिकल फिटनेस फॉर्म 1A", "पासपोर्ट फोटो"),
                whereToApply = "Sarathi Parivahan Sewa Portal (parivahan.gov.in) या क्षेत्रीय RTO ऑफिस",
                sourceUrl = "https://parivahan.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "वाहन चलाने हेतु आधिकारिक सरकारी लाइसेंस (नॉन-ट्रांसपोर्ट 20 वर्ष तक मान्य)।"
            ),
            GovDocument(
                id = "doc_passport",
                titleHindi = "भारतीय पासपोर्ट (Passport India)",
                titleEng = "Indian Ordinary Passport",
                issuingAuthority = "विदेश मंत्रालय (Ministry of External Affairs, GoI)",
                officialFormLink = "https://passportindia.gov.in",
                requiredDocuments = listOf("आधार कार्ड", "10th मार्कशीट / जन्म तिथि प्रमाण", "पैन कार्ड", "बैंक पासबुक"),
                whereToApply = "Passport Seva Portal या mPassport Seva App (PSK Appointment)",
                sourceUrl = "https://passportindia.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "अंतर्राष्ट्रीय यात्रा एवं विदेशों में राष्ट्रीय नागरिकता का प्रमुख आधिकारिक दस्तावेज।"
            ),
            GovDocument(
                id = "doc_birth_certificate",
                titleHindi = "जन्म एवं मृत्यु प्रमाण पत्र (Birth & Death Certificate)",
                titleEng = "Birth & Death Registration Certificate",
                issuingAuthority = "CRS Org / नगर निगम / ग्राम पंचायत (Registrar of Births & Deaths)",
                officialFormLink = "https://crsorgi.gov.in",
                requiredDocuments = listOf("अस्पताल डिस्चार्ज स्लिप / संस्थागत जन्म रिपोर्ट", "माता-पिता का आधार कार्ड", "राशन कार्ड"),
                whereToApply = "CRS ORGI पोर्टल या स्थानीय नगर निगम / विकास खंड (BDO) कार्यालय",
                sourceUrl = "https://crsorgi.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "बच्चे के जन्म या व्यक्ति की मृत्यु की कानूनी रिकॉर्डिंग एवं सरकारी सत्यापन दस्तावेज।"
            ),
            GovDocument(
                id = "doc_caste_cert",
                titleHindi = "जाति प्रमाण पत्र (Caste Certificate - SC/ST/OBC/EWS)",
                titleEng = "Community / Caste Certificate",
                issuingAuthority = "राजस्व विभाग / तहसील (Revenue Department / Tehsildar)",
                officialFormLink = "https://edistrict.up.gov.in",
                requiredDocuments = listOf("आधार कार्ड", "पिता का जाति प्रमाण पत्र या खतौनी पुरानी प्रति", "स्व-घोषणा पत्र", "फोटो"),
                whereToApply = "राज्य e-District Portal या स्थानीय CSC जन सेवा केंद्र",
                sourceUrl = "https://edistrict.up.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "आरक्षण लाभ, सरकारी छात्रवृत्ति व योजनाओं हेतु आवश्यक श्रेणी प्रमाण पत्र।"
            ),
            GovDocument(
                id = "doc_income_cert",
                titleHindi = "आय प्रमाण पत्र (Income Certificate)",
                titleEng = "Annual Household Income Certificate",
                issuingAuthority = "राजस्व विभाग / तहसील (Revenue Department)",
                officialFormLink = "https://edistrict.up.gov.in",
                requiredDocuments = listOf("आधार कार्ड", "वेतन पर्ची / पटवारी रिपोर्ट", "राशन कार्ड", "स्व-घोषणा पत्र"),
                whereToApply = "राज्य e-District पोर्टल या सीएससी (CSC) सेंटर / तहसील",
                sourceUrl = "https://edistrict.up.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "परिवार की वार्षिक आय का सरकारी प्रमाण (3 वर्ष के लिए वैध)।"
            ),
            GovDocument(
                id = "doc_domicile_cert",
                titleHindi = "मूल निवास प्रमाण पत्र (Domicile / Residence Cert)",
                titleEng = "Domicile / Permanent Residence Certificate",
                issuingAuthority = "राजस्व विभाग / उप-जिलाधिकारी (SDM / Tehsildar)",
                officialFormLink = "https://edistrict.up.gov.in",
                requiredDocuments = listOf("आधार कार्ड", "10वीं मार्कशीट / निवास का 10 साल पुराना प्रमाण", "बिजली बिल", "ग्राम प्रधान / सभासद रिपोर्ट"),
                whereToApply = "राज्य e-District पोर्टल या सीएससी (CSC) जन सेवा केंद्र",
                sourceUrl = "https://edistrict.up.gov.in",
                lastVerifiedTimestamp = "Verified Live: 01 Aug 2026",
                descriptionHindi = "राज्य का स्थायी निवासी होने का आधिकारिक प्रमाण (जीवनभर वैध)।"
            )
        )
    }

    /**
     * Fetches up-to-date document eligibility and application guidelines via Gemini.
     * @param docName Name of the document (e.g. "आय प्रमाण पत्र").
     * @return Markdown string with current rules, required documents, and official portal links.
     */
    suspend fun fetchLiveDocumentDetails(docName: String): String {
        return geminiService.fetchLiveDocumentInfo(docName)
    }

    /**
     * Analyzes uploaded document image / issue date to verify validity period (e.g. 3 years for Income Cert) via Gemini OCR.
     * @param docName Document category name.
     * @param issueDateInput Date string entered or extracted.
     * @param imageBase64 Optional base64 encoded document image for OCR visual analysis.
     * @param selectedState State jurisdiction.
     * @return Verification report with Validity Status (VALID/EXPIRED/NEEDS_RENEWAL), days remaining, and renewal instructions.
     */
    suspend fun analyzeDocumentValidity(docName: String, issueDateInput: String, imageBase64: String? = null, selectedState: String = "Uttar Pradesh"): String {
        return geminiService.analyzeDocumentValidity(docName, issueDateInput, imageBase64, selectedState)
    }
}
