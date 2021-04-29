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

public class EtaDate {

    /**
     * Starting from an ETA in the format "17-09 14:30"
     * it's needed to create a string in ISO format
     * without the year like "09-07" for month and day.
     *
     * @param eta the estimates time of arrival.
     * @return the iso format string
     */
    String getMonthDayISOFormat(String eta) {

        if (monthsOrDaysAreUnavailable(eta))
            return null;

        return getMonth(eta) + "-" + getDay(eta);
    }

    private boolean monthsOrDaysAreUnavailable(String eta) {
        return Integer.valueOf(getDay(eta)).equals(0) ||
                Integer.valueOf(getMonth(eta)).equals(0);
    }

    private String getMonth(String eta) {
        return getMonthDashDay(eta).split("-")[1];
    }

    private String getDay(String eta) {
        return getMonthDashDay(eta).split("-")[0];
    }

    private String getMonthDashDay(String eta) {
        return eta.split(" ")[0];
    }

}
