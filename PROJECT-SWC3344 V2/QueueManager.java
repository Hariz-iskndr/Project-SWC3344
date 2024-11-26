import java.util.*;

public class QueueManager {
    private Queue<CustomerInfo> queue1;
    private Queue<CustomerInfo> queue2;
    private Queue<CustomerInfo> queue3;
    private static final int MAX_SIZE = 5; // or whatever maximum size you want for each queue

    public QueueManager() {
        this.queue1 = new LinkedList<>();
        this.queue2 = new LinkedList<>();
        this.queue3 = new LinkedList<>();
    }

    public boolean enqueue(CustomerInfo customer) {
        int numServices = customer.getServices().size();

        if (numServices > 3) {
            if (queue3.size() < MAX_SIZE) {
                return queue3.offer(customer);
            }
        } else {
            if (queue1.size() < MAX_SIZE) {
                return queue1.offer(customer);
            } else if (queue2.size() < MAX_SIZE) {
                return queue2.offer(customer);
            }
        }
        return false; // All queues are full
    }

    // Getter methods for each queue
    public Queue<CustomerInfo> getQueue1() { return queue1; }
    public Queue<CustomerInfo> getQueue2() { return queue2; }
    public Queue<CustomerInfo> getQueue3() { return queue3; }
}