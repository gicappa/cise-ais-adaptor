package eu.cise.adaptor;

import eu.cise.adaptor.normalize.NavigationStatus;

import java.time.Instant;

public class AISMsg {


    // POSITION
    private int messageType;
    private float latitude;
    private float longitude;
    private int userId;
    private float cog;
    private int trueHeading;
    private Instant timestamp;
    private float sog;
    private NavigationStatus navigationStatus;
    private int positionAccuracy;

    // VOYAGE
    private String destination;
    private Instant eta;
    private final Integer imoNumber;
    private String callsign;

    private AISMsg(AISMsg.Builder builder) {
        messageType = builder.messageType;
        latitude = builder.latitude;
        longitude = builder.longitude;
        positionAccuracy = builder.positionAccuracy;
        userId = builder.userId;
        cog = builder.cog;
        trueHeading = builder.trueHeading;
        timestamp = builder.timestamp;
        sog = builder.sog;
        navigationStatus = builder.navigationStatus;

        destination = builder.destination;
        eta = builder.eta;
        imoNumber = builder.imoNumber;
        callsign = builder.callsign;
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

    public int getUserId() {
        return userId;
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

    public NavigationStatus getNavigationStatus() {
        return navigationStatus;
    }

    public Integer getPositionAccuracy() {
        return positionAccuracy;
    }

    public String getDestination() {
        return destination;
    }

    public Instant getETA() {
        return eta;
    }

    public Integer getIMONumber() {
        return imoNumber;
    }

    public String getCallSign() {
        return callsign;
    }

    public static class Builder {
        private int messageType;

        // POSITION
        private float latitude;
        private float longitude;
        private int positionAccuracy;
        private int userId;
        private float cog;
        private int trueHeading;
        private Instant timestamp = Instant.MIN;
        private float sog;
        private NavigationStatus navigationStatus;

        // VOYAGE
        private String destination;
        private Instant eta;
        private Integer imoNumber;
        private String callsign;

        public Builder(int messageType) {
            this.messageType = messageType;
        }

        public AISMsg.Builder withLatitude(float l) {
            this.latitude = l;
            return this;
        }

        public AISMsg.Builder withLongitude(float l) {
            this.longitude = l;
            return this;
        }

        public AISMsg.Builder withPositionAccuracy(int la) {
            this.positionAccuracy = la;
            return this;
        }

        public AISMsg.Builder withUserId(int m) {
            this.userId = m;
            return this;
        }

        public AISMsg.Builder withCOG(float c) {
            this.cog = c;
            return this;
        }

        public AISMsg.Builder withTrueHeading(int t) {
            this.trueHeading = t;
            return this;
        }

        public AISMsg.Builder withTimestamp(Instant received) {
            this.timestamp = received;
            return this;
        }

        public AISMsg.Builder withSOG(float s) {
            this.sog = s;
            return this;
        }

        public AISMsg.Builder withNavigationStatus(NavigationStatus n) {
            this.navigationStatus = n;
            return this;
        }

        // VOYAGE
        public Builder withDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder withETA(Instant eta) {
            this.eta = eta;
            return this;
        }

        public Builder withIMONumber(Integer imoNumber) {
            this.imoNumber = imoNumber;
            return null;
        }

        public AISMsg build() {
            return new AISMsg(this);
        }

        public Builder withCallSign(String callsign) {
            this.callsign = callsign;
            return this;
        }
    }

}
