package com.company;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.java_polytech.pipeline_interfaces.RC;
public class Main {

    public static void main(String[] argv) {
        RC error;
        Logger logger = Logger.getLogger("lab3");
        //Проверка аргументов командной строки
        if (argv.length != 1 || argv[0] == null)
        {
            logger.log(Level.SEVERE, Log.LogItems.INVALID_ARGUMENT.getTitle());
            error= RC.RC_MANAGER_INVALID_ARGUMENT;
            System.out.println(error.info);
            return;
        }

        Manager manager = new Manager();

        error = manager.setConfig(argv[0]);
        if(!error.isSuccess())
        {
            System.out.println(error.info);
            return;
        }
        error = manager.makePipeLine();
        if(!error.isSuccess())
        {
            System.out.println(error.info);
            return;
        }
        error = manager.run();
        if(!error.isSuccess())
        {
            System.out.println(error.info);
            return;
        }
        System.out.println("END");
    }

}