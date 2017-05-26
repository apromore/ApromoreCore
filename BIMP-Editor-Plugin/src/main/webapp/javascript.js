$(document).ready(function () {

    browserValidation();
    removeLastButton();

    // Read in the BPMN from Apromore
    bimp.file.readTextToDocument(window.apromoreBPMN);
    bimp.parser.start();
    showForm(0);

    if (bimp.file.inputFiles[0]) {
        try {
            showForm(0);
        } catch (e) {
            console.log(e);
        }
    }

    if (validate) {
        $.each(validate, function (name, content) {
            $("body").delegate(validate[name]["class"], "change", function () {
                validate.validateField(this, name);
            });

            $("body").delegate(validate[name]["class"], "keyup", function () {
                validate.validateField(this, name);
            });
        });
    }

    $('body').delegate(".xor", "keyup", function () {
        validateXOR(this);
    });

    $(".resources .add").click(function () {
        var row = $(this).parents().find(".resources").find(".resource:first").clone(true);
        $(row).find("input").each(function () {
            $(this).val("");
        });
        $(row).attr("data-id", generateId());
        var count = $(this).parents().find(".resources").find(".resource").size();
        $(row).find("._name").val("Resource" + (count + 1));

        var tbody = $(this).parents().find(".resources tbody");
        $(row).appendTo(tbody)
        .find('td')
        .wrapInner('<div style="display: none;" />')
        .parent()
        .find('td > div')
        .slideDown(300, function () {
            var $set = $(this);
            $set.replaceWith($set.contents());
            bimp.forms.read.resources();
            updateResourceDropdowns();
        });
        $(".resources").find(".remove").show();
    });

    $(".timetables .add").click(function () {
        var row = $(this).parents().find(".timetables").find(".timetable:first").clone(false);
        var count = $(this).parents().find(".timetables").find(".timetable").size();
        if (count == 0) {
            row = timeTableRow;
        }
        $(row).attr("data-default", "false");
        $(row).attr("data-id", generateId());
        $(row).find(".timepicker").removeClass("hasDatepicker");
        $(row).find(".timepicker").attr("data-id", "");
        $(row).find(".timepicker").attr("data-default", "false");
        $(row).find(".startday").val("MONDAY");
        $(row).find(".endday").val("FRIDAY");
        $(row).find(".begintime").val(bimp.forms.defaultBeginTime);
        $(row).find(".endtime").val(bimp.forms.defaultEndTime);
        $(row).find(".name").val("Timetable" + (count + 1));
        $(row).find(".remove").show();
        $(row).find(".name").show();
        $(row).find(".defaultNameLabel").hide();
        var tbody = $(this).parents().find(".timetables tbody");
        $(row).appendTo(tbody)
        .find('td')
        .wrapInner('<div style="display: none;" />')
        .parent()
        .find('td > div')
        .slideDown(300, function () {
            var $set = $(this);
            $set.replaceWith($set.contents());
            bimp.forms.read.timetables();
            updateTimetableDropdowns();
            $(".timepicker").timepicker({
                timeFormat: "hh:mm:ss"
            });
        });
        removeLastButton();
    });

    $("#continue-button").click(function () {
        $("#continue-button").attr("disabled", true);
        try {
            bimp.parser.start();
            showForm(400);
        } catch (e) {
            bimp.tools.openError("Unable to parse model! See the browser console for details.");
            console.log(e);
        }
    });

    $(".toggle-trigger").click(function () {
        $(this).next(".toggle-div").slideToggle("slow", function () {
            if (areAllExpanded()) {
                $(".toggle-all").removeClass("expand");
                $(".toggle-all").text("Collapse all");
            } else {
                $(".toggle-all").addClass("expand");
                $(".toggle-all").text("Expand all");
            };
        });
    });

    $(".toggle-all").click(function () {

        if ($(this).hasClass("expand")) {

            $(".toggle-div").each(function () {
                if (!($(this).is(":visible"))) {
                    $(this).slideToggle("slow");
                }
            });

            $(".toggle-all").removeClass("expand"); ;
            $(".toggle-all").text("Collapse all");
        } else {
            $(".toggle-div").each(function () {
                if ($(this).is(":visible")) {
                    $(this).slideToggle("slow");
                }
            });
            $(".toggle-all").addClass("expand"); ;
            $(".toggle-all").text("Expand all");
        }
    });

    $("body").delegate(".remove", "click", function () {
        var tr = $(this).parent().parent();
        if ($(tr).hasClass("timetables") && $(".timetable").size() == 1) {
            timeTableRow = $(tr).clone(true);
        }
        $(tr)
        .find('td')
        .wrapInner('<div style="display: block;" />')
        .parent()
        .find('td > div')
        .slideUp(300, function () {
            $(this).parent().parent().remove();
            bimp.forms.read.resources();
            bimp.forms.read.timetables();
            updateResourceDropdowns();
            updateTimetableDropdowns();
            removeLastButton();
            updateHistogramSumAndError();
        });
    });

    $("body").delegate(".resource ._name", "focusout", function () {
        bimp.forms.read.resources();
        updateResourceDropdowns();
    });

    $("body").delegate(".timetable .name", "focusout", function () {
        bimp.forms.read.timetables();
        updateTimetableDropdowns();
    });

    $("body").delegate("._type", "change", function () {
        updateTypeSelection(this);
    });

    $("body").delegate("._currency", "change", function () {
        $(".currencyText").text($("._currency").val());
    });

    $("body").delegate("._probability", "change", function () {
        updateHistogramSumAndError(this);
    });

    $("#startSimulationButton").click(function () {
        if (validateForm()) {
            bimp.forms.read.start();
            bimp.file.updateFile();
            bimp.file.uploadFile(function (errorText) {
                console.log(errorText);
                bimp.tools.openError("Failed to start simulation: " + errorText);
            });
        }
    });

    $(".saveFileButton").click(function () {
        bimp.forms.read.start();
        bimp.file.saveFileAs(false);
    });

    $(".saveResultsButton").click(function () {
        bimp.forms.read.start();
        bimp.file.saveFileAs(true);
    });    

    $("#downloadLog").click(function () {
        bimp.requests.getSimulationResultsMxml();
    });

    $("#downloadCsv").click(function () {
        bimp.requests.getSimulationResultsCsv();
    });

    $("#viewModelButton").click(function () {
        var previewWindow = window.open(bimp.bpmnviewerurl,
                "previewModelWindow",
                "width=1000, location=no, menubar=no, status=no, resizable=yes, toolbar=no, scrollbars=yes");
    });
    
    $("#viewModelHeatmapButton").click(function () {
        var previewWindow = window.open(bimp.bpmnviewerurl,
                "heatmapWindow",
                "width=1000, location=no, menubar=no, status=no, resizable=yes, toolbar=no, scrollbars=yes");
    });

    $(".help-nav-button").click(function () {
        var clickedButton = $(this);
        $(".help-text").each(function () {
            if ($(this).is(":visible")) {
                if ($(this).attr("id") == "bimp-help" && $(clickedButton).attr("id") == "bimp-help-trigger"
                     || $(this).attr("id") == "bimp2-help" && $(clickedButton).attr("id") == "bimp2-trigger"
                     || $(this).attr("id") == "bimpeditors-help" && $(clickedButton).attr("id") == "bimpeditors-trigger"
                     || $(this).attr("id") == "ui-help" && $(clickedButton).attr("id") == "ui-help-trigger") {
                    return false;
                }
                $(this).fadeOut(300, function () {
                    if ($(clickedButton).attr("id") == "bimp-help-trigger") {
                        $("#bimp-help").fadeIn(300);
                    }
                    if ($(clickedButton).attr("id") == "bimp2-trigger") {
                        $("#bimp2-help").fadeIn(300);
                    }
                    if ($(clickedButton).attr("id") == "bimpeditors-trigger") {
                        $("#bimpeditors-help").fadeIn(300);
                    }
                    if ($(clickedButton).attr("id") == "ui-help-trigger") {
                        $("#ui-help").fadeIn(300);
                    }
                });
                return false;
            }
        });
    });

    $("body").delegate("#backToEditData", "click", function () {
        $("#resultsPage").fadeOut("fast", function () {
            //$(this).remove();
        });
        $("#uploadPage").fadeIn("fast");
        isResultsShown = false;
    });

    $("body").delegate(".close", "click", function () {
        closeLoadingModal();
    });

    $("body").delegate(".gatewayGroup,.modelSimInfo,.task,.catchEvent", "focus", function () {
        var that = this;
        if ($(this).hasClass("gatewayGroup")) {
            $(this).find(".gateway").each(function (index, element) {
                var sourceId = $(element).parent().attr("id");
                var source = $(bimp.parser.xmlFile).find(
                        $(bimp.parser.xmlFile).find(
                            "#" + $($(bimp.parser.xmlFile).find(
                                    '[targetRef=' + sourceId + ']')[0]).attr('sourceRef'))[0]).attr("id");
                var target = $(element).find(".targetRef").text();
                $("[data-id='" + source + "']").addClass("source");
                $("[data-id='" + target + "']").addClass("target");
                if ($("[data-id='" + target + "']").size() > 0 && $("[data-id='" + target + "']").is(":visible")) {
                    $(that).addClass("focus");
                    addTargetIndicators($("[data-id='" + target + "']").position().top, $(element).parent().position().top);
                }
                if ($("[data-id='" + source + "']").size() > 0 && $("[data-id='" + source + "']").is(":visible")) {
                    $(that).addClass("focus");

                    addSourceIndicators($("[data-id='" + source + "']").position().top, $(element).parent().position().top);
                }
            });
        } else {
            var id = $(this).attr("data-id");
            var source = $(bimp.parser.xmlFile).find(
                    $(bimp.parser.xmlFile).find(
                        "#" + $($(bimp.parser.xmlFile).find(
                                '[targetRef=' + id + ']')[0]).attr('sourceRef'))[0]).attr("id");
            var target = $(bimp.parser.xmlFile).find(
                    $(bimp.parser.xmlFile).find(
                        "#" + $($(bimp.parser.xmlFile).find(
                                '[sourceRef=' + id + ']')[0]).attr('targetRef'))[0]).attr("id");
            $("[data-id='" + source + "']").addClass("source");
            $("#" + source).addClass("source");
            $("[data-id='" + target + "']").addClass("target");
            $("#" + target).addClass("target");
            if ($("[data-id='" + target + "']").size() > 0 && $("[data-id='" + target + "']").is(":visible")) {
                $(that).addClass("focus");
                addTargetIndicators($("[data-id='" + target + "']").position().top, $(this).position().top);
            }
            if ($("[data-id='" + source + "']").size() > 0 && $("[data-id='" + source + "']").is(":visible")) {
                $(that).addClass("focus");
                addSourceIndicators($("[data-id='" + source + "']").position().top, $(this).position().top);
            }
            if ($("#" + target).size() > 0 && $("#" + target).is(":visible")) {
                $(that).addClass("focus");
                addTargetIndicators($("#" + target).position().top, $(this).position().top);
            }
            if ($("#" + source).size() > 0 && $("#" + source).is(":visible")) {
                $(that).addClass("focus");
                addSourceIndicators($("#" + source).position().top, $(this).position().top);
            }
        }
    });

    $("body").delegate(".gatewayGroup,.task,.modelSimInfo,.catchEvent", "focusout", function () {
        $(this).removeClass("focus");
        $(".target").removeClass("target");
        $(".source").removeClass("source");
        $(".targetText").remove();
        $(".sourceText").remove();
        $(".targetIndicator").remove();
        $(".sourceIndicator").remove();
    });
    $("body").on("click", "#uploadNewFile", function (e) {
        e.preventDefault();
        window.location.reload();
        return false;
    });

    $(window).on('resize', function(){
        updateAllVisibleHistogramPosition();
    });    
    
    $("._histogramButton").click(function () {
        var div = $(this).parents(".distribution-div").first().parent().find(".histogram-data-div");        
        
        if (div.is(":visible")) {
            div.hide();
        }
        else {
            if (div.find(".histogram-data-table").length == 0) {
                createAndAppendHistogramTable(div.parent());
            }
            
            div.find(".histogram-data-table tbody").find(".remove").show();
            div.find(".histogram-data-table tbody").find(".remove:first").hide();
            
            updateHistogramSumAndError(div.find("._probability:first"));
            div.show();
            updateAllVisibleHistogramPosition();
        }
    });
    
    $(".closeHx").click(function () {
        var div = $(this).parents(".histogram-data-div:first").first();
        div.hide();
    });
    
    $(".addHx").click(function () {
        var row = $(".histogram-data-table:first").find(".histogram-data-row:first").first().clone(true);
        $(row).find("input").each(function () {
            $(this).val("");
        });
        
        var tbody = $(this).parents(".histogram-data-table:first").find("tbody");
        $(row).appendTo(tbody)
        .find('td')
        .wrapInner('<div style="display: none;" />')
        .parent()
        .find('td > div')
        .slideDown(300, function () {
            var $set = $(this);
            $set.replaceWith($set.contents());
        });
     
        $(".histogram-data-table tbody").find(".remove:last").show();
    });
});
// namespace the global functions and objects under bimp.util
var timeTableRow;

