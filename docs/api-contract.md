# API Contract — Ergene Nehri Su Kirliliği İzleme Sistemi

Bu doküman, backend'in sunduğu ve web/mobil/AI servisinin tükettiği API sözleşmesidir.
**Kural:** Buradaki bir şey değişecekse önce bu dosya güncellenir, sonra kod yazılır — tersi değil.
Durum sütununu (`Taslak` / `Onaylandı` / `Değişti`) güncel tutun.

---


## Ortak Tanımlar

### `parameter` — geçerli değerler
| `parameter` | Sembol |
|---|---|
| `arsenic` | As |
| `copper` | Cu |
| `iron` | Fe |
| `zinc` | Zn |
| `chromium` | Cr |
| `cadmium` | Cd |
| `lead` | Pb |
| `nickel` | Ni |
| `manganese` | Mn |


> **Birim sabit değil, `sample_type`'a bağlıdır:**
> - `groundwater` / `surface_water` → **mg/L**
> - `sediment` → **mg/kg**
> Backend, `unit` alanını `sample_type`'a göre doğrulamalı (mg/kg değeri "surface_water" ile
> gelirse reddedilmeli).
> ve backend enum'u birlikte güncellenir.

### `sample_type` — geçerli değerler
`groundwater` · `surface_water` · `sediment`


### `source_type` — geçerli değerler
`literature` (yayınlanmış makaleden) · `simulated` (bizim ürettiğimiz) · `sensor` · `citizen`

### BDL (Below Detection Limit) kuralı
Cihazın tespit limitinin altında kalan ölçümlerde `value: null` ve
`below_detection_limit: true` gönderilir. **Bu sıfır anlamına gelmez.**
Backend bu kayıtları eşik karşılaştırmasına ve ortalama hesabına DAHİL ETMEZ.



## 1. Gözlem Gönderme (Sensör / Mock Veri)
**Durum:** Güncellendi — gerçek veri setine göre
**Endpoint:** `POST /api/observations`
**Kim çağırır:** Mock veri üreten script / manuel test

Request:
```json
{
  "location_name": "Ergene Havzasi - Kuyu 9",
  "coordinates": { "lat": 41.271667, "lon": 27.9725 },
  "timestamp": "2013-05-15T10:00:00+03:00",
  "parameter": "chromium",
  "value": 0.1,
  "unit": "mg/L",
  "below_detection_limit": false,
  "sample_type": "groundwater",
  "source_type": "literature",
  "citation": "Arkoc, O. (2014) Bull Environ Contam Toxicol 93:429-433, Table 1"
}
```

### Ek alanlar
- `method` (string) — örn. `"ICP-MS (Agilent 7700)"`
- `station_no` (integer, opsiyonel)
- `coordinates` — artık **nullable** olabilir (bazı literatür kaynaklarında sayısal koordinat yok)
- `coordinate_source` (string, opsiyonel) — örn. `"approximated_from_figure"`, koordinatın
  nereden geldiğini işaretler
- `water_quality` (obje, opsiyonel, sadece su örneklerinde) —
  `{ ph, ec_us_cm, tds_mg_l, salinity_psu, do_mg_l }`



BDL örneği (kadmiyum tüm kuyularda BDL çıkmıştır):
```json
{
  "location_name": "Ergene Havzasi - Kuyu 9",
  "coordinates": { "lat": 41.271667, "lon": 27.9725 },
  "timestamp": "2013-05-15T10:00:00+03:00",
  "parameter": "cadmium",
  "value": null,
  "unit": "mg/L",
  "below_detection_limit": true,
  "sample_type": "groundwater",
  "source_type": "literature"
}
```

Response `201 Created`:
```json
{
  "id": "erg-obs-2026-0347",
  "fhir_observation_id": "erg-obs-2026-0347",
  "risk_flagged": true,
  "exceeded_standards": ["TS_2005", "WHO_2006"]
}
```
> `exceeded_standards`: hangi standartların aşıldığını listeler. Hiçbiri aşılmadıysa boş dizi,
> BDL kayıtlarında da boş dizi (`risk_flagged: false`).

