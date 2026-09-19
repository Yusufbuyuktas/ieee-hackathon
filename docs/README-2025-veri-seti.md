# Ergene Nehri 2025 Veri Seti

## Kaynak

Aydin, G.B., Tas-Divrik, M., Atun, R. (2026).
*Potentially toxic element contamination in water and sediments of the Ergene river basin
(Turkiye): ecological and human health risk assessment.*
International Journal of Environmental Science and Technology, 23:621.
DOI: 10.1007/s13762-026-07424-6

## Dosyalar ve kapsam

- `data/ergene-2025-measurements.json`: 90 kayit, 5 istasyon x 9 element x 2 ortam
- `data/ergene-2025-risk.json`: 5 hazir insan sagligi risk degerlendirmesi
- Ortamlar: `surface_water` ve `sediment`
- Su birimi: `mg/L`; sediman birimi: `mg/kg`
- Kaynak: `literature`

Bu veri seti Ergene Nehri suyu ve sedimanini 2025 Mayis orneklemesiyle temsil eder.
2013 yeraltı suyu veya 2021 kayitlariyla tek bir trend serisinde birlestirilmemelidir.

## Veri kurallari

- `value: null` ve `below_detection_limit: true` BDL'dir; sifir degildir.
- BDL kayitlari ortalama ve esik hesaplarina dahil edilmez.
- Su ve sediman farkli birimlere sahiptir; ayni grafik veya ayni esik ile karsilastirilmaz.
- `coordinates` bu JSON'da yaklasik koordinatlardir.
- Yaklasik koordinatlar `coordinate_source: "approximated_from_figure"` ile isaretlenir.
- Frontend koordinat ve location degerlerini JSON'dan tahmin etmemeli, API response'larini
  kullanmalidir.

## Saglik riski kurali

`ergene-2025-risk.json` icindeki CR ve THI degerleri makaleden aktarilmistir; backend
bu degerleri yeniden hesaplamaz. CR degerleri kaynaklar arasinda ayni olcekte kabul
edilmedigi icin backend CR uzerinden otomatik esik karsilastirmasi yapmaz.

Her risk kaydinda `source_concluded_high_risk` bulunur. Bu alan kaynagin nihai yargisidir
ve backend tarafindan hesaplanmaz.

Backend `risk_level` degeri su kuralla belirlenir:

```text
HIGH if total_hazard_index.child > 1.0
   or total_hazard_index.adult > 1.0
   or source_concluded_high_risk == true
NORMAL otherwise
```

Bu nedenle `risk_level`, kayitta risk assessment bulunup bulunmadigini degil,
siniflandirma sonucunu ifade eder.

## Backend ve FHIR davranisi

Seeder iki dosyayi `RiskAssessmentService.create()` uzerinden idempotent yukler.
Ayni `assessment_id` veritabaninda varsa kayit tekrar eklenmez.

Risk assessment listesi:

```text
GET /api/risk-assessments
GET /api/risk-assessments?location=<API'den gelen location_name>
```

FHIR `RiskAssessment` kaynaginda LOINC veya uydurma kod kullanilmaz. Backend'in mevcut
MVP FHIR resource'u `resourceType`, `status`, `code.text`, `subject.display` ve
`prediction` alanlarini gonderir; donen FHIR `id` veritabanina kaydedilir.

## Frontend kullanimi

- Gozlemler: `GET /api/observations?from=2025-01-01&to=2025-12-31`
- Risk kayitlari: `GET /api/risk-assessments`
- Konum dropdown'i: `GET /api/locations`
- `location` parametresi case-sensitive tam eslesir; bosluk ve ozel karakterler
  `URLSearchParams` veya `encodeURIComponent` ile encode edilmelidir.
