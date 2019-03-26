
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 *
 * To hash ServerNode objects to a hash ring with a certain amount of virtual node.
 * Method routeNode will return a ServerNode instance which the object key should be allocated to according to consistent hash algorithm
 *
 * @param <T>
 */
public class ConsistentHashRouter<T extends ServerNode> {
    private final SortedMap<Long, VirtualServerNode<T>> ring = new TreeMap<>();
    private final HashingFunction hashingFunction;

    public ConsistentHashRouter(Collection<T> pNodes, int vNodeCount) {
        this(pNodes,vNodeCount, new MD5Hashing());
    }

    /**
     *
     * @param pNodes collections of physical nodes
     * @param vNodeCount amounts of virtual nodes
     * @param hashingFunction hash Function to hash ServerNode instances
     */
    public ConsistentHashRouter(Collection<T> pNodes, int vNodeCount, HashingFunction hashingFunction) {
        if (hashingFunction == null) {
            throw new NullPointerException("Hash Function is null");
        }
        this.hashingFunction = hashingFunction;
        if (pNodes != null) {
            for (T pNode : pNodes) {
                addNode(pNode, vNodeCount);
            }
        }
    }

    /**
     * add physic node to the hash ring with some virtual nodes
     * @param pNode physical node needs added to hash ring
     * @param vNodeCount the number of virtual node of the physical node. Value should be greater than or equals to 0
     */
    public void addNode(T pNode, int vNodeCount) {
        if (vNodeCount < 0) throw new IllegalArgumentException("illegal virtual node counts :" + vNodeCount);
        int existingReplicas = getExistingReplicas(pNode);
        for (int i = 0; i < vNodeCount; i++) {
            VirtualServerNode<T> vNode = new VirtualServerNode<>(pNode, i + existingReplicas);
            ring.put(hashingFunction.hash(vNode.getKey()), vNode);
        }
    }

    /**
     * remove the physical node from the hash ring
     * @param pNode
     */
    public void removeNode(T pNode) {
        Iterator<Long> it = ring.keySet().iterator();
        while (it.hasNext()) {
            Long key = it.next();
            VirtualServerNode<T> virtualNode = ring.get(key);
            if (virtualNode.isVirtualNodeOf(pNode)) {
                it.remove();
            }
        }
    }

    /**
     * with a specified key, route the nearest ServerNode instance in the current hash ring
     * @param objectKey the object key to find a nearest ServerNode
     * @return
     */
    public T routeNode(String objectKey) {
        if (ring.isEmpty()) {
            return null;
        }
        Long hashVal = hashingFunction.hash(objectKey);
        SortedMap<Long, VirtualServerNode<T>> tailMap = ring.tailMap(hashVal);
        Long nodeHashVal = !tailMap.isEmpty() ? tailMap.firstKey() : ring.firstKey();
        return ring.get(nodeHashVal).getPhysicalNode();
    }


    public int getExistingReplicas(T pNode) {
        int replicas = 0;
        for (VirtualServerNode<T> vNode : ring.values()) {
            if (vNode.isVirtualNodeOf(pNode)) {
                replicas++;
            }
        }
        return replicas;
    }


    //default hash function
    private static class MD5Hashing implements HashingFunction {
        MessageDigest instance;

        public MD5Hashing() {
            try {
                instance = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
            }
        }

        @Override
        public long hash(String key) {
            instance.reset();
            instance.update(key.getBytes());
            byte[] digest = instance.digest();

            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= ((int) digest[i]) & 0xFF;
            }
            return h;
        }
    }

}
