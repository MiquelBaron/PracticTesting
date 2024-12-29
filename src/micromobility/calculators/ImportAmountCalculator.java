package micromobility.calculators;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class ImportAmountCalculator {
    private static final BigDecimal PRICEDISTANCE = new BigDecimal(3);
    private static final BigDecimal PRICETIME = new BigDecimal(4);
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
    private static final BigDecimal FINE_PERCENTAGE = new BigDecimal("0.05");
    private static final int SPEED_LIMIT = 40;


    public static BigDecimal calculateImport(int duration, float distance, float avgSpeed) {
        BigDecimal durationPrice = BigDecimal.valueOf(duration).multiply(PRICETIME);
        BigDecimal distancePrice = BigDecimal.valueOf(distance).max(PRICEDISTANCE);
        BigDecimal total = durationPrice.add(distancePrice);

        // Aplicar descuento en fines de semana
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            total = total.subtract(total.multiply(DISCOUNT_PERCENTAGE));
        }

        // Aplicar multa por exceso de velocidad
        if (avgSpeed > SPEED_LIMIT) {
            total = total.add(total.multiply(FINE_PERCENTAGE));
        }

        return total;
    }
}
