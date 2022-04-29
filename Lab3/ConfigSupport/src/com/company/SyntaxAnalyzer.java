package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.java_polytech.pipeline_interfaces.RC;
import java.util.HashMap;



public class SyntaxAnalyzer {

    RC.RCWho who = RC.RCWho.UNKNOWN;
    IGrammar myGrammar;

    SyntaxAnalyzer(RC.RCWho owner,IGrammar grammar)
    {
        who=owner;
        myGrammar = grammar;
    }

    private RC ReadLine(String line,HashMap<String,String> elements) {
        String[] tokens = line.replaceAll("\\s+", "").split(myGrammar.getSeparator());
        if (tokens.length != 2)
            return new RC(who,RC.RCType.CODE_CONFIG_FILE_ERROR,"Couldn't open config file");

        if(myGrammar.isGrammarKey(tokens[0]))
        {
            elements.put(tokens[0],tokens[1]);
            return RC.RC_SUCCESS;
        }
        else
        {
            return new RC(who,RC.RCType.CODE_CONFIG_GRAMMAR_ERROR,"Some grammar error in config");
        }

    }

    public RC MyConfigReader(File file,HashMap<String,String> elements) {
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            line = br.readLine();
            while (line != null) {
                line=line.replaceAll("\\s+", "");
                if(!line.equals("") && !line.contains("//")) {
                    if (ReadLine(line, elements) != RC.RC_SUCCESS)
                        return new RC(who, RC.RCType.CODE_CONFIG_GRAMMAR_ERROR, "Some grammar error in config");
                }
                line = br.readLine();

            }
        } catch (IOException ex) {
            return new RC(who,RC.RCType.CODE_FAILED_TO_READ,"Can not read file");
        }
        return RC.RC_SUCCESS;
    }


}
