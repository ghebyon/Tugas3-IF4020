import java.util.*;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) {
        RunCrypto runCrypto = new RunCrypto();

        try{
            System.out.println(runCrypto.encrypt("halohaloBandung"));
            System.out.println(runCrypto.decrypt("ff815e76774886f66a7baa3773fb96f3"));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

// 