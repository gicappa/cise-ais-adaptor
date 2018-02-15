package eu.cise.adaptor;

import java.time.Instant;

public class InternalAISMessage {
    private int messageType;
    private float latitude;
    private float longitude;
    private int mmsi;
    private float cog;
    private int trueHeading;
    private Instant timestamp;
    private float sog;
    private Object navigationStatus;

    public int getMessageType() {
        return messageType;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getMMSI() {
        return mmsi;
    }

    public float getCOG() {
        return cog;
    }

    public int getTrueHeading() {
        return trueHeading;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public float getSOG() {
        return sog;
    }

    public Object getNavigationStatus() {
        return navigationStatus;
    }

    public static class Builder extends InternalAISMessage {
        public Builder(int messageType) {
            super.messageType = messageType;
        }

        public Builder withLatitude(float l) {
            super.latitude = l;
            return this;
        }

        public Builder withLongitude(float l) {
            super.longitude = l;
            return this;
        }

        public Builder withMMSI(int m) {
            super.mmsi = m;
            return this;
        }

        public Builder withCOG(float c) {
            super.cog = c;
            return this;
        }

        public Builder withTrueHeading(int t) {
            super.trueHeading = t;
            return this;
        }

        public Builder withTimestamp(Instant received) {
            super.timestamp = received;
            return this;
        }

        public Builder withSOG(float s) {
            super.sog = s;
            return this;
        }

        public Builder withNavigationStatus(NavigationStatus n) {
            super.navigationStatus = n;
            return this;
        }
    }

}
