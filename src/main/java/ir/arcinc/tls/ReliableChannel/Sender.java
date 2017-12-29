package ir.arcinc.tls.ReliableChannel;

import ir.arcinc.tls.Commons.AbstractSender;
import ir.arcinc.tls.Commons.UnreliableChannel;

import java.util.LinkedList;

/**
 * Created by tahae on 4/18/2016.
 */
public class Sender extends AbstractSender {

    private byte lastAssignedSeqNum = -1; // 0 and 1 are valid
    private byte lastSentSeqNum = -1;
    private boolean lastSentSegmentAcked = true;
    private LinkedList<SegmentProvider> segmentQueue;

    public Sender(UnreliableChannel channel) {
        super(channel);

        segmentQueue = new LinkedList<SegmentProvider>();
    }

    /**
     *
     * @param data data received from application to be sent to another application
     * Use send(byte[]) to send a data to channel.
     */

    @Override
    public void receiveFromApplication(byte[] data) {
        lastAssignedSeqNum = Util.getNextSeqNum(lastAssignedSeqNum);
        segmentQueue.add(new SegmentProvider(lastAssignedSeqNum, data));
        if (segmentQueue.size() > 1) System.out.println("SND: queue size = " + segmentQueue.size());
        //System.out.println("SND: queued " + lastAssignedSeqNum);
        if (lastSentSegmentAcked) {
            lastSentSegmentAcked = false;
            lastSentSeqNum = segmentQueue.getFirst().getSeqNum();
            //System.out.println("SND: byte " + Arrays.toString(data));
            IntegrityChecker.sentMessage(lastSentSeqNum, data);
            send(segmentQueue.getFirst().getSegment());
        }
    }

    @Override
    public void receive(byte[] data) {
        SegmentProvider segAck;
        byte ackNum;
        try {
            segAck = new SegmentProvider(data);
            ackNum = segAck.getSeqNum();
        } catch (IllegalArgumentException iae) {
            ackNum = -1;    // invalid
        }
        //System.out.println("SND: Ack " + ackNum);
        if (!lastSentSegmentAcked) {
            if (ackNum == lastSentSeqNum) {
                //System.out.println("SND: Acked " + ackNum);
                lastSentSegmentAcked = true;
                segmentQueue.removeFirst();
                //System.out.println("SND: queue size = " + segmentQueue.size());
            } else {
                //System.out.println("SND: sending " + segmentQueue.getFirst().getSeqNum());
                send(segmentQueue.getFirst().getSegment());
            }
        }
    }
    
    @Override
    public void timeOut() {
        if (!lastSentSegmentAcked) {
            //System.out.println("SND: timeout, sending " + segmentQueue.getFirst().getSeqNum());
            send(segmentQueue.getFirst().getSegment());
        }
    }
}
