package Broker;

public class BrokerMain {
    public static void main(String[] args) {
        Broker broker = new Broker(2, 5000);
//        Broker.Broker broker2 = new Broker.Broker(1, 5000);
        broker.start();
//        broker2.start();
        while(true) {
        }
    }
}
