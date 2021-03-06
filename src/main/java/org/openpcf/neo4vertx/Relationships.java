package org.openpcf.neo4vertx;

import java.util.Map;

/**
 * The Relationships interface.
 *
 * @author mailto:b.phifty@gmail.com[Philipp Brüll]
 */
public interface Relationships {

    public void create(Object fromId, Object toId, String name, Map<String, Object> properties, Handler<Object> handler) throws Exception;

    public void update(Object id, Map<String, Object> properties, Handler<Boolean> handler) throws Exception;

    public void fetch(Object id, Handler<Map<String, Object>> handler);

    public void fetchAllOfNode(Object nodeId, Handler<Iterable<Map<String, Object>>> handler);

    public void remove(Object id, Handler<Boolean> handler) throws Exception;

}
