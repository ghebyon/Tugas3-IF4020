import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Permutation {
  
  public Permutation(){
    //
  }

  public List<Integer> generatePBox(String seed, int size){
    List<Integer> p_box = new ArrayList<>();
    MessageDigest digest = null;

    for (int i = 0; i < size; i++){
      p_box.add(i, i);
    }
    try{
      digest = MessageDigest.getInstance("SHA-256");
    }catch(NoSuchAlgorithmException e){
      System.out.println(e.toString());
    }

    for (int i = 0; i <  size; i++){
      byte[] bytes = (seed + (char) i).getBytes();
      byte[] hash = digest.digest(bytes);
      String hashString = bytesToHex(hash);

      BigDecimal bigDecimal = new BigDecimal(new BigInteger(hashString, 16));
      BigDecimal bdJ = bigDecimal.remainder(new BigDecimal(size-i));

      int j = bdJ.intValue();
      int temp = p_box.get(i);
      p_box.set(i, p_box.get(j));
      p_box.set(j, temp);
    }

    return p_box;
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  public byte[] permute(byte[] string_byte, List<Integer> p_box) {
      byte[] byte_list = string_byte;
      StringBuilder string_bit_builder = new StringBuilder();
      for (byte b : byte_list) {
          string_bit_builder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
      }
      String string_bit = string_bit_builder.toString();

      StringBuilder permuted_block_builder = new StringBuilder();
      for (int i = 0; i < p_box.size(); i++) {
        int idxTemp = p_box.get(i)-1;
        if(idxTemp == -1){
          idxTemp = p_box.size()-1;
        }
        permuted_block_builder.append(string_bit.charAt(idxTemp));
      }
      String permuted_block = permuted_block_builder.toString();

      byte[] result_byte_string = new byte[byte_list.length];
      for (int i = 0; i < result_byte_string.length; i++) {
          String bit = permuted_block.substring(i*8, i*8+8);
          result_byte_string[i] = (byte) Integer.parseInt(bit, 2);
      }

      return result_byte_string;
  }

  public byte[] netralize(byte[] stringByte, List<Integer> p_box) {
    List<Byte> byteList = new ArrayList<Byte>();
    for (byte b : stringByte) {
        byteList.add(b);
    }

    StringBuilder string_bit_builder = new StringBuilder();
    for (Byte b : byteList) {
      string_bit_builder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
    }
    String string_bit = string_bit_builder.toString();
    
    char[] backagainBlock = new char[128];
    Arrays.fill(backagainBlock, '0');
    for (int i = 0; i < p_box.size(); i++) {
        int idxTemp = p_box.get(i)-1;
        if(idxTemp == -1){
          idxTemp = p_box.size()-1;
        }
        backagainBlock[idxTemp] = string_bit.charAt(i);
    }
    
    String resultStringBit = new String(backagainBlock);
    
    List<String> bitList = new ArrayList<String>();
    for (int i = 0; i < resultStringBit.length(); i += 8) {
        bitList.add(resultStringBit.substring(i, Math.min(i + 8, resultStringBit.length())));
    }
    byte[] resultByteString = new byte[bitList.size()];
    for (int i = 0; i < bitList.size(); i++) {
        resultByteString[i] = (byte) Integer.parseInt(bitList.get(i), 2);
    }

    return resultByteString;
  }

}
