/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import MiniSearch from "minisearch";

/** Search for graph
 *
 */
const SEARCH_ID = '#ap-pd-search-graph';
const SEARCH_OPTIONS = '.ap-pd-search-graph-options';
let searchOptions = $(SEARCH_OPTIONS);
let searchClear = $(`${SEARCH_ID} i:last-child`);
let searchInput = $(`${SEARCH_ID} input`);
let miniSearch;
let nodeNames = [];
let selectedNodeIds = [];
let searchResults = [];
let currentOptionIndex = null;

let PDp = {};

PDp.getSearchOptionEl = function() {
    return searchOptions[0];
}

PDp.getSearchInputEl = function() {
    return searchInput[0];
}

PDp.collectNodeNames = function(source) {
    nodeNames = [];
    source.forEach((el) => {
        let data = el && el.data || {};
        if (data.id && data.oriname && data.shape === 'roundrectangle') {
            nodeNames.push({
                label: data.oriname,
                value: data.oriname,
                id: data.id,
            });
        }
    });
}

PDp.selectNodes = function() {
    let cy = this._private.cy;
    selectedNodeIds.forEach(function (id) {
        cy.getElementById(id).unselect();
    });

    selectedNodeIds = [];
    searchResults.forEach(function (result) {
        selectedNodeIds.push(result.id);
        cy.getElementById(result.id).select();
    });
}

PDp.updateSelectedNodes = function() {
    let r = this;

    if (!miniSearch) {
        return;
    }
    let value = searchInput.val();
    let lowerValue = value.toLowerCase();
    let newSearchResults = [];
    let dirty = 0;

    searchResults = miniSearch.search(value);
    searchResults.forEach(function (result) {
        if (result.label.toLowerCase().indexOf(lowerValue) >= 0) { // exact match
            dirty = 1;
            newSearchResults.push({id: result.id});
        }
    });
    if (dirty) {
        searchResults = newSearchResults;
    }
    r.selectNodes();
    searchOptions.hide();
}

PDp.resetSearchInput = function() {
    let r = this;
    searchInput.val('');
    searchClear.css({visibility: 'hidden'});
    searchOptions.hide();
    searchResults = [];
    r.selectNodes();
}

PDp.highlightOption = function(dIndex) {
    if (currentOptionIndex === null) {
        if (dIndex === -1) {
            currentOptionIndex = searchResults.length - 1;
        } else {
            currentOptionIndex = 0;
        }
    } else {
        $(`${SEARCH_OPTIONS} > div[data-index=${currentOptionIndex}]`).removeClass('selected');
        currentOptionIndex += dIndex;
        if (currentOptionIndex < 0) {
            currentOptionIndex = searchResults.length - 1;
        } else if (currentOptionIndex >= searchResults.length) {
            currentOptionIndex = 0;
        }
    }
    $(`${SEARCH_OPTIONS} > div[data-index=${currentOptionIndex}]`).addClass('selected');
}

PDp.checkForSearchOptions = function() {
    if (searchResults.length > 0) {
        searchOptions.show();
    } else {
        searchOptions.hide();
    }
}

PDp.forceSelect = function(target) {
    let r = this;
    let value = $(target).text();
    let id = $(target).attr('data-id');
    searchInput.val(value);
    searchResults = [
        {id}
    ];
    r.selectNodes();
    searchOptions.hide();
}

PDp.setupSearch = function(source, reset) {
    let r = this;

    // call here in case setupSearch is called earlier
    searchOptions = $('.ap-pd-search-graph-options');
    searchClear = $(`${SEARCH_ID} i:last-child`);
    searchInput = $(`${SEARCH_ID} input`);

    if (reset) {
        r.resetSearchInput();
    }

    r.collectNodeNames(source);
    miniSearch = new MiniSearch({
        fields: ['label'],
        storeFields: ['label'],
        searchOptions: {
            prefix: true
        }
    });
    let inputVal;
    let {left, top} = searchInput.offset();

    miniSearch.addAll(nodeNames);
    top += searchInput.outerHeight();
    searchOptions.css({left, top, minWidth: searchInput.outerWidth()});
    searchOptions.hide();
    searchInput.unbind();
    searchClear.unbind();
    searchClear.click(() => {
        r.resetSearchInput();
    });

    searchInput.focus((e) => {
        r.checkForSearchOptions();
    });
    // searchInput.blur((e) => {
    //   searchOptions.hide();
    // });
    searchInput.keyup((e) => {
        switch (e.keyCode) {
            case 38: // Up
                r.highlightOption(-1);
                e.preventDefault();
                break;
            case 40: // Down
                r.highlightOption(1);
                e.preventDefault();
                break;
            case 9:  // Tab
            case 13: // Enter
                if (currentOptionIndex !== null) {
                    let target = $(`${SEARCH_OPTIONS} > div[data-index=${currentOptionIndex}]`);
                    r.forceSelect(target);
                } else {
                    r.updateSelectedNodes();
                }
                break;
            case 27: // Esc
                r.resetSearchInput();
                break;
            default:
                let v = searchInput.val();
                if (v !== inputVal) {
                    inputVal = v;
                    if (inputVal.length > 0) {
                        searchClear.css({visibility: 'visible'});
                    } else {
                        searchClear.css({visibility: 'hidden'});
                    }
                     searchResults = miniSearch.search(inputVal);
                    searchOptions.empty();
                    currentOptionIndex = null;
                    searchResults.forEach(function (result, index) {
                        const label = result.label;
                        searchOptions.append(
                            $("<div></div>")
                                .attr('data-id', result.id)
                                .attr('data-index', index)
                                .attr('title', label)
                                .text(label)
                                .click(
                                    function (e) {
                                        currentOptionIndex = index;
                                        r.forceSelect(e.target);
                                    }
                                )
                        );
                    });
                    r.checkForSearchOptions();
                }
                break;
        }
    });
    r.updateSelectedNodes();
}

export default PDp;

