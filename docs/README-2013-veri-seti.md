# Ergene Havzası Ağır Metal Veri Seti

## Kaynak

Arkoç, O. (2014). *Heavy Metal Concentrations of Groundwater in the East of Ergene Basin, Turkey.*
Bulletin of Environmental Contamination and Toxicology, 93:429–433.
DOI: 10.1007/s00128-014-1347-x

Veriler makalenin **Tablo 1** (ölçümler) ve **Tablo 2** (eşik değerleri) bölümlerinden aktarılmıştır.

## İçerik

- `ergene-measurements.json` — 108 kayıt (18 kuyu × 6 metal), API contract formatında
- `ergene-measurements.csv` — aynı veri, düz tablo formatında
- `generate.py` — dönüşümü yapan script (kaynak değerler script içinde açıkça görülebilir)

---

## ⚠️ Ekibin Mutlaka Bilmesi Gerekenler

Bu maddeler jüri sorularında karşınıza çıkabilir, önceden karar verin:

### 1. Bu veri NEHİR suyu değil, YERALTI suyu
Örnekler içme suyu amaçlı açılmış kuyulardan alınmış. Projeyi "Ergene Nehri izleme" diye
sunuyorsanız bu bir tutarsızlık olur. İki seçenek:
- Projeyi "Ergene Havzası su kalitesi izleme" olarak konumlandırın (önerilen), veya
- `sample_type` alanını açıkça gösterip "şu an elimizde yeraltı suyu verisi var, sistem nehir
  verisiyle de aynı şekilde çalışır" deyin.

Veri setinde bunun için `"sample_type": "groundwater"` alanı eklendi.

### 2. Bu makalede ARSENİK yok
Ölçülen metaller: Cu (bakır), Fe (demir), Zn (çinko), Cr (krom), Cd (kadmiyum), Pb (kurşun).
`docs/api-contract.md` dosyasındaki `parameter` alanı örneği `arsenic` idi — bunu bu 6 metale
göre güncelleyin, veya arsenik için ayrı bir kaynak bulun.

### 3. Sonuçlar büyük ölçüde TEMİZ çıkmış — bu aslında iyi bir hikaye
Makaleye göre ölçülen metaller, krom hariç, tüm ulusal/uluslararası sınırların altında.
Sadece **9. kuyu (Cr = 0.1 mg/L)** ve **13. kuyu (Cr = 0.09 mg/L)** TS ve WHO eşiğini (0.05 mg/L)
aşıyor.

Makale bunu, Çerkezköy/Çorlu arıtma tesislerinin devreye girmesine ve AB'nin 2005'teki krom
kısıtlamasına bağlıyor. Ayrıca makale sonuç bölümünde **aylık örnekleme yapılmasını öneriyor.**

Anlatınızı buna göre kurun: "Kirlilik tamamen bitmiş değil, ama yatırımlar işe yarıyor —
bunu ancak sürekli izleme ile görebiliyoruz. Makalenin kendi önerisi de tam bu."
Bu, "her yer zehirli" anlatısından hem daha dürüst hem bilimsel olarak daha savunulabilir.

### 4. Birim mg/L (contract'ta µg/L yazıyordu)
Kaynağa sadık kalmak için mg/L olarak bırakıldı. Dönüştürmek isterseniz: 1 mg/L = 1000 µg/L.
Karar verip contract'ı güncelleyin, iki birim karışmasın.

### 5. BDL (Below Detection Limit) kayıtları
Bazı ölçümler cihazın tespit limitinin altında kalmış. JSON'da bunlar
`"value": null, "below_detection_limit": true` olarak işaretlendi.
**Bu 0 (sıfır) DEĞİLDİR** — "ölçülemeyecek kadar az" demektir. Backend'de bunları sıfır gibi
işlemeyin, ortalama hesabına katmayın.
Kadmiyum tüm kuyularda BDL çıkmış.

### 6. Küçük tutarsızlıklar (makalenin kendisinde var)
- Makale metninde "17 kuyudan örnek alındı" yazıyor ama Tablo 1'de 18 satır var. Tablo esas alındı.
- Demir için tespit limiti 0.01 mg/L denmiş, ama birçok kuyuda 0.009 değeri raporlanmış
  (limitin altında bir sayı). Kaynaktaki haliyle bırakıldı.
- Örnekleme tarihi olarak sadece "Mayıs 2013" verilmiş, gün belirtilmemiş.
  Veri setinde `2013-05-15T10:00:00+03:00` varsayıldı — bu bizim koyduğumuz bir varsayımdır.
- Koordinatlar PDF'ten derece-dakika-saniye formatında okunup ondalık dereceye çevrildi.
  Harita üzerinde bir kez gözle doğrulayın; PDF metin çıkarımı bu alanda hataya açıktır.

---

## Örnek Kayıt (ham veri formatı)

Eşiği aşan 9. kuyu krom ölçümü:

```json
{
  "measurement_id": "ERG-2013-W09-CR",
  "location_name": "Ergene Havzasi - Kuyu 9",
  "coordinates": { "lat": 41.271667, "lon": 27.9725 },
  "timestamp": "2013-05-15T10:00:00+03:00",
  "parameter": "chromium",
  "parameter_symbol": "Cr",
  "value": 0.1,
  "unit": "mg/L",
  "below_detection_limit": false,
  "detection_limit": 0.00005,
  "method": "ICP-ES / ICP-MS",
  "source_type": "literature",
  "sample_type": "groundwater",
  "well_depth_m": 300,
  "ph": 6.1,
  "ec_us_cm": 683,
  "thresholds_mg_l": { "TS_2005": 0.05, "WHO_2006": 0.05, "EPA_2013": 0.1 },
  "citation": "Arkoc, O. (2014) Bull Environ Contam Toxicol 93:429-433, Table 1"
}
```

## Aynı Kaydın FHIR Observation Karşılığı

```json
{
  "resourceType": "Observation",
  "id": "erg-2013-w09-cr",
  "status": "final",
  "category": [{
    "coding": [{
      "system": "http://terminology.hl7.org/CodeSystem/observation-category",
      "code": "environmental",
      "display": "Environmental"
    }]
  }],
  "code": {
    "text": "Su numunesinde krom konsantrasyonu"
  },
  "effectiveDateTime": "2013-05-15T10:00:00+03:00",
  "valueQuantity": {
    "value": 0.1,
    "unit": "mg/L",
    "system": "http://unitsofmeasure.org",
    "code": "mg/L"
  },
  "subject": {
    "reference": "Location/ergene-kuyu-09",
    "display": "Ergene Havzasi - Kuyu 9"
  },
  "interpretation": [{
    "coding": [{
      "system": "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation",
      "code": "H",
      "display": "High"
    }]
  }],
  "referenceRange": [{
    "high": { "value": 0.05, "unit": "mg/L" },
    "text": "WHO (2006) icme suyu rehber degeri"
  }]
}
```

**LOINC notu:** `code` alanında hâlâ LOINC kodu yok, sadece `text` var. Çevresel su örneklerine
özel LOINC kodları araştırılıp doğrulanmadan uydurma kod yazılmamalı. Doğrulanana kadar
`code.text` ile devam etmek geçerli ve dürüst bir yaklaşım.

**BDL kayıtları için FHIR:** `valueQuantity` yerine `dataAbsentReason` kullanın
(`"code": "not-performed"` yerine uygun olanı seçin), ya da `valueQuantity.comparator: "<"`
ile tespit limitini verin. Sıfır yazmayın.