var showForm = function (delay) {
    $("body").trigger(bimp.testutil.config.modelSimInfo, ["showForm"]);
    
    $("#top-model-area").fadeOut(delay, function () {
        $("#data-input").fadeIn(delay, (function () {
                $("#startSimulationButton").fadeIn(delay);
                $("#logCheckBox").fadeIn(delay);
                $("input[title]").tooltip({
                    effect: "fade"
                });
                $("img[title]").tooltip({
                    effect: "fade"
                });
                $("button[title]").tooltip({
                    effect: "fade"
                });
                $("a[title]").tooltip({
                    effect: "fade"
                });
                $("div[title]").tooltip({
                    effect: "fade"
                });
                $("span[title]").tooltip({
                    effect: "fade"
                });
            }));
    });
    
    if (bimp.results.results.process) {
       showResults();    
       return;
    }
    
    $("body").trigger(bimp.testutil.config.endEvent, ["showForm"]);
};

var areAllExpanded = function () {
    var result = true;
    $(".toggle-div").each(function (i, element) {
        if (!($(element).is(":visible"))) {
            result = false;
        }
    });
    return result;
};

var removeLastButton = function () {
    if ($(".resources .resource").size() == 1) {
        $(".resources .resource .remove").hide();
    }
    if ($(".timetables .timetable").size() == 1) {
        $(".timetables .timetable .remove").hide();
    }
};

