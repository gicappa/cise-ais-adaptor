package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.normalize.NavigationStatus;
import eu.cise.datamodel.v1.entity.event.Event;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType;
import eu.cise.datamodel.v1.entity.location.PortLocation;
import eu.cise.datamodel.v1.entity.movement.Movement;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.period.Period;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.datamodel.v1.entity.vessel.VesselType;
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
import java.util.*;

import static eu.cise.datamodel.v1.entity.movement.MovementType.VOYAGE;
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

    private static final Set<String> ISO_COUNTRIES = new HashSet<>
            (Arrays.asList(Locale.getISOCountries()));
    private final AISAdaptorConfig config;

    public DefaultAISTranslator(AISAdaptorConfig config) {
        this.config = config;
    }

    public static boolean isValidISOCountry(String s) {
        return ISO_COUNTRIES.contains(s);
    }

    @Override
    public Optional<Push> translate(AISMsg aisMsg) {
        if (aisMsg.getMessageType() == 1 ||
                aisMsg.getMessageType() == 2 ||
                aisMsg.getMessageType() == 3)
            return translateAISMsg123(aisMsg);
        else if (aisMsg.getMessageType() == 5)
            return translateAISMsg5(aisMsg);

        return Optional.empty();
    }

    private Optional<Push> translateAISMsg5(AISMsg aisMsg) {
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
                .addEntity(toVessel5(
                        Long.valueOf(aisMsg.getUserId()),
                        aisMsg.getShipName(),
                        getBeam(aisMsg),
                        getLength(aisMsg),
                        aisMsg.getCallSign(),
                        f2d(aisMsg.getDraught()),
                        getImoNumber(aisMsg),
                        Long.valueOf(aisMsg.getUserId()),
                        fromAISShipType(aisMsg.getShipType()),
                        aisMsg.getDestination()
                ))
                .build());
    }

    private Long getImoNumber(AISMsg aisMsg) {
        return aisMsg.getImoNumber() == null ? null : Long.valueOf(aisMsg.getImoNumber());
    }

    private Double getLength(AISMsg aisMsg) {
        if (aisMsg.getDimensionA() == null || aisMsg.getDimensionB() == null)
            return null;

        return Double.valueOf(aisMsg.getDimensionA() + aisMsg.getDimensionB());
    }

    private Integer getBeam(AISMsg aisMsg) {
        if (aisMsg.getDimensionC() == null || aisMsg.getDimensionD() == null)
            return null;

        return aisMsg.getDimensionC() + aisMsg.getDimensionD();
    }

    private Vessel toVessel5(Long userId,
                             String vesselName,
                             Integer beam,
                             Double length,
                             String callSign,
                             Double draught,
                             Long imoNumber,
                             Long mmsi,
                             VesselType shipType,
                             String locationCode
    ) {

        Vessel vessel = new Vessel();
        vessel.setMMSI(userId);
        Objet.InvolvedEventRel involvedEventRel = new Objet.InvolvedEventRel();
        Movement movement = new Movement();
        movement.setMovementType(VOYAGE);
        Event.LocationRel locationRel = new Event.LocationRel();
        PortLocation location = new PortLocation();

        if (isLocationCode(locationCode))
            location.setLocationCode(locationCode);

        location.setPortName(locationCode);

        locationRel.setLocation(location);
        movement.getLocationRels().add(locationRel);

        involvedEventRel.setEvent(movement);
        vessel.getInvolvedEventRels().add(involvedEventRel);
        vessel.getNames().add(vesselName);
        vessel.setBeam(beam);
        vessel.setLength(length);
        vessel.setCallSign(callSign);
        vessel.setDraught(draught);
        if (imoNumber != null)
            vessel.setIMONumber(imoNumber);

        vessel.setMMSI(mmsi);
        vessel.getShipTypes().add(shipType);

        return vessel;
    }

    private boolean isLocationCode(String locationCode) {
        if (locationCode == null)
            return false;

        if (locationCode.trim().length() != 5)
            return false;

        String countryCode = locationCode.substring(0, 2);

        if (!isValidISOCountry(countryCode))
            return false;

        return true;
    }

    private Optional<Push> translateAISMsg123(AISMsg aisMsg) {
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
                        Long.valueOf(aisMsg.getUserId()),
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

    private VesselType fromAISShipType(Integer st) {
        if (st == null)
            return null;

        switch (st) {
            case 30:
                return VesselType.FISHING_VESSEL;
            case 31:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 32:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 33:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 34:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 35:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 40:
                return VesselType.HIGH_SPEED_CRAFT;
            case 41:
                return VesselType.HIGH_SPEED_CRAFT;
            case 42:
                return VesselType.HIGH_SPEED_CRAFT;
            case 43:
                return VesselType.HIGH_SPEED_CRAFT;
            case 44:
                return VesselType.HIGH_SPEED_CRAFT;
            case 45:
                return VesselType.HIGH_SPEED_CRAFT;
            case 46:
                return VesselType.HIGH_SPEED_CRAFT;
            case 47:
                return VesselType.HIGH_SPEED_CRAFT;
            case 48:
                return VesselType.HIGH_SPEED_CRAFT;
            case 49:
                return VesselType.HIGH_SPEED_CRAFT;
            case 50:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 51:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 52:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 53:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 54:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 55:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 58:
                return VesselType.SPECIAL_PURPOSE_SHIP;
            case 60:
                return VesselType.PASSENGER_SHIP;
            case 61:
                return VesselType.PASSENGER_SHIP;
            case 62:
                return VesselType.PASSENGER_SHIP;
            case 63:
                return VesselType.PASSENGER_SHIP;
            case 64:
                return VesselType.PASSENGER_SHIP;
            case 65:
                return VesselType.PASSENGER_SHIP;
            case 66:
                return VesselType.PASSENGER_SHIP;
            case 67:
                return VesselType.PASSENGER_SHIP;
            case 68:
                return VesselType.PASSENGER_SHIP;
            case 69:
                return VesselType.PASSENGER_SHIP;
            case 70:
                return VesselType.GENERAL_CARGO_SHIP;
            case 71:
                return VesselType.GENERAL_CARGO_SHIP;
            case 72:
                return VesselType.GENERAL_CARGO_SHIP;
            case 73:
                return VesselType.GENERAL_CARGO_SHIP;
            case 74:
                return VesselType.GENERAL_CARGO_SHIP;
            case 75:
                return VesselType.GENERAL_CARGO_SHIP;
            case 76:
                return VesselType.GENERAL_CARGO_SHIP;
            case 77:
                return VesselType.GENERAL_CARGO_SHIP;
            case 78:
                return VesselType.GENERAL_CARGO_SHIP;
            case 79:
                return VesselType.GENERAL_CARGO_SHIP;
            case 80:
                return VesselType.OIL_TANKER;
            case 81:
                return VesselType.OIL_TANKER;
            case 82:
                return VesselType.OIL_TANKER;
            case 83:
                return VesselType.OIL_TANKER;
            case 84:
                return VesselType.OIL_TANKER;
            case 85:
                return VesselType.OIL_TANKER;
            case 86:
                return VesselType.OIL_TANKER;
            case 87:
                return VesselType.OIL_TANKER;
            case 88:
                return VesselType.OIL_TANKER;
            case 89:
                return VesselType.OIL_TANKER;
            default:
                return VesselType.OTHER;
        }
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

