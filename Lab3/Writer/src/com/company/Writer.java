package com.company;

import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.util.HashMap;


public class Writer implements IWriter {
    private int bufSize;
    private BufferedOutputStream fos;
    private String configName;
    private TYPE commonType;
    private IMediator mediator;
    private byte[] buffer;

    private final static TYPE[] SUPPORTED_TYPES = new TYPE[]{
            TYPE.BYTE_ARRAY,
            TYPE.CHAR_ARRAY,
            TYPE.INT_ARRAY };


    @Override
    public RC setOutputStream(OutputStream buffer) {
        fos = new BufferedOutputStream(buffer);
        return RC.RC_SUCCESS;
    }

    private byte[] convertToByte(Object data){
        if (data == null)
            return null;
        byte[] res = null;
        if (commonType == TYPE.BYTE_ARRAY)
            res = (byte[])data;
        else if (commonType == TYPE.CHAR_ARRAY)
            res = TypeChanger.charsToBytes((char[]) data, ((char[])data).length);
        else if (commonType == TYPE.INT_ARRAY)
            res = TypeChanger.intsToBytes((int[]) data, ((int[])data).length);
        return res;
    }

    @Override
    public RC setProvider(IProvider provider) {
        commonType = (TypeChanger.getCommonTypes(
                provider.getOutputTypes(),
                SUPPORTED_TYPES)
        );
        if (commonType == null){
            return RC.RC_WRITER_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        mediator = provider.getMediator(commonType);
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

        return SemanticParser(elements);
    }
    @Override
    public RC consume() {
        buffer = convertToByte(mediator.getData());
        if(buffer==null)
            return RC.RC_SUCCESS;
        int byteSize = buffer.length;
        int index = 0, length = 0;


        try
        {
            while(byteSize > 0)
            {
                if (byteSize < bufSize)
                    length = byteSize;
                else
                    length = bufSize;
                fos.write(buffer, index, length);

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


