package security;

public class GenerateHash {
    public static void main(String[] args) {
     
        String adminPass = "admin123";
        String cashierPass = "cashier123";

    
        String adminHash = PasswordHasher.hash(adminPass);
        String cashierHash = PasswordHasher.hash(cashierPass);

        System.out.println("Admin Password: " + adminPass);
        System.out.println("Admin Hash: " + adminHash);

        System.out.println("Cashier Password: " + cashierPass);
        System.out.println("Cashier Hash: " + cashierHash);
    }
}
