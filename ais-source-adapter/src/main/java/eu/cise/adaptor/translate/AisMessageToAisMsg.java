package eu.cise.adaptor.translate;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import dk.tbsalling.aismessages.ais.messages.types.ShipType;
import eu.cise.adaptor.AisMsg;
import eu.cise.adaptor.translate.utils.Eta;
import eu.cise.adaptor.translate.utils.NavigationStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;

/**
 * This classes normalize the AISMessage class read by the tbsalling's library
 * into an internal one.
 * <p>
 * The message is translated field by field in order to support many different
 * AIS libraries.
 * <p>
 * The timestamp sometimes is not filled in the source AISMessage object and in
 * this case the timestamp field is filled with Instant.MIN value.
 */
public class AisMessageToAisMsg implements Translator<AISMessage, AisMsg> {

    private final Eta eta;

    public AisMessageToAisMsg() {
        this(Clock.systemUTC());
    }

    public AisMessageToAisMsg(Clock clock) {
        this.eta = new Eta(clock);
    }

    @Override
    public AisMsg translate(AISMessage m) {
        Integer type = m.getMessageType().getCode();
        AisMsg.Builder b = new AisMsg.Builder(type);

        // TODO the remaining fields are not supported by other type of messages
        // than message type 1,2,3
        if (isPositionMessage(type))
            return b.build();

        // POSITION
        b.withUserId(m.getSourceMmsi().getMMSI());
        b.withLatitude((Float) m.dataFields().getOrDefault("latitude", 0F));
        b.withLongitude((Float) m.dataFields().getOrDefault("longitude", 0F));
        b.withPositionAccuracy(getPositionAccuracy(m.dataFields()));
        b.withCOG((Float) m.dataFields().getOrDefault("courseOverGround", 0F));
        b.withSOG((Float) m.dataFields().getOrDefault("speedOverGround", 0F));
        b.withTrueHeading((Integer) m.dataFields().getOrDefault("trueHeading", 0));
        b.withNavigationStatus(getNavigationStatus((String) m.dataFields().get("navigationStatus")));
        b.withTimestamp(oMeta(m).map(Metadata::getReceived).orElse(Instant.MIN));

        // VOYAGE
        b.withDestination((String) m.dataFields().getOrDefault("destination", ""));
        b.withEta(eta.computeETA((String) m.dataFields().get("eta")));
        b.withIMONumber((Integer) m.dataFields().getOrDefault("imo.IMO", 0));
        b.withCallSign((String) m.dataFields().getOrDefault("callsign", ""));
        b.withDraught((Float) m.dataFields().getOrDefault("draught", 0F));
        b.withDimensionC((Integer) m.dataFields().getOrDefault("toPort", 0));
        b.withDimensionD((Integer) m.dataFields().getOrDefault("toStarboard", 0));
        b.withDimensionA((Integer) m.dataFields().getOrDefault("toBow", 0));
        b.withDimensionB((Integer) m.dataFields().getOrDefault("toStern", 0));
        b.withShipType(translateShipType(m));
        b.withShipName((String) m.dataFields().getOrDefault("shipName", ""));

        return b.build();
    }

    private Integer translateShipType(AISMessage m) {
        String shipType = (String) m.dataFields().get("shipType");

        if (shipType == null)
            return 0;

        return ShipType.valueOf(shipType).getCode();
    }

    private NavigationStatus getNavigationStatus(String ns) {
        return ns == null ? null : NavigationStatus.valueOf(ns);
    }

    /**
     * @param m is a map containing the properties coming from the AIS
     *          unmarshalling
     * @return 1 if position accuracy lte 10m; 0 otherwise.
     */
    int getPositionAccuracy(Map<String, Object> m) {
        return (Boolean) m.getOrDefault("positionAccuracy", FALSE) ? 1 : 0;
    }

    private boolean isPositionMessage(Integer type) {
        return type != 1 && type != 2 && type != 3 && type != 5;
    }

    private Optional<Metadata> oMeta(AISMessage m) {
        return Optional.ofNullable(m.getMetadata());
    }

}
