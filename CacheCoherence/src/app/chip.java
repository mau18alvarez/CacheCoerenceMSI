package app;

public class chip extends Thread{

    private boolean chipStatus = true;
    private int chip_id;
    private boolean miss;
    private String[][] L2 = {{"Block", "Coherence", "Memory Dir", "Data"},{"0","","",""},{"","","",""},{"","","",""},{"","","",""}};

    
    private core core0;
    private core core1;


    public chip(int chip_id, boolean chipStatus){
        //super(msg);
        this.chipStatus = chipStatus;
        this.chip_id = chip_id;
        this.core0 = new core(0, chip_id);
        this.core1 = new core(1, chip_id);

    }

    public void run(){
        this.core0.start();
        this.core1.start();
    }



    /**
     * -----------------------------------------------GETTERS AND SETTERS------------------------------------------------------------------------
     */

    //Getter of core0
    public core getCore0(){
        return core0;
    }

    //Getter of core1
    public core getCore1(){
        return core1;
    }

    //Getter of L2
    public String[][] getL2(){
        return L2;
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