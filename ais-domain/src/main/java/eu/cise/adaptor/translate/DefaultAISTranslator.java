package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.normalize.NavigationStatus;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.period.Period;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.authority.SeaBasinType;
import eu.cise.servicemodel.v1.message.*;
import eu.cise.servicemodel.v1.service.DataFreshnessType;
import eu.cise.servicemodel.v1.service.ServiceOperationType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.*;
import static eu.cise.servicemodel.v1.service.ServiceOperationType.PUSH;
import static eu.eucise.helpers.ParticipantBuilder.newParticipant;
import static eu.eucise.helpers.PushBuilder.newPush;
import static eu.eucise.helpers.ServiceBuilder.newService;

/**
 * This is the translator from the internal AISMsg object to a CISE Push message
 * <p>
 * TODO There is a difference in latitude and longitude between the AIS and the
 * CISE calculation. Here for simplicity it hasn't been taken into account.
 * <p>
 * Please refer to:
 * https://webgate.ec.europa.eu/CITnet/confluence/display/MAREX/AIS+Message+1%2C2%2C3
 */
public class DefaultAISTranslator implements AISTranslator {

    private final AISAdaptorConfig config;

    public DefaultAISTranslator(AISAdaptorConfig config) {
        this.config = config;
    }

    @Override
    public Optional<Push> translate(AISMsg aisMsg) {
        if (isTypeSupported(aisMsg)) {
            return Optional.empty();
        }

        return Optional.of(newPush()
                .id(UUID.randomUUID().toString())
                .contextId(UUID.randomUUID().toString())
                .correlationId(UUID.randomUUID().toString())
                .creationDateTime(new Date())
                .sender(newService()
                        .id(config.getServiceId())
                        .dataFreshness(DataFreshnessType.fromValue(config.getDataFreshnessType()))
                        .seaBasin(SeaBasinType.fromValue(config.getSeaBasinType()))
                        .operation(ServiceOperationType.fromValue(config.getServiceOperation()))
                        .participant(newParticipant().endpointUrl(config.getEndpointUrl())))
                .recipient(newService()
                        .id("it.gc-ls01.vessel.push.gcs04")
                        .operation(PUSH)
                )
                .priority(PriorityType.fromValue(config.getMessagePriority()))
                .isRequiresAck(false)
                .informationSecurityLevel(InformationSecurityLevelType.fromValue(config.getSecurityLevel()))
                .informationSensitivity(InformationSensitivityType.fromValue(config.getSensitivity()))
                .isPersonalData(false)
                .purpose(PurposeType.fromValue(config.getPurpose()))
                .addEntity(toVessel(
                        latitude(aisMsg),
                        longitude(aisMsg),
                        fromPositionAccuracy(aisMsg),
                        fromCourseOverGround(aisMsg.getCOG()),  // casting float to double
                        fromTrueHeading(aisMsg.getTrueHeading()),
                        aisMsg.getTimestamp(),
                        fromSpeedOverGround(aisMsg.getSOG()),  // casting float to double
                        Long.valueOf(aisMsg.getMMSI()),
                        fromNavigationStatus(aisMsg.getNavigationStatus())
                        )
                )

                .build());
    }

    private LocationQualitativeAccuracyType fromPositionAccuracy(AISMsg aisMsg) {
        return aisMsg.getPositionAccuracy() == 1 ?
                LocationQualitativeAccuracyType.HIGH :
                LocationQualitativeAccuracyType.LOW;
    }

    private Vessel toVessel(String latitude,
                            String longitude,
                            LocationQualitativeAccuracyType lqat,
                            Double cog,
                            Double heading,
                            Instant timestamp,
                            Double sog,
                            Long mmsi,
                            NavigationalStatusType nst) {

        Vessel vessel = new Vessel();

        // This is needed because the AIS doesn't have an IMO number specified
        // but only the MMSI and the light client we built
        if (config.isDemoEnvironment())
            vessel.setIMONumber(mmsi);

        vessel.setMMSI(mmsi);
        vessel.getLocationRels().add(getLocationRel(latitude, longitude, lqat, cog, heading, timestamp, sog));
        vessel.setNavigationalStatus(nst);
        return vessel;
    }

