export const mockCitizenReports = [
  {
    id: "CIT-2026-001",
    timestamp: "2026-09-16T14:30:00+03:00",
    location_name: "Çorlu Stream / Sağlık Bridge Catchment",
    coordinates: { lat: 41.1610, lon: 27.7985 },
    category: "kirli_renk_degisimi",
    category_label: "Severe Water Discoloration",
    note: "Discharge is running deep purple and black with dense, pungent chemical fumes emanating from the stream.",
    photo_url: "https://images.unsplash.com/photo-1617886322207-6f504e7470c8?w=500&auto=format&fit=crop&q=60",
    ai_verification: {
      verified: true,
      confidence: 0.94,
      model: "Gemini-2.5-Flash-Vision",
      feedback: "Textile dye effluent discharge and synthetic chemical foam formation detected."
    },
    status: "approved"
  },
  {
    id: "CIT-2026-002",
    timestamp: "2026-09-17T09:15:00+03:00",
    location_name: "Muratlı Inflow - Bridge Sector",
    coordinates: { lat: 41.1712, lon: 27.5024 },
    category: "balik_olumu",
    category_label: "Fish Mortality Event",
    note: "Substantial number of deceased freshwater fish washed ashore along the riverbank, indicating acute hypoxia.",
    photo_url: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=500&auto=format&fit=crop&q=60",
    ai_verification: {
      verified: true,
      confidence: 0.89,
      model: "Gemini-2.5-Flash-Vision",
      feedback: "Mass aquatic wildlife mortality morphology verified along riparian buffer."
    },
    status: "approved"
  },
  {
    id: "CIT-2026-003",
    timestamp: "2026-09-17T11:45:00+03:00",
    location_name: "Lüleburgaz - Historic Stone Bridge Sector",
    coordinates: { lat: 41.4055, lon: 27.3512 },
    category: "kotu_koku",
    category_label: "Noxious Chemical Odor",
    note: "Intense acidic solvent fumes causing throat irritation and stinging eyes.",
    photo_url: "https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?w=500&auto=format&fit=crop&q=60",
    ai_verification: {
      verified: true,
      confidence: 0.82,
      model: "Gemini-2.5-Flash-Vision",
      feedback: "Industrial sheen and volatile chemical surface film identified on water surface."
    },
    status: "approved"
  }
];