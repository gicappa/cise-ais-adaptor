package eu.cise.adaptor.helpers;

import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Utils {

    public static XMLGregorianCalendar xmlDate(int year, int month, int day)
            throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(year, month, day,
                        0, 0, 0, 0, 0);
    }

    public static XMLGregorianCalendar xmlTime(int hour, int minute, int second)
            throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(1970, 1, 1,
                        hour, minute, second, 0, 0);
    }

    public static Objet.LocationRel extractLocationRel(Vessel v) {
        return v.getLocationRels().get(0);
    }

    public static Geometry extractGeometry(Vessel v) {
        return v.getLocationRels().get(0).getLocation().getGeometries().get(0);
    }

    public static Vessel extractVessel(Push translate) {
        return (Vessel) extractPayload(translate).getAnies().get(0);
    }

    public static XmlEntityPayload extractPayload(Push m) {
        return (XmlEntityPayload) m.getPayload();
    }
}