    private Objet.LocationRel getLocationRel(String latitude,
                                             String longitude,
                                             LocationQualitativeAccuracyType lqat,
                                             Double cog,
                                             Double heading,
                                             Instant timestamp,
                                             Double sog) {

        Objet.LocationRel locationRel = new Objet.LocationRel();
        locationRel.setLocation(toLocation(latitude, longitude, lqat));
        locationRel.setCOG(cog);
        locationRel.setHeading(heading);

        if (config.isOverridingTimestamps()) {
            timestamp = Instant.now();
        }

        if (!timestamp.equals(Instant.MIN)) {
            Period period = new Period();
            period.setStartDate(toXMLDate(timestamp));
            period.setStartTime(toXMLTime(timestamp));
            locationRel.setPeriodOfTime(period);
        }

        locationRel.setSourceType(SourceType.DECLARATION);
        locationRel.setSensorType(SensorType.AUTOMATIC_IDENTIFICATION_SYSTEM);
        locationRel.setSOG(sog);

        return locationRel;
    }

    private XMLGregorianCalendar toXMLCalendar(int year, int month, int day, int hours, int minutes, int seconds) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(year, month, day, hours, minutes, seconds, 0, 0);
        } catch (DatatypeConfigurationException e) {
            throw new AISAdaptorException("Can't create a correct XMLGregorianCalendar DATE/TIME out of the instant ", e);
        }
    }

    private XMLGregorianCalendar toXMLDate(Instant timestamp) {
        LocalDateTime l = LocalDateTime.ofInstant(timestamp, ZoneId.of("UTC"));
        return toXMLCalendar(l.getYear(), l.getMonthValue(), l.getDayOfMonth(), 0, 0, 0);
    }

    private XMLGregorianCalendar toXMLTime(Instant timestamp) {
        LocalDateTime l = LocalDateTime.ofInstant(timestamp, ZoneId.of("UTC"));
        return toXMLCalendar(1970, 01, 01, l.getHour(), l.getMinute(), l.getSecond());
    }

    private String longitude(AISMsg aisMsg) {
        return Float.toString(aisMsg.getLongitude());
    }

    private String latitude(AISMsg aisMsg) {
        return Float.toString(aisMsg.getLatitude());
    }

    private Location toLocation(String latitude, String longitude, LocationQualitativeAccuracyType lqat) {
        Location location = new Location();
        Geometry geometry = new Geometry();
        geometry.setLatitude(latitude);
        geometry.setLongitude(longitude);
        location.getGeometries().add(geometry);
        location.setLocationQualitativeAccuracy(lqat);
        return location;
    }

    private boolean isTypeSupported(AISMsg aisMessage) {
        return aisMessage.getMessageType() != 1 &&
                aisMessage.getMessageType() != 2 &&
                aisMessage.getMessageType() != 3 &&
                aisMessage.getMessageType() != 5;
    }

    private Double f2d(Float fValue) {
        return Double.valueOf(fValue.toString());
    }

    private Double fromCourseOverGround(Float cog) {
        return cog == 3600 ? null : f2d(cog) / 10D;
    }

    private Double fromSpeedOverGround(Float sog) {
        return sog == 1023 ? null : f2d(sog) / 10D;
    }

    private Double fromTrueHeading(int th) {
        return th == 511 ? null : Double.valueOf(th);
    }

    /**
     * @param ns
     * @return
     */
    private NavigationalStatusType fromNavigationStatus(NavigationStatus ns) {
        if (ns == null)
            return UNDEFINED_DEFAULT;

        switch (ns) {
            case UnderwayUsingEngine:
                return UNDER_WAY_USING_ENGINE;
            case AtAnchor:
                return AT_ANCHOR;
            case NotUnderCommand:
                return NOT_UNDER_COMMAND;
            case RestrictedManoeuverability:
                return RESTRICTED_MANOEUVRABILITY;
            case ConstrainedByHerDraught:
                return CONSTRAINED_BY_HER_DRAUGHT;
            case Moored:
                return MOORED;
            case Aground:
                return AGROUND;
            case EngagedInFising:
                return ENGAGED_IN_FISHING;
            case UnderwaySailing:
                return UNDER_WAY_SAILING;
            case ReservedForFutureUse9:
                return OTHER;
            case ReservedForFutureUse10:
                return OTHER;
            case PowerDrivenVesselTowingAstern:
                return POWER_DRIVEN_VESSEL_TOWING_ASTERN;
            case PowerDrivenVesselPushingAheadOrTowingAlongside:
                return POWER_DRIVEN_VESSEL_TOWIG_AHEAD_OR_PUSHING_ALONGSIDE;
            case ReservedForFutureUse13:
                return OTHER;
            case SartMobOrEpirb:
                return OTHER;
            case Undefined:
                return UNDEFINED_DEFAULT;
            default:
                return UNDEFINED_DEFAULT;
        }

    }

}

