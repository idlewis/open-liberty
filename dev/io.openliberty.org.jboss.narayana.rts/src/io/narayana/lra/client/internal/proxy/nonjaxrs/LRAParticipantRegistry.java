package io.narayana.lra.client.internal.proxy.nonjaxrs;

public class LRAParticipantRegistry {

    private final String message;

    public LRAParticipantRegistry(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
