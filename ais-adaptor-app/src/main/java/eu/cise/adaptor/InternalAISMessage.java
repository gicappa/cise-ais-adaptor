package eu.cise.adaptor;

public class InternalAISMessage {
    private int messageType;
    private float latitude;

    public int getMessageType() {
        return messageType;
    }

    public float getLatitude() {
        return latitude;
    }

    public static class Builder extends InternalAISMessage {
        public Builder(int messageType) {
            super.messageType = messageType;
        }

        public Builder withLatitude(float l) {
            super.latitude = l;
            return this;
        }

    }

}
