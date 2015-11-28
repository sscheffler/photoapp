package de.turtle.moving.http;

/**
 * @author Stefan Scheffler
 */
public enum BodyType {
    JSON("application/json", "Accept");

    private final String bodyType;
    private final String headerKey;

    BodyType(final String bodyType, final String headerKey) {
        this.bodyType = bodyType;
        this.headerKey = headerKey;
    }

    public String getBodyType() {
        return bodyType;
    }

    public String getHeaderKey() {
        return headerKey;
    }
}
