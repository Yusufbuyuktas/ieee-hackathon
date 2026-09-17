# Ergene Nehri 2025 Veri Seti (İkinci Kaynak)

## Kaynak

Aydın, G.B., Taş-Divrik, M., Atun, R. (2026).
*Potentially toxic element contamination in water and sediments of the Ergene river basin
(Türkiye): ecological and human health risk assessment.*
International Journal of Environmental Science and Technology, 23:621.
DOI: 10.1007/s13762-026-07424-6

Örnekleme: **Mayıs 2025**, Ergene Nehri üzerinde **5 istasyon**, hem su hem sediman.

## Dosyalar

- `ergene-2025-measurements.json` — 90 kayıt (5 istasyon × 9 element × 2 ortam)
- `ergene-2025-risk.json` — 5 kayıt (istasyon başına insan sağlığı risk değerlendirmesi)
- `generate2025.py` — dönüşüm script'i

---

## Bu Makale Neden Değerli

1. **Nehir suyu verisi** — 2013 makalesi yeraltı suyuydu, bu gerçek Ergene Nehri.
2. **Sediman verisi de var** — su ve sediman ayrı ayrı ölçülmüş.
3. **Hazır sağlık riski hesabı** — makale USEPA metoduyla kanserojen ve kanserojen olmayan
   riskleri hesaplamış. FHIR `RiskAssessment` kaynağınızı **gerçek, kaynaklı veriyle**
   doldurabilirsiniz; risk skoru uydurmanıza gerek yok.
4. **9 element** — öncekinin 6'sına ek olarak arsenik (As), nikel (Ni), mangan (Mn).
   **Arsenik artık veri setinde var.**

---

## ⚠️ Dikkat Edilmesi Gerekenler

### 1. Koordinatlar makalede sayısal olarak verilmemiş
İstasyonlar sadece harita üzerinde (Şekil 1) gösterilmiş, lat/lon değerleri yazılmamış.
JSON'da `"coordinates": null` bırakıldı — **uydurulmadı**.
Haritayı gösteren bölge tanımları metinden alındı (St 1 yağ fabrikası yanı, St 2 köy içi,
St 3-4 OSB içi, St 5 Adasarhanlı yakını).
Harita üzerinden yaklaşık koordinat okuyup ekleyebilirsiniz — o zaman
`"coordinate_source": "approximated_from_figure"` gibi bir alan ekleyip şeffaf olun.

