# Ergene Havzasi 2013 Veri Seti

## Kaynak

Arkoc, O. (2014). *Heavy Metal Concentrations of Groundwater in the East of Ergene Basin, Turkey.*
Bulletin of Environmental Contamination and Toxicology, 93:429-433.
DOI: 10.1007/s00128-014-1347-x

## Dosya ve kapsam

- Dosya: `data/ergene-2013-measurements.json`
- 108 kayit: 18 kuyu x 6 metal
- Ortam: `groundwater`
- Birim: `mg/L`
- Kaynak: `literature`

Bu veri nehri degil, Ergene Havzasi'ndaki yeraltı suyu kuyularini temsil eder.
Dashboard bu kayitlari 2025 ve 2021 nehir suyu kayitlariyla tek bir zaman serisinde
birlestirmemelidir. Su ortami ve sediman verileri de ayni seride birlestirilmemelidir.

## Veri kurallari

- `value: null` ve `below_detection_limit: true` olan kayitlar BDL'dir; sifir degildir.
- BDL kayitlari risk esigi karsilastirmasina dahil edilmez.
- 2013 kayitlarinda tarih, makalede yalnızca Mayis 2013 olarak verildigi icin
  `2013-05-15T10:00:00+03:00` varsayimiyla temsil edilir.
- Koordinatlar PDF haritasindan ondalik dereceye donusturulmustur; kesinlikleri
  veri kaynaginin sinirlari icinde degerlendirilmelidir.
- `coordinate_source` bu veri setinde bulunmayabilir ve opsiyoneldir.
- Kayitlarda `parameter_symbol`, `detection_limit`, `well_depth_m`, `ph` ve benzeri
  kaynak metadata alanlari bulunabilir; backend request DTO'su bunlari API response'una
  tasimaz. Frontend listeleme icin `/api/observations` response'unu kullanmalidir.

## Esik ve risk

Su orneklerinde yalnizca `ThresholdConfig` icinde tanimli ve dogrulanmis esikler
kullanilir. 2013 verisinde özellikle 9. ve 13. kuyudaki chromium degerleri su
esiklerini asan orneklerdir. Sediman kurallari bu veri seti icin uygulanmaz.

## Backend ve FHIR davranisi

Seeder bu dosyayi `ObservationService.create()` uzerinden idempotent olarak yukler.
Ayni `measurement_id` veritabaninda varsa kayit tekrar eklenmez.

Backend'in mevcut FHIR client'i `Observation` kaynagi icin su alanlari gonderir:

```json
{
  "resourceType": "Observation",
  "status": "final",
  "code": { "text": "chromium" },
  "valueQuantity": { "value": 0.1, "unit": "mg/L" }
}
```

LOINC kodu, subject, referenceRange ve interpretation uydurulmaz. BDL kayitlarinda
`valueQuantity` gonderilmez; mevcut MVP FHIR akisinda deger alani bos birakilir.

## Frontend kullanimi

- Tum gozlemler: `GET /api/observations`
- 2013 filtresi: `GET /api/observations?from=2013-01-01&to=2013-12-31`
- Location secimi: `GET /api/locations`
- Location degeri frontend tarafindan uretilmez; API'den gelen `location_name` aynen
  `GET /api/risk-status?location=...` icin geri gonderilir.
