/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
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
/**
 *  Color handling for log animation
 */
import Color from 'color';

const DARKEST = 30; // min lightness
const LIGHTEST = 250; // max lightness

/**
 * Calculate gradation color
 *
 * Example:
 *
 * colorCode -> x -> end, stepNum 3 (0 <= index < 3)
 *
 * @param {String} colorCode Color in hex format
 * @param {Number} index Index of the gradation step
 * @param {Number} stepNum Total steps
 * @param {Number} end Target lightness
 */
export const step = function (colorCode, index, stepNum, end) {
    let color = Color(colorCode);
    let start = color.lightness();
    let delta = (end - start) / (stepNum - 1);
    let lightness = start + index * delta;
    return color.lightness(lightness).hex();
};

/**
 * Lighten gradation color
 *
 * @param {String} colorCode Color in hex format
 * @param {Number} index Index of the gradation step
 * @param {Number} stepNum Total steps
 * @param {Number} lightest Target lightness
 */
export const lighten = function (colorCode, index, stepNum, lightest) {
    lightest = lightest || LIGHTEST;
    return step(colorCode, index, stepNum, lightest);
};

/**
 * Lighten gradation color
 *
 * @param {String} colorCode Color in hex format
 * @param {Number} index Index of the gradation step
 * @param {Number} stepNum Total steps
 * @param {Number} darkest Target lightness
 */
export const darken = function (colorCode, index, stepNum, darkest) {
    darkest = darkest || DARKEST;
    return step(colorCode, index, stepNum, darkest);
}

