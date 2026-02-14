
package util;
public class Validation {
    public static boolean isPositiveInt(String s) {
        try { return Integer.parseInt(s) > 0; } catch (Exception e) { return false; }
    }
    public static boolean isMoney(String s) {
        try { return Double.parseDouble(s) >= 0; } catch (Exception e) { return false; }
    }
}
