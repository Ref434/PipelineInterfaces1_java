package com.company;

import com.java_polytech.pipeline_interfaces.RC;
public class Main {

    public static void main(String[] argv) {
        RC error;
        //Проверка аргументов командной строки
        if (argv.length != 1 || argv[0] == null)
        {
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
    }

}