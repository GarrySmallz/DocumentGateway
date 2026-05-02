package de.documentgateway.message.service;

import de.documentgateway.audit.AuditService;
import de.documentgateway.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.xml.sax.SAXException;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class XmlSchemaValidationServiceTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private XmlSchemaValidationService xmlSchemaValidationService;

    @BeforeEach
    void setUp() throws SAXException {
        xmlSchemaValidationService = new XmlSchemaValidationService();
        xmlSchemaValidationService.loadSchema();
    }

    @Test
    @DisplayName("processIncomingMessage: Validation failure -> 400 XML_VALIDATION_FAILED")
    void shouldRejectUnmatchingStructure() {

        String xml = """
                        <message xmlns="http://documentgateway.de/invoice/v1">
                            <id>1</id>
                            <unknownElement>unexpected</unknownElement>
                        </message>
                        """;

        //act
        ApiException ex = assertThrows(ApiException.class, () -> xmlSchemaValidationService.validate(xml));

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals("XML_VALIDATION_FAILED", ex.getCode());
    }

    @Test
    @DisplayName("validate: Valid XML -> no exception")
    void shouldAcceptValidXml() {
        String xml = """
                <message xmlns="http://documentgateway.de/invoice/v1">
                    <id>1</id>
                </message>
                """;

        assertDoesNotThrow(() -> xmlSchemaValidationService.validate(xml));
    }

    @Test
    @DisplayName("loadSchema: schema is available and usable")
    void shouldLoadSchemaSuccessfully() {
        XmlSchemaValidationService service = new XmlSchemaValidationService();

        assertDoesNotThrow(service::loadSchema);

        String validXml = """
            <message xmlns="http://documentgateway.de/invoice/v1">
                <id>1</id>
            </message>
            """;

        assertDoesNotThrow(() -> service.validate(validXml));
    }


}
