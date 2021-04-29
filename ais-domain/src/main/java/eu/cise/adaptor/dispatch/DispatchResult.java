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

package eu.cise.adaptor.dispatch;

import java.io.Serializable;

/**
 * Dispatching a message will produce a result (success or failure) with a
 * related message.
 * <br>
 * This class is mostly an immutable  value object that holds the resulting
 * status.
 */
@SuppressWarnings("unused")
public class DispatchResult implements Serializable {

    // life, the universe and the everything.
    private static final long serialVersionUID = 42L;

    // internal state
    private final boolean ok;
    private final String result;

    /**
     * The constructor allow to create an immutable object that will contain
     * the status (success or failure) using a boolean and a string containing
     * a message
     *
     * @param ok     a boolean indicating if it's successful or not
     * @param result a result message describing the status
     */
    public DispatchResult(boolean ok, String result) {
        this.ok = ok;
        this.result = result;
    }

    /**
     * Helper method to create a default success result
     *
     * @return a new instance of successful DispatchResult
     */
    public static DispatchResult success() {
        return new DispatchResult(true, "SUCCESS");
    }

    /**
     * Helper method to create a default failure result
     *
     * @return a new instance of failed DispatchResult
     */
    public static DispatchResult failure() {
        return new DispatchResult(false, "FAILURE");
    }

    /**
     * @return the status of the result: true is successful while false is
     * failed
     */
    public boolean isOK() {
        return ok;
    }

    /**
     * @return an additional message of the results that in the failure could
     * contain a description of the error
     */
    public String getResult() {
        return result;
    }

    // To compare value objects we need equals and hashcode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DispatchResult that = (DispatchResult) o;

        if (ok != that.ok) return false;
        return result != null ? result.equals(that.result) : that.result == null;
    }

    @Override
    public int hashCode() {
        int result1 = (ok ? 1 : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "DispatchResult{" +
                "ok=" + ok +
                ", result='" + result + '\'' +
                '}';
    }

}
