export const mockPatients = [
  {
    id: "PAT-TR-849201",
    name: "Hasan Demir",
    age: 58,
    gender: "Male",
    district: "Çorlu",
    neighborhood: "Sağlık District",
    occupation: "Agriculture / Untreated Well Water Consumer",
    // Exact string match for live backend /api/locations & /api/risk-status:
    location_name: "Ergene Havzasi - Kuyu 9",
    chief_complaint: "Chronic irritation of upper respiratory tract and nasal mucosa, persistent contact dermatitis with slow-healing cutaneous ulcerations, and generalized fatigue.",
    vitals: {
      blood_pressure: "135/85 mmHg",
      heart_rate: "78 bpm",
      spO2: "96%"
    },
    primary_concern: "Occupational & Environmental Heavy Metal Toxicity (Chromium Exposure)"
  },
  {
    id: "PAT-TR-392019",
    name: "Ayşe Yılmaz",
    age: 42,
    gender: "Female",
    district: "Çerkezköy",
    neighborhood: "Village Center Catchment",
    occupation: "Homemaker / Smallholder Agriculture",
    // Exact string match for live backend /api/locations & /api/risk-assessments:
    location_name: "St 2 - koy ici, sanayiden uzak",
    chief_complaint: "Unexplained chronic fatigue across family members and child, recurrent abdominal discomfort, and tingling peripheral neuropathic sensations.",
    vitals: {
      blood_pressure: "128/82 mmHg",
      heart_rate: "80 bpm",
      spO2: "98%"
    },
    primary_concern: "Longitudinal Environmental Hazard Exposure (USEPA HRA Child THI > 1.0)"
  },
  {
    id: "PAT-TR-102948",
    name: "Mehmet Kaya",
    age: 34,
    gender: "Male",
    district: "Saray",
    neighborhood: "Büyükyoncalı",
    occupation: "Public Sector Official",
    // Exact string match for live backend /api/locations:
    location_name: "St 1 - Saray Buyukyoncali (Ergene Menba / Referans)",
    chief_complaint: "Routine occupational pre-employment checkup and sports license medical clearance; asymptomatic.",
    vitals: {
      blood_pressure: "120/80 mmHg",
      heart_rate: "72 bpm",
      spO2: "99%"
    },
    primary_concern: "Routine Outpatient Health Clearance (Reference Catchment Baseline)"
  }
];