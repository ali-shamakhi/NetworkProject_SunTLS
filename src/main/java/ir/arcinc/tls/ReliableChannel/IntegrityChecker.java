package ir.arcinc.tls.ReliableChannel;

import java.util.Arrays;

public class IntegrityChecker {
//    private static byte[][] message = new byte[2][0];
    private static byte[] message0;
    private static byte[] message1;
    private static int count = 0;

    public static void sentMessage(int seqNum, byte[] message) {
        if (seqNum == 0)
            IntegrityChecker.message0 = message;
        else
            IntegrityChecker.message1 = message;
    }

    public static void receivedMessage(int seqNum, byte[] message) {
        if (seqNum == 0) {
            if (!Arrays.equals(IntegrityChecker.message0, message)) {
                System.out.println("Anomaly " + ++count + " :");
                System.out.println("SND: " + Arrays.toString(IntegrityChecker.message0));
                System.out.println("RCV: " + Arrays.toString(message));
            }
        } else {
            if (!Arrays.equals(IntegrityChecker.message1, message)) {
                System.out.println("Anomaly " + ++count + " :");
                System.out.println("SND: " + Arrays.toString(IntegrityChecker.message1));
                System.out.println("RCV: " + Arrays.toString(message));
            }
        }
    }
}
