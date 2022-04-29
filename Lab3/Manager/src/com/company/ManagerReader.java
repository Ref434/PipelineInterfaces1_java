package com.company;
import java.util.Map;
import com.java_polytech.pipeline_interfaces.RC;


import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ManagerReader {
    private static String separator = ":";
    private String line;

    public enum ConfigElements {
        INPUT("INPUT"),
        OUTPUT("OUTPUT"),
        WRITER_NAME("WRITER_NAME"),
        READER_NAME("READER_NAME"),
        EXECUTORS_NAME("EXECUTORS_NAME"),
        WRITER_CONFIG("WRITER_CONFIG"),
        READER_CONFIG("READER_CONFIG"),
        EXECUTORS_CONFIGS("EXECUTORS_CONFIGS"),
        ORDER("ORDER");

        private final String title;

        ConfigElements(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

    }

    private static RC ReadLine(Map<String, String> elements,String line) {
        String[] tokens = line.replaceAll("\\s+", "").split(separator);
        if (tokens.length != 2)
            return RC.RC_READER_CONFIG_GRAMMAR_ERROR;

        for (ConfigElements item : ConfigElements.values()) {
            if (item.getTitle().equals(tokens[0])) {
                elements.put(item.getTitle(),tokens[1]);
                return RC.RC_SUCCESS;
            }
        }
        return RC.RC_READER_CONFIG_GRAMMAR_ERROR;
    }

    static RC MyConfigReader(File file, Map<String, String> elements) {
        String line;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            line = br.readLine();
            while (line != null) {
                line=line.replaceAll("\\s+", "");
                if(line.contains("//"))
                    line = line.substring(0, line.indexOf("//"));
                if(!line.equals("") ) {
                    if (ReadLine(elements, line) != RC.RC_SUCCESS)
                        return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
                }

                line = br.readLine();
            }
        } catch (IOException ex) {
            return RC.RC_MANAGER_CONFIG_FILE_ERROR;
        }
        return RC.RC_SUCCESS;
    }
}
