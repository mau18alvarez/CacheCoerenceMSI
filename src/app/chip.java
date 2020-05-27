package app;

public class chip extends Thread{

    private core core0;
    private core core1;
    private Thread t;



    public chip(int chip_id){
        this.core0 = new core(0, chip_id);
        this.core1 = new core(1, chip_id);
        this.core0.start();
        this.core1.start();

    }
}