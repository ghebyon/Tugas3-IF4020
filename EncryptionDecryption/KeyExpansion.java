import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class KeyExpansion {
  private String seed;
  private String externalKey;
  private int countRound;
  public List<String> roundKey;
  private String[][] rawCurrentKey;
  private List<Integer> s_box;

  public KeyExpansion(String externalKey, int countRound){
    this.seed = externalKey;
    this.externalKey = externalKey.substring(0, 16);
    this.countRound = countRound;
    this.roundKey = new ArrayList<>();
    this.rawCurrentKey = new String[4][4];
    this.s_box = new ArrayList<>();
    this.initCurrentKey();
    this.generateSBox();
    this.concatCurrentKey();
  }

  private void initCurrentKey(){
    int count = 0;
    int idx = 0;
    String[] temp = new String[4];
    for (int i = 0; i < this.externalKey.length(); i++){
      if(count != 4){
        temp[count] = Integer.toHexString((int) externalKey.charAt(i)).substring(0, 2);
        count++;
      }else{
        this.rawCurrentKey[idx] = temp;
        idx++;
        temp = new String[4];
        temp[0] = Integer.toHexString((int) externalKey.charAt(i));
        count = 1;
      }
    }
    this.rawCurrentKey[3] = temp;
  }

  private void generateSBox(){
    List<Integer> temp_box = new ArrayList<>();
    MessageDigest digest = null;
    try{
      digest = MessageDigest.getInstance("SHA-256");
    }catch(NoSuchAlgorithmException e){
      System.out.println(e.toString());
    }

    for (int i = 0; i < 256; i++) {
      byte[] bytes = (this.seed + (char) i).getBytes();
      byte[] hash = digest.digest(bytes);
      String hashString = bytesToHex(hash);
      int candidateElmt = Integer.parseInt(hashString.substring(0, 2), 16);
      if (!temp_box.contains(candidateElmt)) {
          this.s_box.add(candidateElmt);
          temp_box.add(candidateElmt);
      }
    }
    
    for (int i = 0; i < 256; i++) {
        if (!temp_box.contains(i)) {
            this.s_box.add(i);
            temp_box.add(i);
        }
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  private void concatCurrentKey(){
    String concat = "0x";
    for(String[] outer : this.rawCurrentKey){
      for(String inner : outer){
        concat += inner;
      }
    }
    this.roundKey.add(concat);
  }

  private List<String[]> shiftKey(){
    List<String[]> shiftedKey = new ArrayList<>();
    shiftedKey.add(this.rawCurrentKey[0]);
    for (int i = 1; i < this.rawCurrentKey.length; i++) {
        String[] row = this.rawCurrentKey[i];
        String[] shiftedRow = new String[row.length];
        System.arraycopy(row, i, shiftedRow, 0, row.length - i);
        System.arraycopy(row, 0, shiftedRow, row.length - i, i);
        shiftedKey.add(shiftedRow);
    }
    return shiftedKey;
  }

  private List<String[]> substitution() {
    List<String[]> shiftedKey = this.shiftKey();
    List<String[]> substitutionKey = new ArrayList<>();
    for (String[] outer : shiftedKey) {
        String[] temp = new String[outer.length];
        for (int i = 0; i < outer.length; i++) {
            temp[i] = Integer.toHexString(this.s_box.get(Integer.parseInt(outer[i], 16)));
        }
        substitutionKey.add(temp);
    }
    return substitutionKey;
  }

  private List<List<String>> xorSubAndCurrent() {
    List<String[]> subsKey = this.substitution();
    List<String> temp = new ArrayList<>();
    List<List<String>> xorKey = new ArrayList<>();

    for (int i = 0; i < subsKey.get(0).length; i++) {
        int xorValue = Integer.parseInt(subsKey.get(0)[i], 16) ^ Integer.parseInt(subsKey.get(3)[i], 16) ^ i;
        if (Integer.toHexString(xorValue).length() == 1) {
            temp.add("0" + Integer.toHexString(xorValue));
        } else {
            temp.add(Integer.toHexString(xorValue));
        }
    }
    xorKey.add(temp);
    temp = new ArrayList<>();

    for (int i = 1; i < subsKey.size(); i++) {
        for (int j = 0; j < subsKey.get(i).length; j++) {
            int xorValue = Integer.parseInt(subsKey.get(i)[j], 16) ^ Integer.parseInt(xorKey.get(xorKey.size()-1).get(j), 16) ^ j;
            if (Integer.toHexString(xorValue).length() == 1) {
                temp.add("0" + Integer.toHexString(xorValue));
            } else {
                temp.add(Integer.toHexString(xorValue));
            }
        }
        xorKey.add(temp);
        temp = new ArrayList<>();
    }

    return xorKey;
  }

  public void makeRoundKey(){
    for (int p = 0; p < this.countRound; p++){
      List<List<String>> xorKey = this.xorSubAndCurrent();
      String[][] array = new String[xorKey.size()][];
      for (int i = 0; i < xorKey.size(); i++) {
          List<String> innerList = xorKey.get(i);
          array[i] = new String[innerList.size()];
          for (int j = 0; j < innerList.size(); j++) {
              array[i][j] = innerList.get(j);
          }
      }

      this.rawCurrentKey = array;
      this.concatCurrentKey();
    }
  }
}
