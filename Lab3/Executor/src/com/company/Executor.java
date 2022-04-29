package com.company;

import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;


public class Executor implements IExecutor {
    private int bufSize;
    private String configName;
    private IConsumer consumer;
    private byte[] result;
    private byte[] buffer;
    private TYPE commonType;
    private IMediator mediator;
    private final static TYPE[] SUPPORTED_TYPES = new TYPE[]{
            TYPE.BYTE_ARRAY,
            TYPE.CHAR_ARRAY,
            TYPE.INT_ARRAY };


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
    public RC setConsumer(IConsumer buffer)
    {
        consumer = buffer;
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
    public TYPE[] getOutputTypes() {
        return Arrays.copyOf(SUPPORTED_TYPES,SUPPORTED_TYPES.length);
    }


    @Override
    public RC setProvider(IProvider provider) {
        commonType = (TypeChanger.getCommonTypes(
                provider.getOutputTypes(),
                SUPPORTED_TYPES)
        );


        if (commonType == null){
            return RC.RC_EXECUTOR_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        mediator = provider.getMediator(commonType);
        return RC.RC_SUCCESS;
    }


    @Override
    public IMediator getMediator(TYPE type)
    {
        switch(type)
        {
            case BYTE_ARRAY:
                return new Executor.byteMediator();
            case INT_ARRAY:
                return new Executor.intMediator();
            case CHAR_ARRAY:
                return new Executor.charMediator();
            default:
                return null;
        }
    }


    public class byteMediator implements IMediator{
        @Override
        public Object getData() {
            if (result==null)
                return null;
            else
                return Arrays.copyOf(result,result.length);
        }
    }

    public class intMediator implements IMediator{
        @Override
        public Object getData() {
            if (result==null)
                return null;
            return TypeChanger.bytesToInts(result,result.length);
        }
    }


    public class charMediator implements IMediator{
        @Override
        public Object getData() {
            if (result==null)
                return null;
            return TypeChanger.bytesToChars(result,result.length);
        }
    }

    @Override
    public RC consume() {
        buffer = convertToByte(mediator.getData());


        if(buffer==null) {
            consumer.consume();
            return RC.RC_SUCCESS;
        }
        RC rc;
        int byteSize = buffer.length;
        result = new byte[byteSize];
        result=byteInverting(buffer,byteSize);
        if ((rc = consumer.consume()) != RC.RC_SUCCESS)
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

