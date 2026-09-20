export const mockPatients = [
  {
    id: "PAT-TR-849201",
    name: "Hasan Demir",
    age: 58,
    gender: "Erkek",
    district: "Çorlu",
    neighborhood: "Sağlık Mahallesi",
    occupation: "Tarım / Kuyu Suyu Kullanıcısı",
    // Backend /api/locations ile birebir aynı isim:
    location_name: "Ergene Havzasi - Kuyu 9",
    chief_complaint: "Burun ve üst solunum yollarında kronik tahriş, deride iyileşmeyen temas ülserleri ve halsizlik.",
    vitals: {
      blood_pressure: "135/85 mmHg",
      heart_rate: "78 bpm",
      spO2: "%96"
    },
    primary_concern: "Mesleki/Çevresel Ağır Metal Toksisitesi (Krom Maruziyeti)"
  },
  {
    id: "PAT-TR-392019",
    name: "Ayşe Yılmaz",
    age: 42,
    gender: "Kadın",
    district: "Çerkezköy",
    neighborhood: "Köy İçi Mevkii",
    occupation: "Ev Hanımı / Küçük Ölçekli Tarım",
    // Backend /api/locations ile birebir aynı isim:
    location_name: "St 2 - koy ici, sanayiden uzak",
    chief_complaint: "Aile bireylerinde ve çocukta nedeni anlaşılamayan kronik yorgunluk, karın ağrısı ve periferik nöropatik sızılar.",
    vitals: {
      blood_pressure: "128/82 mmHg",
      heart_rate: "80 bpm",
      spO2: "%98"
    },
    primary_concern: "Havza Geneli Kronik Sağlık Riski Değerlendirmesi (HRA)"
  },
  {
    id: "PAT-TR-102948",
    name: "Mehmet Kaya",
    age: 34,
    gender: "Erkek",
    district: "Saray",
    neighborhood: "Büyükyoncalı",
    occupation: "Kamu Görevlisi",
    // Backend /api/locations ile birebir aynı isim:
    location_name: "St 1 - Saray Buyukyoncali (Ergene Menba / Referans)",
    chief_complaint: "İşe giriş ve spor lisansı için genel sağlık kontrolü, belirgin semptom yok.",
    vitals: {
      blood_pressure: "120/80 mmHg",
      heart_rate: "72 bpm",
      spO2: "%99"
    },
    primary_concern: "Genel Poliklinik Kontrolü (Referans Havza Bölgesi)"
  }
];