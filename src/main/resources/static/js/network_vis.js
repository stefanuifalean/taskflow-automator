var container = document.getElementById("derivedSemanticGraph");

// provide data in the DOT language
var DOTstring = inline_dot;
console.log(DOTstring);
var parsedData = vis.parseDOTNetwork(DOTstring);

// provide the data in the vis format
var data = {
  nodes: parsedData.nodes,
  edges: parsedData.edges,
}
var options = {};
//var options = parsedData.options;
// you can extend the options like a normal JSON variable:
//options = {
//  physics: {
//    stabilization: false,
//    barnesHut: {
//      springLength: 100
//    },
//  },
//};

//options.physics = {
//  stabilization: false,
//  barnesHut: {
//    springLength: 100
//  }
//};
//options.nodes = {
//  color: 'orange',
//  font: {color: 'black', strokeWidth: 0, face: 'Monospace', size: 16} // 12
//};
//options.edges = {
//  font: {color: 'black', strokeWidth: 0, face: 'Monospace', align: 'bottom', size: 16},
//  arrows: { to: {scaleFactor: 0.5}, from: {scaleFactor: 0.5}} // 0.5
//};

// create/initialize a network
var network = new vis.Network(container, data, options);