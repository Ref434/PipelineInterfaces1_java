package com.company;

import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IReader;
import com.java_polytech.pipeline_interfaces.RC;
import java.io.*;
import java.util.Arrays;


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
        RC rc;
        if (s.length() == 0) {
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        configName = s;
        File file = new File(configName);
        String[] elements = new String[ConfigReader.max_elem];

        rc = ConfigReader.MyConfigReader(file, elements);
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
            while (true)
            {
                byte[] buffer = new byte[bufSize];
                byteCount = fis.read(buffer, 0, bufSize);

                if (byteCount == -1)
                    return RC.RC_SUCCESS;

                if (byteCount != bufSize)
                    buffer = Arrays.copyOfRange(buffer, 0, byteCount);


                if ((rc = consumer.consume(buffer)) != RC.RC_SUCCESS)
                {
                    return rc;
                }

            }
        }
        catch (IOException e)
        {
            return RC.RC_READER_FAILED_TO_READ;
        }
    }





    @Override
    public RC setConsumer(IConsumer var1)
    {
        consumer = var1;
        return RC.RC_SUCCESS;
    }

    private RC SemanticParser(String[] elements)
    {

        for (String element : elements)
        {
            if (element == elements[ConfigReader.ConfigElements.SIZE.ordinal()])
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