--- 

## 2. Vatandaş Bildirimi Gönderme
**Durum:** Taslak
**Endpoint:** `POST /api/citizen-reports`
**Kim çağırır:** Mobil uygulama

Request (multipart/form-data ya da JSON + ayrı foto upload — takım karar verecek):
```json
{
  "coordinates": { "lat": 41.1605, "lon": 27.8021 },
  "timestamp": "2026-08-18T14:22:00+03:00",
  "category": "kirli_renk_degisimi",
  "note": "Suyun rengi koyu kahverengiye dönmüş, koku var.",
  "photo_url": "https://.../uploads/citreport-1183.jpg"
}
```
Response `201 Created`:
```json
{
  "id": "cit-2026-1183",
  "ai_validation_status": "onaylandi",
  "ai_confidence": 0.87
}
```

---

## 3. Gözlemleri Listeleme (Dashboard için)
**Durum:** güncellendi
**Endpoint:** `GET /api/observations?parameter=chromium&from=2013-01-01&to=2026-12-31`
**Kim çağırır:** Web dashboard

Response `200 OK`:
```json
{
  "results": [
    {
      "id": "erg-2013-w09-cr",
      "location_name": "Ergene Havzasi - Kuyu 9",
      "coordinates": { "lat": 41.271667, "lon": 27.9725 },
      "timestamp": "2013-05-15T10:00:00+03:00",
      "parameter": "chromium",
      "value": 0.1,
      "unit": "mg/L",
      "below_detection_limit": false,
      "sample_type": "groundwater",
      "source_type": "literature",
      "risk_flagged": true
    }
  ]
}
```

---

## 4. Risk Durumu Sorgulama
**Durum:** güncellendi — location eşleştirme kuralı netleştirildi
**Endpoint:** `GET /api/risk-status?location=Ergene%20Havzasi%20-%20Kuyu%209`
**Kim çağırır:** Web dashboard, mock hastane paneli

> **KURAL:** `location` parametresi, `/api/locations` veya `/api/observations`
> yanıtlarından alınan `location_name` değeriyle **birebir (case-sensitive)**
> eşleşmelidir. Frontend bu değeri kendi üretmemeli/tahmin etmemeli — sadece daha
> önce API'den aldığı bir değeri geri göndermelidir. Boşluk ve özel karakterler için
> `URLSearchParams`/`encodeURIComponent` kullanılmalıdır.

Response `200 OK`:
```json
{
  "location": "ergene-kuyu-09",
  "current_risk_level": "high",
  "parameter": "chromium",
  "value": 0.1,
  "unit": "mg/L",
  "threshold": 0.05,
  "standard": "WHO_2006",
  "reason": "Olculen krom degeri (0.1 mg/L), WHO (2006) icme suyu rehber degerini (0.05 mg/L) asiyor.",
  "last_updated": "2013-05-15T10:00:00+03:00"
}
```

---

## 5. AI Foto Doğrulama (Backend → AI Servisi arası, iç API)
**Durum:** Taslak
**Endpoint:** `POST http://ai-service:8000/validate-photo`
**Kim çağırır:** Backend (vatandaş bildirimi geldiğinde otomatik)

Request:
```json
{
  "photo_url": "https://.../uploads/citreport-1183.jpg",
  "category": "kirli_renk_degisimi"
}
```
Response `200 OK`:
```json
{
  "tutarli": true,
  "guven_skoru": 0.87,
  "aciklama": "Fotoğrafta koyu renkli, bulanık su net görülüyor, seçilen kategoriyle tutarlı."
}
```

---

## 6. Sağlık Riski Değerlendirmesi Gönderme (Literatürden)
**Durum:** Taslak
**Endpoint:** `POST /api/risk-assessments`

