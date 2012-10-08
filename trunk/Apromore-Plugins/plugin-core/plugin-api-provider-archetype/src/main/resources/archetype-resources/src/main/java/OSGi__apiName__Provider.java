package ${package};

import java.util.Set;

import ${apiPackage}.${apiName};
import org.springframework.stereotype.Service;

@Service("osgi${apiName}ProviderImpl")
public class OSGi${apiName}Provider extends ${apiName}ProviderImpl {

    public Set<${apiName}> get${apiName}Set() {
        return getInternal${apiName}Set();
    }

    public void set${apiName}Set(final Set<${apiName}> new${apiName}Set) {
        setInternal${apiName}Set(new${apiName}Set);
    }

}
