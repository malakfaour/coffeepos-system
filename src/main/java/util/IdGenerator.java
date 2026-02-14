
package util;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
public class IdGenerator {
    public static String orderCode() {
    	return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    		       + "-" + (int)(Math.random() * 900 + 100);

    }
}
