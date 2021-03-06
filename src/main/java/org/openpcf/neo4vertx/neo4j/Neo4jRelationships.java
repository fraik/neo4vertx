package org.openpcf.neo4vertx.neo4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.openpcf.neo4vertx.Handler;
import org.openpcf.neo4vertx.Relationships;

/**
 * The Neo4jRelationships object.
 *
 * @author mailto:b.phifty@gmail.com[Philipp Brüll]
 * @author mailto:rubin.simons@raaftech.com[Rubin Simons]
 */
public class Neo4jRelationships implements Relationships {

    private final GraphDatabaseService graphDatabaseService;
    private final Finder finder;

    public Neo4jRelationships(GraphDatabaseService graphDatabaseService, Finder finder) {
        this.graphDatabaseService = graphDatabaseService;
        this.finder = finder;
    }

    @Override
    public void create(Object fromId, Object toId, String name, Map<String, Object> properties, Handler<Object> handler) throws Exception {
        Transaction transaction = graphDatabaseService.beginTx();
        try {
            Node fromNode = finder.getNode(fromId);
            Node toNode = finder.getNode(toId);
            RelationshipType relationshipType = DynamicRelationshipType.forName(name);

            Relationship relationship = fromNode.createRelationshipTo(toNode, relationshipType);
            PropertyHandler.setProperties(relationship, properties);
            transaction.success();

            handler.handle(relationship.getId());
        } catch (Exception exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.close();
        }
    }

    @Override
    public void update(Object id, Map<String, Object> properties, Handler<Boolean> handler) throws Exception {
        Transaction transaction = graphDatabaseService.beginTx();
        try {
            Relationship relationship = finder.getRelationship(id);

            PropertyHandler.setProperties(relationship, properties);
            transaction.success();

            handler.handle(true);
        } catch (Exception exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.close();
        }
    }

    @Override
    public void fetch(Object id, Handler<Map<String, Object>> handler) {
        Transaction transaction = graphDatabaseService.beginTx();
        try {
            Relationship relationship = finder.getRelationship(id);
            transaction.success();
            if (relationship == null) {
                handler.handle(null);
            } else {
                handler.handle(PropertyHandler.getProperties(relationship));
            }

        } catch (Exception exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.close();
        }
    }

    @Override
    public void fetchAllOfNode(Object nodeId, Handler<Iterable<Map<String, Object>>> handler) {
        Transaction transaction = graphDatabaseService.beginTx();
        try {
            Node node = finder.getNode(nodeId);
            if (node == null) {
                handler.handle(new ArrayList<Map<String, Object>>());
            } else {
                final Iterable<Relationship> relationships = node.getRelationships();
                handler.handle(new Iterable<Map<String, Object>>() {
                    @Override
                    public Iterator<Map<String, Object>> iterator() {
                        final Iterator<Relationship> relationshipsIterator = relationships.iterator();
                        return new Iterator<Map<String, Object>>() {
                            @Override
                            public boolean hasNext() {
                                return relationshipsIterator.hasNext();
                            }

                            @Override
                            public Map<String, Object> next() {
                                Transaction innerTransaction = graphDatabaseService.beginTx();
                                try {
                                    innerTransaction.success();
                                    return PropertyHandler.getProperties(relationshipsIterator.next());
                                } catch (Exception exception) {
                                    innerTransaction.failure();
                                    throw exception;
                                } finally {
                                    innerTransaction.close();
                                }
                            }

                            @Override
                            public void remove() {
                                relationshipsIterator.remove();
                            }
                        };
                    }
                });
            }

            transaction.success();

        } catch (Exception exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.close();
        }

    }

    @Override
    public void remove(Object id, Handler<Boolean> handler) throws Exception {
        Transaction transaction = graphDatabaseService.beginTx();
        try {
            Relationship relationship = finder.getRelationship(id);
            relationship.delete();
            transaction.success();

            handler.handle(true);
        } catch (Exception exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.close();
        }
    }

}
