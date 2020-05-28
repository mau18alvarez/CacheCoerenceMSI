package app;

public class main{

    public static void main(String[] args) throws Exception {
        chip cpu1 = new chip(1);
        chip cpu2 = new chip(2);
        
        cpu1.start();
        cpu2.start();
    }
}
