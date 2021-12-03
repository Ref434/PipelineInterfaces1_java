package com.company;

import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IExecutor;
import com.java_polytech.pipeline_interfaces.RC;
import java.io.*;



public class Executor implements IExecutor {
    private int bufSize;
    private String configName;
    private IConsumer consumer;
    private byte[] result;


    @Override
    public RC setConsumer(IConsumer var1)
    {
        consumer = var1;
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

        return SemanticParser(elements);
    }

    @Override
    public RC consume(byte[] var1) {
        RC rc;
        int byteSize = var1.length;
        result = new byte[byteSize];
        if(var1==null)
            return RC.RC_EXECUTOR_CONFIG_FILE_ERROR;
        result=byteInverting(var1,byteSize);
        if ((rc = consumer.consume(result)) != RC.RC_SUCCESS)
        {
            return rc;
        }
        return RC.RC_SUCCESS;
    }

    private byte[] byteInverting(byte[] buffer,int byteSize)
    {
        for (int i = 0; i < byteSize; i++) {
            result[i] = (byte)(~buffer[i]);
        }
        return result;
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

