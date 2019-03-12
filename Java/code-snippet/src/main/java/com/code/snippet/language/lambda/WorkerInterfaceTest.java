package com.code.snippet.language.lambda;

/**
 * @description: Test for worker interface.
 * @author: NiYanchun
 * @version: 1.0
 * @create: 2019-03-11 23:01
 **/
public class WorkerInterfaceTest {

    public static void main(String[] args) {
        // invoke by anonymous class
        execute(new WorkerInterface() {
            @Override
            public void doSomeWork() {
                System.out.println("invoke by anonymous class");
            }
        });

        // invoke by lambda
        execute(() -> System.out.println("invoke by lambda"));
    }

    private static void execute(WorkerInterface worker) {
        worker.doSomeWork();
    }
}
