;(function () {
    'use strict';

// registers the extension on a cytoscape lib ref
    var register = function (cytoscape) {

        if (!cytoscape) {
            return;
        } // can't register if cytoscape unspecified

        var cy;
        var actions = {};
        var undoStack = [];
        var redoStack = [];

        var _instance = {
            options: {
                isDebug: false, // Debug mode for console messages
                actions: {},// actions to be added
                undoableDrag: true, // Whether dragging nodes are undoable
                keyboardShortcuts: {
                    ctrl_z: true, // undo
                    ctrl_y: true, // redo
                    ctrl_shift_z: false // redo
                },
                beforeUndo: function () { // callback before undo is triggered.

                },
                afterUndo: function () { // callback after undo is triggered.

                },
                beforeRedo: function () { // callback before redo is triggered.

                },
                afterRedo: function () { // callback after redo is triggered.

                },
                ready: function () {

                },
                defaultActions: true
            }
        };

        var isInitialized = false;
        // design implementation
        cytoscape("core", "undoRedo", function (options) {
            cy = this;
            if (options) {
                for (var key in options)
                    if (_instance.options.hasOwnProperty(key))
                        _instance.options[key] = options[key];

                if (options.actions)
                    for (var key in options.actions)
                        actions[key] = options.actions[key];


                if (_instance.options.keyboardShortcuts) {
                    var sh = _instance.options.keyboardShortcuts;
                    setKeyboardShortcuts(sh.ctrl_z, sh.ctrl_y, sh.ctrl_shift_z);
                } else
                    setKeyboardShortcuts(false, false, false);

            }
            if (!isInitialized && (!options || options.defaultActions)) {
                var defActions = defaultActions();
                for (var key in defActions)
                    actions[key] = defActions[key];


                setDragUndo(_instance.options.undoableDrag);

                isInitialized = true;
            }

            _instance.options.ready();
            return _instance;

        });

        // Undo last action
        _instance.undo = function () {
            if (!this.isUndoStackEmpty()) {
                _instance.options.beforeUndo();

                var action = undoStack.pop();
                cy.trigger("undo", [action.name, action.args]);

                var res = actions[action.name]._undo(action.args);

                redoStack.push({
                    name: action.name,
                    args: res
                });

                _instance.options.afterUndo();

                return res;
            } else if (_instance.options.isDebug) {
                console.log("Undoing cannot be done because undo stack is empty!");
            }
        };

        // Redo last action
        _instance.redo = function () {

            if (!this.isRedoStackEmpty()) {
                _instance.options.beforeRedo();

                var action = redoStack.pop();

                cy.trigger(action.firstTime ? "do" : "redo", [action.name, action.args]);
                if (!action.args)
                    action.args = {};
                action.args.firstTime = action.firstTime ? true : false;

                var res = actions[action.name]._do(action.args);

                undoStack.push({
                    name: action.name,
                    args: res
                });

                _instance.options.afterRedo();

                return res;
            } else if (_instance.options.isDebug) {
                console.log("Redoing cannot be done because undo stack is empty!");
            }

        };

        // Calls registered function with action name actionName via actionFunction(args)
        _instance.do = function (actionName, args) {

            redoStack.push({
                name: actionName,
                args: args,
                firstTime: true
            });

            return this.redo();
        };

        // Register action with its undo function & action name.
        _instance.action = function (actionName, _do, _undo) {

            actions[actionName] = {
                _do: _do,
                _undo: _undo
            };


            return _instance;
        };

        // Removes action stated with actionName param
        _instance.removeAction = function (actionName) {
            delete actions[actionName];
        };

        // Gets whether undo stack is empty
        _instance.isUndoStackEmpty = function () {
            return (undoStack.length === 0);
        };

        // Gets whether redo stack is empty
        _instance.isRedoStackEmpty = function () {
            return (redoStack.length === 0);
        };

        // Gets actions (with their args) in undo stack
        _instance.getUndoStack = function () {
            return undoStack;
        };

        // Gets actions (with their args) in redo stack
        _instance.getRedoStack = function () {
            return redoStack;
        };

        // Set keyboard shortcuts according to options
        function setKeyboardShortcuts(ctrl_z, ctrl_y, ctrl_shift_z) {
            document.addEventListener("keydown", function (e) {
                if (e.ctrlKey)
                    if (ctrl_z && e.which === 90)
                        if (ctrl_shift_z && e.shiftKey)
                            _instance.redo();
                        else
                            _instance.undo();
                    else if (ctrl_y && e.which === 89)
                        _instance.redo();
            });
        }

        var lastMouseDownNodeInfo = null;
        var isDragDropSet = false;

        function setDragUndo(undoable) {
            isDragDropSet = true;
            cy.on("mousedown", "node", function () {
                if (undoable) {
                    lastMouseDownNodeInfo = {};
                    lastMouseDownNodeInfo.lastMouseDownPosition = {
                        x: this.position("x"),
                        y: this.position("y")
                    };
                    lastMouseDownNodeInfo.node = this;
                }
            });
            cy.on("mouseup", "node", function () {
                if (undoable) {
                    if (lastMouseDownNodeInfo == null) {
                        return;
                    }
                    var node = lastMouseDownNodeInfo.node;
                    var lastMouseDownPosition = lastMouseDownNodeInfo.lastMouseDownPosition;
                    var mouseUpPosition = {
                        x: node.position("x"),
                        y: node.position("y")
                    };
                    if (mouseUpPosition.x != lastMouseDownPosition.x ||
                      mouseUpPosition.y != lastMouseDownPosition.y) {
                        var positionDiff = {
                            x: mouseUpPosition.x - lastMouseDownPosition.x,
                            y: mouseUpPosition.y - lastMouseDownPosition.y
                        };

                        var nodes;
                        if (node.selected()) {
                            nodes = cy.nodes(":visible").filter(":selected");
                        }
                        else {
                            nodes = [];
                            nodes.push(node);
                        }

                        var param = {
                            positionDiff: positionDiff,
                            nodes: nodes, move: false
                        };
                        _instance.do("drag", param);

                        lastMouseDownNodeInfo = null;
                    }
                }
            });
        }

        function moveNodes(positionDiff, nodes) {
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                var oldX = node.position("x");
                var oldY = node.position("y");
                node.position({
                    x: oldX + positionDiff.x,
                    y: oldY + positionDiff.y
                });
                var children = node.children();
                moveNodes(positionDiff, children);
            }
        }

        function getEles(_eles) {
            return (typeof _eles === "string") ? cy.$(_eles) : _eles;
        }

        function restoreEles(_eles) {
            return getEles(_eles).restore();
        }


        function returnToPositionsAndSizes(nodesData) {
            var currentPositionsAndSizes = {};
            cy.nodes().positions(function (i, ele) {
                currentPositionsAndSizes[ele.id()] = {
                    width: ele.width(),
                    height: ele.height(),
                    x: ele.position("x"),
                    y: ele.position("y")
                };
                var data = nodesData[ele.id()];
                ele._private.data.width = data.width;
                ele._private.data.height = data.height;
                return {
                    x: data.x,
                    y: data.y
                };
            });

            return currentPositionsAndSizes;
        }

        function getNodesData() {
            var nodesData = {};
            var nodes = cy.nodes();
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                nodesData[node.id()] = {
                    width: node.width(),
                    height: node.height(),
                    x: node.position("x"),
                    y: node.position("y")
                };
            }
            return nodesData;
        }

        // Default actions
        function defaultActions() {
            return {
                "add": {
                    _do: function (eles) {
                        return eles.firstTime ? cy.add(eles) : restoreEles(eles);
                    },
                    _undo: cy.remove
                },
                "remove": {
                    _do: cy.remove,
                    _undo: restoreEles
                },
                "restore": {
                    _do: restoreEles,
                    _undo: cy.remove
                },
                "clone": {
                    _do: function (_eles) {
                        return _eles.firstTime ? getEles(_eles).clone() : restoreEles(_eles);
                    },
                    _undo: cy.remove
                },
                "select": {
                    _do: function (_eles) {
                        return getEles(_eles).select();
                    },
                    _undo: function (_eles) {
                        return getEles(_eles).unselect();
                    }
                },
                "unselect": {
                    _do: function (_eles) {
                        return getEles(_eles).unselect();
                    },
                    _undo: function (_eles) {
                        return getEles(_eles).select();
                    }
                },
                "move": {
                    _do: function (args) {
                        var eles = getEles(args.eles);
                        var nodes = eles.nodes();
                        var edges = eles.edges();

                        return {
                            oldNodes: nodes,
                            newNodes: nodes.move(args.location),
                            oldEdges: edges,
                            newEdges: edges.move(args.location)
                        };
                    },
                    _undo: function (eles) {
                        var newEles = cy.collection();
                        var location = {};
                        if (eles.newNodes.length > 0) {
                            location.parent = eles.newNodes[0].parent();

                            for (var i = 0; i < eles.newNodes.length; i++) {
                                var newNode = eles.newNodes[i].move({
                                    parent: eles.oldNodes[i].parent()
                                });
                                newEles.union(newNode);
                            }
                        } else {
                            location.source = location.newEdges[0].source();
                            location.target = location.newEdges[0].target();

                            for (var i = 0; i < eles.newEdges.length; i++) {
                                var newEdge = eles.newEdges[i].move({
                                    source: eles.oldEdges[i].source(),
                                    target: eles.oldEdges[i].target()
                                });
                                newEles.union(newEdge);
                            }
                        }
                        return {
                            eles: newEles,
                            location: location
                        };
                    }
                },
                "drag": {
                    _do: function (args) {
                        if (args.move)
                            moveNodes(args.positionDiff, args.nodes);
                        return args;
                    },
                    _undo: function (args) {
                        var diff = {
                            x: -1 * args.positionDiff.x,
                            y: -1 * args.positionDiff.y
                        };
                        var result = {
                            positionDiff: args.positionDiff,
                            nodes: args.nodes,
                            move: true
                        };
                        moveNodes(diff, args.nodes);
                        return result;
                    }
                },
                "layout": {
                    _do: function (options) {
                        if(options.firstTime){
                            var nodesData = getNodesData();
                            cy.layout(options);
                            return nodesData;
                        } else
                            return returnToPositionsAndSizes(options);
                    },
                    _undo: function (nodesData) {
                        return returnToPositionsAndSizes(nodesData);
                    }
                }
            };
        }

    };

    if (typeof module !== 'undefined' && module.exports) { // expose as a commonjs module
        module.exports = register;
    }

    if (typeof define !== 'undefined' && define.amd) { // expose as an amd/requirejs module
        define('cytoscape.js-undo-redo', function () {
            return register;
        });
    }

    if (typeof cytoscape !== 'undefined') { // expose to global cytoscape (i.e. window.cytoscape)
        register(cytoscape);
    }

})();
