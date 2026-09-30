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

### Veri kaynaklari ve karsilastirma kurallari

- `data/ergene-2013-measurements.json`: 108 yeraltı suyu (`groundwater`) olcumu,
  18 kuyu x 6 metal. Bu kayitlar 2021 ve 2025 nehir suyu kayitlariyla tek trend
  serisinde birlestirilmemelidir.
- `data/ergene-2021-measurements.json`: 9 nehir suyu (`surface_water`) olcumu.
- `data/ergene-2021-risk.json`: 4 hazir literatur risk degerlendirmesi.
- `data/ergene-2025-measurements.json`: 90 nehir suyu ve sediman olcumu,
  5 istasyon x 9 element x 2 ortam.
- `data/ergene-2025-risk.json`: 5 hazir literatur risk degerlendirmesi.
- 2013 yeraltı suyu, 2021/2025 nehir suyu ve sediman kayitlari ayni fiziksel ortam
  veya ayni birim degildir; frontend bunlari ayri seri/filtre olarak gostermelidir.
- 2013 ve 2021 koordinatlari kaynak/varsayim sinirlarina tabidir. 2025 koordinatlari
  sekil üzerinden yaklasiktir ve `coordinate_source` ile isaretlenir.

### Frontend entegrasyon kurallari

- Frontend location, parameter, sample type veya source type degerlerini kendi
  uretmez/tahmin etmez; API response'larindan aldigi degerleri kullanir.
- Konum dropdown'i icin `GET /api/locations` kullanilir. Secilen `location_name`,
  `GET /api/risk-status` ve `GET /api/risk-assessments` isteklerinde degistirilmeden
  gonderilir. Query string icin `URLSearchParams` veya `encodeURIComponent` kullanilir.
- `station_no` 2013 kayitlarinda null olabilir; frontend bunu istasyon numarasi varmis
  gibi varsaymamalidir.
- `coordinates` null olabilir. Harita, koordinati olmayan kayitlari atlamali veya
  koordinatsiz olarak gostermelidir.
- `GET /api/observations` response'u `location_name`, `coordinates`, `timestamp`,
  `parameter`, `value`, `unit`, `below_detection_limit`, `sample_type`, `source_type`
  ve `risk_flagged` alanlarini verir. Kaynak JSON metadata alanlari bu response'a
  otomatik olarak eklenmez.
- `GET /api/risk-assessments` response'u hazir CR/THI degerlerini, `risk_level` ve
  `source_concluded_high_risk` alanlarini verir. CR yeniden hesaplanmaz.
- Observation request'indeki `water_quality` alani su an kabul edilir; ancak mevcut
  MVP response DTO'sunda donulmez ve ayri sorgulanabilir kolon olarak saklanmaz.

### FHIR MVP sinirlari

- Backend FHIR'a ham JSON ile Observation ve RiskAssessment POST eder.
- Observation icin LOINC kodu uydurulmaz; `code.text` kullanilir.
- FHIR sunucusunun dondugu `id`, ilgili entity'deki `fhir_observation_id` veya
  `fhir_risk_assessment_id` alanina yazilir. Bu ID, uygulamanin `measurement_id` veya
  `assessment_id` degeriyle ayni olmak zorunda degildir.
- FHIR entegrasyonu basarisiz olsa bile MVP'de lokal DB kaydi korunur ve FHIR ID null
  kalabilir; frontend bu alanin nullable olabilecegini kabul etmelidir.
- Vatandaş bildirimi AI değerlendirmesi için `POST /moderate-photo` endpoint'ini kullanır.



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
**Durum:** Güncellendi
**Endpoint:** `POST /api/citizen-reports`
**Kim çağırır:** Mobil uygulama

Request (`multipart/form-data`):
- `photo` (MultipartFile)
- `category` (String)
- `note` (String)
- `latitude` (Double)
- `longitude` (Double)
- `timestamp` (String)

Örnek form alanları:
```text
photo=<binary file>
category=kirli_renk_degisimi
note=Suyun rengi koyu kahverengiye dönmüş, koku var.
latitude=41.1605
longitude=27.8021
timestamp=2026-08-18T14:22:00+03:00
```
Response `201 Created`:
```json
{
  "id": "cit-uuid",
  "ai_validation_status": "ONAYLANDI",
  "ai_confidence": 0.87
}
```

