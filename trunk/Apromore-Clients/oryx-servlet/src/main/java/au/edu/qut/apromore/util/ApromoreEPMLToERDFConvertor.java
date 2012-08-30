package au.edu.qut.apromore.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ApromoreEPMLToERDFConvertor {

    public String convert(ServletContext context, String epmlString) throws TransformerException {
        //remove namespace
        epmlString = fixNameSpace(epmlString);

        //fix range connectors
//		epmlString=fixDirectory(epmlString);
//		epmlString=fixEPC(epmlString);
        try {
            epmlString = fixRangeConnector(epmlString);
        } catch (Exception e1) {
            throw new TransformerException(e1);
        }

        double offset = getOffset(epmlString);
        final String xsltFilename = context.getRealPath("/xslt/EPML2eRDF.xslt");
//    	final File epml2eRDFxsltFile = new File(xsltFilename);

        String transformXSLTContents = null;
        try {
            transformXSLTContents = getTransformXSLTContents(xsltFilename);
        } catch (IOException e) {
            throw new TransformerException(e);
        }

        //fix offset
        transformXSLTContents = transformXSLTContents.replaceAll("@x", "(@x+" + (int) offset + ")"); //FIXME: quick fix for AUDIO model offset
        final Source epml2eRDFxsltSource = new StreamSource(new ByteArrayInputStream(transformXSLTContents.getBytes()));
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // Get the epml source
        final Source epmlSource;
        ByteArrayInputStream bis = new ByteArrayInputStream(epmlString.getBytes());
        epmlSource = new StreamSource(bis);

        // Get the result string
        String resultString = null;
        Transformer transformer = transformerFactory.newTransformer(epml2eRDFxsltSource);
        StringWriter writer = new StringWriter();
        transformer.transform(epmlSource, new StreamResult(writer));
        resultString = writer.toString();

        //fix outgoings
        resultString = fixEmptyXmlns(resultString);
        return resultString;
    }


    private String fixEPC(String epmlString) {
        if (epmlString.indexOf("<epc") > 0) {
            return epmlString;
        }
        epmlString = epmlString.replaceAll("<directory[^>]+", "<directory name=\"Root\"><epc epcId=\"1\" name=\"\">");
        epmlString = epmlString.replaceAll("</directory>", "</epc></directory>");
        return epmlString;
    }


    private String fixNameSpace(String epmlString) {
        return epmlString.replaceAll("ns2:", "epml:").replaceAll("xmlns:ns2", "xmlns:epml");//TODO: fix this
    }


    private String getTransformXSLTContents(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

    private Double getOffset(String epmlString) {
        Pattern pattern = Pattern.compile("x=\"-[0-9]+\"");
        Matcher matcher = pattern.matcher(epmlString);

        double offset = 0.0;
        while (matcher.find()) {
            String xPos = matcher.group().substring(3, matcher.group().length() - 1);
            double newOffset = Double.parseDouble(xPos);
            if (newOffset < offset) {
                offset = newOffset;
            }
        }

        return Math.abs(offset);
    }

    private String fixEmptyXmlns(String eRdfString) {
        return eRdfString.replaceAll("xmlns=\"\"", "");
    }

    private String fixDirectory(String epmlString) {
        if (epmlString.indexOf("<directory") > 0) {
            return epmlString;
        }
        epmlString = epmlString.replaceAll("<epc", "<directory name=\"Root\"><epc");
        epmlString = epmlString.replaceAll("</epc>", "</epc></directory>");
        return epmlString;
    }


    private String getAttributeFromTag(String tag, String attributeName) {
        Pattern arcPattern = Pattern.compile(attributeName + "[^\"]*=[^\"]*\"[^\"]*\"");
        Matcher arcMatcher = arcPattern.matcher(tag);

        if (arcMatcher.find()) {
            String result = arcMatcher.group();
            return result.substring(result.indexOf("\"") + 1, result.lastIndexOf("\""));
        } else {
            return null;
        }
    }

    private String fixRangeConnector(String epmlString) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new ByteArrayInputStream(epmlString.getBytes("utf-8"))));
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer tFormer = tFactory.newTransformer();

            //get range connector ids
            List<String> rangeConnectorIds = getRangeConnectorIds(doc);
            long maxId = getMaxId(epmlString);

            Node rootElement = doc.getElementsByTagName("epc").item(0);
            for (String rangeConnectorId : rangeConnectorIds) {
                List<String> incommingsToTheRange = getElementIdConnectedToARangeConnector(doc, rangeConnectorId, true);
                List<String> outgoingsOfTheRange = getElementIdConnectedToARangeConnector(doc, rangeConnectorId, false);
                for (String incommingElementId : incommingsToTheRange) {
                    for (String outgoingElementId : outgoingsOfTheRange) {
                        Element arcElement = doc.createElement("arc");
                        arcElement.setAttribute("id", "" + (++maxId)); //increase max id
                        Element relationElement = doc.createElement("relation");
                        relationElement.setAttribute("source", incommingElementId);
                        relationElement.setAttribute("target", outgoingElementId);
                        arcElement.appendChild(relationElement);
                        rootElement.appendChild(arcElement);
                    }
                }
            }

            removeRangeConnectors(doc, rangeConnectorIds);
            doc.normalize();
            Source source = new DOMSource(doc);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result dest = new StreamResult(bos);
            tFormer.transform(source, dest);

            return new String(bos.toByteArray());
        } catch (Exception e) {
            throw e;
        }
    }

    private long getMaxId(String epmlString) {

        Pattern idPattern = Pattern.compile("id[ ]*=[ ]*\"[0-9]+\"");
        Matcher idMatcher = idPattern.matcher(epmlString);
        long maxId = 0;
        while (idMatcher.find()) {
            String group = idMatcher.group();
            String id = group.substring(group.indexOf("\"") + 1, group.lastIndexOf("\""));
            long parseLong = Long.parseLong(id);
            if (parseLong > maxId)
                maxId = parseLong;
        }
        return maxId;
    }


    private void removeRangeConnectors(Document doc, List<String> rangeConnectorIds) {
//		NodeList rangeConnectorElements = doc.getElementsByTagName("range");

        if (rangeConnectorIds == null || rangeConnectorIds.size() == 0)
            return;

        List<Node> rangeConnectorNodes = getAllRangeConnectors(doc);
        //remove range connectors
        for (Node rangeNode : rangeConnectorNodes) {

            rangeNode.getParentNode().removeChild(rangeNode);
        }

        //remove arcs to/from range connectors
        List<Node> arcs = getAllRelationsOfArcs(doc);
        for (Node arc : arcs) {
            String sourceId = null;
            String targetId = null;
            for (int j = 0; j < arc.getAttributes().getLength(); j++) {
                Node attribute = arc.getAttributes().item(j);
                if ("source".equalsIgnoreCase(attribute.getNodeName()))
                    sourceId = attribute.getNodeValue();
                if ("target".equalsIgnoreCase(attribute.getNodeName()))
                    targetId = attribute.getNodeValue();
            }
            if (rangeConnectorIds.contains(sourceId) || rangeConnectorIds.contains(targetId)) {
                arc.getParentNode().getParentNode().removeChild(arc.getParentNode());// relation is inside arcs
            }
        }


    }

    private List<String> getElementIdConnectedToARangeConnector(Document doc,
                                                                String rangeConnectorId, boolean incomming) {

        NodeList arcs = doc.getElementsByTagName("relation");
        List<String> retValue = new ArrayList<String>();
        for (int i = 0; i < arcs.getLength(); i++) {
            Node arc = arcs.item(i);
            String sourceId = null;
            String targetId = null;
            for (int j = 0; j < arc.getAttributes().getLength(); j++) {
                Node attribute = arc.getAttributes().item(j);
                if ("source".equalsIgnoreCase(attribute.getNodeName()))
                    sourceId = attribute.getNodeValue();
                if ("target".equalsIgnoreCase(attribute.getNodeName()))
                    targetId = attribute.getNodeValue();
            }
            if (incomming) {
                if (rangeConnectorId.equals(targetId))
                    retValue.add(sourceId);
            } else//outgoing
            {
                if (rangeConnectorId.equals(sourceId))
                    retValue.add(targetId);

            }
        }
        return retValue;

    }


    private List<String> getRangeConnectorIds(Document doc) {
        NodeList rangeConnectorNodes = doc.getElementsByTagName("range");
        List<String> retValue = new ArrayList<String>();
        for (int i = 0; i < rangeConnectorNodes.getLength(); i++) {
            Node rangeConnectorNode = rangeConnectorNodes.item(i);
            for (int j = 0; j < rangeConnectorNode.getAttributes().getLength(); j++) {
                Node item2 = rangeConnectorNode.getAttributes().item(j);
                if ("id".equalsIgnoreCase(item2.getNodeName())) {
                    retValue.add(item2.getNodeValue());
                }
            }
        }
        return retValue;

    }

    private List<Node> getAllRangeConnectors(Document doc) {

        Node item = doc.getElementsByTagName("epc").item(0);
        NodeList childNodes = item.getChildNodes();
        List<Node> ret = new ArrayList<Node>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName().equals("range")) {
                ret.add(childNodes.item(i));
            }
        }
        return ret;
    }


    private List<Node> getAllRelationsOfArcs(Document doc) {

        Node item = doc.getElementsByTagName("epc").item(0);
        NodeList childNodes = item.getChildNodes();
        List<Node> ret = new ArrayList<Node>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName().equals("arc")) {
                NodeList childNodes2 = childNodes.item(i).getChildNodes();
                for (int j = 0; j < childNodes2.getLength(); j++) {
                    if (childNodes2.item(j).getNodeName().equals("relation")) {
                        ret.add(childNodes2.item(j));
                    }
                }
            }
        }
        return ret;
    }


