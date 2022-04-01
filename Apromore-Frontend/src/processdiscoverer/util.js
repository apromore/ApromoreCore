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

import * as moment from "moment";

let PDp = {};

/**
 * To force select ZK combo when the old and new values are the same
 */
PDp.handleComboitemClick = function(src) {
    return function (ev) {
        // if click on the already selected item
        if ($(ev.currentTarget).hasClass('z-comboitem-selected')) {
            // force onSelect
            setTimeout(function () {
                zk.Widget.$('$' + src).fire(
                    'onForceSelect',
                    {},
                    {toServer: true},
                );
            }, 200);
        }
    };
}

PDp.installComboitemHandlers = function() {
    $('.ap-pd-duration-selector .z-comboitem').click(
        this.handleComboitemClick('durationAggSelector'),
    );
    $('.ap-pd-freq-selector .z-comboitem').click(
        this.handleComboitemClick('frequencyAggSelector'),
    );
}

PDp.swapOrdering = function() {
    $('.ap-pd-chart-bi-legend').toggleClass('reversed');
}

PDp.toggleOptions = function() {
    let r = this;

    // let options = $('.ap-pd-drawer')
    let options = $('[data-t=ap-pd-aux]');
    let height = options.height();
    let cy = $('#ap-pd-process-model');
    let up = $('[data-t=collapse]'); // $('.ap-pd-drawer-splitter .z-icon-caret-up');
    let down = $('[data-t=expand]'); // $('.ap-pd-drawer-splitter .z-icon-caret-down');
    if (options.is(':visible')) {
        down.show();
        up.hide();
        options.slideUp(300, function () {
            cy.css({top: '0px'});
            r.resize();
            $(window).trigger('resize'); // to make sure other components will redraw as well
        });
    } else {
        up.show();
        down.hide();
        options.slideDown(300, function () {
            cy.css({top: height + 'px'});
            r.resize();
            $(window).trigger('resize'); // to make sure other components will redraw as well
        });
    }
}

/**
 * Drawing pie chart
 *
 */

const PIE_SIZE = 50;
const PIE_R = PIE_SIZE / 2;
const FULL_CIRCLE = Math.PI * PIE_R;


// Using path

const SVG_TEMPLATE = `<svg height="${PIE_SIZE}" width="${PIE_SIZE}" viewBox="0 0 ${PIE_SIZE} ${PIE_SIZE}" >
  <circle r="${PIE_R}" cx="${PIE_R}" cy="${PIE_R}" fill="#D7DAE0" stroke-width="1" stroke="white" />
  <path
    stroke="white"
    fill="#afdaed"
    stroke-width="1"
  />
</svg>`;

PDp.genChart = function(chartType, percentage) {
    let id = `#ap-pd-chart-${chartType}`;
    let container = $(`${id}`);
    if (!$(`${id} svg`).length) {
        container.html(SVG_TEMPLATE);
    }
    if (percentage > 99.9) {
        percentage = 99.9;
    }
    const percent = percentage / 100;
    const r = PIE_R;
    const a = 2 * Math.PI * percent;
    const x = r + r * Math.sin(a);
    const y = r - r * Math.cos(a);
    let lArc = percent > .5 ? 1 : 0;
    let path = [
        `M ${r} ${r}`,
        `L ${r} 0`,
        `A ${r} ${r} 0 ${lArc} 1 ${x} ${y}`,
        `Z`,
    ].join(' ');
    $(`${id} path`).attr({'d': path});
};

/**
 * Humanizing date, currently not used
 *
 * Example:
 *   const startTimeCls = '.ap-pd-start-time';
 *   const endTimeCls = '.ap-pd-end-time';
 *   humanizeDate(startTimeCls);
 *   humanizeDate(endTimeCls);
 */
const parseFormat = 'DD.MM.YYYY HH:mm:ss';
const displayFormat = 'D MMM YY, HH:mm';

PDp.humanizeDate = selector => {
    let originalDate = $(selector).text();
    $(selector).text(moment(originalDate, parseFormat).format(displayFormat)).attr('title', originalDate);
};

export default PDp;




