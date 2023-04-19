import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class runKeccak {
   public static void main(String[] args) throws IOException {
        // Pesan yang akan dikirimkan ke Python
        String message = "Hello ayang";
        
        // Menjalankan script Python dari Java
        Process process = Runtime.getRuntime().exec("python Keccak.py " + message);
        
        // Menerima output dari script Python
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String output = bufferedReader.readLine();
        
        // Menampilkan hasil output dari script Python
        System.out.println(output);
   } 
}
