package ${package};

import java.util.Set;

import ${apiPackage}.${apiName};
import ${apiPackage}.provider.${apiName}Provider;

/**
 * Providing the default Provider implementation
 *
 * @author <a href="mailto:${yourMail}">${yourName}</a>
 *
 */
public abstract class ${apiName}ProviderImpl implements ${apiName}Provider {

    private Set<${apiName}> internal${apiName}Set;
	
	@Override
	public ${apiName} findExamplePlugin(String name) {
		//TODO search in our list
		return null;
	};

    protected Set<${apiName}> getInternal${apiName}Set() {
        return internal${apiName}Set;
    }

    protected void setInternal${apiName}Set(final Set<${apiName}> internal${apiName}Set) {
        this.internal${apiName}Set = internal${apiName}Set;
    }

}
