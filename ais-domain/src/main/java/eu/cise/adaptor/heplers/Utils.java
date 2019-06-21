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

package eu.cise.adaptor.heplers;

import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utils {

    public static XMLGregorianCalendar xmlDate(Instant instant) {
        ZonedDateTime zdt = instant.atZone(ZoneId.of("UTC"));
        return xmlDate(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth());
    }

    public static XMLGregorianCalendar xmlDate(int year, int month, int day) {
        try {
            return DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(year, month, day,
                                             0, 0, 0, 0, 0);
        } catch (DatatypeConfigurationException e) {
            throw new AdaptorException(e);
        }
    }

    public static XMLGregorianCalendar xmlTime(Instant instant) {
        ZonedDateTime zdt = instant.atZone(ZoneId.of("UTC"));
        return xmlTime(zdt.getHour(), zdt.getMinute(), zdt.getSecond());
    }

    public static XMLGregorianCalendar xmlTime(int hour, int minute, int second) {
        try {
            return DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(1970, 1, 1,
                                             hour, minute, second, 0, 0);
        } catch (DatatypeConfigurationException e) {
            throw new AdaptorException(e);
        }
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
