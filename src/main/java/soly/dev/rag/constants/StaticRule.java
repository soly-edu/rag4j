package soly.dev.rag.constants;

public enum StaticRule {
    REGULAR_PUNCTUATION("(?<=[。！？\\n])", "regular_punctuation"),
    RETURN("\\r", "return"),
    PERIOD("\\。", "period"),
    NEWLINE("\\n", "newline"),
    CUSTOM(null, "custom"),;

    private final String pattern;
    private final String name;

    StaticRule(String pattern, String name) {
        this.pattern = pattern;
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }
}
