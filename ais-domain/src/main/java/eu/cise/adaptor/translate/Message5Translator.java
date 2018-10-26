/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
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

import eu.cise.adaptor.AisMsg;
import eu.cise.datamodel.v1.entity.event.Event;
import eu.cise.datamodel.v1.entity.location.PortLocation;
import eu.cise.datamodel.v1.entity.movement.Movement;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.datamodel.v1.entity.vessel.VesselType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static eu.cise.datamodel.v1.entity.movement.MovementType.VOYAGE;

/**
 * This object translate messages of type 5 into CISE Vessel objects.
 */
public class Message5Translator implements Translator<AisMsg, Vessel> {

    // internal attributes
    private static final Set<String> ISO_COUNTRIES
            = new HashSet<>(Arrays.asList(Locale.getISOCountries()));

    public static boolean isValidISOCountry(String s) {
        return ISO_COUNTRIES.contains(s);
    }

    /**
     * Main method to translate an AIS message into a CISE Vessel object.
     * Each and every field is translated in the corresponding vessel field
     * respecting corner cases, special encoding and different base scale
     * of the data.
     *
     * @param message the AIS message of type 5
     * @return a translated CISE vessel
     */
    @Override
    public Vessel translate(AisMsg message) {
        Vessel vessel = new Vessel();

        Long imoNumber = getImoNumber(message);

        // todo check if it should be added the check on the config
        // to override the MMSI
        if (imoNumber != null)
            vessel.setIMONumber(imoNumber);

        vessel.setMMSI(Long.valueOf(message.getUserId()));
        vessel.getInvolvedEventRels().add(getInvolvedEventRel(message));
        vessel.getNames().add(message.getShipName());
        vessel.setBeam(getBeam(message));
        vessel.setLength(getLength(message));
        vessel.setCallSign(message.getCallSign());
        vessel.setDraught(f2d(message.getDraught()));
        vessel.setMMSI(Long.valueOf(message.getUserId()));
        vessel.getShipTypes().add(fromAISShipType(message.getShipType()));

        return vessel;
    }

    // PRIVATE HELPERS /////////////////////////////////////////////////////////

    private Objet.InvolvedEventRel getInvolvedEventRel(AisMsg message) {
        Objet.InvolvedEventRel involvedEventRel = new Objet.InvolvedEventRel();
        involvedEventRel.setEvent(getMovement(message));
        return involvedEventRel;
    }

    private Movement getMovement(AisMsg message) {
        Movement movement = new Movement();
        movement.setMovementType(VOYAGE);
        movement.getLocationRels().add(getLocationRel(message));
        return movement;
    }

    private Event.LocationRel getLocationRel(AisMsg message) {
        Event.LocationRel locationRel = new Event.LocationRel();
        locationRel.setLocation(getPortLocation(message));
        return locationRel;
    }

    private PortLocation getPortLocation(AisMsg message) {
        PortLocation location = new PortLocation();

        String locationCode = message.getDestination();
        if (isLocationCode(locationCode))
            location.setLocationCode(locationCode);

        location.setPortName(locationCode);
        return location;
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

    private Double f2d(Float fValue) {
        return Double.valueOf(fValue.toString());
    }

    private Long getImoNumber(AisMsg aisMsg) {
        return aisMsg.getImoNumber() == null ? null : Long.valueOf(aisMsg.getImoNumber());
    }

    private Double getLength(AisMsg aisMsg) {
        if (aisMsg.getDimensionA() == null || aisMsg.getDimensionB() == null)
            return null;

        return Double.valueOf(aisMsg.getDimensionA() + aisMsg.getDimensionB());
    }

    private Integer getBeam(AisMsg aisMsg) {
        if (aisMsg.getDimensionC() == null || aisMsg.getDimensionD() == null)
            return null;

        return aisMsg.getDimensionC() + aisMsg.getDimensionD();
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

}
