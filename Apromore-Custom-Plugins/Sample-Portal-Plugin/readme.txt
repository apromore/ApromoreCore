All portal plug-ins go here. To create a new portal plug-in:

 1) Copy the example plug-in in the "sample" directory
 2) Change the name of the plug-in in the pom.xml file and Java package. Please use a unique package name
    with prefix "org.apromore.plguin.portal") to ensure class loading works properly.
 3) Make sure that your plug-in class extends DefaultPortalPlugin (do not use the interface directly unless needed)
 4) Make sure that your plug-in class is annotated with @Component("plugin")
 4) Adapt the file "main/resources/org.apromore.plugin.portal.org.apromore.plugin.logic.SimilaritySearchPlugin.config" to your needs and rename it to match your plug-in name/package.
 5) Adapt the file "main/resource/META-INF.spring/osgi-context.xml"
 6) Override the methods of DefaultPortalPlugin. Please beware that only one instance of your plug-in will be used globally.
    So, make sure that your plug-in is stateless!
