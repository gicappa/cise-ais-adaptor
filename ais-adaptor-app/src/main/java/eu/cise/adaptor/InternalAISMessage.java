package eu.cise.adaptor;

public class InternalAISMessage {
    private int messageType;

    public int getMessageType() {
        return messageType;
    }

    public static class Builder extends InternalAISMessage {
        public Builder(int messageType) {
            super.messageType = messageType;
        }
    }
}
