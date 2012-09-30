/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ${package};

import java.util.List;

import ${apiPackage}.${apiName};
import ${apiPackage}.provider.${apiName}Provider;

/**
 * Providing the default Provider implementation
 *
 * @author <a href="mailto:${yourMail}">${yourName}</a>
 *
 */
public abstract class ${apiName}ProviderImpl implements ${apiName}Provider {

    private List<${apiName}> internal${apiName}List;
	
	@Override
	public ${apiName} findExamplePlugin(String name) {
		//TODO search in our list
		return null;
	};

    protected List<${apiName}> getInternal${apiName}List() {
        return internal${apiName}List;
    }

    protected void setInternal${apiName}List(final List<${apiName}> internal${apiName}List) {
        this.internal${apiName}List = internal${apiName}List;
    }

}
