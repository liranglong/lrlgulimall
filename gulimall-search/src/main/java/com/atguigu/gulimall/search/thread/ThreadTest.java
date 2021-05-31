package com.atguigu.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
    public static ExecutorService excutor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start ....");

        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("任务1 start..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务1 end..");
            return 1+1;
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            System.out.println("任务2 start..");
            System.out.println("任务2 end..");
            return "hello";
        });

        // 两个任务都完成才执行任务3
//        CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (result1, result2) -> {
//            return "任务3 ：组合前两个任务的返回值返回 --" + result1 + "---" + result2;
//        }, excutor);

        // 任一任务完成就可以执行任务3【返回值是future1的泛型】
        CompletableFuture<String> future3 = future1.applyToEitherAsync(future2, (result) -> {
            return "任务3 ：组合先执行完的任务的结果 --" + result;
        }, excutor);
        System.out.println("main end.... 返回值：" + future3.get());
    }

    public static void testCompletableFuture(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start ....");
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(ThreadTest::task, excutor)
                .whenComplete(ThreadTest::accept)
                .exceptionally(ThreadTest::apply)
                .handle(ThreadTest::handle)
                .thenApply(ThreadTest::thenApply);
        System.out.println("main end.... 返回值：" + future.get());
    }

    public static int thenApply(Integer result) {
        System.out.println("任务2启动   ： 1 + 1 = ");
        System.out.println("任务2获取任务1的结果：" + result);
        return 2;
    }

    public static int handle(Integer result, Throwable exception) {
        System.out.println("获取任务1的结果：" + result);
        System.out.println("获取任务1的异常：" + exception);
        System.out.println("异常不会传播，前面调用exceptionally方法处理了异常");
        return 200;
    }

    public static int apply(Throwable exception) {
        System.out.println("获取任务1的异常，并提供一个默认返回值" + exception);
        return 100;
    }


    public static void accept(Integer result, Throwable exception) {
        System.out.println("获取任务1的结果：" + result);
        System.out.println("获取任务1的异常：" + exception);
    }

    public static int task() {
        System.out.println("任务1启动   ： 10 / 0");
        return 10 / 0;
    }

    public static void testExcutor(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start..........");
        FutureTask<String> futureTask = new FutureTask<>(new Callable01());
        new Thread(futureTask).start();
        // 阻塞等待
        System.out.println(futureTask.get());
        System.out.println("main end..........");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(  5,
                 200,
                 10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(  100000),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy());
    }

    public static class Callable01 implements Callable {
        @Override
        public String call() throws Exception {
            System.out.println("start call ()");
            return "call方法执行完毕";
        }
    }
}
