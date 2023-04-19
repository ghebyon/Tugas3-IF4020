import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class RunCrypto {

    public static String hexToString(String hexString) {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < hexString.length(); i += 2) {
          String str = hexString.substring(i, i + 2);
          char ch = (char) Integer.parseInt(str, 16);
          stringBuilder.append(ch);
      }
      return stringBuilder.toString();
    }

    public String encrypt(String message) throws Exception{
        
      // Menjalankan script Python dari Java
      Process process = Runtime.getRuntime().exec("python main.py encrypt " + message);
      
      // Menerima output dari script Python
      InputStream inputStream = process.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String output = bufferedReader.readLine();
      
      // Menampilkan hasil output dari script Python
      return hexToString(output);
    }

    public String decrypt(String message) throws Exception{
        
      // Menjalankan script Python dari Java
      Process process = Runtime.getRuntime().exec("python main.py decrypt " + message);
      
      // Menerima output dari script Python
      InputStream inputStream = process.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String output = bufferedReader.readLine();
      
      // Menampilkan hasil output dari script Python
      return hexToString(output);
    }
  
}
