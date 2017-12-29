package ir.arcinc.tls.ReliableChannel;

public class Util {
    public static byte getNextSeqNum(byte seqNum) {
        return (seqNum == (byte)0 ? (byte)1 : (byte)0);
    }

    public static byte byteRotateLeft(int b, int amount) {
        amount = amount % 8;
        if (amount == 0) return (byte)b;
        return (byte)((b << amount) | (b >> (8 - amount)));
    }
}
