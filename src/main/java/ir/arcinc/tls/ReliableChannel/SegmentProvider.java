package ir.arcinc.tls.ReliableChannel;

import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class SegmentProvider {

    //private byte[] message;
    private byte[] segmentNoChecksum;
    private byte checksum;
    private byte checksum2;
    private static final int CHKSUM_LEN = 2;
    public SegmentProvider(byte seqNum, byte[] message) {
        this.segmentNoChecksum = new byte[message.length + 1];
        segmentNoChecksum[0] = seqNum;
        System.arraycopy(message, 0, segmentNoChecksum, 1, message.length);
        this.checksum = checksum(segmentNoChecksum, 0);
        this.checksum2 = checksum2(segmentNoChecksum, 0);
    }

    public SegmentProvider(byte[] segment) throws IllegalArgumentException {
        if (!validSegment(segment)) throw new IllegalArgumentException();
        this.segmentNoChecksum = Arrays.copyOfRange(segment, 0, segment.length - CHKSUM_LEN);
        this.checksum = segment[segment.length - 2];
        this.checksum2 = segment[segment.length - 1];
    }

    public byte getSeqNum() {
        return segmentNoChecksum[0];
    }

    public byte[] getSegment() {
        byte[] segment = new byte[this.segmentNoChecksum.length + CHKSUM_LEN];
        System.arraycopy(segmentNoChecksum, 0, segment, 0, segmentNoChecksum.length);
        segment[segment.length - 2] = checksum;
        segment[segment.length - 1] = checksum2;
        return segment;
    }

    public byte[] getMessage() {
        return Arrays.copyOfRange(segmentNoChecksum, 1, segmentNoChecksum.length);
    }

    // test
    public static void main(String[] args) {
        SegmentProvider s;

        // all must be true
        s = new SegmentProvider((byte) 0, new byte[0]);
        System.out.println(validSegment(s.getSegment()));

        s = new SegmentProvider((byte) 1, new byte[0]);
        System.out.println(validSegment(s.getSegment()));

        s = new SegmentProvider((byte) 0, new byte[] {(byte)1, (byte)2});
        System.out.println(validSegment(s.getSegment()));

        s = new SegmentProvider((byte) 0, new byte[] {(byte)-12, (byte)-128});
        System.out.println(validSegment(s.getSegment()));

    }

    public static byte checksum(byte[] data, int endOffset) {
        if (data.length <= endOffset) return 0;
        int sum = 0;
        for (int i = 0; i < data.length - endOffset; i++) {
            sum += data[i];
        }
        return (byte)(~sum);
    }

    public static byte checksum2(byte[] data, int endOffset) {
        if (data.length <= endOffset) return 0;
        byte sum = data[0];
        for (int i = 1; i < data.length - endOffset - 1; i++) {
            sum ^= Util.byteRotateLeft(data[i] + data[data.length - endOffset - i], data[i - 1]);
        }
        sum += Util.byteRotateLeft(data[data.length - endOffset - 1], data.length - endOffset);
        return (byte)(~sum);
    }

//    public static byte checksum2(byte[] data, int endOffset) {
//        byte[] message = Arrays.copyOfRange(data, 0, data.length - endOffset);
//        Checksum checksum = new CRC32();
//        checksum.update(message, 0, message.length);
//        return (byte) ((checksum.getValue() % 256) - 128);
//    }

    public static boolean validSegment(byte[] segment) {
        return(checksum(segment, 2) == segment[segment.length - 2])
        && (checksum2(segment, 2) == segment[segment.length - 1]);
    }
}