var updateResourceDropdowns = function () {
    $("select.resourceId").each(function () {
        var curVal = $(this).val();

        $(this).find("option").remove();
        var that = this;

        $.each(bimp.parser.modelSimInfo.resources.resource, function (i, resource) {
            $(that).append($("<option></option>").attr("value", resource._id).text(resource._name));
        });
        $(this).val(curVal);

    });
};

var updateTimetableDropdowns = function () {
    $("select._timetableId").each(function () {
        var curVal = $(this).val();

        $(this).find("option").remove();
        var that = this;

        $.each(bimp.parser.modelSimInfo.timetables.timetable, function (i, tt) {
            $(that).append($("<option></option>").attr("value", tt._id).text(getTimetableName(tt)));
        });
        $(this).val(curVal);

    });

    $(".timetables .timetable").each(function (name, obj) {
        var isdefault = $(obj).attr("data-default") == "true";
        var is247 = $(obj).attr("data-id") == bimp.timetable247Id;
        if (isdefault || is247) {
            $(obj).find(".remove").hide();
            $(obj).find(".name").hide();

            if (isdefault) {
                $(obj).find(".defaultNameLabel").show();
                $(obj).find(".default247Label").hide();
            } else {
                $(obj).find(".defaultNameLabel").hide();
                $(obj).find(".default247Label").show();
            }
        } else {
            $(obj).find(".remove").show();
            $(obj).find(".name").show();
            $(obj).find(".defaultNameLabel").hide();
            $(obj).find(".default247Label").hide();
        }

        if (is247) {
            $(obj).find(".startday").prop("disabled", true);
            $(obj).find(".endday").prop("disabled", true);
            $(obj).find(".begintime").prop("disabled", true);
            $(obj).find(".endtime").prop("disabled", true);
        } else {
            $(obj).find(".startday").prop("disabled", false);
            $(obj).find(".endday").prop("disabled", false);
            $(obj).find(".begintime").prop("disabled", false);
            $(obj).find(".endtime").prop("disabled", false);
        }
    });
};

