/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.adaptor.translate;

import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.AGROUND;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.AT_ANCHOR;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.CONSTRAINED_BY_HER_DRAUGHT;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.ENGAGED_IN_FISHING;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.MOORED;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.NOT_UNDER_COMMAND;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.OTHER;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.POWER_DRIVEN_VESSEL_TOWIG_AHEAD_OR_PUSHING_ALONGSIDE;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.POWER_DRIVEN_VESSEL_TOWING_ASTERN;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.RESTRICTED_MANOEUVRABILITY;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.UNDEFINED_DEFAULT;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.UNDER_WAY_SAILING;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.UNDER_WAY_USING_ENGINE;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.adaptor.AisMsg;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.translate.utils.NavigationStatus;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.period.Period;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This object translate messages of type 5 into CISE Vessel objects.
 */
public class Message123Translator implements Translator<AisMsg, Vessel> {

    // internal attributes
    private final AdaptorConfig config;

    /**
     * The configuration is needed to adjust the behavior at runtime.
     *
     * @param config the adaptor config
     */
    public Message123Translator(AdaptorConfig config) {
        this.config = config;
    }

    /**
     * Main method to translate an AIS message into a CISE Vessel object. Each and every field is
     * translated in the corresponding vessel field respecting corner cases, special encoding and
     * different base scale of the data.
     *
     * @param message the AIS message
     * @return a translated CISE vessel
     */
    @Override
    public Vessel translate(AisMsg message) {

        // casting float to double
        Long mmsi = Long.valueOf(message.getUserId());

        Vessel vessel = new Vessel();

        // This is needed because the AIS doesn't have an IMO number specified
        // but only the MMSI and the light client we built
        if (config.isDemoEnvironment())
            vessel.setIMONumber(mmsi);

        vessel.setMMSI(mmsi);
        vessel.getLocationRels().add(getLocationRel(latitude(message),
            longitude(message),
            fromPositionAccuracy(message),
            fromCourseOverGround(message.getCOG()),
            fromTrueHeading(message.getTrueHeading()),
            message.getTimestamp(),
            fromSpeedOverGround(message.getSOG())));
        vessel.setNavigationalStatus(
            fromNavigationStatus(message.getNavigationStatus()));

        return vessel;
    }

    // PRIVATE HELPERS /////////////////////////////////////////////////////////
    private LocationQualitativeAccuracyType fromPositionAccuracy(AisMsg aisMsg) {
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

    private XMLGregorianCalendar toXMLCalendar(int year, int month, int day, int hours, int minutes,
        int seconds) {
        try {
            return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(year, month, day, hours, minutes, seconds, 0, 0);
        } catch (DatatypeConfigurationException e) {
            throw new AdaptorException(
                "Can't create a correct XMLGregorianCalendar DATE/TIME out of the instant ", e);
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

    private String longitude(AisMsg aisMsg) {
        return Float.toString(aisMsg.getLongitude());
    }

    private String latitude(AisMsg aisMsg) {
        return Float.toString(aisMsg.getLatitude());
    }

    private Location toLocation(String latitude, String longitude,
        LocationQualitativeAccuracyType lqat) {
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
        return cog == 360.0F ? null : f2d(cog);
    }

    private Double fromSpeedOverGround(Float sog) {
        return sog == 102.3F ? null : f2d(sog);
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
