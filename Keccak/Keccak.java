import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Keccak {
    final long[] RC = new long[]{
        0x0000000000000001L, 0x0000000000008082L, 0x800000000000808AL,
        0x8000000080008000L, 0x000000000000808BL, 0x0000000080000001L,
        0x8000000080008081L, 0x8000000000008009L, 0x000000000000008AL,
        0x0000000000000088L, 0x0000000080008009L, 0x000000008000000AL,
        0x000000008000808BL, 0x800000000000008BL, 0x8000000000008089L,
        0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
        0x000000000000800AL, 0x800000008000000AL, 0x8000000080008081L,
        0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    final int[][] ROT_OFFSET = new int[][]{
        {0, 36, 3, 41, 18},
        {1, 44, 10, 45, 2},
        {62, 6, 43, 15, 61},
        {28, 55, 25, 21, 56},
        {27, 20, 39, 8, 14}
    };

    public byte[] getBytesLittleEndianFromString(String msg){
        ByteBuffer buffer = ByteBuffer.allocate(msg.length()).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(msg.getBytes());
        byte[] bytes = buffer.array();
        return bytes;
    }

    public byte[] getBytesLittleEndianFromInt(int value){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        byte[] bytes = buffer.array();
        return bytes;
    }
    public byte[] getBytesLittleEndianFromLong(long value){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(value);
        byte[] bytes = buffer.array();
        return bytes;
    }

    public int stdModulo(int a, int b){
        if (a >= 0) {
            return a % b;
        } else {
            int divisionResult = a / b;
            return b * divisionResult - a;
        }
    }
    public long stdModulo(long a, long b){
        if (a >= 0) {
            return a % b;
        } else {
            long divisionResult = a / b;
            return b * divisionResult - a;
        }
    }

    public byte[] runKeccak(String message) throws IOException {
        final int digest = 256;
        final int r = 1088;
        final int c = 512;
        final int w = 360;
        
// PADDING
        byte[] Mbytes = getBytesLittleEndianFromString(message);
        String Mbits = Integer.toBinaryString(ByteBuffer.wrap(Mbytes).order(ByteOrder.LITTLE_ENDIAN).getInt());
        // d = 2^|Mbits| + sum for i=0..|Mbits|-1 of 2^i*Mbits[i]
        long sum = 0;
        int i = 0;
        // Kalau dari kiri ke kanan sum for i=0..|Mbits|-1 of 2^i*Mbits[i]
        for ( char x : Mbits.toCharArray()) {
            sum += (long) (((long) Math.pow(2, i)) * (((int)x)-48));
            i += 1;
        }
        /* Kalau dari kanan ke kiri sum for i=0..|Mbits|-1 of 2^i*Mbits[i]
        int i = Mbits.length()-1;
        for ( char x : Mbits.toCharArray()) {
            sum += (long) (((long) Math.pow(2, i)) * (((int)x)-48));
            i -= 1;
        }*/
        long d = ((long) Math.pow(2, Mbits.length())) + sum;
        
        // P = Mbytes || d || 0x00 || … || 0x00
        ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();
        tempOutputStream.write(Mbytes);
        tempOutputStream.write(getBytesLittleEndianFromLong(d));
        byte[] P = tempOutputStream.toByteArray();
        int padding_size = (int) ( ((int) Math.floorDiv(r, 8) * (int) Math.ceil((double) P.length/(double) (Math.floorDiv(r, 8)))) - P.length);
        byte[] paddingBytes = new byte[padding_size];
        Arrays.fill(paddingBytes, (byte) 0);
        tempOutputStream.write(paddingBytes);
        P = tempOutputStream.toByteArray();
        
        // P = P xor (0x00 || … || 0x00 || 0x80)
        byte[] xoring_P = new byte[P.length];
        xoring_P[xoring_P.length-1] = (byte) 128;
        for (int j=0; j < P.length; j++){
            P[j] ^= xoring_P[j];
        }
// INITIALIZATION
        long[][] S = new long[5][5];
        
        
        // ABSORBING PHASE        
        byte[][] P_blocks = new byte[(int) P.length/Math.floorDiv(r, 8)][Math.floorDiv(r, 8)];
        for (int j=0; j<P_blocks.length ; j++){
            for (int k=0; k<P_blocks[j].length; k++){
                P_blocks[j][k] = P[j*P_blocks[j].length+k];
            }
        }
        
        for (byte[] P_block : P_blocks) {
            int idx_P_block_sublock = 0;
            int countFlatten = 0;
            int j = 0;
            while (j < S.length && countFlatten < Math.floorDiv(P_block.length, 8)){
                int k = 0;
                while (k < S[j].length && countFlatten < Math.floorDiv(P_block.length, 8)){
                    if (j+5*k < r/w){
                        System.out.println("J : "+  j);
                        System.out.println("K : "+  k);
                        System.out.println();
                        byte[] Mbytes_block = new byte[8];
                        for (int l=0; l<8; l++){
                            Mbytes_block[l] =  P_block[8*idx_P_block_sublock+l];
                        }
                        S[j][k] ^= ByteBuffer.wrap(Mbytes_block).order(ByteOrder.LITTLE_ENDIAN).getLong();
                    }
                    k+=1;
                    countFlatten += 1;
                    idx_P_block_sublock += 1;
                }
                j += 1;
            }
            
            S = KeccakPermutation(S,r+c);
        }
        for (long[] s : S) {
            for (long x : s){
                System.out.print(x + " ");
            }
            System.out.println();
        }
        
        ByteArrayOutputStream tempOutputStream2 = new ByteArrayOutputStream();
        int countFlatten = 0;
        int count = 0;
        byte[] Z = tempOutputStream2.toByteArray();
        while (Z.length < Math.floorDiv(digest, 8)){
            int j = 0;
            while (j < S.length && countFlatten < Math.floorDiv(Math.floorDiv(r, 8), 8)  && Z.length < Math.floorDiv(digest, 8)){
                int k = 0;
                while (k < S[0].length && countFlatten < Math.floorDiv(Math.floorDiv(r, 8), 8) && Z.length < Math.floorDiv(digest, 8)){
                    if (j+5*k < r/w){
                        count += 1;
                        System.out.println(count );
                        tempOutputStream2.write(getBytesLittleEndianFromLong(S[j][k]) );
                        Z = tempOutputStream2.toByteArray();
                        System.out.println(Arrays.toString(Z));
                    }
                    countFlatten += 1;
                    k+=1;
                }
                j+=1;
            }
            // System.out.println(Z.length + " < " + Math.floorDiv(digest, 8));
            S = KeccakPermutation(S, r+c);
        }
        return Z;
    }
    public long[][] KeccakPermutation(long[][] A, int b){
        int n = 12 + 2 *(int) (Math.log(b/25)/Math.log(2));
        for (int i=0; i<n; i++){
            A = KeccakRound(A, RC[i], b);
        }
        return A;
    }

    public long[][] KeccakRound(long[][] A,long RCnow, int b){
// TETHA STEP
        long[] C = new long[A.length];
        for (int i=0; i<A.length; i++){
            C[i] = A[i][0] ^ A[i][1] ^ A[i][2] ^ A[i][3];
        } 
        long[] D = new long[A.length];
        for (int i=0; i<A.length; i++){
            D[i] = stdModulo(C[stdModulo(i-1, 5)] ^ rot(C[stdModulo(i+1, 5)],1) , (long) ((long)1L << (long)64L));
        }
        for (int i=0; i<A.length; i++){
            for (int j=0; j<A[i].length; j++){
                A[i][j] = A[i][j] ^ D[i];
            }
        }
// RHO AND PHI STEPS
        long[][] B = new long[A.length][A[0].length];
        for (int i=0; i<A.length; i++){
            for (int j=0; j<A[0].length; j++){
                B[j][stdModulo(2 * i + 3 * j, 5)] = rot(A[i][j], ROT_OFFSET[i][j]);
            }
        }
// CHI STEP
        for (int i=0; i<A.length; i++){
            for (int j=0; j<A[0].length; j++){
                A[i][j] = B[i][j] ^ (~B[stdModulo((i+1),5)][j] & B[stdModulo((i+2),5)][j]);
            }
        }
// IOTA STEP
        A[0][0] ^= RCnow;
        return A;
    }
    public long rot(long x, long y){
        return stdModulo(((x << y) | (x >> (64 - y))), (1 << 64));
    }
}
