
package security;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(12));
    }
    public static boolean verify(String plaintext, String hash) {
        return BCrypt.checkpw(plaintext, hash);
    }
    
}
