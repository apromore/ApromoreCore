
package org.apromore.anf;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.anf package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Annotations_QNAME = new QName("http://www.apromore.org/ANF", "Annotations");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.anf
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LineType }
     * 
     */
    public LineType createLineType() {
        return new LineType();
    }

    /**
     * Create an instance of {@link GraphicsType }
     * 
     */
    public GraphicsType createGraphicsType() {
        return new GraphicsType();
    }

    /**
     * Create an instance of {@link AnnotationsType }
     * 
     */
    public AnnotationsType createAnnotationsType() {
        return new AnnotationsType();
    }

    /**
     * Create an instance of {@link FontType }
     * 
     */
    public FontType createFontType() {
        return new FontType();
    }

    /**
     * Create an instance of {@link AnnotationType }
     * 
     */
    public AnnotationType createAnnotationType() {
        return new AnnotationType();
    }

    /**
     * Create an instance of {@link SizeType }
     * 
     */
    public SizeType createSizeType() {
        return new SizeType();
    }

    /**
     * Create an instance of {@link FillType }
     * 
     */
    public FillType createFillType() {
        return new FillType();
    }

    /**
     * Create an instance of {@link PositionType }
     * 
     */
    public PositionType createPositionType() {
        return new PositionType();
    }

    /**
     * Create an instance of {@link DocumentationType }
     * 
     */
    public DocumentationType createDocumentationType() {
        return new DocumentationType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnnotationsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/ANF", name = "Annotations")
    public JAXBElement<AnnotationsType> createAnnotations(AnnotationsType value) {
        return new JAXBElement<AnnotationsType>(_Annotations_QNAME, AnnotationsType.class, null, value);
    }

}
