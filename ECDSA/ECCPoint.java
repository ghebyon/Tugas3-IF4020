import java.math.BigInteger;

public class ECCPoint {
    private final BigInteger x;
    private final BigInteger y;
    public static final ECCPoint INFINITY = new ECCPoint(null, null);

    public ECCPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public boolean equalsPoint(Object o) {
        System.out.println(o);
        if (!(o instanceof ECCPoint) || o == null) {
            return false;
        }
        if (this == INFINITY) {
            return o == INFINITY;
        }
        // System.out.println(o);
        ECCPoint p = (ECCPoint) o;
        return x.equals(p.x) && y.equals(p.y);
    }

    public ECCPoint addPoint(ECCPoint Q, BigInteger a, BigInteger p) {
        if (this.equalsPoint(INFINITY)) {
            return Q;
        }
        System.out.println("Q: "+Q);
        if (Q.equalsPoint(INFINITY)) {
            return this;
        }
        BigInteger slope;
        if (this.equalsPoint(Q)) {
            slope = x.multiply(x).multiply(new BigInteger("3")).add(a).multiply(y.multiply(new BigInteger("2")).modInverse(p));
        } else {
            slope = Q.getY().subtract(y).multiply(Q.getX().subtract(x).modInverse(p));
        }

        BigInteger x3 = slope.multiply(slope).subtract(x).subtract(Q.getX()).mod(p);
        BigInteger y3 = slope.multiply(x.subtract(x3)).subtract(y).mod(p);
        return new ECCPoint(x3, y3);
    }

    public ECCPoint multiplyPoint(BigInteger k, BigInteger a, BigInteger p) {
        ECCPoint result = INFINITY;
        byte[] kBytes = k.toByteArray();
        // for (int i = 0; i < kBytes.length; i++) {
        //     for (int j = 0; j < 8; j++) {
        //         result = result.addPoint(result, a, p);
        //         if ((kBytes[i] & (1 << j)) != 0) {
        //             result = result.addPoint(this, a, p);
        //         }
        //     }
        // }
        for (int i = 0; i < kBytes.length* 8; i++) {
            if ((kBytes[kBytes.length - 1 - i / 8] & (1 << (i % 8))) != 0) {
                result = result.addPoint(this, a, p);
            }
            result = result.addPoint(result, a, p);
        }
        return result;
    }

    public String toString() {
        if (x == null || y == null) {
            return "POINT_AT_INFINITY";
        }
        return "(" + x.toString(16) + ", " + y.toString(16) + ")";
    }
}
