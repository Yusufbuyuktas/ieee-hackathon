export const mockPatients = [
  {
    id: "PAT-TR-849201",
    name: "Hasan Demir",
    age: 58,
    gender: "Erkek",
    district: "Çorlu",
    neighborhood: "Sağlık Mahallesi",
    occupation: "Emekli / Tarım İşçisi",
    coordinates: { lat: 41.1592, lon: 27.8033 },
    // Gerçek veri setindeki lokasyon parçacıklarıyla birebir örtüşen anahtarlar
    station_keywords: ["corlu", "cerkezkoy", "yag fabrikasi", "ust havza", "kuyu 1", "kuyu 2", "kuyu 3", "kuyu 4"],
    chief_complaint: "3 aydır süregelen inatçı kuru öksürük, halsizlik, avuç içi ve ayak tabanında pullanma/hiperkeratoz lezyonları.",
    vitals: {
      blood_pressure: "135/85 mmHg",
      heart_rate: "78 bpm",
      spO2: "%96"
    },
    primary_concern: "Akciğer Onkolojisi Ön Değerlendirme",
    suspected_exposure: "arsenic"
  },
  {
    id: "PAT-TR-392019",
    name: "Ayşe Yılmaz",
    age: 46,
    gender: "Kadın",
    district: "Muratlı",
    neighborhood: "İstasyon Mevkii",
    occupation: "Tekstil İşçisi",
    coordinates: { lat: 41.1712, lon: 27.5024 },
    station_keywords: ["muratli", "tekirdag", "orta havza", "kuyu 5", "kuyu 6", "st 3"],
    chief_complaint: "El ve ayak parmaklarında uyuşma (periferik nöropati), kemik ağrıları ve nedeni açıklanamayan böbrek fonksiyon gerilemesi.",
    vitals: {
      blood_pressure: "140/90 mmHg",
      heart_rate: "82 bpm",
      spO2: "%98"
    },
    primary_concern: "Nefroloji ve Toksikoloji Takibi",
    suspected_exposure: "cadmium"
  },
  {
    id: "PAT-TR-102948",
    name: "Mehmet Kaya",
    age: 34,
    gender: "Erkek",
    district: "Lüleburgaz",
    neighborhood: "Taşköprü Çevresi",
    occupation: "Lojistik Şoförü",
    coordinates: { lat: 41.4055, lon: 27.3512 },
    station_keywords: ["luleburgaz", "babaeski", "alt havza", "kuyu 7", "kuyu 8"],
    chief_complaint: "Rutin işe giriş kardiyoloji kontrolü, hafif göğüs sıkışması hissi.",
    vitals: {
      blood_pressure: "125/80 mmHg",
      heart_rate: "72 bpm",
      spO2: "%99"
    },
    primary_concern: "Kardiyovasküler Genel Kontrol",
    suspected_exposure: null
  }
];