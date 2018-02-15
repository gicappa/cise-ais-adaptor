package eu.cise.adaptor;

public class InternalAISMessage {
    private int messageType;
    private float latitude;
    private float longitude;
    private int mmsi;

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
    }

}
