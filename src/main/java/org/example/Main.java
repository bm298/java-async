package org.example;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    private static BigInteger calculateFactorial(BigInteger num) {
        BigInteger result = BigInteger.ONE;
        for (BigInteger i = BigInteger.ONE; i.compareTo(num) <= 0; i = i.add(BigInteger.ONE)) {
            result = result.multiply(i);
        }
        return result;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Random random= new Random();
        int num1= random.nextInt(1,10);
        int num2= random.nextInt(1,10);


        System.out.println("Main thread started...");

        CompletableFuture<String> future=  CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(num1);
                TimeUnit.SECONDS.sleep(num1);
                return "hello";
            } catch (InterruptedException e)  {
                throw new RuntimeException(e);
            }
        }
        );

        CompletableFuture<String> future2=  CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(num2);
                TimeUnit.SECONDS.sleep(num2);
                return "world";
            } catch (InterruptedException e)  {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> combinedFuture = future.thenCombineAsync(future2, String::concat);
        combinedFuture.join(); //Wait to complete Line 32 before moving on with main thread
        System.out.println(combinedFuture.get());

        CompletableFuture<Object> completeableFuture = future.thenCombine(future2, (s1, s2) -> {
            try{

                if (num1 + num2 <= 10){
                    return num1+num2;
                }

                else{
                    throw new RuntimeException();
                }

            } catch (RuntimeException e ){
                System.out.println("Error thread delay is > 10 seconods");
            }
            return "";
        });

        System.out.println(completeableFuture.get());

//        q5
        String data = "85671 34262 92143 50984 24515 68356 77247 12348 56789 98760";
        List<BigInteger> numbers = Arrays.stream(data.split("\\s+"))
                .map(BigInteger::new)
                .collect(Collectors.toList());

        // Create CompletableFuture to perform factorial transformation for each number
        CompletableFuture<List<BigInteger>> factorialFuture = CompletableFuture.supplyAsync(() -> numbers)
                .thenApplyAsync(nums -> nums.stream()
                        .map(Main::calculateFactorial)
                        .collect(Collectors.toList()));

        String story = "Mary had a little lamb, its fleece was white as snow.";


        CompletableFuture<Void> printWordsFuture = CompletableFuture.runAsync(() -> {
            String[] words = story.split("\\s+");
            for (String word : words) {
                System.out.println(word);
                try {
                    TimeUnit.SECONDS.sleep(1); // Wait for one second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Wait for both tasks to complete
        CompletableFuture.allOf(factorialFuture, printWordsFuture).join();

        // Print the resulting list of factorials
        factorialFuture.thenAcceptAsync(System.out::println)
                .join();

        System.out.println("Main thread complete...");



    }

}