### Vatandaş Bildirimlerini Listeleme
**Durum:** Güncellendi
**Endpoint:** `GET /api/citizen-reports`
**Kim çağırır:** Web dashboard

Response `200 OK` (en yeni bildirim en üstte):
```json
{
  "results": [
    {
      "id": "cit-uuid",
      "photo_url": "/uploads/cit-report-uuid.jpg",
      "category": "BALIK_OLUMU",
      "note": "Nehir kenarında ölü balıklar görüldü.",
      "latitude": 41.445,
      "longitude": 27.925,
      "timestamp": "2026-09-24T10:48:03Z",
      "ai_validation_status": "ONAYLANDI",
      "ai_confidence": 0.87,
      "ai_explanation": "Fotoğraftaki içerik seçilen kategoriyle tutarlı.",
      "fhir_observation_id": "observation-id"
    }
  ]
}
```
`photo_url` görsel yoldur; tam fotoğraf adresi, backend base URL'si ile birleştirilerek oluşturulur.

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
## 4. Vatandaş Bildirimi Durum Güncelleme

**Durum:** Onaylandı  
**Endpoint:** `PATCH /api/citizen-reports/{id}/status`  
**Kim çağırır:** Belediye personeli

Request:

```json
{
  "status": "ONAYLANDI"
}
```

Geçerli hedef durumlar:
- `ONAYLANDI`
- `TUTARSIZ`

MVP kapsamında yalnızca `INCELEMEDE` durumundaki vatandaş bildirimleri belediye personeli tarafından `ONAYLANDI` veya `TUTARSIZ` durumuna geçirilebilir.

Response `200 OK`:

```json
{
  "id": "cit-uuid",
  "ai_validation_status": "ONAYLANDI"
}
```

Bulunamayan bir rapor için `404 Not Found`, geçersiz durum veya geçersiz durum geçişi için `400 Bad Request` döndürülür.
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
  "location": "Ergene Havzasi - Kuyu 9",
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
**Durum:** Güncellendi
**Endpoint:** `POST http://ai-service:8000/moderate-photo`
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
  "aciklama": "Fotoğrafta koyu renkli, bulanık su net görülüyor, seçilen kategoriyle tutarlı.",
  "moderation_status": "approved"
}
```
Moderation status:
- approved: Görsel kategoriyle tutarlı ve güven göstergesi >= 0.80.
- review: Düşük güven veya görselden doğrulanamayan kategori.
- inconsistent: Görsel kategoriyle yüksek güven göstergesiyle uyumsuz.

kotu_koku ve diger kategorileri her zaman review durumuna yönlendirilir.


---

## 6. Sağlık Riski Değerlendirmesi Gönderme (Literatürden)
**Durum:** Onaylandı
**Endpoint:** `POST /api/risk-assessments`

### Zorunlu ek alan
- `source_concluded_high_risk` (boolean) — kaynağın kendi metninde veya
  `basis_note` alanında belirttiği nihai risk yargısı. Backend bu değeri hesaplamaz;
  request'te gönderilen değeri saklar.

> `carcinogenic_risk` değerleri kaynaklar arasında tutarlı bir ölçekte değildir ve
> backend tarafından otomatik eşik karşılaştırmasına tabi tutulmaz. `risk_level`,
> `total_hazard_index.child > 1.0` veya `total_hazard_index.adult > 1.0` ya da
> `source_concluded_high_risk: true` ise `high`, aksi halde `normal` olur.

Request:
```json
{
  "location_name": "St 2 - koy ici, sanayiden uzak",
  "station_no": 2,
  "timestamp": "2025-05-15T10:00:00+03:00",
  "carcinogenic_risk": { "child": 1.097609, "adult": 1.015173 },
  "total_hazard_index": { "child": 3.050103, "adult": 2.58 },
  "source_concluded_high_risk": true,
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
**Durum:** Onaylandı
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
      "source_concluded_high_risk": true,
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
**Durum:** Onaylandı
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