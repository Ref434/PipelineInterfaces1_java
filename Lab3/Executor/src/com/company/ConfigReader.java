package com.company;



public class ConfigReader implements IGrammar{
    private final static String separator = ":";

    public enum ConfigElements {
        SIZE("buffer_size");

        private final String title;

        ConfigElements(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

    }

    public boolean isGrammarKey(String s)
    {
        for (ConfigElements item : ConfigElements.values()) {
            if (s.equalsIgnoreCase(item.getTitle()))
            {
                return true;
            }

        }
        return false;
    }

    public int numElements()
    {
        return ConfigElements.values().length;
    }
    public String getSeparator()
    {
        return separator;
    }
}
