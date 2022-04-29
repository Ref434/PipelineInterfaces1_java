package com.company;

import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IReader;
import com.java_polytech.pipeline_interfaces.RC;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;


public class Reader implements IReader {
    private int bufSize;
    private BufferedInputStream fis;
    private String configName;
    private IConsumer consumer;


    @Override
    public RC setInputStream(InputStream var1) {
        fis = new BufferedInputStream(var1);
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        SyntaxAnalyzer config = new SyntaxAnalyzer(RC.RCWho.READER,new ConfigReader());
        RC rc;
        if (s.length() == 0) {
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        configName = s;
        File file = new File(configName);
        HashMap<String,String> elements = new HashMap<>();
        rc=config.MyConfigReader(file,elements);
        if (!rc.isSuccess())
            return rc;

        rc = SemanticParser(elements);
        if (!rc.isSuccess())
            return rc;
        return RC.RC_SUCCESS;
    }
    @Override
    public RC run() {
        int byteCount;
        RC rc;

        try
        {
            while (fis.available()>0)
            {
                byte[] buffer = new byte[bufSize];
                byteCount = fis.read(buffer, 0, bufSize);


                if (byteCount != bufSize)
                    buffer = Arrays.copyOfRange(buffer, 0, byteCount);


                if ((rc = consumer.consume(buffer)) != RC.RC_SUCCESS)
                {
                    return rc;
                }

            }
            consumer.consume(null);
        }
        catch (IOException e)
        {
            return RC.RC_READER_FAILED_TO_READ;
        }
        return RC.RC_SUCCESS;
    }





    @Override
    public RC setConsumer(IConsumer var1)
    {
        consumer = var1;
        return RC.RC_SUCCESS;
    }

    private RC SemanticParser(HashMap<String,String> elements)
    {

        for (String element : elements.values())
        {
            if (element == elements.get(ConfigReader.ConfigElements.SIZE.getTitle()))
            {
                try
                {
                    bufSize = Integer.parseInt(element);
                }
                catch (NumberFormatException nfe)
                {
                    return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
                }
            }

        }

        return RC.RC_SUCCESS;
    }
}

