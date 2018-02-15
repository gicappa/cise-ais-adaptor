package eu.cise.adaptor;

import java.time.Instant;

public class AISMsg {
    private int messageType;
    private float latitude;
    private float longitude;
    private int mmsi;
    private float cog;
    private int trueHeading;
    private Instant timestamp;
    private float sog;
    private Object navigationStatus;

    private AISMsg(Builder builder) {
        messageType = builder.messageType;
        latitude = builder.latitude;
        longitude = builder.longitude;
        mmsi = builder.mmsi;
        cog = builder.cog;
        trueHeading = builder.trueHeading;
        timestamp = builder.timestamp;
        sog = builder.sog;
        navigationStatus = builder.navigationStatus;
    }

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

    public static class Builder {
        private int messageType;
        private float latitude;
        private float longitude;
        private int mmsi;
        private float cog;
        private int trueHeading;
        private Instant timestamp;
        private float sog;
        private Object navigationStatus;

        public Builder(int messageType) {
            this.messageType = messageType;
        }

        public Builder withLatitude(float l) {
            this.latitude = l;
            return this;
        }

        public Builder withLongitude(float l) {
            this.longitude = l;
            return this;
        }

        public Builder withMMSI(int m) {
            this.mmsi = m;
            return this;
        }

        public Builder withCOG(float c) {
            this.cog = c;
            return this;
        }

        public Builder withTrueHeading(int t) {
            this.trueHeading = t;
            return this;
        }

        public Builder withTimestamp(Instant received) {
            this.timestamp = received;
            return this;
        }

        public Builder withSOG(float s) {
            this.sog = s;
            return this;
        }

        public Builder withNavigationStatus(NavigationStatus n) {
            this.navigationStatus = n;
            return this;
        }

        public AISMsg build() {
            return new AISMsg(this);
        }
    }

}
