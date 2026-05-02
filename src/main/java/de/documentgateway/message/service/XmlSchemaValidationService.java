package de.documentgateway.message.service;

import de.documentgateway.common.exception.ApiException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class XmlSchemaValidationService {



    private Schema invoiceSchema;

    @PostConstruct
    void loadSchema()  throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = getClass().getResource("/xsd/invoice-message.xsd");
        if (url == null) {
            throw new IllegalStateException("XSD not found: /xsd/invoice-message.xsd");
        }
        this.invoiceSchema = factory.newSchema(url);
    }


    public void validate(String xmlPayload) {
        if (invoiceSchema == null) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "XSD_NOT_AVAILABLE",
                    "XML schema is not available"
            );
        }

        try {
            Validator validator = invoiceSchema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlPayload)));
        } catch (SAXException e) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "XML_VALIDATION_FAILED",
                    "XML does not match the expected schema"
            );
        } catch (IOException e) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "XSD_VALIDATION_FAILED",
                    "Could not read XML for validation"
            );
        }
    }
}
