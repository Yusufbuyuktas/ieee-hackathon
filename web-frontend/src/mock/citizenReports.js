export const mockCitizenReports = [
  {
    id: "CIT-2026-001",
    timestamp: "2026-09-16T14:30:00+03:00",
    location_name: "Çorlu Deresi / Sağlık Mahallesi Köprüsü",
    coordinates: { lat: 41.1610, lon: 27.7985 },
    category: "kirli_renk_degisimi",
    category_label: "Kirli / Renk Değişimi",
    note: "Su koyu mor/siyah renkte akıyor, kimyasal koku çok yoğun.",
    photo_url: "https://images.unsplash.com/photo-1617886322207-6f504e7470c8?w=500&auto=format&fit=crop&q=60",
    ai_verification: {
      verified: true,
      confidence: 0.94,
      model: "Gemini-2.5-Flash-Vision",
      feedback: "Atık boya kaynaklı renk değişimi ve köpüklenme tespit edildi."
    },
    status: "approved"
  },
  {
    id: "CIT-2026-002",
    timestamp: "2026-09-17T09:15:00+03:00",
    location_name: "Muratlı Girişi - Köprü Mevkii",
    coordinates: { lat: 41.1712, lon: 27.5024 },
    category: "balik_olumu",
    category_label: "Balık Ölümü",
    note: "Kıyıya vurmuş çok sayıda ölü balık var, oksijensizlik belirtisi.",
    photo_url: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=500&auto=format&fit=crop&q=60",
    ai_verification: {
      verified: true,
      confidence: 0.89,
      model: "Gemini-2.5-Flash-Vision",
      feedback: "Kıyı hattında toplu canlı ölümü morfolojisi doğrulandı."
    },
    status: "approved"
  },
  {
    id: "CIT-2026-003",
    timestamp: "2026-09-17T11:45:00+03:00",
    location_name: "Lüleburgaz - Taşköprü Çevresi",
    coordinates: { lat: 41.4055, lon: 27.3512 },
    category: "kotu_koku",
    category_label: "Yoğun Kimyasal Koku",
    note: "Boğaz yakan asidik solvent kokusu geliyor.",
    photo_url: "https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?w=500&auto=format&fit=crop&q=60",
    ai_verification: {
      verified: true,
      confidence: 0.82,
      model: "Gemini-2.5-Flash-Vision",
      feedback: "Su yüzeyinde endüstriyel film tabakası gözlemlendi."
    },
    status: "approved"
  }
];