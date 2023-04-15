import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class RoundFunction {
  public static final String[] S_BOX = new String[] {
    "ee", "e0", "9e", "56", "96", "29", "ce", "a9", "19", "14", "31", "02", "53", "d0", "2a", "ab", 
    "6e", "95", "1f", "27", "c0", "90", "85", "33", "a8", "66", "5a", "7e", "41", "40", "76", "84", 
    "8b", "fa", "81", "c8", "6b", "0f", "c3", "30", "5f", "62", "df", "3f", "de", "2f", "fc", "5d", 
    "9f", "f4", "0d", "e1", "b3", "f2", "92", "e5", "34", "d1", "06", "28", "ca", "c5", "60", "c9", 
    "9a", "15", "13", "86", "d6", "7b", "d9", "8a", "25", "3b", "8c", "99", "e6", "f9", "da", "42", 
    "35", "8f", "a7", "70", "cf", "22", "7f", "75", "97", "e2", "9d", "2c", "b2", "ac", "0e", "88", 
    "3a", "a6", "be", "f6", "7c", "a3", "b9", "26", "00", "a2", "ad", "93", "17", "51", "b4", "d3", 
    "91", "a4", "d4", "83", "77", "69", "a0", "ff", "0c", "e4", "a5", "43", "c2", "b0", "46", "1c", 
    "67", "e9", "68", "6f", "09", "49", "2b", "47", "dd", "d2", "fe", "54", "01", "04", "38", "10", 
    "72", "b1", "4a", "23", "4b", "c7", "dc", "89", "21", "aa", "94", "52", "71", "61", "45", "24", 
    "74", "44", "57", "6d", "0a", "03", "0b", "11", "f7", "ef", "82", "18", "e8", "39", "48", "f5", 
    "b7", "d7", "bb", "50", "20", "8d", "37", "4e", "07", "b6", "cb", "ae", "78", "bc", "55", "b8", 
    "6c", "af", "2d", "ec", "f3", "65", "1a", "59", "f1", "f0", "9b", "fb", "73", "cd", "3c", "bf", 
    "2e", "a1", "4f", "05", "d5", "5b", "8e", "3e", "fd", "5e", "c1", "12", "db", "4d", "6a", "eb", 
    "f8", "bd", "63", "e7", "9c", "ed", "b5", "7d", "5c", "58", "c6", "7a", "79", "64", "32", "c4", 
    "98", "4c", "cc", "87", "e3", "d8", "08", "16", "1d", "1e", "36", "ba", "3d", "1b", "ea", "80"
  };

  public static final int[] P_BOX = new int[] {
    29, 14, 25,  7, 15,  3, 27,  1, 
    31, 22,  9, 21, 10,  4, 13, 11, 
    16,  8, 20, 28, 30,  2, 23, 12, 
    26, 18,  5, 19,  0, 24, 17,  6
  };

  public byte[] plaintext;
  public byte[] ciphertext;
  private List<String> keySpace;
  private byte[] key;
  private int round;
  private boolean mode;

  public RoundFunction(byte[] text, List<String> key, int round, boolean mode){
    this.plaintext = mode ? text : null;
    this.ciphertext = mode ? null : text;
    this.keySpace = key;
    this.key = null;
    this.round = round;
    this.mode = mode;
  }

  public void encrypt(){
    byte[] cipher = this.plaintext;
    for (int i = 0; i < this.round; i++){
      this.key = hexToByte(this.keySpace.get(i + 1).substring(2));
      cipher = this.feistelRound(cipher);
    }
    this.ciphertext = cipher;
  }



  public void decrypt(){
    byte[] plain = this.ciphertext;
    for (int i = 0; i < this.round; i++){
      this.key = hexToByte(this.keySpace.get(this.round - 1).substring(2));
      plain = this.feistelRound(plain);
    }
    this.plaintext = plain;
  }


  public byte[] hexToByte(String hex){
    byte[] res = new byte[hex.length()/2];
    for (int i = 0; i < res.length; i++){
      int idx = i * 2;
      int val = Integer.parseInt(hex.substring(idx, idx+2), 16);
      res[i] = (byte) val;
    }
    return res;
  }
  
  public byte[] feistelRound(byte[] input){
    if(this.mode){ //encrypt
      int halfLen = this.plaintext.length / 2;
      byte[] leftPlain = Arrays.copyOfRange(input, 0, halfLen);
      byte[] rightPlain = Arrays.copyOfRange(input, halfLen, this.plaintext.length);
      byte[] cipher = new byte[this.plaintext.length];

      byte[] xorResult = xorBytes(this.roundFunction(rightPlain), leftPlain);
      cipher = concat(rightPlain, xorResult);

      return cipher;

    }else{
      int halfLen = this.ciphertext.length / 2;
      byte[] leftCipher = Arrays.copyOfRange(input, 0, halfLen);
      byte[] rightCipher = Arrays.copyOfRange(input, halfLen, this.ciphertext.length);
      byte[] plain = new byte[this.ciphertext.length];

      byte[] xorBytes = xorBytes(this.roundFunction(leftCipher), rightCipher);
      plain = concat(xorBytes, leftCipher);

      return plain;

    }
  }


  public byte[] roundFunction(byte[] input){
    byte[] result = this.switchBits(input);
    result = this.expandBits(result);
    result = xorBytes(input, this.key);
    result =this.substitution(result);
    result = this.permutation(result);
    return result;
  }


  public byte[] switchBits(byte[] input){
    BigInteger evenPair = (new BigInteger(1, input)).and(new BigInteger("CCCCCCCCCCCCCCCC", 16));
    BigInteger oddPair = (new BigInteger(1, input)).and(new BigInteger("3333333333333333", 16));
    byte[] result = (evenPair.shiftRight(1).or(oddPair.shiftLeft(1))).toByteArray();
    return result;
  }


  public byte[] expandBits(byte[] input){
    byte[] expanded = new byte[0];
    BigInteger inputInt = new BigInteger(1, input);

    for (int i = 0; i < inputInt.bitLength(); i+= 4){
      int chunk1 = inputInt.shiftRight(i).and(new BigInteger("1111", 2)).intValue();
      int chunk2 = inputInt.shiftRight(((i - 4) % 64)).and(new BigInteger("1111", 2)).intValue();
      int subtext1 = chunk1;
      int subtext2 = chunk1 ^ chunk2;
      
      byte[] bytes = new byte[]{(byte) ((subtext1 << 4) + subtext2)};
      expanded = concat(expanded, bytes);
    }
    expanded = reverseByte(expanded);
    return expanded;
  }

  public byte[] substitution(byte[] input){
    byte[] result = new byte[0];
    BigInteger inputInt = new BigInteger(1, input);

    for (int i = 0; i < inputInt.bitLength(); i+=8){
      int chunk1 = inputInt.shiftRight(i).and(new BigInteger("1111", 2)).intValue();
      int chunk2 = inputInt.shiftRight((i + 4) % 128).and(new BigInteger("1111", 2)).intValue();
      int index = chunk2 * 16 + chunk1;
      byte[] bytes = new byte[]{(byte) (Integer.parseInt(S_BOX[index], 16))};
      result = concat(result, bytes);
    }

    result = reverseByte(result);
    return result;
  }

  private byte[] reverseByte(byte[] array) {
    byte[] reversedArray = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
        reversedArray[array.length - 1 - i] = array[i];
    }
    return reversedArray;
  }
  

  public byte[] permutation(byte[] input){
    byte[] result = new byte[0];
    BigInteger inputInt = new BigInteger(1, input);
    int[] permutation = new int[P_BOX.length];
    for(int i = 0; i < P_BOX.length; i++){
      permutation[i] = (inputInt.shiftRight((4*P_BOX[i]))).and(new BigInteger("1111", 2)).intValue();
    }
    permutation = reverse(permutation);
  
    int[] reduce = new int[16];
    for (int i = 0; i < 16; i++){
      reduce[i] = permutation[i] ^ permutation[31-i];
    }

    for (int i = 0; i < reduce.length; i += 2) {
      byte[] bytes = new byte[]{(byte) ((reduce[i] << 4) + reduce[i+1])};
      result = concat(result, bytes);
    }

    return result;
  }

  private int[] reverse(int[] array) {
    int[] reversedArray = new int[array.length];
    for (int i = 0; i < array.length; i++) {
        reversedArray[array.length - 1 - i] = array[i];
    }
    return reversedArray;
  }

  public byte[] concat(byte[] a, byte[] b) {
    byte[] result = new byte[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }

  public static byte[] xorBytes(byte[] b1, byte[] b2){
    BigInteger i1 = new BigInteger(1, b1);
    BigInteger i2 = new BigInteger(1, b2);
    BigInteger result =  i1.xor(i2);
    byte[] res = result.toByteArray();
    return res;
  }
}
