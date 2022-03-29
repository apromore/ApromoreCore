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

package org.apromore.plugin.portal.useradmin.listbox;

public class TristateModel {

    public static final Integer UNCHECKED = 0;
    public static final Integer CHECKED = 1;
    public static final Integer INDETERMINATE = 2;

    private String label;
    private String key;
    private Integer state;
    private Object obj;
    private boolean twoStateOnly;
    private boolean disabled;
    private boolean coSelectable; //True if this object and another can be selected at the same time

    public TristateModel(String label, String key, Object obj, Integer state, boolean disabled) {
        this(label, key, obj, state, disabled, true);
    }

    public TristateModel(String label, String key, Object obj, Integer state, boolean disabled, boolean coSelectable) {
        this.label = label;
        this.key = key;
        this.obj = obj;
        this.state = state;
        this.twoStateOnly = false;
        this.disabled = disabled;
        this.coSelectable = coSelectable;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getState() {
        return state;
    }

    public boolean isTwoStateOnly() {
        return twoStateOnly;
    }

    public void setTwoStateOnly(boolean twoStateOnly) {
        this.twoStateOnly = twoStateOnly;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isCoSelectable() {
        return coSelectable;
    }

    public void setCoSelectable(boolean coSelectable) {
        this.coSelectable = coSelectable;
    }
}
