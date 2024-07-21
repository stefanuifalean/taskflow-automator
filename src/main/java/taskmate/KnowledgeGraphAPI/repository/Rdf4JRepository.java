package taskmate.KnowledgeGraphAPI.repository;

import org.eclipse.rdf4j.query.BindingSet;

import java.util.Collection;

public interface Rdf4JRepository {
  Collection<BindingSet> runSelect(String query);
  void runUpdate(String query);
}
