package eu.cise.adaptor;

import eu.cise.datamodel.v1.entity.cargo.Cargo;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UnmarshallerTest {

    private String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns4:Push xmlns:ns2=\"http://www.cise.eu/servicemodel/v1/authority/\" " +
            "xmlns:ns4=\"http://www.cise.eu/servicemodel/v1/message/\" xmlns:ns3=\"http://www" +
            ".cise.eu/servicemodel/v1/service/\">\n" +
            "    <CorrelationID>476d949d-5aa4-44cc-8e20-c1a2288fe098</CorrelationID>\n" +
            "    <CreationDateTime>2019-04-18T16:40:16.842+02:00</CreationDateTime>\n" +
            "    <MessageID>ce7adf36-417c-4229-aeba-39b10af4fc97</MessageID>\n" +
            "    <Priority>Low</Priority>\n" +
            "    <Sender>\n" +
            "        <SeaBasin>NorthSea</SeaBasin>\n" +
            "        <ServiceID>de.sim1-node01.vessel.push.provider</ServiceID>\n" +
            "        <ServiceOperation>Push</ServiceOperation>\n" +
            "        <ServiceRole>Provider</ServiceRole>\n" +
            "        <ServiceStatus>Online</ServiceStatus>\n" +
            "        <ServiceType>VesselService</ServiceType>\n" +
            "    </Sender>\n" +
            "    <Recipient>\n" +
            "        <SeaBasin>NorthSea</SeaBasin>\n" +
            "        <ServiceID>de.sim2-node01.vessel.push.consumer</ServiceID>\n" +
            "        <ServiceOperation>Push</ServiceOperation>\n" +
            "        <ServiceRole>Consumer</ServiceRole>\n" +
            "        <ServiceStatus>Online</ServiceStatus>\n" +
            "        <ServiceType>VesselService</ServiceType>\n" +
            "    </Recipient>\n" +
            "    <Payload xsi:type=\"ns4:XmlEntityPayload\" xmlns:xsi=\"http://www.w3" +
            ".org/2001/XMLSchema-instance\">\n" +
            "        <InformationSecurityLevel>NonClassified</InformationSecurityLevel>\n" +
            "        <InformationSensitivity>Green</InformationSensitivity>\n" +
            "        <Purpose>NonSpecified</Purpose>\n" +
            "        <EnsureEncryption>false</EnsureEncryption>\n" +
            "        <Cargo>\n" +
            "            <Identifier>\n" +
            "                <UUID>884cb678-9386-4bff-9233-2bc882doi8f5</UUID>\n" +
            "    </Identifier>\n" +
            "            <LocationRel>\n" +
            "                <Location>\n" +
            "                    <Geometry>\n" +
            "                        <Latitude>41.3242</Latitude>\n" +
            "                        <Longitude>14.32</Longitude>\n" +
            "        </Geometry>\n" +
            "      </Location>\n" +
            "    </LocationRel>\n" +
            "            <CargoType>LargeFreightContainers</CargoType>\n" +
            "            <ContainedCargoUnitRel>\n" +
            "                <CargoUnit xsi:type=\"ns3:ContainmentUnit\" xmlns:ns3=\"http://www" +
            ".cise.eu/datamodel/v1/entity/cargo/\">\n" +
            "                    <CommunityStatusOfGoods>OtherGoods</CommunityStatusOfGoods>\n" +
            "                    <ContainerMarksAndNumber>FRT0002</ContainerMarksAndNumber>\n" +
            "                    <GrossQuantity>22.2</GrossQuantity>\n" +
            "                    <LocationOnBoardContainer>1F</LocationOnBoardContainer>\n" +
            "                    <PackageType>Bulk</PackageType>\n" +
            "      </CargoUnit>\n" +
            "    </ContainedCargoUnitRel>\n" +
            "            <ContainedCargoUnitRel>\n" +
            "                <CargoUnit xsi:type=\"ns3:ContainmentUnit\" xmlns:ns3=\"http://www" +
            ".cise.eu/datamodel/v1/entity/cargo/\">\n" +
            "                    <CommunityStatusOfGoods>OtherGoods</CommunityStatusOfGoods>\n" +
            "                    <ContainerMarksAndNumber>PTA12</ContainerMarksAndNumber>\n" +
            "                    <DangerousSubstancesCode>Class31Petrol</DangerousSubstancesCode" +
            ">\n" +
            "                    <LocationOnBoardContainer>64G</LocationOnBoardContainer>\n" +
            "                    <PackageType>Bulk</PackageType>\n" +
            "                    <UnitsOfMeasure>Kilogram</UnitsOfMeasure>\n" +
            "      </CargoUnit>\n" +
            "    </ContainedCargoUnitRel>\n" +
            "\n" +
            "  </Cargo>\n" +
            "    </Payload>\n" +
            "    <Signature:Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" " +
            "xmlns:Signature=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
            "        <SignedInfo>\n" +
            "            <CanonicalizationMethod Algorithm=\"http://www.w3" +
            ".org/2001/10/xml-exc-c14n#\"/>\n" +
            "            <SignatureMethod Algorithm=\"http://www.w3" +
            ".org/2000/09/xmldsig#rsa-sha1\"/>\n" +
            "            <Reference URI=\"\">\n" +
            "                <Transforms>\n" +
            "                    <Transform Algorithm=\"http://www.w3" +
            ".org/TR/1999/REC-xslt-19991116\">\n" +
            "                        <xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3" +
            ".org/1999/XSL/Transform\" xmlns:s=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
            "                            <xsl:strip-space elements=\"*\"/>\n" +
            "                            <xsl:output indent=\"false\" method=\"xml\" " +
            "omit-xml-declaration=\"yes\"/>\n" +
            "                            <xsl:template match=\"*[not(self::s:Signature)]\">\n" +
            "                                <xsl:element name=\"{local-name()}\">\n" +
            "                                    <xsl:apply-templates select=\"*|text()\"/>\n" +
            "                                </xsl:element>\n" +
            "                            </xsl:template>\n" +
            "                            <xsl:template match=\"s:Signature\"/>\n" +
            "                        </xsl:stylesheet>\n" +
            "                    </Transform>\n" +
            "                    <Transform Algorithm=\"http://www.w3" +
            ".org/2000/09/xmldsig#enveloped-signature\"/>\n" +
            "                </Transforms>\n" +
            "                <DigestMethod Algorithm=\"http://www.w3" +
            ".org/2000/09/xmldsig#sha1\"/>\n" +
            "                <DigestValue>F+NAnTljYmvKPbY7u6ZoUwPrdiI=</DigestValue>\n" +
            "            </Reference>\n" +
            "        </SignedInfo>\n" +
            "        <SignatureValue>i4Ck8pnG16Q+i" +
            "+BGfAURiZHMQMjIikd27p5yHOf1SwSHsQNRyMfHPDz6JXW0mBz1UqzfTljLpymV\n" +
            "UpOFU+bt92csudRDsjQ4J8KhmMA1rHYKxM1mCnBfmoMc133yV/EBMUaA2v3A5uJf3uwadmdS9xum\n" +
            "RviCPJD/saTkqwRxgSXWC5z8QWoDg8vT3U+cZC9AKb+3GyRaKS3hBoXGPaZ7X+BSi+0WnArbWA6d\n" +
            "x5r2nQ15paZoWDCQVU8AOu7Y+RJJKZryAjpxLDvHpEz8sQ7SJvdCW5ZnB5O8TBgWQfaOn9o1Qa7j\n" +
            "daOHMtvBmb1AVzHEurbVTJ1VZO4ZyptANXABGQ==</SignatureValue>\n" +
            "        <KeyInfo>\n" +
            "            <X509Data>\n" +
            "                <X509SubjectName>C=fr, DC=eucise, O=node01, OU=HOSTS, CN=apache" +
            ".node01.eucise.fr</X509SubjectName>\n" +
            "                " +
            "<X509Certificate" +
            ">MIIEJDCCAwygAwIBAgIIMOXnDeKwPAQwDQYJKoZIhvcNAQELBQAwPTEdMBsGA1UEAwwUc2lnbmlu\n" +
            "Zy1jYS5ldWNpc2UuZnIxDzANBgNVBAoMBmV1Y2lzZTELMAkGA1UEBhMCZnIwHhcNMTgwNTA5MTYx\n" +
            "NTQ1WhcNMjgwNTA4MTYxMjExWjBoMSAwHgYDVQQDDBdhcGFjaGUubm9kZTAxLmV1Y2lzZS5mcjEO\n" +
            "MAwGA1UECwwFSE9TVFMxDzANBgNVBAoMBm5vZGUwMTEWMBQGCgmSJomT8ixkARkWBmV1Y2lzZTEL\n" +
            "MAkGA1UEBhMCZnIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCbluis2I78UuV09KZD\n" +
            "sXMGABuHM1xm+TvoUTbG16zyY8HwyxUzW3KaNl46KWD1sVWEmVQrlU0IoCDc9sfZaWzCcPPTvUSJ\n" +
            "W/dxvZ2VwIRIwiFEWe4KZzEFXHFeX3LG475T4D9usgBUighpP/HHh+t9zbUILt+On/p6H0dfgzY9\n" +
            "Rh/VcEyC/jIg9btTHoTlbNUBll+19yquoBJAgrAR1M66SWXYYabL6XOkFv9XL6hsFp5D9GZRPLjw\n" +
            "HXQJolMMIJTLvZaLvlBP9Auz6KC7fT8DAW4/52q4Qy0mZrYX/oq9AG6lof9TrAwT3IlN2Ge6krm5\n" +
            "78TTOXoYqCPKLtUW+DJZAgMBAAGjgfwwgfkwDAYDVR0TAQH/BAIwADAfBgNVHSMEGDAWgBRmUALb\n" +
            "i2sa21C+MX+4mQ1mHtmtHzBWBggrBgEFBQcBAQRKMEgwRgYIKwYBBQUHMAGGOmh0dHA6Ly9lamJj\n" +
            "YS5jYS5ldWNpc2UuZnI6ODA4MC9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwIgYDVR0RBBsw\n" +
            "GYIXYXBhY2hlLm5vZGUwMS5ldWNpc2UuZnIwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMB\n" +
            "MB0GA1UdDgQWBBSNhdC2fr0BNngQ6cf/9TZFPi4v0DAOBgNVHQ8BAf8EBAMCAvwwDQYJKoZIhvcN\n" +
            "AQELBQADggEBAIpc98mWMWsJwOY9PllDAaScBhRo0NWIm8BBx2mz8NmAveTJVtGyj4s9netpdwOh\n" +
            "/TRGT2gcHtKThE+P3FhJpjgYPIR/GeLyI72Kc2aUyd9jrCJcI/2S6s6u3o38nVg5caC04OLmPWYw\n" +
            "ACtQmFWaJ8XtHRYJi0C7t41TvM/1auwH5QlpMAZ1G2FQfrftDcgc9ngNGnTg8VMZNvsHWqjrJSrK\n" +
            "Ykg1FodIoGoUWg0m9BYWxiZYEE+KcrOSZwnZJKR8fFBE0bTey7HCvk+udCyJs4BUPSEIn2RgSOpb\n" +
            "FHxYxsecwBqqVCN+zSm2Ivvb73hez6nhwbo3xbLuRHl/sJEyGFw=</X509Certificate>\n" +
            "            </X509Data>\n" +
            "        </KeyInfo>\n" +
            "    </Signature:Signature>\n" +
            "</ns4:Push>";

    private Push pushCargo;

    @Before
    public void before() {
        XmlMapper xmlMapper = new DefaultXmlMapper.Pretty();
        pushCargo = xmlMapper.fromXML(xmlString);
    }

    @Test
    public void it_should_unmarshall_an_object() {
        assertThat(pushCargo, is(notNullValue()));
    }

    @Test
    public void it_should_unmarshall_an_object_with_a_payload() {
        assertThat(pushCargo.getAny(), is(notNullValue()));
    }

    @Test
    public void it_should_have_a_cargo_payload() {

        XmlEntityPayload xep = (XmlEntityPayload) pushCargo.getPayload();

        assertThat(xep.getAnies().get(0), is(instanceOf(Cargo.class)));
    }
}
