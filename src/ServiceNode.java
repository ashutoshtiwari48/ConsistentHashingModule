import java.util.Arrays;

/**
 * a sample usage for routing a request to services based on requester ip
 */
public class ServiceNode implements ServerNode {
    private final String idc;
    private final String ip;
    private final int port;

    public ServiceNode(String idc, String ip, int port) {
        this.idc = idc;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getKey() {
        return idc + "-"+ip+":"+port;
    }

    @Override
    public String toString(){
        return getKey();
    }

    public static void main(String[] args) {
        //initialize 4 service node
        ServiceNode node1 = new ServiceNode("IDC1","127.0.0.1",8080);
        ServiceNode node2 = new ServiceNode("IDC1","127.0.0.1",8081);
        ServiceNode node3 = new ServiceNode("IDC1","127.0.0.1",8082);
        ServiceNode node4 = new ServiceNode("IDC1","127.0.0.1",8084);

        //hash them to hash ring
        ConsistentHashRouter<ServiceNode> consistentHashRouter = new ConsistentHashRouter<>(Arrays.asList(node1,node2,node3,node4),10);//10 virtual node

        //we have 5 requester ip, we are trying them to route to one service node
        String requestIP1 = "192.168.0.1";
        String requestIP2 = "192.168.0.2";
        String requestIP3 = "192.168.0.3";
        String requestIP4 = "192.168.0.4";
        String requestIP5 = "192.168.0.5";

        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);

        ServiceNode node5 = new ServiceNode("IDC2","127.0.0.1",8080);//put new service online
        System.out.println("-------------putting new node online " +node5.getKey()+"------------");
        consistentHashRouter.addNode(node5,10);

        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);

        consistentHashRouter.removeNode(node3);
        System.out.println("-------------remove node online " + node3.getKey() + "------------");
        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);


    }

    private static void goRoute(ConsistentHashRouter<ServiceNode> consistentHashRouter , String ... requestIps){
        for (String requestIp: requestIps) {
            System.out.println(requestIp + " is route to " + consistentHashRouter.routeNode(requestIp));
        }
    }
}