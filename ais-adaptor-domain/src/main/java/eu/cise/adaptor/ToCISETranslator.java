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
 *
 */
public class ToCISETranslator {
    public Optional<Push> translate(AISMsg aisMsg) {
        if (isTypeSupported(aisMsg)) {
            return Optional.empty();
        }

        return Optional.of(newPush()
                .addEntity(toVessel(latitude(aisMsg), longitude(aisMsg)))
                .build());
    }

    private Vessel toVessel(String latitude, String longitude) {
        Vessel vessel = new Vessel();
        vessel.getLocationRels().add(getLocationRel(latitude, longitude));
        return vessel;
    }

    private Objet.LocationRel getLocationRel(String latitude, String longitude) {
        Objet.LocationRel locationRel = new Objet.LocationRel();
        locationRel.setLocation(toLocation(latitude, longitude));
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
}
