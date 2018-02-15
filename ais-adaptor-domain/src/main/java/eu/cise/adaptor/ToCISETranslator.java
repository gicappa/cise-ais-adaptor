package eu.cise.adaptor;

import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;

import java.util.Optional;

import static eu.eucise.helpers.PushBuilder.newPush;

/**
 * This is the translator from the internal AISMsg object to a CISE Push message
 * <p>
 * TODO There is a difference in latitude and longitude between the AIS and the
 * CISE calculation. Here for simplicity it hasn't been taken into account.
 * <p>
 * Please refer to:
 * https://webgate.ec.europa.eu/CITnet/confluence/display/MAREX/AIS+Message+1%2C2%2C3
 */
public class ToCISETranslator {
    public Optional<Push> translate(AISMsg aisMsg) {
        if (isTypeSupported(aisMsg)) {
            return Optional.empty();
        }

        return Optional.of(newPush()
                .addEntity(toVessel(
                        latitude(aisMsg),
                        longitude(aisMsg),
                        f2d(aisMsg.getCOG()), // casting float to double
                        fromTrueHeading(aisMsg.getTrueHeading()))
                )

                .build());
    }

    private Vessel toVessel(String latitude, String longitude, Double cog, Double heading) {
        Vessel vessel = new Vessel();
        vessel.getLocationRels().add(getLocationRel(latitude, longitude, cog, heading));
        return vessel;
    }

    private Objet.LocationRel getLocationRel(String latitude,
                                             String longitude,
                                             Double cog,
                                             Double heading) {
        Objet.LocationRel locationRel = new Objet.LocationRel();
        locationRel.setLocation(toLocation(latitude, longitude));
        locationRel.setCOG(cog);
        locationRel.setHeading(heading);

        return locationRel;
    }

    private String longitude(AISMsg aisMsg) {
        return Float.toString(aisMsg.getLongitude());
    }

    private String latitude(AISMsg aisMsg) {
        return Float.toString(aisMsg.getLatitude());
    }

    private Location toLocation(String latitude, String longitude) {
        Location location = new Location();
        Geometry geometry = new Geometry();
        geometry.setLatitude(latitude);
        geometry.setLongitude(longitude);
        location.getGeometries().add(geometry);
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

    private Double fromTrueHeading(int th) {
        if (th == 511)
            return null;
        else
            return Double.valueOf(th);
    }

}

