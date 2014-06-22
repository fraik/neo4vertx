package org.openpcf.neo4vertx.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.AutoIndexer;

/**
 * The Finder object.
 *
 * @author Philipp Brüll <b.phifty@gmail.com>
 * @since 2012-12-13
 * @version 1.0.0
 */
public class Finder {

    private GraphDatabaseService graphDatabaseService;
    private String alternateNodeIdField;
    private String alternateRelationshipIdField;
    private AutoIndexer<Node> nodeAutoIndexer;
    private AutoIndexer<Relationship> relationshipAutoIndexer;

    public Finder(
        GraphDatabaseService graphDatabaseService,
        String alternateNodeIdField,
        String alternateRelationshipIdField) {

        this.graphDatabaseService = graphDatabaseService;
        this.alternateNodeIdField = alternateNodeIdField;
        this.alternateRelationshipIdField = alternateRelationshipIdField;

        initializeNodeAutoIndexer();
        initializeRelationshipAutoIndexer();
    }

    public boolean hasAlternateNodeIdField() {
        return alternateNodeIdField != null;
    }

    public boolean hasAlternateRelationshipIdField() {
        return alternateRelationshipIdField != null;
    }

    public Node getNode(Object id) {
        if (hasAlternateNodeIdField()) {
            return nodeAutoIndexer.getAutoIndex().get(alternateNodeIdField, (String) id).getSingle();
        } else {
            try {
                return graphDatabaseService.getNodeById((Long) id);
            } catch (NotFoundException exception) {
                return null;
            }
        }
    }

    public Object getNodeId(Node node) {
        if (hasAlternateNodeIdField()) {
            return node.getProperty(alternateNodeIdField);
        } else {
            return node.getId();
        }
    }

    public Relationship getRelationship(Object id) {
        if (hasAlternateRelationshipIdField()) {
            return relationshipAutoIndexer.getAutoIndex().get(alternateRelationshipIdField, (String) id).getSingle();
        } else {
            try {
                return graphDatabaseService.getRelationshipById((Long) id);
            } catch (NotFoundException exception) {
                return null;
            }
        }
    }

    public Object getRelationshipId(Relationship relationship) {
        if (hasAlternateRelationshipIdField()) {
            return relationship.getProperty(alternateRelationshipIdField);
        } else {
            return relationship.getId();
        }
    }

    private void initializeNodeAutoIndexer() {
        if (hasAlternateNodeIdField()) {
            nodeAutoIndexer = graphDatabaseService.index().getNodeAutoIndexer();
            nodeAutoIndexer.startAutoIndexingProperty(alternateNodeIdField);
            nodeAutoIndexer.setEnabled(true);
        }
    }

    private void initializeRelationshipAutoIndexer() {
        if (hasAlternateRelationshipIdField()) {
            relationshipAutoIndexer = graphDatabaseService.index().getRelationshipAutoIndexer();
            relationshipAutoIndexer.startAutoIndexingProperty(alternateRelationshipIdField);
            relationshipAutoIndexer.setEnabled(true);
        }
    }

}
