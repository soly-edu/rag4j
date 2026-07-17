package soly.dev.rag.constants;

public enum SplitterStrategy {
    STATIC_RULE("static_rule"),
    SLIDING_WINDOW("sliding_window"),
    SEMANTIC("semantic"),
    TOKEN_WINDOW("token_window"),
    ;
    private final String name;

    SplitterStrategy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