var updateAllTypeSelections = function () {
    $("select._type").each(function () {
        updateTypeSelection(this);
    });
};

var updateTypeSelection = function (element) {
    // update the fields to be show for selected duration option values

    var selection = $(element).val();
    $(element).parent().find("input").parent().hide();
    $(element).parent().find("button").parent().hide();

    if (selection === "HISTOGRAM")
      $(element).parent().find(".timeUnit").hide();
    else
      $(element).parent().find(".timeUnit").show();

    
    $(element).parent().find("." + selection).parent().show();
    $(element).parent().find("input").each(function (name, obj) {
        if (!$(obj).hasClass(selection)) {
            $(obj).val("");
        }
    });
};

var openLoadingModal = function () {
    $("body").trigger(bimp.testutil.config.modelSimInfo, ["openLoadingModal"]);
    $("body").append("<div id='modal-bg'></div>");
    $("body").append("<div id='loading'>" +
        "<div class='close'><span>x</span></div>" +
        "<div class='header'><h2 class='title'>Running your simulation, please wait</h2></div>" +
        "<div class='status-wrap'><p><span class='status'>Status</span></p></div>" +
        "<div class='progressBarContainer'><div class='progressBar'></div></div>" +
        "<h2 class='progress'>Progress</h2>" +
        "</div>");
    var left = ($(document).width() - $("#loading").width()) / 2;
    $("#loading").css({
        "left": left
    });
    $("#modal-bg").css({
        "height": $(document).height()
    });
    timerId = setInterval(function () {
            getStatus();
        }, interval);
    $("#modal-bg").fadeIn();
    $("#loading").fadeIn();
};

