import java.util.*;
import java.nio.charset.StandardCharsets;

public class App {
    public static byte[][] splitText(byte[] plaintext) {
        // Split plaintext into 16 bytes chunks
        int numChunks = (int) Math.ceil((double) plaintext.length / 16);
        byte[][] chunks = new byte[numChunks][16];
        for (int i = 0; i < numChunks; i++) {
            int start = i * 16;
            int end = Math.min(start + 16, plaintext.length);
            byte[] chunk = Arrays.copyOfRange(plaintext, start, end);
            chunk = Arrays.copyOf(chunk, 16);
            for (int j = chunk.length; j < 16; j++) {
                chunk[j] = 0x00;
            }
            chunks[i] = chunk;
        }
        return chunks;
    }

    public static void main(String[] args) throws Exception {
        KeyExpansion kExpansion = new KeyExpansion("H-2 Menuju UTS Semangat Gaes!", 16);
        kExpansion.makeRoundKey();
        // for(int i = 0; i < kExpansion.roundKey.size(); i++){
        //         System.out.println(kExpansion.roundKey.get(i));
        // }

        Permutation permutation = new Permutation();
        // for(int i = 0; i < kExpansion.roundKey.size(); i++){
        //     System.out.println(kExpansion.roundKey.get(i).substring(2, kExpansion.roundKey.get(i).length()));
        // }
        List<Integer> p_box = permutation.generatePBox("H-2 Menuju UTS Semangat", 128);
        // for(int i = 0; i < p_box.size(); i++){
        //     System.out.print(p_box.get(i) + ",");
        // }
        byte[] byteArray = new byte[] { (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef, (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
        
        byte[] x = permutation.permute(byteArray, p_box);

        byte[] y = permutation.netralize(x, p_box);
        

        Substitution substitution = new Substitution();

        // System.out.println("SUBSTITUTION");
        List<Integer> s_box = substitution.generateSBox("H-2 Menuju UTS Semangat Gaes!", 256);

        List<Integer> i_s_box = substitution.inverseSBox(s_box);

        byte[] subs = substitution.substitute(byteArray, s_box);

        byte[] reverse = substitution.reverse(
            subs, s_box);
        // for (int i=0; i < reverse.length; i++){
        //     System.out.print(reverse[i]+ ", ");
        // }

        RoundFunction roundFunction = new RoundFunction("halokamusiapahah".getBytes(), kExpansion.roundKey, 16, true);
        roundFunction.encrypt();
        System.out.println(new String(roundFunction.ciphertext));
        RoundFunction roundFunction1 = new RoundFunction(roundFunction.ciphertext, kExpansion.roundKey, 16, false);
        roundFunction1.decrypt();
        System.out.println(new String(roundFunction.plaintext, StandardCharsets.UTF_8));

        // byte[] cipher = new byte[0];
        // while (true){
        //     System.out.println("Go-Block-Cipher");
        //     System.out.println("===============");
        //     System.out.println("1. Encrypt");
        //     System.out.println("2. Decrypt");
        //     System.out.println("3. Exit");
        //     System.out.println("===============");
        //     System.out.print("Choice : ");
        //     Scanner sc= new Scanner(System.in);
        //     int choice = sc.nextInt();
        //     System.out.println("===============");

        //     if(choice == 1){
        //         System.out.print("Key : ");
        //         String key = sc.next();
        //         System.out.print("Plaintext : ");
        //         String plaintext = sc.next();
        //         byte[] bytePlainText = plaintext.getBytes();

        //         //Key Expansion
        //         KeyExpansion keyExpansion = new KeyExpansion(key, 16);
        //         keyExpansion.makeRoundKey();

        //         byte[][] plaintextChunks = splitText(bytePlainText);
        //         byte[] ciphertext = new byte[0];

        //         Permutation permutation = new Permutation();
        //         Substitution substitution = new Substitution();

        //         List<Integer> pBox = permutation.generatePBox(key, 128);
        //         List<Integer> sBox = substitution.generateSBox(key, 256);
                
        //         int i = 0;
        //         for (byte[] pln : plaintextChunks){
        //             byte[] chunk = permutation.permute(pln, pBox);

        //             RoundFunction roundFunction = new RoundFunction(chunk, keyExpansion.roundKey, 16, true);
        //             roundFunction.encrypt();

        //             byte[] ciphertext_chunks = roundFunction.ciphertext;

        //             ciphertext_chunks = substitution.substitute(ciphertext_chunks, sBox);

        //             ciphertext = roundFunction.concat(ciphertext, ciphertext_chunks);

        //             i++;
        //         }
        //         cipher = ciphertext;
        //         System.out.println( "Ciphetext : " + new String(ciphertext, StandardCharsets.UTF_8));
        //     }
        //     else if(choice == 2){
        //         System.out.print("Key : ");
        //         String key = sc.next();

        //         KeyExpansion keyExpansion = new KeyExpansion(key, 16);
        //         keyExpansion.makeRoundKey();

        //         byte[][] ciphertext_chunks = splitText(cipher);
        //         byte[] plaintext = new byte[0];

        //         Permutation permutation = new Permutation();
        //         Substitution substitution = new Substitution();

        //         List<Integer> pBox = permutation.generatePBox(key, 128);
        //         List<Integer> sBox = substitution.generateSBox(key, 256);

        //         int i = 0;
        //         for(byte[] cpr : ciphertext_chunks){
        //             byte[] chunk = substitution.reverse(cpr, sBox);

        //             RoundFunction roundFunction = new RoundFunction(chunk, keyExpansion.roundKey, 16, false);
        //             roundFunction.decrypt();

        //             byte[] plaintextChunks = roundFunction.plaintext;

        //             plaintextChunks = permutation.netralize(plaintextChunks, pBox);

        //             plaintext = roundFunction.concat(plaintext, plaintextChunks);

        //             i++;
        //         }

        //         System.out.println("Plaintext : " + new String(plaintext, StandardCharsets.UTF_8));
        //     }
        // }

    }
}
// 