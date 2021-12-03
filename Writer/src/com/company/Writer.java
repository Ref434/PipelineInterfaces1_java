package com.company;

import com.java_polytech.pipeline_interfaces.IWriter;
import com.java_polytech.pipeline_interfaces.RC;
import java.io.*;



public class Writer implements IWriter {
    private int bufSize;
    private BufferedOutputStream fos;
    private String configName;


    @Override
    public RC setOutputStream(OutputStream var1) {
        fos = new BufferedOutputStream(var1);
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
        int byteSize = var1.length;
        int index = 0, length = 0;

        if(var1==null)
            return RC.RC_WRITER_FAILED_TO_WRITE;

        try
        {
            while(byteSize > 0)
            {
                if (byteSize < bufSize)
                    length = byteSize;
                else
                    length = bufSize;
                fos.write(var1, index, length);

                byteSize -= bufSize;
                index += length;
            }
            fos.flush();
            return RC.RC_SUCCESS;
        }
        catch (IOException e)
        {
            return RC.RC_WRITER_FAILED_TO_WRITE;
        }
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

