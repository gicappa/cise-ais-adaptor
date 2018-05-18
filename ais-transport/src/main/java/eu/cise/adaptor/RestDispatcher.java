package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

/**
 * This class is meant to perform RESTful request to nodes or legacy systems.
 * The current implementation is just sending CISE Messages
 */
@SuppressWarnings({"WeakerAccess", "Unused"})
public class RestDispatcher implements Dispatcher {

    private final RestClient client;
    private final XmlMapper xmlMapper;

    /**
     * This constructor is called by the class for name
     */
    @SuppressWarnings("unused")
    public RestDispatcher() {
        this(new JerseyRestClient(), new DefaultXmlMapper.NotValidating());
    }

    public RestDispatcher(RestClient client, XmlMapper xmlMapper) {
        this.client = client;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public DispatchResult send(Message message, String address) {
        String payload = xmlMapper.toXML(message);

        RestResult result = client.post(address, payload);

        return new DispatchResult(result.isOK(), result.getBody());
    }

}