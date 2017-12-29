package ir.arcinc.tls.ReliableChannel;

import ir.arcinc.tls.Commons.AbstractReceiver;
import ir.arcinc.tls.Commons.AbstractSender;
import ir.arcinc.tls.Commons.UnreliableChannel;


/**
 * Created by tahae on 4/18/2016.
 */
public class Receiver extends AbstractReceiver {

    private byte nextReceivingSeqNum = 0;

    public Receiver(UnreliableChannel channel, AbstractSender sender) {
        super(channel,sender);
    }

    /**
     *
     * @param data data received from channel;
     *
     *  You can use method send(byte[]) to send a data to channel
     *  Or use sendToApplication(byte[]) to send data to application
     */

    @Override
    public void receive(byte[] data) {
        SegmentProvider segData;
        SegmentProvider segAck;
        try {
            segData = new SegmentProvider(data);
            segAck = new SegmentProvider(segData.getSeqNum(), new byte[0]);
            if (segData.getSeqNum() == nextReceivingSeqNum) {
                //System.out.println("RCV: got " + segData.getSeqNum());
                byte[] message = segData.getMessage();
                sendToApplication(message);
                IntegrityChecker.receivedMessage(nextReceivingSeqNum, message);
                //System.out.println("RCV: byte " + Arrays.toString(segData.getMessage()));
                nextReceivingSeqNum = Util.getNextSeqNum(nextReceivingSeqNum);
            }
        } catch (IllegalArgumentException iae) {
            segAck = new SegmentProvider((byte)-1, new byte[0]);    // NAck
        }
        //System.out.println("RCV: Acking " + segAck.getSeqNum());
        send(segAck.getSegment());
    }
}
