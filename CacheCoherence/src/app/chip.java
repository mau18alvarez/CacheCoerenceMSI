package app;

public class chip extends Thread{

    private boolean chipStatus = true;
    private String[][] L2 = {{"Block", "Coherence", "Memory Dir", "Data"},{"","","",""},{"","","",""},{"","","",""},{"","","",""}};

    
    private core core0;
    private core core1;


    public chip(int chip_id){
        this.core0 = new core(0, chip_id);
        this.core1 = new core(1, chip_id);
        this.core0.start();
        this.core1.start();
    }

    public core getCore0(){
        return core0;
    }

    public core getCore1(){
        return core1;
    }

    //Set chip on pause
    public void pauseChip(){
        this.chipStatus = false;
    }

    //Resume chip from pause
    public void resumeChip(){
        this.chipStatus = true;
    }
    
}