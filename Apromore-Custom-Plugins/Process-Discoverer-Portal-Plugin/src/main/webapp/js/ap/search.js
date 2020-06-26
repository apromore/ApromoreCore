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
/** Search for graph
 *
 */
(function() {
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

  function collectNodeNames(source) {
    nodeNames = [];
    source.forEach((el) => {
      let data = el && el.data || {};
      if(data.id && data.oriname && data.shape === 'roundrectangle') {
        nodeNames.push({
          label: data.oriname,
          value: data.oriname,
          id: data.id,
        })
      }
    });
  }

  function selectNodes() {
    selectedNodeIds.forEach(function (id) {
      cy.getElementById(id).unselect();
    });

    selectedNodeIds = [];
    searchResults.forEach(function (result) {
      selectedNodeIds.push(result.id);
      cy.getElementById(result.id).select();
    });
  }

  document.addEventListener("click", (evt) => {
    const el = searchOptions[0];
    const input = searchInput[0];
    const target = evt.target;

    if (el !== target && input !== target) {
      updateSelectedNodes();
    }
  });

  function updateSelectedNodes () {
    if (!miniSearch) { return; }
    let value = searchInput.val();
    let lowerValue = value.toLowerCase();
    let newSearchResults = [];
    let dirty = 0;

    searchResults = miniSearch.search(value);
    searchResults.forEach(function (result) {
      if (result.label.toLowerCase().indexOf(lowerValue) >= 0) { // exact match
        dirty = 1;
        newSearchResults.push({ id: result.id });
      }
    });
    if (dirty) {
      searchResults = newSearchResults;
    }
    selectNodes();
    searchOptions.hide();
  }

  function resetSearchInput() {
    searchInput.val('');
    searchClear.css({ visibility: 'hidden'});
    searchOptions.hide();
    searchResults = [];
    selectNodes();
  }

  function highlightOption(dIndex) {
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
      } else if(currentOptionIndex >= searchResults.length) {
        currentOptionIndex = 0;
      }
    }
    $(`${SEARCH_OPTIONS} > div[data-index=${currentOptionIndex}]`).addClass('selected');
  }

  function checkForSearchOptions() {
    if (searchResults.length > 0) {
      searchOptions.show();
    } else {
      searchOptions.hide();
    }
  }

  function forceSelect(target) {
    let value = $(target).text();
    let id = $(target).attr('data-id');
    searchInput.val(value);
    searchResults = [
      { id }
    ]
    selectNodes();
    searchOptions.hide();
  }

  function setupSearch(source) {
    // call here in case setupSearch is called earlier
    searchOptions = $('.ap-pd-search-graph-options');
    searchClear = $(`${SEARCH_ID} i:last-child`);
    searchInput = $(`${SEARCH_ID} input`);

    collectNodeNames(source);
    miniSearch = new MiniSearch({
      fields: ['label'],
      storeFields: ['label'],
      searchOptions: {
        prefix: true
      }
    });
    let inputVal;
    let { left, top } = searchInput.offset();

    miniSearch.addAll(nodeNames)
    top += searchInput.outerHeight();
    searchOptions.css({ left, top, minWidth: searchInput.outerWidth() });
    searchOptions.hide();
    searchInput.unbind();
    searchClear.unbind();
    searchClear.click(() => {
      resetSearchInput();
    });
      
    searchInput.focus((e) => {
      checkForSearchOptions();
    });
    // searchInput.blur((e) => {
    //   searchOptions.hide();
    // });
    searchInput.keyup((e) => {
      switch (e.keyCode) {
        case 38: // Up
          highlightOption(-1);
          e.preventDefault();
          break;
        case 40: // Down
          highlightOption(1);
          e.preventDefault();
          break;
        case 13: // Enter
          if (currentOptionIndex !== null) {
            let target = $(`${SEARCH_OPTIONS} > div[data-index=${currentOptionIndex}]`);
            forceSelect(target);
          } else {
            updateSelectedNodes();
          }
          break;
        case 9:  // Tab
        case 27: // Esc
          updateSelectedNodes();
          break;
        default:
          let v = searchInput.val();
          if (v !== inputVal) {
            inputVal = v;
            if (inputVal.length > 0) {
              searchClear.css({ visibility: 'visible'});
            } else {
              searchClear.css({ visibility: 'hidden'});
            }
            searchResults = miniSearch.search(inputVal);
            searchOptions.empty();
            currentOptionIndex = null;
            searchResults.forEach(function (result, index) {
              searchOptions.append(
                $("<div></div>")
                .attr('data-id', result.id)
                .attr('data-index', index)
                .text(result.label)
                .click(
                  function (e) {
                    currentOptionIndex = index;
                    forceSelect(e.target);
                  }
                )
              );
            });
            checkForSearchOptions();
          }
          break;
      }
    });
    updateSelectedNodes();
  }

  /*
  // Exact search using jQuery UI

  let prevSelected;

  function selectNode(nodeId) {
    if (prevSelected) {
      cy.getElementById(prevSelected).unselect();
      prevSelected = null;
    }
    if (typeof nodeId !== 'undefined' || nodeId !== null) {
      prevSelected = nodeId;
      if (prevSelected) {
        cy.getElementById(prevSelected).select();
      }
    };
  }

  function setupSearchExact(source) {
    collectNodeNames(source);
    $(SEARCH_ID).autocomplete({
      source: nodeNames,
      select: function( event, ui ) {
        selectNode(ui.item && ui.item.id);
      }
    });
  }
  */

  Object.assign(Ap.pd, {
    setupSearch,
  })

})();
