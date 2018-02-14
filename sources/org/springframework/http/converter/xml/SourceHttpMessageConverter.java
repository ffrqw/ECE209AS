package org.springframework.http.converter.xml;

import android.os.Build.VERSION;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class SourceHttpMessageConverter<T extends Source> extends AbstractHttpMessageConverter<T> {
    private static final EntityResolver NO_OP_ENTITY_RESOLVER = new EntityResolver() {
        public final InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
        }
    };
    private static final Set<Class<?>> SUPPORTED_CLASSES;
    private boolean processExternalEntities = false;
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    private static class CountingOutputStream extends OutputStream {
        long count;

        private CountingOutputStream() {
            this.count = 0;
        }

        public final void write(int b) throws IOException {
            this.count++;
        }

        public final void write(byte[] b) throws IOException {
            this.count += (long) b.length;
        }

        public final void write(byte[] b, int off, int len) throws IOException {
            this.count += (long) len;
        }
    }

    protected final /* bridge */ /* synthetic */ Long getContentLength(Object x0, MediaType x1) throws IOException {
        return getContentLength$551b34af((Source) x0);
    }

    protected final /* bridge */ /* synthetic */ void writeInternal(Object x0, HttpOutputMessage x1) throws IOException, HttpMessageNotWritableException {
        Source source = (Source) x0;
        try {
            transform(source, new StreamResult(x1.getBody()));
        } catch (Throwable e) {
            throw new HttpMessageNotWritableException("Could not transform [" + source + "] to output message", e);
        }
    }

    static {
        Set hashSet = new HashSet(4);
        SUPPORTED_CLASSES = hashSet;
        hashSet.add(DOMSource.class);
        SUPPORTED_CLASSES.add(SAXSource.class);
        SUPPORTED_CLASSES.add(StreamSource.class);
        SUPPORTED_CLASSES.add(Source.class);
    }

    public SourceHttpMessageConverter() {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
    }

    public final boolean supports(Class<?> clazz) {
        return SUPPORTED_CLASSES.contains(clazz);
    }

    private DOMSource readDOMSource(InputStream body) throws IOException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(NO_OP_ENTITY_RESOLVER);
            return new DOMSource(documentBuilder.parse(body));
        } catch (ParserConfigurationException ex) {
            throw new HttpMessageNotReadableException("Could not set feature: " + ex.getMessage(), ex);
        } catch (SAXException ex2) {
            throw new HttpMessageNotReadableException("Could not parse document: " + ex2.getMessage(), ex2);
        }
    }

    private SAXSource readSAXSource(InputStream body) throws IOException {
        try {
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            if (VERSION.SDK_INT >= 14) {
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            }
            byte[] bytes = StreamUtils.copyToByteArray(body);
            reader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
            return new SAXSource(reader, new InputSource(new ByteArrayInputStream(bytes)));
        } catch (ParserConfigurationException ex) {
            throw new HttpMessageNotReadableException("Could not parse document: " + ex.getMessage(), ex);
        } catch (SAXException ex2) {
            throw new HttpMessageNotReadableException("Could not parse document: " + ex2.getMessage(), ex2);
        }
    }

    private Long getContentLength$551b34af(T t) {
        if (t instanceof DOMSource) {
            try {
                CountingOutputStream os = new CountingOutputStream();
                transform(t, new StreamResult(os));
                return Long.valueOf(os.count);
            } catch (TransformerException e) {
            }
        }
        return null;
    }

    private void transform(Source source, Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }

    protected final /* bridge */ /* synthetic */ Object readInternal(Class x0, HttpInputMessage x1) throws IOException, HttpMessageNotReadableException {
        InputStream body = x1.getBody();
        if (DOMSource.class.equals(x0)) {
            return readDOMSource(body);
        }
        if (SAXSource.class.equals(x0)) {
            return readSAXSource(body);
        }
        if (StreamSource.class.equals(x0) || Source.class.equals(x0)) {
            return new StreamSource(new ByteArrayInputStream(StreamUtils.copyToByteArray(body)));
        }
        throw new HttpMessageConversionException("Could not read class [" + x0 + "]. Only DOMSource, SAXSource, and StreamSource are supported.");
    }
}
