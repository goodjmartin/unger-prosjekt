package no.goodtech.admin.tabs.report;

public class SelectionEntry {

    private final String key;
    private final String value;

    public SelectionEntry(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
