import java.util.*;
import java.nio.charset.StandardCharsets;

public class App {
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
    }
}
// 