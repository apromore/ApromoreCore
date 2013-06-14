/**
 * Copyright (c) 2012 Felix Mannhardt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */

/**
 * Visualise Similarity Clusters using D3 Force-Directed-Layout
 *
 * Written as a self executing anonymous function to prevent global namespace
 * pollution.
 */
(function (clusterVisualisation, d3) {

    d3.selection.prototype.size = function () {
        var n = 0;
        this.each(function () {
            ++n;
        });
        return n;
    };

    this.defaultParam = {
        data:'',
        doAnimate:false,
        canvasId:'canvas',
        onNodeClick:function (data, onClose) {
        },
        onSelectNode:function (selection, size) {
        }
    };

    this.nodeColorScale = d3.scale.category20();
    this.numberFormat = d3.format('.3r');

    this.currentParam = null;
    this.forceLayout = null;
    this.svgCanvas = null;

    this.currentLinks = [];
    this.currentNodes = [];

    /**
     * Show the Visualisation for given Parameters.
     * See defaultParam for the parameter object.
     */
    clusterVisualisation.visualiseData = function (param) {

        if (!param) {
            param = defaultParam;
        }

        // Guess width and height from parent element
        param.width = jq("#" + param.canvasId).innerWidth();
        param.height = jq("#" + param.canvasId).innerHeight();

        currentParam = param;

        var data = JSON.parse(param.data);

        currentLinks.push.apply(currentLinks, data.edges);
        currentNodes.push.apply(currentNodes, data.nodes);

        showVisualisation();

    };

    /**
     * Update the Visualisation with new Data.
     */
    clusterVisualisation.refreshData = function (data) {

        forceLayout.stop();

        // Remove all Elements that are not persistent e.g. loaded first time
        for (var i = 0; i < currentLinks.length; i++) {
            if (currentLinks[i].isVolantile) {
                currentLinks.splice(i, 1);
                i--;
            }
        }

        data.edges = data.edges.map(function (el) {
            el.isVolantile = true;
            return el;
        });

        data.nodes = data.nodes.map(function (el) {
            el.isVolantile = true;
            return el;
        });

        currentLinks.push.apply(currentLinks, data.edges);
        currentNodes.push.apply(currentNodes, data.nodes);

        setupLinks();
        setupNodes();

        startWithAnimation();

    };

    /**
     * Adjust the size of the visualisation
     */
    clusterVisualisation.resize = function (width, height) {

        forceLayout.size([ width, height ]);
        forceLayout.resume();

    };

    clusterVisualisation.toggleFragments = function () {
        var currentVisibility = d3.select("circle.fragment").attr("display");
        d3.selectAll("circle.fragment").attr("display",
            (currentVisibility == "none") ? "block" : "none");
    };

    clusterVisualisation.toggleMedoids = function () {
        var currentVisibility = d3.select("circle.medoid").attr("display");
        d3.selectAll("circle.medoid").attr("display",
            (currentVisibility == "none") ? "block" : "none");
    };

    clusterVisualisation.toggleIntra = function () {
        var currentVisibility = d3.select("line.intra").attr("display");
        d3.selectAll("line.intra").attr("display",
            (currentVisibility == "none") ? "block" : "none");
    };

    clusterVisualisation.toggleInter = function () {
        var currentVisibility = d3.select("line.inter").attr("display");
        d3.selectAll("line.inter").attr("display",
            (currentVisibility == "none") ? "block" : "none");
    };

    clusterVisualisation.getSelectedNodes = function () {
        var selectedNodeIdArray = [];
        d3.selectAll("circle.selected").each(function (d, index) {
            selectedNodeIdArray.push(d.id);
        });
        return selectedNodeIdArray;
    };

    /**
     * Run the visualisation with current data and parameters
     */
    function showVisualisation() {

        forceLayout = setupForceDirectedLayout();
        svgCanvas = setupCanvas();

        // Load Data
        forceLayout.nodes(currentNodes).links(currentLinks);

        setupLinks();
        setupNodes();

        if (currentParam.doAnimate) {
            startWithAnimation();
        } else {
            startWithoutAnimation();
        }
    }

    /**
     * Initialize the Force-Directed-Layout of D3
     */
    function setupForceDirectedLayout() {

        return force = d3.layout.force().charge(-100).linkDistance(
            function (link, index) {
                return link.value;
            }).linkStrength(function (link, index) {
                if (link.isInterClusterEdge) {
                    return 1;
                } else {
                    return 1;
                }
            }).size([ currentParam.width, currentParam.height ]);

    }

    /**
     *  Initialize the main SVG element of the visualisation
     */
    function setupCanvas() {

        var canvas = d3.select('#' + currentParam.canvasId);

        if (canvas.select("svg").size() > 0) {
            canvas.selectAll("line.link").data([]).exit().remove();
            canvas.selectAll("circle.node").data([]).exit().remove();
            canvas.select("svg").remove();
        }

        var svg = canvas.append("svg").attr("pointer-events", "all").attr(
            "viewbox", "0 0 800 800").attr("preserveAspectRatio",
            "xMinYMin meet").attr("width", currentParam.width).attr("height",
            currentParam.height).call(
            d3.behavior.zoom().on(
                "zoom",
                function () {
                    svg.attr("transform", "translate("
                        + d3.event.translate + ")" + " scale("
                        + d3.event.scale + ")");
                })).append('svg:g').call(
            d3.behavior.zoom().on(
                "zoom",
                function () {
                    svg.attr("transform", "translate("
                        + d3.event.translate + ")" + " scale("
                        + d3.event.scale + ")");
                }));

        return svg;
    }

    /**
     * Generate the new Links
     */
    function setupLinks() {

        var linkUpdates = svgCanvas.selectAll("line.link").data(currentLinks);

        linkUpdates.exit().remove();

        var links = linkUpdates.enter().insert("line");

        links.classed("link", true).classed("intra",function (d) {
            return !d.isInterClusterEdge;
        }).classed("inter",function (d) {
                return d.isInterClusterEdge;
            }).classed("persistent",function (d) {
                return d.isPersistent;
            }).append("title").text(function (d) {
                return numberFormat(d.value / 100);
            });

    }

    /**
     * Generate the new Nodes
     */
    function setupNodes() {

        var nodesUpdates = svgCanvas.selectAll("circle.node").data(currentNodes);

        nodesUpdates.exit().remove();

        var nodes = nodesUpdates.enter().append("circle").attr("class",function (d) {
            return (d.isMedoid) ? "node medoid" : "node fragment";
        }).attr("r",function (d) {
                return (d.isMedoid) ? 7 : 5;
            }).style("fill",function (d) {
                return nodeColorScale(d.group);
            }).call(forceLayout.drag).on("mouseover",function () {
                d3.select(this).transition().attr("r",function () {
                    return 10;
                }).delay(0).duration(2000).ease("elastic", 2, .5);
            }).on("mouseout", function () {
                d3.select(this).transition().attr("r",function (d) {
                    return (d.isMedoid) ? 7 : 5;
                }).delay(100).duration(1000);
            });

        nodes.append("title").text(function (d) {
            return (d.isMedoid) ? "Medoid " + d.id : "Fragment " + d.id;
        });

        setupNodesEvent(nodes);
    }

    /**
     * Setup the onClick Event on a Node. param.onNodeClick will be called if
     * initialized.
     */
    function setupNodesEvent(nodes) {

        nodes.on("click", function (d) {

            if (d3.event.ctrlKey || d3.event.altKey) {

                if (d3.select(this).datum().isMedoid) {
                    d3.select(this).classed("selected",
                        !(d3.select(this).classed("selected")));

                }

                var selectedNodes = d3.selectAll("circle.selected");
                currentParam.onSelectNode(selectedNodes, selectedNodes.size());

            } else {

                // Store old event handlers
                var oldClickHandler = d3.select(this).on("click");
                var oldOverHandler = d3.select(this).on("mouseover");
                var oldOutHandler = d3.select(this).on("mouseout");

                var oldNode = d3.select(this);

                // Remove handlers on this node only
                d3.select(this).on("click", function () {
                });
                d3.select(this).on("mouseover", function () {
                });
                d3.select(this).on("mouseout", function () {
                });

                d3.select(this).transition().attr("r",function () {
                    return 10;
                }).delay(0).duration(2000).ease("elastic", 2, .5);

                var text = svgCanvas.append("text").text(d.id).attr("x",
                    d3.select(this).attr("cx")).attr("y",
                    d3.select(this).attr("cy") - 15).attr("dy", ".35em");

                if (currentParam.onNodeClick) {
                    currentParam.onNodeClick(d, function () {
                        // Reattach handlers
                        oldNode.on("click", oldClickHandler);
                        oldNode.on("mouseover", oldOverHandler);
                        oldNode.on("mouseout", oldOutHandler);

                        oldNode.transition().attr("r",function (d) {
                            return (d.isMedoid) ? 7 : 5;
                        }).delay(100).duration(1000);

                        text.remove();
                    });
                }

            }

        });

    }

    function startWithoutAnimation() {

        var loadingMessage = svgCanvas.append("text").attr("x", 30).attr("y",
            30).attr("dy", ".35em").attr("text-anchor", "left").text(
            "Loading, please wait ...");

        setTimeout(function () {
            var maxRounds = 250;

            forceLayout.start();
            for (var i = maxRounds; i > 0; --i) {
                forceLayout.tick();
            }
            forceLayout.stop();

            svgCanvas.selectAll("line.link").attr("x1",function (d) {
                return d.source.x;
            }).attr("y1",function (d) {
                    return d.source.y;
                }).attr("x2",function (d) {
                    return d.target.x;
                }).attr("y2", function (d) {
                    return d.target.y;
                });

            svgCanvas.selectAll("circle.node").attr("cx",function (d) {
                return d.x;
            }).attr("cy", function (d) {
                    return d.y;
                });

            loadingMessage.remove();
        }, 10);

    }

    function startWithAnimation() {

        var link = svgCanvas.selectAll("line.link");
        var node = svgCanvas.selectAll("circle.node");

        forceLayout.start();
        forceLayout.on("tick", function () {
            link.attr("x1",function (d) {
                return d.source.x;
            }).attr("y1",function (d) {
                    return d.source.y;
                }).attr("x2",function (d) {
                    return d.target.x;
                }).attr("y2", function (d) {
                    return d.target.y;
                });

            node.attr("cx",function (d) {
                return d.x;
            }).attr("cy", function (d) {
                    return d.y;
                });
        });

    }

})(window.clusterVisualisation = window.clusterVisualisation || {}, d3);