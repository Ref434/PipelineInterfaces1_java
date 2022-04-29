package com.company;
import com.java_polytech.pipeline_interfaces.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class Manager {
    private final Map<String, Object> configParams;
    private static final String list_delimiter = "-";
    private String inputFileName;
    private String outputFileName;
    private RC rc = RC.RC_SUCCESS;
    private IConfigurable[] pipeLine;



    Manager()
    {
        configParams = new HashMap<>();
    }

    public RC setConfig(String cfgName) {

        File file = new File(cfgName);
        Map <String, String > elements=new HashMap<>();
        rc = ManagerReader.MyConfigReader(file, elements);
        if (!rc.isSuccess())
            return rc;
        return SemanticParser(elements);
    }

    public RC run()
    {
        try(FileInputStream fis = new FileInputStream(inputFileName))
        {
            try(FileOutputStream fos = new FileOutputStream(outputFileName)) {

                ((IWriter) pipeLine[pipeLine.length - 1]).setOutputStream(fos);
                ((IReader) pipeLine[0]).setInputStream(fis);

                rc = ((IReader)pipeLine[0]).run();

                if(!rc.isSuccess())
                    return rc;


                fos.close();
                fis.close();

            }
            catch (Exception e)
            {
                System.out.println(e);
                return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
            }

        }
        catch (Exception e)
        {
            return RC.RC_MANAGER_INVALID_INPUT_FILE;
        }
        return RC.RC_SUCCESS;
    }




    public RC makePipeLine()
    {
        String[] line = (String[]) configParams.get(ManagerReader.ConfigElements.ORDER.getTitle());
        pipeLine = new IConfigurable[line.length];
        IConfigurable worker;

        for (int i = 0; i < pipeLine.length; i++)
        {
            if ((worker = createWorker(line[i])) != null)
                pipeLine[i] = worker;
            else
            {
                return RC.RC_MANAGER_INVALID_ARGUMENT;
            }
        }

        rc = initPipeLine();
        if (!rc.isSuccess())
        {
            return rc;
        }
        return RC.RC_SUCCESS;
    }



   private IConfigurable createWorker(String className)
   {
       try
       {
           Class<?> clazz = Class.forName(className);
           Constructor<?> constructor = clazz.getDeclaredConstructor();
           return (IConfigurable)constructor.newInstance();
       }
       catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
       {
           return null;
       }
   }

    private RC initPipeLine()
    {

        rc = setWorkerConfigs();
        if (!rc.isSuccess())
            return rc;

        rc = setReaderParams();
        if (!rc.isSuccess())
            return rc;

        rc = setExecutorsParams();
        if (!rc.isSuccess())
            return rc;



        return RC.RC_SUCCESS;
    }

    private RC setWorkerConfigs()
    {
        String readerConfigName = (String)configParams.get(ManagerReader.ConfigElements.READER_CONFIG.getTitle());
        if (readerConfigName.length() == 0)
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }

        rc=pipeLine[0].setConfig(readerConfigName);
        if(!rc.isSuccess())
            return rc;

        String writerConfigName = (String)configParams.get(ManagerReader.ConfigElements.WRITER_CONFIG.getTitle());
        if (writerConfigName.length() == 0)
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        rc=pipeLine[pipeLine.length - 1].setConfig(writerConfigName);
        if(!rc.isSuccess())
            return rc;

        String[] executorsConfigNames = (String[])configParams.get(ManagerReader.ConfigElements.EXECUTORS_CONFIGS.getTitle());
        if (executorsConfigNames.length != pipeLine.length - 2)
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }

        for (int i = 1; i < pipeLine.length - 1; i++)
        {
            pipeLine[i].setConfig(executorsConfigNames[i-1]);
        }

        return RC.RC_SUCCESS;
    }
    private RC setReaderParams()
    {
        IReader reader= (IReader)pipeLine[0];
        rc= reader.setConsumer((IConsumer)pipeLine[1]);
        return rc;

    }

    private RC setExecutorsParams()
    {
        for (int i = 1; i < pipeLine.length - 1; i++)
        {
           rc= ((IExecutor)pipeLine[i]).setConsumer((IConsumer)pipeLine[i+1]);
           if(!rc.isSuccess())
               return rc;
        }

        return rc;
    }

    private boolean isListStringItem(String key)
    {
        return ManagerReader.ConfigElements.EXECUTORS_CONFIGS.getTitle().equals(key) ||
                ManagerReader.ConfigElements.EXECUTORS_NAME.getTitle().equals(key) ||
                ManagerReader.ConfigElements.ORDER.getTitle().equals(key);
    }

    private RC OrderParser()
    {
        int i;
        boolean isFind = false;
        String[] Order = (String[])configParams.get(ManagerReader.ConfigElements.ORDER.getTitle());

        if (!configParams.get(ManagerReader.ConfigElements.READER_NAME.getTitle()).equals(Order[0]) ||
                !configParams.get(ManagerReader.ConfigElements.WRITER_NAME.getTitle()).equals(Order[Order.length - 1]))
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }


        String[] exNames = (String[])configParams.get(ManagerReader.ConfigElements.EXECUTORS_NAME.getTitle());

        if (exNames.length == 0)
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }

        for (i = 1; i < Order.length - 1; i++)
        {
            for (String ex : exNames)
            {
                if (Order[i].equals(ex))
                {
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
            {
                return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
            }
            isFind = false;
        }
        return RC.RC_SUCCESS;
    }


    private RC SemanticParser(Map <String, String > elements)
    {


        for (Map.Entry<String, String> element : elements.entrySet())
        {
            if (isListStringItem(element.getKey()))
            {
                configParams.put(element.getKey(), element.getValue().split(list_delimiter));
            }
            else
            {
                configParams.put(element.getKey(), element.getValue());
            }
        }


        inputFileName = (String)configParams.get(ManagerReader.ConfigElements.INPUT.getTitle());
        if (inputFileName.length() == 0)
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }


        outputFileName = (String)configParams.get(ManagerReader.ConfigElements.OUTPUT.getTitle());
        if (outputFileName.length() == 0)
        {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }

        rc=OrderParser();

        return rc;
    }

}