var closeLoadingModal = function () {
    $("#loading").fadeOut(function () {
        $(this).remove();
    });
    $("#modal-bg").fadeOut(function () {
        $(this).remove();
    });
};

showResults = function() {
    $("#uploadPage").fadeOut();
    closeLoadingModal();
    if ($("#mxmlLog").is(':checked'))
        $("#download-div").show();
    else
        $("#download-div").hide();
    $("#resultsPage").fadeIn("fast");
    //$("#header").after(data);
    $("body").trigger(bimp.testutil.config.endEvent, ["openLoadingModal"]);
    isResultsShown = true;  
};

getStatus = function () {
    //getting the status of the simulation
    timer += interval;
    bimp.requests.getSimulationStatus(function (respDom) {
        var data = x2js.xml2json(respDom);
        if (!data.SimulationStatusResponse) {
            clearInterval(timerId);
            showLoadingError({
                _error: "Simulator did not return simulation status"
            });
            return;
        }
        data = data.SimulationStatusResponse.status;
        switch (data._status) {
        case ("QUEUED"):
            if (pointCount < 4) {
                pointCount += 1;
                $(".status").text(data._status + generateXCharacters(pointCount, "."));
            } else {
                pointCount = 0;
            }
            break;
        case ("RUNNING"):
            $(".progressBarContainer").fadeIn();
            var width = data._completed / data._total * 100 + "%";
            $(".progressBar").css({
                "width": width
            });
            if (pointCount < 4) {
                $(".status").text(data._status + generateXCharacters(pointCount, "."));
                pointCount += 1;
            } else {
                pointCount = 0;
            }
            break;
        case ("FINALIZING"):
            $(".progressBarContainer").fadeOut();
            if (pointCount < 4) {
                $(".status").text(data._status + " and writing logs" + generateXCharacters(pointCount, "."));
                pointCount += 1;
            } else {
                pointCount = 0;
            }
            break;
        case ("COMPLETED"):
            clearInterval(timerId);
            if (!isResultsShown) {
                bimp.requests.getSimulationResults(function (respObj) {
                    var data = x2js.xml2json(respObj);
                    bimp.charts.init(data.SimulationKPIResponse);
                    bimp.results.init(data.SimulationKPIResponse);
                    showResults();
                },
                    // error handler
                    function (data) {
                    clearInterval(timerId);
                    data._error = "Unable to retrieve results";
                    showLoadingError(data);
                    isResultsShown = false;
                });
            }
            break;
        case ("FAILED"):
            clearInterval(timerId);
            showLoadingError(data);
            break;
        }
    },
        // error handler
        function (data) {
        clearInterval(timerId);
        showLoadingError(data);
        throw data;
    });
};