/*	private String fixRangeConnector(String epmlString)
    {
        Map<String,List> rangeToOutgoingsMap=new HashMap<String, List>();
        Set<String> rangeIds=new HashSet<String>();

        //find range connectors
        Pattern pattern=Pattern.compile("<range [^>]*>");
        Matcher matcher = pattern.matcher(epmlString);

        while(matcher.find())
        {
            rangeIds.add(getAttributeFromTag(matcher.group(), "id"));
        }

        //find node ids connected to each range connector (range connector is the source)
        for (String rangeId : rangeIds) {

            Pattern arcPattern=Pattern.compile("<[^>]*target=[^>]*\""+rangeId+"\"[^>]*>");
            Matcher arcMatcher = arcPattern.matcher(epmlString);
            while(arcMatcher.find())
            {
                List<String> targets=rangeToOutgoingsMap.get(rangeId);
                if(targets==null)
                {
                    targets=new ArrayList<String>();
                    rangeToOutgoingsMap.put(rangeId, targets);
                }
                String target = getAttributeFromTag(arcMatcher.group(), "target");
                if(!rangeId.equals(target))
                        targets.add(target);
            }

        }
        String changedEpml=epmlString;
        for(String rangeId:rangeIds)
        {
            if(rangeToOutgoingsMap.get(rangeId).size()!=0)
            {
                changedEpml=changedEpml.replaceAll("target[ \t]*=[ \t]*\""+rangeId+"\"", "target=\""
                        +rangeToOutgoingsMap.get(rangeId).get(0)+"\"");
            }
        }

        Map<String,List> entitiesToRangeMap=new HashMap<String, List>();
        for (String rangeId : rangeIds) {

            Pattern arcPattern=Pattern.compile("<[^>]*target=[^>]*\""+rangeId+"\"[^>]*>");
            Matcher arcMatcher = arcPattern.matcher(epmlString);
            while(arcMatcher.find())
            {
                List<String> targets=rangeToOutgoingsMap.get(rangeId);
                if(targets==null)
                {
                    targets=new ArrayList<String>();
                    rangeToOutgoingsMap.put(rangeId, targets);
                }
                String target = getAttributeFromTag(arcMatcher.group(), "target");
                if(!rangeId.equals(target))
                        targets.add(target);
            }



        }


        return changedEpml;
    }
*/

}
