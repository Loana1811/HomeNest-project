package utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyUtil {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static String formatMoney(BigDecimal money) {
        if (money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            return "0 đ";
        }
        return formatter.format(money) + " đ";
    }
}