### 2. "0" değerleri BDL olarak işlendi
Makale bazı ölçümler için 0 mg/L yazmış ama metinde "tespit edilmedi" diye açıklamış
(örn. krom St 2 ve St 3'te, kadmiyum St 1 hariç hepsinde).
Bunlar `value: null, below_detection_limit: true` olarak kaydedildi — 7 kayıt.
Sıfır olarak işlerseniz ortalamalarınız yanlış çıkar.

### 3. 2013 ve 2025 verisi DOĞRUDAN karşılaştırılamaz
Bu çok önemli. İki veri seti farklı:

| | 2013 (Arkoç) | 2025 (Aydın ve ark.) |
|---|---|---|
| Ortam | Yeraltı suyu (kuyu) | Nehir suyu + sediman |
| Nokta | 18 kuyu | 5 istasyon |
| Element | 6 | 9 |

Bunları tek bir trend çizgisinde birleştirmek **bilimsel olarak yanlış** olur — yeraltı suyu
ile nehir suyu farklı şeyler. Jüri bunu sorabilir.

**Doğru kullanım:** Dashboard'da iki ayrı katman/seri olarak gösterin
("2013 yeraltı suyu" ve "2025 nehir suyu"), tek çizgi halinde birleştirmeyin.
Anlatı şöyle kurulabilir: *"Havza iki farklı su kaynağı açısından da izlenmeli — sistemimiz
her iki veri tipini de aynı standartta (FHIR) saklayabiliyor."*

### 4. Yeni elementler için eşik değeri henüz YOK
Arsenik, nikel ve mangan için içme suyu eşik değerleri **bu iki makalenin hiçbirinde
tablo halinde verilmemiş**. Uydurma değer yazmayın.
Backend'in eşik tablosuna eklemeden önce WHO/TS/EPA rehber değerlerini ayrıca doğrulayın.
(Arsenik için yaygın bilinen WHO değeri 10 µg/L = 0.01 mg/L'dir, ancak güncel haliyle
teyit edilmeli.)

### 5. Sediman ölçümleri farklı birimde
Su mg/L, sediman mg/kg. **Aynı grafikte gösterilemez, aynı eşikle karşılaştırılamaz.**
Backend'de `sample_type` alanına göre ayrı işleyin.

### 6. Makalenin kendi çelişkisi
Metinde "HQ was highest at St. 5 (4.08) and lowest at St. 5 as well (5.92E−02)" yazıyor —
aynı istasyon hem en yüksek hem en düşük olarak gösterilmiş. Makalede bir yazım hatası var.
Tablo 9'daki sayılar aktarıldı, bu cümle yok sayıldı.

---

## Sağlık Riski Verisi — Projenin En Güçlü Kartı

Makaleye göre kanserojen risk değerleri kabul edilebilir eşiği (1.0) **tüm istasyonlarda**
aşıyor; kesin olarak 1.0'ı geçen değerler:

| İstasyon | Çocuk | Yetişkin |
|---|---|---|
| St 2 | 1.098 | 1.015 |
| St 4 | 1.039 | — |

Ayrıca sindirim yoluyla tehlike katsayısı (HQ), çocuklarda kabul edilebilir eşiğin
**85 katına**, yetişkinlerde **76 katına** kadar çıkıyor.

Bu, projenizin One Health anlatısının çekirdeği: çevresel ölçüm → hesaplanmış insan sağlığı
riski → FHIR `RiskAssessment` → hastane paneli. Zincirin her halkası gerçek ve kaynaklı.

**Önemli:** Bu risk değerlerini **siz hesaplamadınız**, makaleden aldınız. Demo'da bunu açıkça
söyleyin. `source_type: "literature"` alanı bunu zaten işaretliyor.

---

## Örnek Kayıt — Su Ölçümü

```json
{
  "measurement_id": "ERG-2025-ST1-ZN-WAT",
  "location_name": "St 1 - yag fabrikasi yani (Corlu/Cerkezkoy ust havza)",
  "station_no": 1,
  "coordinates": null,
  "timestamp": "2025-05-15T10:00:00+03:00",
  "parameter": "zinc",
  "parameter_symbol": "Zn",
  "value": 0.0214,
  "unit": "mg/L",
  "below_detection_limit": false,
  "method": "ICP-MS (Agilent 7700)",
  "source_type": "literature",
  "sample_type": "surface_water",
  "water_quality": {
    "ph": 7.27, "ec_us_cm": 1961, "tds_mg_l": 961.3,
    "salinity_psu": 1.047, "do_mg_l": 1.33
  },
  "citation": "Aydin, G.B., Tas-Divrik, M., Atun, R. (2026) Int J Environ Sci Technol 23:621, Table 6"
}
```

## Örnek Kayıt — Sağlık Riski

```json
{
  "assessment_id": "ERG-2025-ST2-HRA",
  "location_name": "St 2 - koy ici, sanayiden uzak",
  "station_no": 2,
  "timestamp": "2025-05-15T10:00:00+03:00",
  "carcinogenic_risk": { "child": 1.097609, "adult": 1.015173 },
  "total_hazard_index": { "child": 3.050103, "adult": 2.58 },
  "basis_note": "Kanserojen risk As ve Ni uzerinden; kanserojen olmayan risk Zn, Mn, Fe, Cu, Cr uzerinden hesaplanmistir (USEPA 2004).",
  "source_type": "literature",
  "citation": "Aydin, G.B., Tas-Divrik, M., Atun, R. (2026) Int J Environ Sci Technol 23:621, Table 9"
}
```

---






### Yeni alanlar
`method` (string) · `station_no` (int, opsiyonel) · `water_quality` (obje, opsiyonel) ·
`coordinates` artık **nullable**