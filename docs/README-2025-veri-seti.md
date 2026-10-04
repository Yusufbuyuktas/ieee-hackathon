# Ergene River 2025 Dataset

## Source

Aydin, G.B., Tas-Divrik, M., Atun, R. (2026).
*Potentially toxic element contamination in water and sediments of the Ergene river basin
(Turkiye): ecological and human health risk assessment.*
International Journal of Environmental Science and Technology, 23:621.
DOI: 10.1007/s13762-026-07424-6

## Files and scope

- `data/ergene-2025-measurements.json`: 90 records, 5 stations x 9 elements x 2 mediums
- `data/ergene-2025-risk.json`: 5 ready human health risk assessments
- Mediums: `surface_water` and `sediment`
- Water unit: `mg/L`; sediment unit: `mg/kg`
- Source: `literature`

This dataset represents Ergene River water and sediment from the May 2025 sampling. It should not be merged into a single trend series with the 2013 groundwater or 2021 records.

## Data rules

- `value: null` and `below_detection_limit: true` are BDL; they are not zero.
- BDL records are excluded from average and threshold calculations.
- Water and sediment have different units; they are not compared on the same chart or against the same threshold.
- `coordinates` in this JSON are approximate coordinates.
- Approximate coordinates are marked with `coordinate_source: "approximated_from_figure"`.
- The frontend should not guess coordinate and location values from the JSON, it must use the API responses.

## Health risk rule

The CR and THI values in `ergene-2025-risk.json` are transferred from the article; the backend does not recalculate these values. Since CR values are not considered to be on the same scale across sources, the backend does not perform automatic threshold comparison on CR.

Every risk record contains `source_concluded_high_risk`. This field is the final judgment of the source and is not calculated by the backend.

The backend `risk_level` value is determined by the following rule:

```text
HIGH if total_hazard_index.child > 1.0
   or total_hazard_index.adult > 1.0
   or source_concluded_high_risk == true
NORMAL otherwise
```

Therefore, `risk_level` expresses the classification result, not whether a risk assessment exists in the record.

## Backend and FHIR behavior

The seeder idempotently loads the two files via `RiskAssessmentService.create()`. If the same `assessment_id` exists in the database, the record is not added again.

Risk assessment list:

```text
GET /api/risk-assessments
GET /api/risk-assessments?location=<location_name from API>
```

No LOINC or fabricated code is used in the FHIR `RiskAssessment` resource. The backend's current MVP FHIR resource sends the `resourceType`, `status`, `code.text`, `subject.display`, and `prediction` fields; the returned FHIR `id` is saved to the database.

## Frontend usage

- Observations: `GET /api/observations?from=2025-01-01&to=2025-12-31`
- Risk records: `GET /api/risk-assessments`
- Location dropdown: `GET /api/locations`
- The `location` parameter is an exact case-sensitive match; spaces and special characters must be encoded with `URLSearchParams` or `encodeURIComponent`.