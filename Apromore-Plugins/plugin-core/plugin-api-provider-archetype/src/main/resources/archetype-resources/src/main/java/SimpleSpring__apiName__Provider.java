package ${package};

import java.util.ArrayList;
import java.util.List;

import ${apiPackage}.${apiName};
import org.apromore.plugin.provider.PluginProviderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleSpring${apiName}Provider extends ${apiName}ProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpring${apiName}Provider.class);

    public SimpleSpring${apiName}Provider() {
        super();
        List<${apiName}> my${apiName}List = new ArrayList<${apiName}>();
        Class<?>[] classes = PluginProviderHelper.getAllClassesImplementingInterfaceUsingSpring(${apiName}.class);
        for (int i = 0; i < classes.length; i++) {
            Class<?> my${apiName}Class = classes[i];
            try {
                Object obj = my${apiName}Class.newInstance();
                if (obj instanceof ${apiName}) {
                    my${apiName}List.add((${apiName}) obj);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.warn("Could not instantiate ${apiName}: "+my${apiName}Class.getName());
            }
        }
        setInternal${apiName}List(my${apiName}List);
    }


}
