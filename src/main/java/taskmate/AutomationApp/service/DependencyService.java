//package taskmate.AutomationApp.service;
//
//import org.apache.commons.lang3.NotImplementedException;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//import taskmate.AutomationApp.helper.context.Container;
//import taskmate.AutomationApp.helper.context.dependency.aggregation.Aggregation;
//import taskmate.AutomationApp.helper.context.dependency.aggregation.DualLayerAggregation;
//import taskmate.AutomationApp.helper.context.dependency.aggregation.SingleLayerAggregation;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.*;
//
//@Service
//public class DependencyService {
//  private final Map<String, Aggregation> placeholderToAggregationLookup = new HashMap<>();
//  //  private final String RESOURCES_ROOT = "olk/";
//  private final String RESOURCES_ROOT = "";
//
//  { /* Initialization block (called once when service is created)
//       Create Aggregation with all ungrouped properties required in flow
//       Parse file and create data aggregation */
//    Path filePath = null;
//    try {
//      filePath = new ClassPathResource(RESOURCES_ROOT + "_ungrouped_").getFile().toPath();
//    } catch (IOException e) {
//      System.out.println("INFO: No _ungrouped_ file found");
//    }
//    if (filePath != null) {
//      Aggregation aggregation = parseSingleLayerFile(filePath);
//      placeholderToAggregationLookup.put("_ungrouped_", aggregation);
//    }
//  }
//
//  /**
//   * Loading of input requirements for RPA step. For file RPA elements (Object DataEntity) the algorithm of
//   * collection processing does not go visually on each element to obtain properties and values but rather
//   * follows strictly the file provided once its name is obtained (without accessing the node further).
//   * The file is parsed so it has to follow a strict structure (see examples) in order for the automation to work.
//   */
//  public void processRequires(WebDriver processTraversalSimulator,
//                              Map<String, Container> urlToStepContextRegistry) throws IOException {
////    String url = processTraversalSimulator.getCurrentUrl();
////    if (!urlToStepContextRegistry.containsKey(url)) return; // skip processing if not an eligible type
////    StepContext stepContext = urlToStepContextRegistry.get(url); // retrieve pre-initialized StepContext entry from the registry
////    if (stepContext.isDependencyProcessed()) return; // skip processing if done previously
////    /**
////     * TODO (optional): SoftwareResource dependency parsing enhanced (get executable path instead of DUMMY app name)
////     * TODO: But this is not possible in current scenario of Selenium browser-based automation (See UIPath for more)
////     * !!! Thus, we won't use requiresApp relation in our models for now (and also Decision and UIElement concepts)!!!
////     */
////    /* process software and data dependencies */
////    String softwareResult = processSoftwareDependencies(processTraversalSimulator);
////    Aggregation dataResult = processDataDependencies(processTraversalSimulator);
////    stepContext.setDependencyProcessed(true);
////    if (dataResult == null && softwareResult == null) return; // skip if step hasn't got any kind of dependencies
////    /* attach discovered software/data dependency(ies) to StepContext */
////    Dependency dependency = new Dependency();
////    if (softwareResult != null) {
////      dependency.setSoftwareResource(softwareResult);
////    }
////    if (dataResult != null) {
////      DataResource dataResource = null;
////      if (stepContext.getMostDerivedStepType().equals(LoopingActivity.class.getSimpleName())) {
////        dataResource = new DataResourceOfLoop();
////        ((DataResourceOfLoop) dataResource).setDualLayerAggregation((DualLayerAggregation) dataResult);
////      } else { // Non-Loop Activity or Action
////        dataResource = new DataResourceOfNonLoop();
////        ((DataResourceOfNonLoop) dataResource).setSingleLayerAggregation((SingleLayerAggregation) dataResult);
////      }
////      dependency.setDataResource(dataResource);
////    }
////    stepContext.setDependency(dependency);
//  }
//
//  /* HELPERS */
//
//  /**
//   * Mandatory one requiresApp out-rel per Invisible Action is a drawing convention
//   */
//  private String processSoftwareDependencies(WebDriver processTraversalSimulator) {
//    List<WebElement> elements = processTraversalSimulator.findElements(
//        By.cssSelector("#outgoing .relation.requiresApp .targetNodeName"));
//    if (elements.isEmpty()) return null;
//    return elements.get(0).getText();
//  }
//
//  /**
//   * Optional one requiresInput out-rel per Task is a drawing convention
//   * requiresInput links to one of the following: a single Property, or a file with multiple Properties,
//   * or the complex case of a file with Collections
//   */
//  private Aggregation processDataDependencies(WebDriver processTraversalSimulator) throws IOException {
//    // findElement should not be used to look for non-present elements, use findElements(By) and assert zero length response instead.
//    List<WebElement> requiresInputRelationRow = processTraversalSimulator.findElements(
//        By.cssSelector("#outgoing .relation.requiresInput"));
//    if (requiresInputRelationRow.isEmpty()) return null; // skip data processing if found no dependency connector
//    WebElement relRow = requiresInputRelationRow.get(0);
//    String targetDataEntityRootName = relRow.findElement(By.className("targetNodeName")).getText();
//
//    /* Skip dependency processing if an ungrouped Property (all were handled prior) */
//    if (targetDataEntityRootName.contains("_val_")) return placeholderToAggregationLookup.get("_ungrouped_");
//
//    /* Skip file processing if already done in another step */
//    if (placeholderToAggregationLookup.containsKey(targetDataEntityRootName))
//      return placeholderToAggregationLookup.get(targetDataEntityRootName);
//
//    /* Parse Object (file) and create data aggregation */
//    Path filePath = null;
//    Aggregation aggregation = null;
//    if (targetDataEntityRootName.contains("_slf_")) { // links to Object concept <=> Single Layer file
//      filePath = new ClassPathResource(RESOURCES_ROOT + targetDataEntityRootName).getFile().toPath();
//      aggregation = parseSingleLayerFile(filePath);
//    } else if (targetDataEntityRootName.contains("_dlf_")) { // links to Object concept <=> Dual Layer file
//      filePath = new ClassPathResource(RESOURCES_ROOT + targetDataEntityRootName).getFile().toPath();
//      aggregation = parseDualLayerFile(filePath);
//    } else throw new RuntimeException("Object file name target of requiresInput must be prefixed!");
//    placeholderToAggregationLookup.put(targetDataEntityRootName, aggregation);
//    return aggregation;
//  }
//
//
//
//  /**
//   * Parses a file on the structure of the model example for EWA Add New Role dataset.
//   * Represented using a binary tree with single parent and child edges except for the
//   * last Collection node which is the parent of a series of finite literal values.
//   *
//   * @param filePath _dlf_anyName file pointed by the root target concept of requiresInput in a RPA model
//   * @return Aggregation based on collections chain with interface allowing to retrieve value based on loop iteration index
//   */
//  private DualLayerAggregation parseDualLayerFile(Path filePath) {
//    //TODO
//    throw new NotImplementedException("!!!Parsing Dual Layer File not implemented yet!!!");
////    List<String> lines = readFile(filePath);
////    Map<String, List<Object>>
////    String currentCollectionName = null;
////    for (String line : lines) {
////      line = line.trim();
////      if (line.contains(":")) { // collection declaration line
////        String[] collectionSplit = line.split(":");
////        String constructName = collectionSplit[0];
////
////        inputConstruct.setCollection(true);
////        inputConstruct.setConstructName(constructName);
////        /* Prepare an empty container for next iteration (which is guaranteed to be next-level indented) */
////        List<InputConstruct> container = new ArrayList<>();
////        inputConstruct.setContainer(container); // attach list with one empty obj
////        /* Add key-value to "evidence" data structure */
////        if (!indentLevelToInputConstructs.containsKey(indentLevel)) {
////          List<InputConstruct> levelBinding = new ArrayList<>();
////          levelBinding.add(inputConstruct);
////          indentLevelToInputConstructs.put(indentLevel, levelBinding);
////        }
////      } else if (line.contains("=")) { // leaf node (property) declaration line
////        String[] propertySplit = line.split("=");
////        if (propertySplit.length == 1) { // if omitted prop value on declaration
////          continue; // skip to next line / iteration
////        }
////        String constructName = propertySplit[0];
////        inputConstruct.setConstructName(constructName);
////        String value = propertySplit[1];
////        inputConstruct.setValue(value);
////      }
////      //!!! Always associate collection / property with parent's container via prev indent level last element in levelBinding !!!
////      // a.k.a. La cel de dinainte ii mai adaugi in lista agregatelor un copil
////      if (indentLevel > 0) { // root element does not have parent a.k.a. skip if first element in file
////        List<InputConstruct> parentList = indentLevelToInputConstructs.get(indentLevel - 1);
////        int lastIndex = parentList.size() - 1;
////        InputConstruct parent = parentList.get(lastIndex);
////        parent.getContainer().add(inputConstruct);
////      }
////    }
////    return indentLevelToInputConstructs.get(0).get(0); // root parent (file-name)
//  }
//
//
//}