/*
 * Copyright 2018 JRC
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package eu.cise.adaptor;

import java.io.Serializable;

public class RestResult implements Serializable {

    private static final long serialVersionUID = 42L;

    private final Integer code;
    private final String body;
    private final String message;
    private final boolean ok;

    public RestResult(Integer code, String body, String message) {
        this.code = code;
        this.ok = isOK(code);
        this.body = body;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    public boolean isOK() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public static boolean isOK(final int statusCode) {
        return statusCode / 100 == 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestResult that = (RestResult) o;

        if (ok != that.ok) return false;
        if (code != null ? !code.equals(that.code) : that.code != null)
            return false;
        if (body != null ? !body.equals(that.body) : that.body != null)
            return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (ok ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RestResult{" +
                "code=" + code +
                ", body='" + body + '\'' +
                ", message='" + message + '\'' +
                ", ok=" + ok +
                '}';
    }
}