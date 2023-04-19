import java.io.Console;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ECDSA {
    private static final BigInteger p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
    private static final BigInteger a = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2C", 16);
    private static final BigInteger b = new BigInteger("1C97BEFC54BD7A8B65ACF89F81D4D4ADC565FA45D", 16);
    private static final BigInteger x = new BigInteger("4A96B5688EF573284664698968C38BB913CBFC82", 16);
    private static final BigInteger y = new BigInteger("23A628553168947D59DCC912042351377AC5FB32", 16);
    private static final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    private static final ECCPoint G = new ECCPoint(x, y);
    private static BigInteger privateKey;
    private static ECCPoint publicKey;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // // Generate a random private key
        // SecureRandom random = new SecureRandom();
        // privateKey = new BigInteger(n.bitLength(), random);

        // System.out.println("Private key: " + privateKey.toString(16));

        // System.out.println("G:" + G);

        // // Compute the public key
        // publicKey = G.multiplyPoint(privateKey, a, p);

        // Generate a key pair
        generateKeyPair();

        // Sign the message
        String message = "Hello, world!";
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(message.getBytes());
        BigInteger[] signature = sign(hash, privateKey);

        // Verify the signature
        boolean valid = verify(hash, signature, publicKey);
        System.out.println("Signature is " + (valid ? "valid" : "invalid"));

        // BigInteger r = publicKey.getX().mod(n);
        // MessageDigest md = MessageDigest.getInstance("SHA-256");
        // byte[] hash = md.digest("Hello, world!".getBytes());
        // BigInteger e = new BigInteger(1, hash);
        // BigInteger s = privateKey.modInverse(n).multiply(e.add(r.multiply(d))).mod(n);
        // System.out.println("r = " + r.toString(16));
        // System.out.println("s = " + s.toString(16));
    }

    public static void generateKeyPair() {
        privateKey = new BigInteger(n.bitLength(), new SecureRandom());
        publicKey = G.multiplyPoint(privateKey, a, p);
    }

    public static BigInteger[] sign(byte[] hash, BigInteger privateKey) {
        BigInteger e = new BigInteger(1, hash);
        BigInteger r, s;
        BigInteger k;
        do {
            do {
                k = new BigInteger(n.bitLength(), new SecureRandom());
            } while (k.compareTo(n) >= 0 || k.compareTo(BigInteger.ZERO) == 0);
            ECCPoint R = G.multiplyPoint(k, a, p);
            r = R.getX().mod(n);
            s = k.modInverse(n).multiply(e.add(r.multiply(privateKey))).mod(n);
        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));
        return new BigInteger[] { r, s };
    }

    public static boolean verify(byte[] hash, BigInteger[] signature, ECCPoint publicKey) {
        BigInteger r = signature[0];
        BigInteger s = signature[1];
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(n) >= 0 || s.compareTo(BigInteger.ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        BigInteger e = new BigInteger(1, hash);
        BigInteger w = s.modInverse(n);
        BigInteger u1 = e.multiply(w).mod(n);
        BigInteger u2 = r.multiply(w).mod(n);
        ECCPoint R = (G.multiplyPoint(u1, a, p)).addPoint(publicKey.multiplyPoint(u2, a, p), a, p);
        return r.equals(R.getX().mod(n));
    }
}
