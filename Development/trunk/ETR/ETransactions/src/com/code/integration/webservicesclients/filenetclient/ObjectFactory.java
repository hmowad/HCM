
package com.code.integration.webservicesclients.filenetclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.code.integration.webservicesclients.filenetclient package. 
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

    private final static QName _Exception_QNAME = new QName("http://services.archms.fg.com/", "Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.code.integration.webservicesclients.filenetclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FnAttachment }
     * 
     */
    public FnAttachment createFnAttachment() {
        return new FnAttachment();
    }

    /**
     * Create an instance of {@link FnAttachment.DocProps }
     * 
     */
    public FnAttachment.DocProps createFnAttachmentDocProps() {
        return new FnAttachment.DocProps();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link DocumentVersion }
     * 
     */
    public DocumentVersion createDocumentVersion() {
        return new DocumentVersion();
    }

    /**
     * Create an instance of {@link FnAttachment.DocProps.Entry }
     * 
     */
    public FnAttachment.DocProps.Entry createFnAttachmentDocPropsEntry() {
        return new FnAttachment.DocProps.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.archms.fg.com/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

}