Request:
```json
{
  "location_name": "St 2 - koy ici, sanayiden uzak",
  "station_no": 2,
  "timestamp": "2025-05-15T10:00:00+03:00",
  "carcinogenic_risk": { "child": 1.097609, "adult": 1.015173 },
  "total_hazard_index": { "child": 3.050103, "adult": 2.58 },
  "source_type": "literature",
  "citation": "Aydin, G.B., Tas-Divrik, M., Atun, R. (2026) Int J Environ Sci Technol 23:621, Table 9"
}
```
Response `201 Created`:
```json
{ "id": "erg-2025-st2-hra", "fhir_riskassessment_id": "erg-2025-st2-hra" }
```

---

## 7. Sağlık Riski Değerlendirmelerini Listeleme (Dashboard için)
**Durum:** Taslak
**Endpoint:** `GET /api/risk-assessments?location=St%202%20-%20koy%20ici,%20sanayiden%20uzak`
**Kim çağırır:** Web dashboard, mock hastane paneli

`location` opsiyoneldir. Verilirse `location_name` ile birebir (case-sensitive)
eşleşir. Verilmezse tüm kayıtlar `timestamp` alanına göre yeniden eskiye döner.

Response `200 OK`:
```json
{
  "results": [
    {
      "id": "ERG-2025-ST2-HRA",
      "location_name": "St 2 - koy ici, sanayiden uzak",
      "station_no": 2,
      "timestamp": "2025-05-15T10:00:00+03:00",
      "carcinogenic_risk": { "child": 1.097609, "adult": 1.015173 },
      "total_hazard_index": { "child": 3.050103, "adult": 2.58 },
      "risk_level": "high",
      "basis_note": "Kanserojen risk As ve Ni uzerinden hesaplanmistir.",
      "source_type": "literature",
      "citation": "Aydin et al. (2026), Table 9",
      "fhir_risk_assessment_id": "1001"
    }
  ]
}
```

---

## 8. Bilinen Konumları Listeleme (yeni)
**Durum:** Taslak
**Endpoint:** `GET /api/locations`
**Kim çağırır:** Web dashboard (harita/filtre dropdown'ı için), mobil (opsiyonel)

Response `200 OK`:
```json
{
  "locations": [
    {
      "location_name": "Ergene Havzasi - Kuyu 9",
      "station_no": 9,
      "sample_types": ["groundwater"],
      "coordinates": { "lat": 41.271667, "lon": 27.9725 }
    },
    {
      "location_name": "St 1 - yag fabrikasi yani (Corlu/Cerkezkoy ust havza)",
      "station_no": 1,
      "sample_types": ["surface_water", "sediment"],
      "coordinates": { "lat": 41.18, "lon": 27.77 }
    }
  ]
}
```

`location_name` değerleri veritabanındaki gözlemlerden gelir. Frontend bu değerleri
kendi üretmemeli veya tahmin etmemeli; `/api/locations` ya da `/api/observations`
yanıtından aldığı değeri `/api/risk-status` çağrısında olduğu gibi geri göndermelidir.

---


## Eşik Değerleri Referansı (mg/L)

Kaynak: Arkoç (2014), Tablo 2 — TS (2005), WHO (2006), EPA (2013)

| Metal | TS 2005 | WHO 2006 | EPA 2013 |
|---|---|---|---|
| copper | 2 | 2 | 1.3 |
| iron | 0.2 | 0.3 | 0.3 |
| zinc | — | 3 | 5 |
| chromium | 0.05 | 0.05 | 0.1 |
| cadmium | 0.005 | 0.003 | 0.005 |
| lead | 0.01 | 0.01 | 0.015 |

> `zinc` için TS standardında değer tanımlı değil — backend bu durumda o standardı
> karşılaştırmaya dahil etmez (null kontrolü gerekir).


## Doldurulacak Açık Sorular
- [ ] `category` alanı için kesin değer listesi netleşti mi? (bulanik / kirli_renk_degisimi / balik_olumu / kotu_koku / diger)
- [ ] Fotoğraf yükleme ayrı bir endpoint mi olacak (`POST /api/uploads`) yoksa mobil doğrudan bir dosya storage'a mı yükleyecek?
- [ ] Kimlik doğrulama var mı, yoksa hackathon MVP'sinde açık mı bırakılacak?