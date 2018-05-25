package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.normalize.NavigationStatus;
import eu.cise.datamodel.v1.entity.Entity;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.period.Period;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.*;

public class Message123Translator implements Translator<AISMsg, Vessel> {

    private final AISAdaptorConfig config;

    public Message123Translator(AISAdaptorConfig config) {
        this.config = config;
    }

    @Override
    public Vessel translate(AISMsg aisMsg) {

        // casting float to double
        // casting float to double
        Long mmsi = Long.valueOf(aisMsg.getUserId());

        Vessel vessel = new Vessel();

        // This is needed because the AIS doesn't have an IMO number specified
        // but only the MMSI and the light client we built
        if (config.isDemoEnvironment())
            vessel.setIMONumber(mmsi);

        vessel.setMMSI(mmsi);
        vessel.getLocationRels().add(getLocationRel(latitude(aisMsg), longitude(aisMsg), fromPositionAccuracy(aisMsg), fromCourseOverGround(aisMsg.getCOG()), fromTrueHeading(aisMsg.getTrueHeading()), aisMsg.getTimestamp(), fromSpeedOverGround(aisMsg.getSOG())));
        vessel.setNavigationalStatus(fromNavigationStatus(aisMsg.getNavigationStatus()));
        return vessel;
    }

    private LocationQualitativeAccuracyType fromPositionAccuracy(AISMsg aisMsg) {
        return aisMsg.getPositionAccuracy() == 1 ?
                LocationQualitativeAccuracyType.HIGH :
                LocationQualitativeAccuracyType.LOW;
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
     * It translates the navigation status from AIS to CISE model.
     *
     * @param ns the navigation status to be translated from AIS Message
     * @return the NavigationalStatusType enum coming from the CISE data model
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
