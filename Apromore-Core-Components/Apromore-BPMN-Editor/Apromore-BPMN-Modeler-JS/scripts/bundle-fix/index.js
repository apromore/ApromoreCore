var fs = require('fs');

(function fix() {
  try {
    var content = fs.readFileSync('dist/bpmn-modeler.development.js', 'utf-8');
    var source = fs.readFileSync(__dirname + '/source.stub', 'utf-8');
    var target = fs.readFileSync(__dirname + '/target.stub', 'utf-8');
    content = content.replace(source, target);
    fs.writeFileSync('dist/bpmn-modeler.development.js', content);
  } catch (err) {
    console.error(err);
  }
})();

