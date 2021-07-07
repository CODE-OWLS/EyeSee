import java.util.Scanner;

class ThreadClass implements Runnable {

    @Override 
    public void run(){
        System.out.println("Thread started: "+ Thread.currentThread().getName());
        System.out.println("Thread ended: "+ Thread.currentThread().getName());
    }
}

class Main{
public static void main(String[] args) {

        ThreadClass ex = new ThreadClass();
        Thread t1= new Thread(ex);
        t1.setName("th1");
        t1.start(); 
        try
        {
            t1.join();
        }catch(Exception x){}

        Thread t2= new Thread(ex);
        t2.setName("th2");
        t2.start(); 
        try
        {
            t2.join();
        }catch(Exception x){}

        Thread t3= new Thread(ex);
        t3.setName("th3");
        t3.start();
        try
        {
            t3.join();
        }catch(Exception x){}

        System.out.println("All three threads have finished execution");
    }
}

