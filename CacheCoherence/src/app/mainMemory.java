package app;

public class mainMemory extends Thread{

    //Chips
    private chip chip0;
    private chip chip1;

    //Render Memory
    public String[][] memory = {{ "Block", "Owner", "Data" },   {"0000","","0000" }, 
                                                                {"0001","","0000" }, 
                                                                {"0010","","0000" },
                                                                {"0011","","0000" },
                                                                {"0100","","0000" },
                                                                {"0101","","0000" },
                                                                {"0110","","0000" },
                                                                {"0111","","0000" },
                                                                {"1000","","0000" },
                                                                {"1001","","0000" },
                                                                {"1010","","0000" },
                                                                {"1011","","0000" },
                                                                {"1100","","0000" },
                                                                {"1101","","0000" },
                                                                {"1110","","0000" },
                                                                {"1111","","0000" }};

    
    public mainMemory(){
        this.chip0 = new chip(0, true);
        this.chip1 = new chip(1, true);
    }

    public void run(){
        System.out.println("Iniciando Chips");
        this.chip0.start();
        this.chip1.start(); 
        
    }

    /**
     * -----------------------------------------------GETTERS AND SETTERS------------------------------------------------------------------------
     */

    public chip getChip0() {
        return chip0;
    }
    
    public chip getChip1() {
        return chip1;
    }
                                                                // Getter of the L1 memory
    public String[][] getMemory() {
        return memory;
    }
}