package taskmate.GraphTraversalSimulator.repository;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

@org.springframework.stereotype.Repository
public class RemoteRdf4JRepository implements Rdf4JRepository {
  private final Repository repository;

  /**
   * Connect to a remote server-side RDF4J repository
   */
  public RemoteRdf4JRepository(@Value("${graphdb.repository.url}") String repoUrl) throws IllegalAccessException {
    // Abstract representation of a remote repository accessible over HTTP
    repository = new HTTPRepository(repoUrl);
  }

  /**
   * Runs a SPARQL SELECT query and returns a set of key-value bindings of the retrieved resource
   */
  @Override
  public Collection<BindingSet> runSelect(String query) {
    // Separate connection to a repository
    try (RepositoryConnection repositoryConnection = repository.getConnection()) {
      return Repositories.tupleQuery(repository, query, queryResult -> QueryResults.asList(queryResult));
    } // connection resource is auto-freed
  }

  /**
   * Runs a SPARQL 1.1 INSERT OR DELETE query
   */
  @Override
  public void runUpdate(String query) {
    try (RepositoryConnection repositoryConnection = repository.getConnection()) {
      repositoryConnection.prepareUpdate(query).execute();
    }
  }
}