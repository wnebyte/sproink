package com.github.wnebyte.editor.project;

import java.util.List;
import java.util.Arrays;

public class Replacement {

    private final List<String> names;

    private final String regex;

    private final String value;

    public Replacement(List<String> names, String regex, String value) {
        this.names = names;
        this.regex = regex;
        this.value = value;
    }

    public List<String> getNames() {
        return names;
    }

    public String getRegex() {
        return regex;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {

        private List<String> names = null;

        private String regex = null;

        private String value = null;

        public Builder setNames(String... names) {
            this.names = Arrays.asList(names);
            return this;
        }

        public Builder setRegex(String regex) {
            this.regex = regex;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Replacement build() {
            return new Replacement(names, regex, value);
        }
    }
}
