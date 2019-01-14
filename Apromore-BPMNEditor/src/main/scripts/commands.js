function ElementCommand(params) {

    return {
        args: [ params.element('element') ],
        exec: function(element) {
            return element;
        }
    };
}

function ElementsCommand(params, elementRegistry) {

    return {
        exec: function() {
            function all() {
                return true;
            }

            function ids(e) {
                return e.id;
            }

            return elementRegistry.filter(all).map(ids);
        }
    };
}

function AppendCommand(params, modeling) {

    return {
        args: [
            params.shape('source'),
            params.string('type'),
            params.point('delta', { defaultValue: { x: 200, y: 0 } })
        ],
        exec: function(source, type, delta) {
            var newPosition = {
                x: source.x + source.width / 2 + delta.x,
                y: source.y + source.height / 2 + delta.y
            };

            return modeling.appendShape(source, { type: type }, newPosition).id;
        }
    };
}

function ConnectCommand(params, modeling) {

    return {
        args: [
            params.shape('source'),
            params.shape('target'),
            params.string('type'),
            params.shape('parent', { optional: true }),
        ],
        exec: function(source, target, type, parent) {
            return modeling.createConnection(source, target, {
                type: type,
            }, parent || source.parent).id;
        }
    };
}

function CreateCommand(params, modeling) {

    return {
        args: [
            params.string('type'),
            params.point('position'),
            params.shape('parent'),
            params.bool('isAttach', { optional: true })
        ],
        exec: function(type, position, parent, isAttach) {

            var hints;

            if (isAttach) {
                hints = {
                    attach: true
                };
            }

            return modeling.createShape({ type: type }, position, parent, hints).id;
        }
    };
}


function MoveCommand(params, modeling) {

    return {
        args: [
            params.shapes('shapes'),
            params.point('delta'),
            params.shape('newParent', { optional: true }),
            params.bool('isAttach', { optional: true })
        ],
        exec: function(shapes, delta, newParent, isAttach) {

            var hints;

            if (isAttach) {
                hints = {
                    attach: true
                };
            }

            modeling.moveElements(shapes, delta, newParent, hints);
            return shapes;
        }
    };
}

function RemoveConnectionCommand(params, modeling) {

    return {
        args: [
            params.element('connection')
        ],
        exec: function(connection) {
            return modeling.removeConnection(connection);
        }
    };
}


function RemoveShapeCommand(params, modeling) {

    return {
        args: [
            params.shape('shape')
        ],
        exec: function(shape) {
            return modeling.removeShape(shape);
        }
    };
}


function SaveCommand(params, bpmnjs) {

    return {
        args: [ params.string('format') ],
        exec: function(format) {

            if (format === 'svg') {
                bpmnjs.saveSVG(function(err, svg) {

                    if (err) {
                        console.error(err);
                    } else {
                        console.info(svg);
                    }
                });
            } else if (format === 'bpmn') {
                return bpmnjs.saveXML(function(err, xml) {

                    if (err) {
                        console.error(err);
                    } else {
                        console.info(xml);
                    }
                });
            } else {
                throw new Error('unknown format, <svg> and <bpmn> are available');
            }
        }
    };
}

function SetLabelCommand(params, modeling) {

    return {
        args: [ params.element('element'), params.string('newLabel') ],
        exec: function(element, newLabel) {
            modeling.updateLabel(element, newLabel);
            return element;
        }
    };
}


function UndoCommand(commandStack) {

    return {
        exec: function() {
            commandStack.undo();
        }
    };
}

function RedoCommand(commandStack) {

    return {
        exec: function() {
            commandStack.redo();
        }
    };
}