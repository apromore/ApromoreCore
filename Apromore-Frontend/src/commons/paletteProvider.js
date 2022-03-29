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
 *  Encapsulation of palette handling for log animation
 */
import { darken } from './colorUtil';
import { range, map, findIndex } from 'ramda';

const DEFAULT_PALETTE = [
    ['#84c7e3', '#76b3cc', '#699fb5', '#5c8b9e', '#4f7788', '#426371', '#344f5a', '#273b44'], // light blue
    ['#bb3a50', '#a83448', '#952e40', '#822838', '#702230', '#5d1d28', '#4a1720', '#381118'], // red
    ['#34AD61', '#249D51', '#148D41', '#047D31', '#047D21', '#046D21', '#047D11', '#046D11'], // green
    ['#E98C2D', '#ffdb4d', '#ffd633', '#ffd11a', '#ffcc00', '#e6b800', '#cca300', '#b38f00'], // orange
    ['#bf80ff', '#b366ff', '#a64dff', '#9933ff', '#8c1aff', '#8000ff', '#7300e6', '#6600cc'], // green
    ['#6666ff', '#4d4dff', '#3333ff', '#1a1aff', '#0000ff', '#0000e6', '#0000cc', '#0000b3'], // dark blue
    ['#ff80ff', '#ff66ff', '#ff4dff', '#ff33ff', '#ff1aff', '#ff00ff', '#e600e6', '#cc00cc'], // pink
];
// Each row is a color set that contains gradation steps of the main color (first element in the row)
// The gradation runs from light to dark

const COLOR_SET_SIZE = 8;

/**
 *  Class to encapsulate palette management and color selection
 */
export default class PaletteProvider {

    /**
     * @param {Array} palette Array of array that contains colorSet. See DEFAULT_PALETTE above
     * @param {Array} selections Indices of the color sets in the palette.
     *     Use case: selections.length = selected logs in log animation
     */
    constructor(palette, selections) {
        this.palette = palette || DEFAULT_PALETTE;
        if (typeof selections === 'number') {
            this.selections = range(0, selections);
        } else {
            this.selections = selections || [0, 1]; // By default select the first two colorSets
        }
    }

    /**
     * Get the main colors of all color sets (the first element in the color sets)
     * @return {Array} Array of main colors in hex
     */
    getPalette() {
        return map(colorSet => colorSet[0], this.palette);
    }

    /**
     * Assign colorSet index to selections[selIndex]
     * @param {Number} selIndex Index of a selection (e.g. log number 0)
     * @param {Number} setIndex Index of a color set in the palette
     */
    select(selIndex, setIndex) {
        this.selections[selIndex] = setIndex;
    }

    /**
     * Add new colorSet based on the main color (colorCode)
     * @param {String} colorCode Main color in hex code
     * @return {Array} A new Color set
     */
    addColor(colorCode) {
        let colorSet = map((i) => darken(colorCode, i, COLOR_SET_SIZE), range(0, COLOR_SET_SIZE));
        this.palette.push(colorSet);
        return colorSet;
    }

    /**
     * Find the colorSet index that contains the supplied main color
     *
     * @param {String} colorCode Main color in hex code
     * @return {Number} A colorSet index
     */
    findColor(colorCode) {
        colorCode = colorCode.toLowerCase();
        return findIndex(colorSet => colorSet[0] === colorCode)(this.palette);
    }

    /**
     * Find the colorCode in the palette's color sets
     * - if found, assign the setIndex to the selected index
     * - if not found, add a new color set and assign the new set index
     *
     * @param {Number} selIndex Index of a selection (e.g. log number 0)
     * @param {String} colorCode Main color in hex code
     */
    matchAndSelectColor(selIndex, colorCode) {
        colorCode = colorCode.toLowerCase();
        let setIndex = this.findColor(colorCode);
        if (setIndex === -1) {
            this.addColor(colorCode);
            setIndex = this.palette.length - 1;
        }
        this.select(selIndex, setIndex);
    }

    /**
     * Get the gradation color for a selection
     *
     * @param {Number} selIndex Index of a selection (e.g. log number 0)
     * @param {Number} step Gradation step
     * @return {String} Color in hex code
     */
    getSelectedColor(selIndex, step) {
        let setIndex = this.selections[selIndex];
        let colorSet = this.palette[setIndex];
        if (step === -1) {
            return colorSet[colorSet.length - 1];
        }
        return colorSet[step || 0];
    }

    /**
     * Get the gradation color from a color set
     *
     * @param {Number} setIndex Index of a color set in the palette
     * @param {Number} step Gradation step
     * @return {String} Color in hex code
     */
    getColor(setIndex, step) {
        return this.palette[setIndex][step || 0];
    }

    /**
     * Get a color set
     *
     * @param {Number} setIndex Index of a color set in the palette
     * @return {Array} A color set
     */
    getColorSet(setIndex) {
        return this.palette[setIndex];
    }
}