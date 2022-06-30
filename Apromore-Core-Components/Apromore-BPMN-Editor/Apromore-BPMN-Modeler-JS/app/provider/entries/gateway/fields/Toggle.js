
var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');


module.exports = function(bpmnFactory, elementRegistry, translate, options) {

   return entryFactory.checkbox('Test',{
        id: 'toggle_'+options.groupId,
        label: ' ', 
        modelProperty: 'isCondition'
        ,
        get: function(element, node) {
          var bo = getBusinessObject(element);
          return {
              'isCondition' : bo.$attrs.isCondition || false
          }
        }
        ,
        set: function (element, values, node) {
          let bo = getBusinessObject(element);
          if(values && values.isCondition){
            probablityEntry.forEach(text => {
            //  text.cssClasses.push('hide');
              if(text.cssClasses.indexOf('hide') === -1){
                text.cssClasses.push('hide');
                var result1 = cmdHelper.updateBusinessObject(element, bo, text);
                console.log(result1);
              }
            });
          }else{
            probablityEntry.forEach(text => {
             var arr = text.cssClasses.filter(item => item !== 'hide');
             text.cssClasses = arr;
            });
          }
          
           var result =  cmdHelper.updateBusinessObject(element, bo, {'isCondition': values.isCondition});
           return result;
        
        }
      });


}