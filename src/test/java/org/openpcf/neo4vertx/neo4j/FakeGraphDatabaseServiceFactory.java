package org.openpcf.neo4vertx.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * The FakeGraphDatabaseServiceFactory object.
 *
 * @author mailto:b.phifty@gmail.com[Philipp Brüll]
 */
public class FakeGraphDatabaseServiceFactory implements GraphDatabaseServiceFactory {

    @Override
    public GraphDatabaseService create(String path) {
        return new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
    }

}
