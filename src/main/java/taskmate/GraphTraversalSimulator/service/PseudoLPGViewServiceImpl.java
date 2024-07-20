package taskmate.GraphTraversalSimulator.service;

import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmate.GraphTraversalSimulator.domain.node.Node;
import taskmate.GraphTraversalSimulator.domain.relation.Relation;
import taskmate.GraphTraversalSimulator.domain.relation.HardNext;
import taskmate.GraphTraversalSimulator.domain.relation.SoftNext;
import taskmate.GraphTraversalSimulator.helper.*;
import taskmate.GraphTraversalSimulator.repository.Rdf4JRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.rdf4j.model.util.Values.iri;

@Service
public class PseudoLPGViewServiceImpl implements PseudoLPGViewService {
  private final CreateUtils createUtils;

  private final Rdf4JRepository repo;

  private final List<IriEncoding> ALLOWED_PROP_RELATIONS = Arrays.asList(
      IriEncodingFactory.fromMaskedIri("ns0!requiresInput"),
      IriEncodingFactory.fromMaskedIri("ns0!Is_inside"));

  @Autowired
  public PseudoLPGViewServiceImpl(CreateUtils createUtils, Rdf4JRepository repo) {
    this.createUtils = createUtils;
    this.repo = repo;
  }

  /**
   * @param nodeIriEncoding IriEncoding of Concept describing resource
   * @return Node object with literal value properties embedded and no relations to other Concepts
   */
  @Override
  public Node getNode(IriEncoding nodeIriEncoding) {
    Collection<BindingSet> bindingSets = repo.runSelect(Queries.triplesForIri(nodeIriEncoding));
    return createUtils.makeNode(new ResourceMapView(bindingSets));
  }

  /**
   * @param relIriEncoding IriEncoding of parameterized Relation describing resource
   * @return Relation object containing literal value properties
   */
  @Override
  public Relation getRichRelation(IriEncoding relIriEncoding) {
    Collection<BindingSet> bindingSets = repo.runSelect(Queries.triplesForIri(relIriEncoding));
    return makeParamRelation(new ResourceMapView(bindingSets));
  }

  /**
   * @param fromNodeIriEncoding IriEncoding of Concept describing resource from which relations to other Concepts go
   * @return List (can be empty) of Simple and Rich Relations
   */
  @Override
  public Collection<Relation> getRelationsDirectOutgoing(IriEncoding fromNodeIriEncoding) {
    return getRelations("outgoing", fromNodeIriEncoding);
  }

  /**
   * @param toNodeIriEncoding IriEncoding of Concept describing resource to which relations from other Concepts come
   * @return List (can be empty) of Simple and Rich Relations
   */
  public Collection<Relation> getRelationsDirectIncoming(IriEncoding toNodeIriEncoding) {
    return getRelations("incoming", toNodeIriEncoding);
  }

  /* RELATION creation utility methods */

  private Collection<Relation> getRelations(String direction, IriEncoding refNodeIriEncoding) {
    Collection<Relation> relations = new ArrayList<>();
    Collection<BindingSet> bindingSets = (Collection<BindingSet>) repo.runSelect(
        Queries.triplesForSimpleRelations(direction, refNodeIriEncoding));
    relations.addAll(makePropRelations(bindingSets));
    bindingSets = repo.runSelect(Queries.irisForRichRelations(direction, refNodeIriEncoding));
    relations.addAll(makeParamRelations(bindingSets));
    return relations;
  }

  /**
   * Creates Simple Relations from Concept resource properties that abstract an outgoing node relation
   *
   * @param bindingSets Result of running a triplesForSimpleRelations("outgoing", srcNodeIriEncoding) query
   */
  private Collection<Relation> makePropRelations(Collection<BindingSet> bindingSets) {
    return bindingSets.stream().map((triple -> {
      IriEncoding propRelIriEnc = IriEncodingFactory.fromFullIri(triple.getValue("propRelIri").stringValue());
      /* We exclude some relations that don't have a Class for it (those related to UI elements drawn in the
      right-part submodel that do not concern us for the RPA workflow */
      if (!ALLOWED_PROP_RELATIONS.contains(propRelIriEnc)) {
        return null;
      }
      IriEncoding outNodeIriEnc = IriEncodingFactory.fromFullIri(triple.getValue("outgoingNodeIri").stringValue());
      IriEncoding incNodeIriEnc = IriEncodingFactory.fromFullIri(triple.getValue("incomingNodeIri").stringValue());
      return makePropRelation(outNodeIriEnc, propRelIriEnc, incNodeIriEnc);
    })).filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * Creates Rich Relations from collection of Relation resource IRI Strings
   *
   * @param bindingSets result of running a richRelations() query on source Concept
   */
  private Collection<Relation> makeParamRelations(Collection<BindingSet> bindingSets) {
    return bindingSets.stream().map(row ->
            getRichRelation(IriEncodingFactory.fromFullIri(row.getValue("relIri").stringValue()))).
        collect(Collectors.toList());
  }

  /**
   * Creates Simple Relation from a triple-like enumeration of IriEncodings of a Resource's property that describes a Relation
   */
  private Relation makePropRelation(IriEncoding outNodeIriEnc, IriEncoding propRelIriEnc, IriEncoding incNodeIriEnc) {
    /* Create relation instance at runtime */
    Relation relation = (Relation) createUtils.makeEmptyObject(propRelIriEnc.getLocalName());
    /* Set generic relation fields */
    Node source = getNode(outNodeIriEnc);
    relation.setSource(source);
    Node target = getNode(incNodeIriEnc);
    relation.setTarget(target);
    return relation;
  }

  /**
   * Creates Rich Relation from parameterized Relation defined as resource (with IRI)
   */
  private Relation makeParamRelation(ResourceMapView relResource) {
    IriEncoding typeIriEncoding = relResource.getType();
    String predicateIriLocalName = typeIriEncoding.getLocalName();
    /* Create concept instance at runtime */
    Relation relation = (Relation) createUtils.makeEmptyObject(predicateIriLocalName);
    /* Set generic relation fields */
    String srcNodeFullIriStr = relResource.get(IriEncodingFactory.fromMaskedIri("cv!from_instance"));
    Node source = getNode(IriEncodingFactory.fromFullIri(srcNodeFullIriStr));
    relation.setSource(source);
    String trgtNodeFullIriStr = relResource.get(IriEncodingFactory.fromMaskedIri("cv!to_instance"));
    Node target = getNode(IriEncodingFactory.fromFullIri(trgtNodeFullIriStr));
    relation.setTarget(target);
    /* Set subclass specific fields */
    if (predicateIriLocalName.equals("hardNext")) {
      ((HardNext) relation).setText(relResource.get(IriEncodingFactory.fromMaskedIri("ns0!Text")));
    } else if (predicateIriLocalName.equals("softNext")) {
      ((SoftNext) relation).setText(relResource.get(IriEncodingFactory.fromMaskedIri("ns0!Text")));
    }
    return relation;
  }
}