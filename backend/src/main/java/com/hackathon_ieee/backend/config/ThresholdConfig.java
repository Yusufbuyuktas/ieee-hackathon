package com.hackathon_ieee.backend.config;

import com.hackathon_ieee.backend.enums.Parameter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Icme suyu esik degerleri. Kaynak: Arkoc, O. (2014) Bull Environ Contam Toxicol
 * 93:429-433, Table 2 (TS 2005, WHO 2006, EPA 2013).

 * ONEMLI KISITLAR (uydurma deger eklenmedi, servis katmani bunlara saygi gostermeli):
 *  1) SADECE su orneklerine (SampleType.supportsDrinkingWaterThresholds() == true) uygulanir.
 *     Sediman (mg/kg) icin bu esikler GECERSIZ - farkli bir olcek/cerceve gerekir.
 *  3) ZINC icin TS_2005'te tanimli deger yok (tablo bos birakmis) - o standart o
 *     parametre icin atlanir, digerleri (WHO/EPA) gecerlidir.
 */
@Component
public class ThresholdConfig {

    public enum Standard {
        TS_2005, WHO_2006, EPA_2013
    }

    private final Map<Parameter, Map<Standard, Double>> thresholds = new EnumMap<>(Parameter.class);

    public ThresholdConfig() {
        put(Parameter.COPPER, Standard.TS_2005, 2.0);
        put(Parameter.COPPER, Standard.WHO_2006, 2.0);
        put(Parameter.COPPER, Standard.EPA_2013, 1.3);

        put(Parameter.IRON, Standard.TS_2005, 0.2);
        put(Parameter.IRON, Standard.WHO_2006, 0.3);
        put(Parameter.IRON, Standard.EPA_2013, 0.3);

        // TS_2005 icin zinc esigi kaynakta tanimli degil - kasitli olarak eklenmedi
        put(Parameter.ZINC, Standard.WHO_2006, 3.0);
        put(Parameter.ZINC, Standard.EPA_2013, 5.0);

        put(Parameter.CHROMIUM, Standard.TS_2005, 0.05);
        put(Parameter.CHROMIUM, Standard.WHO_2006, 0.05);
        put(Parameter.CHROMIUM, Standard.EPA_2013, 0.1);

        put(Parameter.CADMIUM, Standard.TS_2005, 0.005);
        put(Parameter.CADMIUM, Standard.WHO_2006, 0.003);
        put(Parameter.CADMIUM, Standard.EPA_2013, 0.005);

        put(Parameter.LEAD, Standard.TS_2005, 0.01);
        put(Parameter.LEAD, Standard.WHO_2006, 0.01);
        put(Parameter.LEAD, Standard.EPA_2013, 0.015);

        put(Parameter.ARSENIC, Standard.TS_2005, 0.01);
        put(Parameter.ARSENIC, Standard.WHO_2006, 0.01);
        put(Parameter.ARSENIC, Standard.EPA_2013, 0.01);

        put(Parameter.MANGANESE, Standard.TS_2005, 0.05);
        put(Parameter.MANGANESE, Standard.WHO_2006, 0.08);
        put(Parameter.MANGANESE, Standard.EPA_2013, 0.05);

        put(Parameter.NICKEL, Standard.TS_2005, 0.02);
        put(Parameter.NICKEL, Standard.WHO_2006, 0.07);
        put(Parameter.NICKEL, Standard.EPA_2013, 0.10);

    }

    private void put(Parameter parameter, Standard standard, double value) {
        thresholds.computeIfAbsent(parameter, k -> new EnumMap<>(Standard.class)).put(standard, value);
    }

    /** Parametre icin tanimli esik haritasini döndürür (standart -> deger), yoksa null. */
    public Map<Standard, Double> getThresholds(Parameter parameter) {
        return thresholds.get(parameter);
    }

    /** Bu parametre icin en az bir dogrulanmis esik tanimli mi? */
    public boolean hasThreshold(Parameter parameter) {
        return thresholds.containsKey(parameter);
    }
}
