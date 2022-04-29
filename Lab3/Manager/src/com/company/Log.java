package com.company;

public class Log {
    public enum LogItems
    {
        INVALID_ARGUMENT("invalid command line arguments"),
        CONFIG_PARSING_ERROR("error in parsing the config"),
        READING_FAILS("reading fails"),
        CONFIG_SEMANTIC_ERROR("syntax error when parsing the config"),
        INPUT_READING_ERROR("input reading fails"),
        OUTPUT_WRITING_ERROR("output writing fails");

        private String title;

        LogItems(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
