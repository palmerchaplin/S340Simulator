package s340.software;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import s340.software.os.IORequest;

public abstract class SSTF
{

    /**
     *
     * @param start
     * @param end
     * @param diskQueue
     * @return Reorganized Disk Queue, with shortest seek time request at the
     * head.
     */
//    public static LinkedList<IORequest> execute(int end, LinkedList<IORequest> diskQueue) {
//
//        // -- we need a temp list so that we can reorganize our disk requests ; we can't "get" from  queues. 
//        List<IORequest> diskQueueCopy = new LinkedList<>();
//        int length = diskQueue.size();
//        for (int i = 0; i < length; i++) {
//            diskQueueCopy.add(diskQueue.peek());
//            diskQueue.remove();
//
//        }
//
//        int index = findNearest(end, diskQueueCopy); 
//
//        diskQueue.add(diskQueueCopy.get(index));
//
//        diskQueueCopy.remove(diskQueueCopy.get(index)); // -- we remove so we don't add it again in our loop
//
//        for (int i = 0; i < diskQueueCopy.size(); i++) {
//
//            diskQueue.add(diskQueueCopy.get(i));
//
//        }
//
//        return diskQueue;
//
//    }
    public static LinkedList<IORequest> execute(int end, LinkedList<IORequest> diskQueue)
    {
        //int index = findNearest(end, diskQueue); 
        IORequest gotten = findNearest(end, diskQueue);
        diskQueue.remove(gotten);
        diskQueue.addFirst(gotten);
        return diskQueue;
    }

    /**
     * Finds nearest program index relative to the current position of the arm
     * (at the end of the disk read/write of p1)
     *
     * @param end
     * @param copy
     * @return
     */
    private static IORequest findNearest(int end, List<IORequest> list)
    {
        IORequest closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (IORequest current : list)
        {

            int currentDistance = Math.abs(current.getStart() - end) - 1;
            if (currentDistance < minDistance)
            {
                closest = current;
                minDistance = currentDistance;
            }
        }

        return closest;
    }

}
