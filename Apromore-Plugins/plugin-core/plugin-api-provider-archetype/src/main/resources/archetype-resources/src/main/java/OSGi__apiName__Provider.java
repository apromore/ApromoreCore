package ${package};

import java.util.List;

import ${apiPackage}.${apiName};
import org.springframework.stereotype.Service;

@Service("osgi${apiName}ProviderImpl")
public class OSGi${apiName}Provider extends ${apiName}ProviderImpl {

    public List<${apiName}> get${apiName}List() {
        return getInternal${apiName}List();
    }

    public void set${apiName}List(final List<${apiName}> new${apiName}List) {
        setInternal${apiName}List(new${apiName}List);
    }

}