var showLoadingError = function (statusObject) {
    $("#loading").addClass("error");
    $(".title").text("Simulation ended with an error, please revise your data.");
    $(".status").text("Error: " + (statusObject._error ? statusObject._error : "Unknown error code") + ": " + statusObject.errorDetails);
    $(".status-wrap").append("<a id='details' href='#'>Details</a>");
    $("body").off("click", "#details");
    $("body").on("click", "#details", function () {
        $("<div style='height: 200px; overflow-x: hidden; '></div>").html(statusObject.errorDetails ? statusObject.errorDetails : "No information found.").dialog({
            width: "500px",
            height: "400px",
            title: "Error details",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                }
            },
            resizable: false
        }).css({
            height: "200px"
        });
        return false;
    });
    $(".close").show();
    throw new SimulationError("Simulation error: " + (statusObject._error ?  + statusObject._error : "Unknown error") + "||" + statusObject.errorDetails, statusObject.errorDetails);
};

var preloadTaskResources = function () {
    var resources = $(bimp.parser.xmlFile).find(bimp.parser.prefixEscaped + "lane");
    // Assign resource for all non-service tasks based on BPMN lanes
    $(resources).each(function (index, resource) {
        var flowNodeRefs = $(resource).find(bimp.parser.prefixEscaped + "flowNodeRef");
        var flowElementRefs = $(resource).find(bimp.parser.prefixEscaped + "flowElementRef");
        var resourceId = $(resource).attr("id");

        var processFlowNodeFunc = function (index, flowNode) {
            var taskId = $(flowNode).text();
            var target = $(bimp.parser.xmlFile).find("#" + taskId)[0];
            if (taskId && target) {
                $(".task").each(function (index, element) {
                    if ($(element).attr("data-id") == taskId) {
                        var task = bimp.parser.elementsByRefId[taskId];
                        if (!task.__isServiceTask && !task.resourceIds.resourceId[0])
                        {
                            task.resourceIds.resourceId[0] = resourceId;
                            $(element).find(".resourceId").val(resourceId);
                        }
                    }
                });
            }
        };

        $(flowNodeRefs).each(processFlowNodeFunc);
        $(flowElementRefs).each(processFlowNodeFunc);
    });

    // Assign Automated Service resource task to all service tasks
    $(".task").each(function (index, element) {
        var taskId = $(element).attr("data-id");
        var task = bimp.parser.elementsByRefId[taskId];
        if (task.__isServiceTask && !task.resourceIds.resourceId[0])
        {
            var resourceId = bimp.serviceResourceDefaultId;
            task.resourceIds.resourceId[0] = resourceId;
            $(element).find(".resourceId").val(resourceId);
        }    
    });    
};

var createAndAppendHistogramTable = function(elementSelector, htmlObj) {
    var newDiv = $(".histogram-data-div-template").clone(true);
    newDiv.removeClass("histogram-data-div-template");
    newDiv.addClass("histogram-data-div-container");

    if (!htmlObj)
        htmlObj = elementSelector;
    
    $(htmlObj).find(".histogram-data-div").append(newDiv);
};

var updateAllVisibleHistogramPosition = function() {
    var bodyRect = document.body.getBoundingClientRect();
    $(".histogram-data-div").each(function(index, div) {
        if (!$(div).is(":visible"))
            return;

        var button = $(div).parent().find("._histogramButton:first").first()[0];
        var buttonRect = button.getBoundingClientRect();
        var offset   = buttonRect.bottom - bodyRect.top;
        var neededWidth = 700;
        var newLeft = Math.max(0, (buttonRect.left + (buttonRect.right - buttonRect.left) / 2) - neededWidth / 2 + 100);
        $(div).css({
            "left": newLeft,
            "top": 1 + offset
        });
    });
};

var updateHistogramSumAndError = function(element) {
    if (!element) {
        element = $(".histogram-data-table");
    }

    $(element).each(function(index, obj) {
        var inputs = $(obj).parents(".histogram-data-div").find("._probability");
        var sum = 0.0;
        inputs.each(function(index, element) {
            sum += element.value ? parseFloat(element.value) : 0;
        });

        var text = "= " + Math.round(sum * 100.0) / 100.0 + "%";
        if (sum != 100) {
            text += ". Sum of all probabilities must be 100!";
        }

        $(element).parents(".histogram-data-div").find(".histogram-error-span").text(text);
    });
};

