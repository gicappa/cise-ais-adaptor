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

package eu.cise.adaptor.translate.utils;

/**
 * The NavigationStatus is an enum describing the navigation status parameter
 * in an AIS message.
 * <br>
 * The number associated in the navigation status is the one expressed in the
 * AIS encoding itself.
 */
@SuppressWarnings("unused")
public enum NavigationStatus {

    UnderwayUsingEngine(0),
    AtAnchor(1),
    NotUnderCommand(2),
    RestrictedManoeuverability(3),
    ConstrainedByHerDraught(4),
    Moored(5),
    Aground(6),
    EngagedInFising(7),
    UnderwaySailing(8),
    ReservedForFutureUse9(9),
    ReservedForFutureUse10(10),
    PowerDrivenVesselTowingAstern(11),
    PowerDrivenVesselPushingAheadOrTowingAlongside(12),
    ReservedForFutureUse13(13),
    SartMobOrEpirb(14),
    Undefined(15);

    private final Integer code;

    /**
     * The code is the numeric code of the navigation status as it's encoded in
     * the AIS message encoding.
     *
     * @param code the encoded status number
     */
    NavigationStatus(Integer code) {
        this.code = code;
    }

    /**
     * Parse an integer as a navigation status code and returns the
     * corresponding enum value.
     *
     * @param integer the code to be interpreted.
     * @return the navigation status enum item or null in case the code does
     * not correspond to any item.
     */
    public static NavigationStatus fromInteger(Integer integer) {
        if (integer != null) {
            for (NavigationStatus b : NavigationStatus.values()) {
                if (integer.equals(b.code)) {
                    return b;
                }
            }
        }
        return null;
    }

    /**
     * @return the AIS navigation status code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return teh navigation status description
     */
    public String getValue() {
        return toString();
    }
}
