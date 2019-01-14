;(function() { 'use strict';

    // registers the extension on a cytoscape lib ref
    var register = function( cytoscape ){

        if( !cytoscape ){ return; } // can't register if cytoscape unspecified

        cytoscape( 'core', 'svgConvertor', function(){
            var cy = this;

            // makes an svg object

            var draw = SVG('drawing');
            var nested = draw.nested();

            // Creates a collection of elements
            var element = cy.elements() ;
            for (var i = element.length - 1; i >= 0 ; i--) {
                makeEdges(element[i], nested) ;
            }
            window.console.log(nested.svg());
            return this; // chainability
        } );
    };

    function makeEdges(ele , nested) {

        if (ele.isNode()) {
            makeNodeBody(ele , nested) ;
        }

        if (ele.isEdge()) {

            var upPath;
            var downPath;

            if (ele.style('curve-style') === "haystack") {
                makeStraightEdge(ele , nested );
            } else {

                // edges for bezier , unbundled-bezier and segments
                // To check if an edge has a parallel edge or not
                if (ele.parallelEdges().id() ===  ele.id()) {
                    var allEdges = cy.edges();

                    var startNode = ele.source();
                    var endNode = ele.target();

                    var parallelEdge = false ;
                    var count = 0 ;

                    while(!parallelEdge && count < allEdges.length) {
                        if (startNode.id() !== endNode.id() &&
                            startNode.id() === allEdges[count].target().id() &&
                            endNode.id() === allEdges[count].source().id()) {
                            parallelEdge = true ;
                        } else {
                            count++;
                        }
                    }

                    // if no parallel edge then a single edge is made
                    if (!parallelEdge) {
                        if (ele.source().id() === ele.target().id()) {
                            makeToSelfEdge(ele , nested, ele.style('curve-style'));
                        } else if (ele.style('curve-style') === "bezier") {
                            makeStraightEdge(ele , nested );
                        } else if (ele.style('curve-style') === "unbundled-bezier") {
                            makeCurvyEdges(ele , nested);
                        } else {
                            makeSegmentedEdge(ele , nested , 'bezier');
                        }
                    }

                } else {

                    var upPath = makeparllelEdges(ele.parallelEdges() , nested , 1) ;
                    var downPath = makeparllelEdges(ele , nested , -1);
                }
            }
        }
    }

    // make parallel edges
    function makeparllelEdges(ele , nested , side) {

        var startNode = ele.source();
        var targetNode = ele.target();

        var path;
        var midPointX = (startNode.renderedPosition('x') + targetNode.renderedPosition('x'))/2 ;
        var midPointY = (startNode.renderedPosition('y') + targetNode.renderedPosition('y'))/2 ;
        if (typeof ele.style('control-point-distance') !== 'undefined') {

            var num = ele.style('control-point-distance') ;
            var deflection;

            if (ele.style('curve-style') === "unbundled-bezier") {
                deflection = num.substring(0 , num.length - 2) ;
            } else {
                deflection = num.substring(0 , num.length - 2)/2 ;
            }

            deflection *= side ;
            if (ele.style('curve-style') !== 'segments') {

                //making parallel bezier
                var weight = ele.style('control-point-weights') ;
                var leftDeflection ;
                var rightDeflection ;

                if (weight < 0.5) {
                    leftDeflection = deflection*weight;
                    rightDeflection = 2*deflection - leftDeflection ;
                } else if (weight > 0.5) {
                    weight = 1 - weight ;
                    rightDeflection = deflection*weight;
                    leftDeflection = 2*deflection - rightDeflection ;
                } else {
                    leftDeflection = deflection;
                    rightDeflection = deflection;
                }

                path = nested.path("M" + (startNode.renderedPosition('x') ) + " " + startNode.renderedPosition('y') +
                    " C " +
                    ((startNode.renderedPosition('x')) - rightDeflection)+ " " + (startNode.renderedPosition('y') + rightDeflection) + " " +
                    ((targetNode.renderedPosition('x')) + leftDeflection)+ " " + (targetNode.renderedPosition('y') + leftDeflection) + " " +
                    (targetNode.renderedPosition('x')) + " " + targetNode.renderedPosition('y')).style({
                    fill: 'transparent'
                });
                path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});

            } else {

                // making parallel segments
                var weight = ele.style('segment-weights');

                var upperControlPoint ;
                var lowerControlPoint ;

                if (weight < 0.5) {
                    weight = 0.5 - weight ;
                    lowerControlPoint = midPointX + ((targetNode.renderedPosition('x') - startNode.renderedPosition('x'))*weight) ;
                    upperControlPoint = midPointX - ((targetNode.renderedPosition('x') - startNode.renderedPosition('x'))*weight) ;
                } else if (weight > 0.5 ) {
                    weight = weight - 0.5 ;
                    lowerControlPoint = midPointX - ((targetNode.renderedPosition('x') - startNode.renderedPosition('x'))*weight) ;
                    upperControlPoint = midPointX + ((targetNode.renderedPosition('x') - startNode.renderedPosition('x'))*weight) ;
                } else {
                    lowerControlPoint = midPointX ;
                    upperControlPoint = midPointX ;
                }

                path = nested.path("M" + startNode.renderedPosition('x') + " " + startNode.renderedPosition('y') +
                    " L " +
                    (upperControlPoint) + " " + (midPointY + deflection) +
                    " L " +
                    targetNode.renderedPosition('x') + " " + targetNode.renderedPosition('y')).style({
                    fill: 'transparent'
                });
                path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});
            }
        } else {

            var num = ele.style('control-point-step-size') ;

            if (ele.style('curve-style') === "bezier") {
                deflection = num.substring(0 , num.length - 2)/2 ;
            } else {
                deflection = num.substring(0 , num.length - 2)/2*2 ;
            }

            var weight = ele.style('control-point-weights') ;
            var leftDeflection ;
            var rightDeflection ;

            if (weight < 0.5) {
                leftDeflection = deflection*weight;
                rightDeflection = 2*deflection - leftDeflection ;
            } else if (weight > 0.5) {
                weight = 1 - weight ;
                rightDeflection = deflection*weight;
                leftDeflection = 2*deflection - rightDeflection ;
            } else {
                leftDeflection = deflection;
                rightDeflection = deflection;
            }

            path = nested.path("M" + (startNode.renderedPosition('x') ) + " " + startNode.renderedPosition('y') +
                " C " +
                ((startNode.renderedPosition('x')) - rightDeflection) + " " + (startNode.renderedPosition('y') - rightDeflection) + " " +
                ((targetNode.renderedPosition('x')) + leftDeflection) + " " + (targetNode.renderedPosition('y') - leftDeflection) + " " +
                (targetNode.renderedPosition('x')) + " " + targetNode.renderedPosition('y')).style({
                fill: 'transparent'
            });
            path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});

        }

        if (ele.style('line-style') === "dotted") {
            path.style('stroke-dasharray' , "1, 1");
        } else if (ele.style('line-style') === "dashed") {
            path.style('stroke-dasharray' , "10, 5");
        }


        if (ele.style('source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'source') ;

            path.marker("start" , marker) ;
        }
        if (ele.style('mid-source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested) ;

            path.marker("mid" , marker) ;
        }
        if (ele.style('target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'target') ;

            path.marker("end" , marker) ;
        }
        if (ele.style('mid-target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'mid-target') ;

            path.marker("mid" , marker) ;
        }
        return path;
    }

    // make self edge
    function makeToSelfEdge(ele , nested , type) {
        var startNode = ele.source();
        var targetNode = ele.target();

        var deflection ;

        if (type === 'bezier') {
            if (typeof ele.style('control-point-distance') !== 'undefined') {
                deflection = ele.style('control-point-distances');
                deflection = deflection.substring(0 , deflection.length - 2);
            } else {
                deflection = ele.style('control-point-step-size');
                deflection = deflection.substring(0 , deflection.length - 2);
            }
        } else {
            deflection = ele.style('control-point-distances');
            deflection = deflection.substring(0 , deflection.length - 2)*2;
        }

        var path = nested.path("M" + startNode.renderedPosition('x') + " " + (startNode.renderedPosition('y') - startNode.height()/2)+
            " C " +
            (startNode.renderedPosition('x') )+ " " + (startNode.renderedPosition('y') - deflection) + " " +
            (targetNode.renderedPosition('x') - deflection)+ " " + (targetNode.renderedPosition('y')) + " " +
            (targetNode.renderedPosition('x') - targetNode.width()/2 ) + " " + targetNode.renderedPosition('y')).style({
            fill: 'transparent'
        });
        path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});


        if (ele.style('source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'source') ;

            path.marker("start" , marker) ;
        }
        if (ele.style('mid-source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested) ;

            path.marker("mid" , marker) ;
        }
        if (ele.style('target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'target') ;

            path.marker("end" , marker) ;
        }
        if (ele.style('mid-target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'mid-target') ;

            path.marker("mid" , marker) ;
        }
    }

    // makes straight line
    function makeStraightEdge(ele , nested) {
        var startNode = ele.source();
        var targetNode = ele.target();

        var path = nested.path("M" + startNode.renderedPosition('x') + " " + startNode.renderedPosition('y')
            + " L " + targetNode.renderedPosition('x') + " " + targetNode.renderedPosition('y')) ;

        path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});

        if (ele.style('source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'source') ;

            path.marker("start" , marker) ;
        }
        if (ele.style('mid-source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested) ;

            path.marker("mid" , marker) ;
        }
        if (ele.style('target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'target') ;

            path.marker("end" , marker) ;
        }
        if (ele.style('mid-target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'mid-target') ;

            path.marker("mid" , marker) ;
        }
    }

    // makes (un)bundled bezier edges
    function makeCurvyEdges(ele , nested) {
        var startNode = ele.source();
        var targetNode = ele.target();

        var num = ele.style('control-point-distance') ;
        var deflection = num.substring(0 , num.length - 2)/2*2 ;

        var path = nested.path("M" + (startNode.renderedPosition('x') + startNode.width()/2 ) + " " + startNode.renderedPosition('y') +
            " Q " +
            ((targetNode.renderedPosition('x') - startNode.width()/2) - deflection)+ " " + (targetNode.renderedPosition('y') + deflection) + " " +
            (targetNode.renderedPosition('x') - targetNode.width()/2 ) + " " + targetNode.renderedPosition('y')).style({
            fill: 'transparent'
        });
        path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});


        if (ele.style('source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'source') ;

            path.marker("start" , marker) ;
        }
        if (ele.style('mid-source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested) ;

            path.marker("mid" , marker) ;
        }
        if (ele.style('target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'target') ;

            path.marker("end" , marker) ;
        }
        if (ele.style('mid-target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'mid-target') ;

            path.marker("mid" , marker) ;
        }
    }

    // make a segmented edge
    function makeSegmentedEdge(ele , nested) {
        var startNode = ele.source();
        var targetNode = ele.target();

        var num = ele.style('control-point-distance') ;
        var deflection = num.substring(0 , num.length - 2)/2 ;

        var weight = ele.style('segment-weights');

        var midPointX = (startNode.renderedPosition('x') + targetNode.renderedPosition('x'))/2 ;
        var midPointY = (startNode.renderedPosition('y') + targetNode.renderedPosition('y'))/2 ;

        if (weight < 0.5) {
            weight = 0.5 - weight ;
            midPointX = midPointX - ((targetNode.renderedPosition('x') - startNode.renderedPosition('x'))*weight) ;
        } else if (weight > 0.5 ) {
            weight = weight - 0.5 ;
            midPointX = midPointX + ((targetNode.renderedPosition('x') - startNode.renderedPosition('x'))*weight) ;
        }

        var path = nested.path("M" + startNode.renderedPosition('x') + " " + startNode.renderedPosition('y') +
            " L " +
            midPointX + " " + (midPointY + deflection) +
            " L " +
            targetNode.renderedPosition('x') + " " + targetNode.renderedPosition('y')).style({
            fill: 'transparent'
        });
        path.stroke({ width : ele.style('width') , color : ele.style('line-color') , opacity : ele.style('opacity')});


        if (ele.style('source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'source') ;

            path.marker("start" , marker) ;
        }
        if (ele.style('mid-source-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested) ;

            path.marker("mid" , marker) ;
        }
        if (ele.style('target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'target') ;

            path.marker("end" , marker) ;
        }
        if (ele.style('mid-target-arrow-shape') !== 'none') {
            var marker = makeArrowHeads(ele , nested , 'mid-target') ;

            path.marker("mid" , marker) ;
        }
    }

    // makes the arrow heads
    function makeArrowHeads(ele, nested , type) {

        var marker = nested.marker(6,6,function(shape) {

            // arrow shapes
            if (ele.style(type + '-arrow-shape') === 'tee') {
                shape.rect(0.5 , 2.5).center(2,2);
            } else if (ele.style(type + '-arrow-shape') === 'triangle') {
                shape.polygon("0.5,0.5 3.5,2 0.5,3.5");
            } else if (ele.style(type + '-arrow-shape') === 'triangle-tee') {
                shape.rect(0.5 , 2.5).center(0,2.5);
                shape.polygon("0.5,0.5 3.5,2 0.5,3.5").center(4,2.5);
            } else if (ele.style(type + '-arrow-shape') === 'triangle-backcurve') {
                shape.polygon("0.5,0.5 3.5,2 0.5,3.5 1.5,2");
            } else if (ele.style(type + '-arrow-shape') === 'square') {
                shape.rect(2 , 2).center(2,2) ;
            } else if (ele.style(type + '-arrow-shape') === 'circle') {
                shape.circle(2.5).center(2,2);
            } else if (ele.style(type + '-arrow-shape') === 'diamond') {
                shape.polygon('1,2 2,1 3,2 2,3');
            }

            // if - else to style the shapes
            if (ele.style(type + '-arrow-fill') === 'hollow') {

                this.style({
                    fill: 'transparent'
                });

                this.stroke({
                    color : ele.style(type + '-arrow-color')
                });
            } else {

                this.style({
                    fill: ele.style(type + '-arrow-color')
                });

                this.stroke({
                    color : ele.style(type + '-arrow-color')
                });
            }
        }) ;

        // reverse if source
        if (type === 'source' || type === 'mid-source') {
            marker.attr({
                orient : 'auto-start-reverse'
            }) ;
        }
        return marker ;
    }

    // makes the whole node body
    function makeNodeBody(node , nested) {
        var shape = node.style('shape');
        var currNode;

        // if shape is a circle
        // some shapes have width or height subtracted to keep them at the center

        if (shape === 'ellipse') {
            currNode = nested.ellipse(node.style('width') , node.style('height')) ;
            currNode.x(node.renderedPosition('x'));
            currNode.y(node.renderedPosition('y'));

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'rectangle') {
            currNode = nested.rect(node.style('width') , node.style('height')) ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'roundrectangle') {
            currNode = nested.rect(node.style('width') , node.style('height')) ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.radius(8);

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'triangle') {
            currNode = nested.polygon("0,0 15,30 -15,30") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'pentagon') {
            currNode = nested.polygon("0,-15 -14,-5 -9,12 9,12 14,-5") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'hexagon') {
            currNode = nested.polygon("7,-13 -8,-13 -15,0 -7,13 7,13 15,0") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'heptagon') {
            currNode = nested.polygon("0,-15 -12,-9 -15,3 -7,14 7,14 15,3 12,-9") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'octagon') {
            currNode = nested.polygon("6,-14 -6,-14 -14,-6 -14,6 -6,14 6,14 14,6 14,-6") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'star') {
            currNode = nested.polygon("0,-25 7,-10 23,-7.5 11.5,3.5 14.5,20 0,12.5 -14.5,20 -11.5,3.5 -23.5,-7.5 -7,-10") ;

            currNode.x(node.renderedPosition('x') - (node.width()) );
            currNode.y(node.renderedPosition('y') - (node.height()) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else if (shape === 'diamond') {
            currNode = nested.polygon("0,0 15,15 0,30  -15,15") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        }  else if (shape === 'vee') {
            currNode = nested.polygon("0,5 20,-10 0,30  -20,-10") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        }  else if (shape === 'rhomboid') {
            currNode = nested.polygon("0,0 20,0 30,30 10,30 0,0") ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        } else {
            var points = node.style('shape-polygon-points');
            var pointArr = points.split(" ");
            points = "" ;
            for (var i=0 ; i < pointArr.length ;i++) {
                points += pointArr[i] * 20;
                if (i % 2 !== 0) {
                    points += ",";
                } else {
                    points += " " ;
                }
            }
            currNode = nested.polygon(points) ;

            currNode.x(node.renderedPosition('x') - (node.width() / 2) );
            currNode.y(node.renderedPosition('y') - (node.height() / 2) );

            currNode.fill({color : node.style('background-color') , opacity : node.style('background-opacity') });

            currNode.style('stroke-width' , node.style('border-width'));
            currNode.style('stroke' , node.style('border-color'));
            currNode.style('stroke-opacity' , node.style('border-opacity'));

            if (node.style('border-style') === "dotted") {
                currNode.style('stroke-dasharray' , "1, 1");
            } else if (node.style('border-style') === "dashed") {
                currNode.style('stroke-dasharray' , "5, 10");
            }
        }

        var label = nested.text(node.style('label'));
        label.font({
            family : node.style('font-family'),
            size : node.style('font-size'),
            opacity : node.style('text-opacity'),
            color : node.style('color'),
            weight : node.style('font-weight'),
            transform : node.style('text-transform'),
            margin : node.style('text-margin-x')
        });

        // x and y position of the label
        var height = node.height();

        if (node.style('text-valign') === 'top') {
            label.y(node.renderedPosition('y') - height*1.25 );
        } else if (node.style('text-valign') === 'center') {
            label.y(node.renderedPosition('y') - height/3);
        } else {
            label.y(node.renderedPosition('y') + height/2 );
        }

        var width = node.width() ;

        if (node.style('text-halign') === 'left') {
            label.x(node.renderedPosition('x') - 2*width);
        } else if (node.style('text-halign') === 'center') {
            label.x(node.renderedPosition('x') - width/1.5);
        } else {
            label.x(node.renderedPosition('x') + width/2);
        }
    }

    if( typeof module !== 'undefined' && module.exports ){ // expose as a commonjs module
        module.exports = register;
    }

    if( typeof define !== 'undefined' && define.amd ){ // expose as an amd/requirejs module
        define('cytoscape-svg-convertor', function(){
            return register;
        });
    }

    if( typeof cytoscape !== 'undefined' ){ // expose to global cytoscape (i.e. window.cytoscape)
        register( cytoscape );
    }

})();
