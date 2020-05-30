package app;

public class chip extends Thread{

    private core core0;
    private core core1;


    public chip(int chip_id){
        this.core0 = new core(0, chip_id);
        //this.core1 = new core(1, chip_id);
        this.core0.start();
        //this.core1.start();
    }

    public core getCore0(){
        return core0;
    }

    public core getCore1(){
        return core1;
    }
}