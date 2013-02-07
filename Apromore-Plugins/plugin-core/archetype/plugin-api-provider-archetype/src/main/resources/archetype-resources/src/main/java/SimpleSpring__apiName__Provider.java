package ${package};

import java.util.Set;

import ${apiPackage}.${apiName};
import org.apromore.plugin.provider.PluginProviderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleSpring${apiName}Provider extends ${apiName}ProviderImpl {

    public SimpleSpring${apiName}Provider() {
        super();
        Set<${apiName}> my${apiName}Set = PluginProviderHelper.findPluginsByClass(${apiName}.class, "org.apromore");
        setInternal${apiName}Set(my${apiName}Set);
    }


}
