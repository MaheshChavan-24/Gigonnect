package com.example.data.model

object Constants {

    val TRADE_CATEGORIES = listOf(
        TradeCategory(
            id = "plumbing",
            nameEn = "Plumbing",
            nameHi = "नलसाजी (Plumbing)",
            iconKey = "plumbing",
            descriptionEn = "Taps, pipe leakages, bathroom fittings, flush repair",
            descriptionHi = "नल, पाइप रिसाव, बाथरूम फिटिंग और फ्लश मरम्मत",
            baseRate = 350,
            isPopular = true
        ),
        TradeCategory(
            id = "carpentry",
            nameEn = "Carpentry",
            nameHi = "बढ़ईगीरी (Carpentry)",
            iconKey = "carpentry",
            descriptionEn = "Furniture assembly, door locks, hinges, wood polishing",
            descriptionHi = "फर्नीचर असेंबली, दरवाजे के ताले, कब्जे, लकड़ी पॉलिश",
            baseRate = 450,
            isPopular = true
        ),
        TradeCategory(
            id = "electrical",
            nameEn = "Electrical Work",
            nameHi = "इलेक्ट्रिकल कार्य (Electrical)",
            iconKey = "electrical",
            descriptionEn = "Wiring, switchboards, MCB, fans, lights, geyser repairs",
            descriptionHi = "वायरिंग, स्विचबोर्ड, एमसीबी, पंखे, लाइट, गीजर मरम्मत",
            baseRate = 400,
            isPopular = true
        ),
        TradeCategory(
            id = "painting",
            nameEn = "Painting",
            nameHi = "पुताई / पेंटिंग (Painting)",
            iconKey = "painting",
            descriptionEn = "Interior/exterior wall painting, waterproofing, touch-up",
            descriptionHi = "आंतरिक/बाहरी दीवार पेंटिंग, वाटरप्रूफिंग, टच-अप",
            baseRate = 600,
            isPopular = false
        ),
        TradeCategory(
            id = "cleaning",
            nameEn = "Cleaning / Deep Clean",
            nameHi = "सफाई / डीप क्लीनिंग",
            iconKey = "cleaning",
            descriptionEn = "Home deep cleaning, kitchen degreasing, bathroom scrubbing",
            descriptionHi = "घर की डीप क्लीनिंग, किचन डीग्रीजिंग, बाथरूम स्क्रबिंग",
            baseRate = 800,
            isPopular = true
        ),
        TradeCategory(
            id = "appliance",
            nameEn = "Appliance Repair",
            nameHi = "उपकरण मरम्मत (Appliance)",
            iconKey = "appliance",
            descriptionEn = "Washing machine, microwave, refrigerator, chimney repair",
            descriptionHi = "वॉशिंग मशीन, माइक्रोवेव, फ्रिज, चिमनी मरम्मत",
            baseRate = 500,
            isPopular = true
        ),
        TradeCategory(
            id = "gardening",
            nameEn = "Gardening / Landscaping",
            nameHi = "बागवानी (Gardening)",
            iconKey = "gardening",
            descriptionEn = "Lawn mowing, pruning, plant potting, pest treatment",
            descriptionHi = "घास काटना, छंटाई, गमले लगाना, पौधों की देखभाल",
            baseRate = 400,
            isPopular = false
        ),
        TradeCategory(
            id = "pest_control",
            nameEn = "Pest Control",
            nameHi = "कीट नियंत्रण (Pest Control)",
            iconKey = "pest_control",
            descriptionEn = "Termite, cockroach, rodent and bedbug inspection & treatment",
            descriptionHi = "दीमक, कॉकरोच, चूहे और खटमल से मुक्ति उपचार",
            baseRate = 750,
            isPopular = false
        ),
        TradeCategory(
            id = "masonry",
            nameEn = "Masonry / Tiling",
            nameHi = "राजमिस्त्री / टाइल (Masonry)",
            iconKey = "masonry",
            descriptionEn = "Tile replacement, brickwork, plastering, grouting",
            descriptionHi = "टाइल्स बदलना, ईंट का काम, प्लास्टर, ग्राउटिंग",
            baseRate = 650,
            isPopular = false
        ),
        TradeCategory(
            id = "hvac",
            nameEn = "AC & HVAC Servicing",
            nameHi = "एसी सर्विसिंग (AC & HVAC)",
            iconKey = "hvac",
            descriptionEn = "AC gas refilling, filter cleaning, cooling coil diagnosis",
            descriptionHi = "एसी गैस रिफिलिंग, फिल्टर सफाई, कूलिंग कॉइल जांच",
            baseRate = 550,
            isPopular = true
        ),
        TradeCategory(
            id = "moving",
            nameEn = "Moving & Heavy Lifting",
            nameHi = "सामान स्थानांतरण (Moving)",
            iconKey = "moving",
            descriptionEn = "Safe loading, unloading, inter-apartment shifting help",
            descriptionHi = "सामान सुरक्षित चढ़ाना-उतारना और शिफ्टिंग सहायता",
            baseRate = 600,
            isPopular = false
        ),
        TradeCategory(
            id = "welding",
            nameEn = "Welding / Fabrication",
            nameHi = "वेल्डिंग / फेब्रिकेशन",
            iconKey = "welding",
            descriptionEn = "Iron gate, railing, grill repairs, metal fabrication",
            descriptionHi = "लोहे का गेट, रेलिंग, ग्रिल मरम्मत और धातु का काम",
            baseRate = 500,
            isPopular = false
        ),
        TradeCategory(
            id = "interior",
            nameEn = "Interior Design Consultation",
            nameHi = "इंटीरियर डिजाइन सलाह",
            iconKey = "interior",
            descriptionEn = "Room layout optimization, modular kitchen consult, lighting plan",
            descriptionHi = "कमरे का लेआउट, मॉड्यूलर किचन परामर्श, लाइटिंग प्लान",
            baseRate = 1200,
            isPopular = false
        ),
        TradeCategory(
            id = "security",
            nameEn = "Security & CCTV Installation",
            nameHi = "सीसीटीवी एवं सुरक्षा (CCTV)",
            iconKey = "security",
            descriptionEn = "CCTV camera setup, DVR configuration, video doorbells",
            descriptionHi = "सीसीटीवी कैमरा सेटअप, डीवीआर कॉन्फिगरेशन, डोरबेल",
            baseRate = 700,
            isPopular = false
        ),
        TradeCategory(
            id = "it_repair",
            nameEn = "Computer / IT Repair",
            nameHi = "कंप्यूटर / आईटी मरम्मत",
            iconKey = "it_repair",
            descriptionEn = "Laptop screen/battery replacement, OS install, WiFi setup",
            descriptionHi = "लैपटॉप स्क्रीन/बैटरी बदलना, ओएस इंस्टॉल, वाईफाई सेटअप",
            baseRate = 600,
            isPopular = true
        )
    )

    val TIME_SLOTS = listOf(
        "09:00 AM - 10:00 AM",
        "10:00 AM - 11:00 AM",
        "11:00 AM - 12:00 PM",
        "12:00 PM - 01:00 PM",
        "01:00 PM - 02:00 PM",
        "02:00 PM - 03:00 PM",
        "03:00 PM - 04:00 PM",
        "04:00 PM - 05:00 PM",
        "05:00 PM - 06:00 PM"
    )

    val ID_TYPES = listOf(
        "Aadhaar",
        "PAN",
        "Passport",
        "Driving Licence"
    )
}
