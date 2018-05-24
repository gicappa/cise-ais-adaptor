package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.datamodel.v1.entity.Entity;
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

public class Message5Translator implements Translator<AISMsg, Entity> {

    private static final Set<String> ISO_COUNTRIES = new HashSet<>
            (Arrays.asList(Locale.getISOCountries()));
    private final AISAdaptorConfig config;

    public Message5Translator(AISAdaptorConfig config) {
        this.config = config;
    }

    @Override
    public Entity translate(AISMsg message) {
        Vessel vessel = new Vessel();

        Long imoNumber = getImoNumber(message);
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

    private Objet.InvolvedEventRel getInvolvedEventRel(AISMsg message) {
        Objet.InvolvedEventRel involvedEventRel = new Objet.InvolvedEventRel();
        involvedEventRel.setEvent(getMovement(message));
        return involvedEventRel;
    }

    private Movement getMovement(AISMsg message) {
        Movement movement = new Movement();
        movement.setMovementType(VOYAGE);
        movement.getLocationRels().add(getLocationRel(message));
        return movement;
    }

    private Event.LocationRel getLocationRel(AISMsg message) {
        Event.LocationRel locationRel = new Event.LocationRel();
        locationRel.setLocation(getPortLocation(message));
        return locationRel;
    }

    private PortLocation getPortLocation(AISMsg message) {
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

    public static boolean isValidISOCountry(String s) {
        return ISO_COUNTRIES.contains(s);
    }

    private Double f2d(Float fValue) {
        return Double.valueOf(fValue.toString());
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
