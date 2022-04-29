package com.company;

import com.java_polytech.pipeline_interfaces.RC;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigReader {
    private static String separator = ":";
    private String line;
    public static final int max_elem=1;

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


    private static RC ReadLine(String[] elements,String line) {
        String[] tokens = line.replaceAll(" ", "").split(separator);
        if (tokens.length != 2)
            return RC.RC_READER_CONFIG_GRAMMAR_ERROR;

        for (ConfigElements item : ConfigElements.values()) {
            if (item.getTitle().equals(tokens[0])) {
                elements[item.ordinal()] = tokens[1];
                return RC.RC_SUCCESS;
            }
        }
        return RC.RC_READER_CONFIG_GRAMMAR_ERROR;
    }

    static RC MyConfigReader(File file, String[] elements) {
        String line;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            line = br.readLine();
            while (line != null) {
                if (ReadLine(elements,line) != RC.RC_SUCCESS)
                    return RC.RC_READER_CONFIG_GRAMMAR_ERROR;

                line = br.readLine();
            }
        } catch (IOException ex) {
            return RC.RC_READER_FAILED_TO_READ;
        }
        return RC.RC_SUCCESS;
    }
}
