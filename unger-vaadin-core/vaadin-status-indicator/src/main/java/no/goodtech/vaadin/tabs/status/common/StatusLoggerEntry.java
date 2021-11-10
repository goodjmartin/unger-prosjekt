package no.goodtech.vaadin.tabs.status.common;

public class StatusLoggerEntry {

    private final String id;
    private final String name;

    public StatusLoggerEntry(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