var addTargetIndicators = function (targetTop, elementTop) {
    if ($(".targetText").size() > 0) {
        $(".targetText").text("Targets");
    } else {
        var targetText = jQuery('<span/>', {
                css: {
                    position: "absolute",
                    top: elementTop - 10,
                    "margin-left": "-70px",
                    "font-size": "11px"
                },
                text: "Target"
            });
        targetText.addClass("targetText");
        $("#uploadPage").append(targetText);
    }
    if (elementTop < targetTop) {
        var tmp = elementTop;
        elementTop = targetTop;
        targetTop = tmp;
    }
    var div = jQuery('<div/>', {
            css: {
                position: "absolute",
                top: targetTop - 3 + "px",
                height: elementTop - 3 - targetTop + "px",
                "border": "3px solid #666",
                "border-right": "none",
                "border-top-left-radius": "30px",
                "border-bottom-left-radius": "30px",
                width: "70px",
                "margin-left": "-33px"

            }
        });
    $(div).addClass("targetIndicator");
    $("#uploadPage").append(div);
};
var addSourceIndicators = function (sourceTop, elementTop) {
    if ($(".sourceText").size() > 0) {
        $(".sourceText").text("Sources");
    } else {
        var sourceText = jQuery('<span/>', {
                css: {
                    position: "absolute",
                    top: elementTop - 10,
                    "font-size": "11px",
                    "margin-left": $("#uploadPage").width() + 35 + "px"
                },
                text: "Source"
            });
        sourceText.addClass("sourceText");
        $("#uploadPage").append(sourceText);
    }
    if (elementTop < sourceTop) {
        var tmp = elementTop;
        elementTop = sourceTop;
        sourceTop = tmp;
    }
    var div = jQuery('<div/>', {
            css: {
                position: "absolute",
                top: sourceTop - 3 + "px",
                height: elementTop - 3 - sourceTop + "px",
                "border": "3px solid #666",
                "border-left": "none",
                "border-top-right-radius": "30px",
                "border-bottom-right-radius": "30px",
                width: "60px",
                "margin-left": $("#uploadPage").width() - 40 + "px"

            }
        });
    $(div).addClass("sourceIndicator");
    $("#uploadPage").append(div);
};

var generateXCharacters = function (x, character) {
    var result = "";
    for (var i = 0; i < x; i++) {
        result = result + character;
    }
    return result;
};

function loadLikeButton(d, s, id) {
    var js,
    fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {
        return;
    }
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1";
    fjs.parentNode.insertBefore(js, fjs);
};

function browserValidation() {
    var is_chrome = /chrome/.test(navigator.userAgent.toLowerCase());
    var browser = $.browser;
    if (!is_chrome) {
        var fileAPIsupported = $("<input type='file'>").get(0).files !== undefined;
        if (!fileAPIsupported)
            $("body").prepend(
                '<div id="browser" class="gill-font">' +
                '<div id="browserWarning">It seems that your browser is not supported by our application</br>' +
                'We strongly recommend You to download the latest version of Mozilla Firefox or Google Chrome browsers</div>' +
                '<div id="browserIcons"><a href="http://www.mozilla.org/en-US/firefox/new/"><img src="./css/images/firefox.png" border="0"></a>' +
                '<a href="http://www.google.com/chrome"><img src="./css/images/chrome.png" border="0"></a></div>' +
                '</div>');
    }
}

var pointCount = 0,
interval = 500,
timerId = "",
timer = 0,
isResultsShown = false;

function SimulationError(message, stacktrace) {
    this.message = message;
    this.stacktrace = stacktrace;
}

SimulationError.prototype = new Error();
SimulationError.constructor = SimulationError;

function setStartTimeDefaults() {
    if (!$("._startDateTimeDate").val() || !$("._startDateTimeTime").val()) {
        var d = new Date(),
        dateString,
        timeString;
        dateString = d.getFullYear() + "-" + formatTime(d.getMonth() + 1) + "-" + formatTime(d.getDay() + 1);
        timeString = formatTime(d.getHours()) + ":" + formatTime(d.getMinutes()) + ":" + formatTime(d.getSeconds());
        $("._startDateTimeDate").val(dateString);
        $("._startDateTimeTime").val(timeString);
    }
}

function formatTime(input) {
    input = input.toString();
    if (input.length == 1) {
        input = "0" + input;
    }
    return input;
}
bimp = bimp ? bimp : {};
bimp.tools = {
    openError: function (message) {
        $("<div></div>").html(message).dialog({
            width: "300px",
            title: "Error!",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                }
            },
            resizable: false
        });
    }
};
