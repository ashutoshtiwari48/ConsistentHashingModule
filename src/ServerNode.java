
/**
 *
 * Represent a node which should be mapped to a hash ring
 */
public interface ServerNode {
    /**
     *
     * @return the key which will be used for hash mapping
     */
    String getKey();
}
