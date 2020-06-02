package app;
import java.util.concurrent.Semaphore;

public class mainMemory extends Thread{

    //Chips
    private chip chip0;
    private chip chip1;
    private String data= "";
    private boolean runMain = true;
    private static Semaphore mutex = new Semaphore(1);

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

    
    /**
     * -----------------------------------------------MSI------------------------------------------------------------------------
     */

    public void memoryManage(){
         if(chip0.missL2() == true){
            if(chip0.checkOnCache(chip0.getMemDir())){
                this.chip1.setL2Value(2, chip0.getMemDir(), ";E");
                this.chip0.setMissData(this.chip1.getDataDir(this.chip0.getMissData()));
                this.chip0.setFromL2(true);
             }else{
                this.readMem(this.chip0.getMemDir(),this.chip0.getCpuId());
                int bloque = Integer.parseInt(chip0.getMemDir(),2) % 4;
                String owner = chip0.getCpuId()+","+chip0.getReqId();
                String[] Row = {Integer.toString(bloque),"DS",owner,chip0.getMemDir(),this.data};
                this.chip0.setL2Row(Row);
            }
            chip0.setCacheL2Miss(false);

        }else if(chip1.missL2() == true){
            if(chip0.checkOnCache(chip1.getMemDir())){
                this.chip1.setL2Value(2, chip1.getMemDir(), ";E");
                this.chip1.setMissData(this.chip1.getDataDir(this.chip0.getMissData()));
                this.chip1.setFromL2(true);
             }else{
                this.readMem(this.chip1.getMemDir(),this.chip1.getCpuId());
                int bloque = Integer.parseInt(chip1.getMemDir(),2) % 4;
                String owner = chip1.getCpuId()+","+chip1.getReqId();
                String[] Row = {Integer.toString(bloque),"DS",owner,chip0.getMemDir(),this.data};
                this.chip1.setL2Row(Row);
            }
            chip1.setCacheL2Miss(false);
        }
    }

    private void writeManage(){
        if(this.chip0.busMiss()){
          writeMem(this.chip0.getMemDir(), this.chip0.getMissData(), this.chip0.getCpuId());
          this.chip1.invalidCache(this.chip0.getMemDir());
          try{
            Thread.sleep(1000);
          }catch(Exception e){
              System.out.println(e.getMessage());
          }
          this.chip0.setBusMiss(false);
          
        }else if(this.chip1.busMiss()){
          writeMem(chip1.getMemDir(), this.chip1.getMissData(), this.chip1.getCpuId());
          this.chip0.invalidCache(this.chip1.getMemDir());
          try{
            Thread.sleep(1000);
          }catch(Exception e){
              System.out.println(e.getMessage());
          }
          this.chip1.setBusMiss(false);
          
        }
      }


  private void writeMem(String direction, String data, String reqId){
    for (int i = 1; i < memory.length; i++) {
      if(direction.equals(memory[i][0])){
        if(this.memory[i][1].equals("")){
          memory[i][1] = reqId;
        } else if(this.memory[i][1].equals("P0")){
          memory[i][1] += ", P1";
        } else if(this.memory[i][1].equals("P1")){
          memory[i][1] += ", P0";
        }
        memory[i][2] = data; 
      }
    }
  }

  private void readMem(String direction, String reqId){
    for (int i = 1; i < memory.length; i++){
      if(direction.equals(this.memory[i][0])){
        if(this.memory[i][1].equals("")){
          this.memory[i][1] = reqId;

        }else if(memory[i][1].equals("P0")){
          this.memory[i][1] += ", P1";

        }else if(memory[i][1].equals("P1")){
          this.memory[i][1] += ", P0";
        }
        this.data = this.memory[i][2];
      }
    }
  }

    public void run(){
        System.out.println("Iniciando Chips");
        this.chip0.start();
        this.chip1.start(); 
        while(this.runMain){
            try {
              mutex.acquire();
            } catch (Exception e) {
              System.out.println(e.getMessage());
            }
            this.writeManage();
            this.memoryManage();
            mutex.release();
            
        }
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