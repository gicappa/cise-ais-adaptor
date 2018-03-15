package eu.cise.adaptor.tbs;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.normalize.NavigationStatus;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
 *
 * @return an AISMsg object
 */
public class TbsAISNormalizer implements AISNormalizer<AISMessage> {

    private final Clock clock;

    public TbsAISNormalizer() {
        this.clock = Clock.systemUTC();
    }

    public TbsAISNormalizer(Clock clock) {
        this.clock = clock;
    }

    public AISMsg normalize(AISMessage m) {
        Integer type = m.getMessageType().getCode();
        AISMsg.Builder b = new AISMsg.Builder(type);

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
        b.withETA(computeETA(m));

        return b.build();
    }

    // eta=18-07 17:00
    private Instant computeETA(AISMessage m) {
        String etaStr = (String) m.dataFields().get("eta");
        String[] etadt = etaStr.split(" ");
        String dateTimeString = getCurrentYear() + "-" + switchDayMonth(etadt[0]) + "T" + etadt[1] + ":00.000Z";
        Instant eta = Instant.parse(dateTimeString);

        if (eta.isBefore(Instant.now(clock)))
            eta  = eta.plus(365, ChronoUnit.DAYS);

        return eta;
    }

    private String switchDayMonth(String dayMonth) {
        String a[] = dayMonth.split("-");
        return a[1]+"-"+a[0];
    }

    private int getCurrentYear() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).getYear();
    }

    private NavigationStatus getNavigationStatus(String ns) {
        return ns == null ? null : NavigationStatus.valueOf(ns);
    }

    /**
     * @return 1 if position accuracy lte 10m; 0 otherwise.
     */
    private int getPositionAccuracy(Map<String, Object> m) {
        return (Boolean) m.getOrDefault("positionAccuracy", FALSE) ? 1 : 0;
    }

    private boolean isPositionMessage(Integer type) {
        return type != 1 && type != 2 && type != 3 && type != 5;
    }

    private Optional<Metadata> oMeta(AISMessage m) {
        return Optional.ofNullable(m.getMetadata());
    }

}
