package com.company;

import com.java_polytech.pipeline_interfaces.RC;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
