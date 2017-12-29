package ir.arcinc.tls.ReliableChannel;

public class Util {
    public static byte getNextSeqNum(byte seqNum) {
        return (byte)((seqNum % 128) + 1);
    }

    public static byte byteRotateLeft(int b, int amount) {
        amount = amount % 8;
        if (amount == 0) return (byte)b;
        return (byte)((b << amount) | (b >> (8 - amount)));
    }
}
