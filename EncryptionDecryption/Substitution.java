import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Substitution {
  

  public Substitution(){
    //
  }

  public List<Integer> generateSBox(String seed, int size){
    List<Integer> s_box = new ArrayList<>();
    MessageDigest digest = null;
    try{
      digest = MessageDigest.getInstance("SHA-256");
    }catch(NoSuchAlgorithmException e){
      System.out.println(e.toString());
    }

    for (int i = 0; i < size; i++) {
      byte[] bytes = (seed + (char) i).getBytes();
      byte[] hash = digest.digest(bytes);
      String hashString = bytesToHex(hash);
      int candidateElmt = Integer.parseInt(hashString.substring(0, 2), 16);
      if (!s_box.contains(candidateElmt)) {
          s_box.add(candidateElmt);
      }
    }
    
    for (int i = 0; i < size; i++) {
        if (!s_box.contains(i)) {
            s_box.add(i);
        }
    }

    return s_box;
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  public List<Integer> inverseSBox(List<Integer> s_box){
    List<Integer> inverse = new ArrayList<>();

    for(int i = 0; i < s_box.size(); i++){
      inverse.add(s_box.indexOf(i));
    }

    return inverse;
  }

  public byte[] substitute(byte[] string_byte, List<Integer> s_box){
    byte[] result = new byte[string_byte.length];
    int i = 0;
    for(byte s :  string_byte){
      result[i] = s_box.get(s & 0xff).byteValue();
      i++;
    }

    return result;
  }

  public byte[] reverse(byte[] string_byte, List<Integer> s_box){
    byte[] result = new byte[string_byte.length];
    int i = 0;
    for(byte s :  string_byte){
      result[i] = (byte) s_box.indexOf(s & 0xff);
      i++;
    }

    return result;
  }
}
