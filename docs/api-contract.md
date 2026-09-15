# API Contract — Ergene Nehri Su Kirliliği İzleme Sistemi

Bu doküman, backend'in sunduğu ve web/mobil/AI servisinin tükettiği API sözleşmesidir.
**Kural:** Buradaki bir şey değişecekse önce bu dosya güncellenir, sonra kod yazılır — tersi değil.
Durum sütununu (`Taslak` / `Onaylandı` / `Değişti`) güncel tutun.

---

## 1. Gözlem Gönderme (Sensör / Mock Veri)
**Durum:** Taslak
**Endpoint:** `POST /api/observations`
**Kim çağırır:** Mock veri üreten script / manuel test

Request:
```json
{
  "location_name": "Ergene Nehri - Çorlu Mevkii",
  "coordinates": { "lat": 41.1592, "lon": 27.8033 },
  "timestamp": "2026-08-18T10:00:00+03:00",
  "parameter": "arsenic",
  "value": 15.2,
  "unit": "µg/L",
  "source_type": "sensor"
}
```
Response `201 Created`:
```json
{
  "id": "erg-obs-2026-0347",
  "fhir_observation_id": "erg-obs-2026-0347",
  "risk_flagged": true
}
```

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
**Durum:** Taslak
**Endpoint:** `GET /api/observations?location=ergene-corlu&from=2026-01-01&to=2026-08-18`
**Kim çağırır:** Web dashboard

Response `200 OK`:
```json
{
  "results": [
    { "timestamp": "2026-08-18T10:00:00+03:00", "parameter": "arsenic", "value": 15.2, "unit": "µg/L", "source_type": "sensor", "risk_flagged": true }
  ]
}
```

---

## 4. Risk Durumu Sorgulama
**Durum:** Taslak
**Endpoint:** `GET /api/risk-status?location=ergene-corlu`
**Kim çağırır:** Web dashboard, mock hastane paneli

Response `200 OK`:
```json
{
  "location": "ergene-corlu",
  "current_risk_level": "high",
  "reason": "Ölçülen arsenik değeri (15.2 µg/L), WHO içme suyu rehber değerini (10 µg/L) aşıyor.",
  "last_updated": "2026-08-18T10:00:00+03:00"
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

## Doldurulacak Açık Sorular
- [ ] `category` alanı için kesin değer listesi netleşti mi? (bulanik / kirli_renk_degisimi / balik_olumu / kotu_koku / diger)
- [ ] Fotoğraf yükleme ayrı bir endpoint mi olacak (`POST /api/uploads`) yoksa mobil doğrudan bir dosya storage'a mı yükleyecek?
- [ ] `parameter` alanı için kesin değer listesi (arsenic, cadmium, ... ) — veri kaynağı araştırmasına bağlı
- [ ] Kimlik doğrulama var mı, yoksa hackathon MVP'sinde açık mı bırakılacak?