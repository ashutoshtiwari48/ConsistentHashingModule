# ConsistentHashingModule

after running the ServiceNode.java you will get the output :

192.168.0.1 is route to IDC1-127.0.0.1:8084

192.168.0.2 is route to IDC1-127.0.0.1:8082

192.168.0.3 is route to IDC1-127.0.0.1:8081

192.168.0.4 is route to IDC1-127.0.0.1:8081

192.168.0.5 is route to IDC1-127.0.0.1:8081

-------------putting new node online IDC2-127.0.0.1:8080------------

192.168.0.1 is route to IDC1-127.0.0.1:8084

192.168.0.2 is route to IDC2-127.0.0.1:8080

192.168.0.3 is route to IDC1-127.0.0.1:8081

192.168.0.4 is route to IDC1-127.0.0.1:8081

192.168.0.5 is route to IDC1-127.0.0.1:8081

-------------remove node online IDC1-127.0.0.1:8082------------

192.168.0.1 is route to IDC1-127.0.0.1:8084

192.168.0.2 is route to IDC2-127.0.0.1:8080

192.168.0.3 is route to IDC1-127.0.0.1:8081

192.168.0.4 is route to IDC1-127.0.0.1:8081

192.168.0.5 is route to IDC1-127.0.0.1:8081

Process finished with exit code 0



==========================================================================================================================

Node  
Any class that implements Node can be mapped to ConsistentHashRouter.

VirtualNode    
Your custom Node represents a real physical node, which supports numbers of virtual nodes , the replicas of physical node.

When adding new Node to the ConsistentHashRouter, you can specify how many virtual nodes should be replicated.

HashFunction     
By default , ConsistentHashRouter will use MD5 to hash a node, you may specify your custom hash function by implementing HashFunction
