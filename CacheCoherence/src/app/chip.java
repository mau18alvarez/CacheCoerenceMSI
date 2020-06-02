package app;

import java.util.concurrent.Semaphore;
import gui.log;

public class chip extends Thread{

    private int chip_id;
    private static Semaphore mutex = new Semaphore(1); 

    private boolean chipStatus = true;
    private boolean missL2;
    private boolean isFromL2 = false;
    private boolean busMiss = false;
  
    private String memMiss = "";
    private String dataMiss = "";
    private String reqId = "";
    private String[][] cacheL2 = {{"Block", "Coherence", "Owner", "Memory Dir", "Data"},{"0","","","",""},{"1","","","",""},{"2","","","",""},{"3","","","",""}};
    private String[] L2Row;
    private log logging;
    private String logName;
    
    private core core0;
    private core core1;


    public chip(int chip_id, boolean chipStatus){
        //super(msg);
        this.chipStatus = chipStatus;
        this.chip_id = chip_id;
        this.core0 = new core(0, chip_id);
        this.core1 = new core(1, chip_id);
        this.logName = "Test";
        this.logging = new log(this.logName);

    }

    /**
     * -----------------------------------------------MSI------------------------------------------------------------------------
     */

    public void missL1Controller(){
        //Check if there is a miss
        if(core0.missL1() == true){
            logging.newInfo("Searching on first L2");
            this.memMiss = core0.getMemDir();
            //Check if data is on cache
            if (checkOnCache(this.memMiss) == false){
                logging.newInfo("Data from chip " + chip_id + " core " + core0.getCoreId() + "not found in L2");
                this.missL2 = true;
                this.reqId = core0.getCoreId();
                logging.newInfo("Searching on second L2");
                while(this.missL2){
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        logging.newInfo("Fail in L1 Controller:" + e.getMessage());
                    }
                }
                if(this.isFromL2){
                    this.core1.setData(this.getCacheL2Data(this.memMiss));
                }else{
                    logging.newInfo("Writing data in L2 from" + chip_id + " core " + core0.getCoreId());
                    this.writeOnL2(this.L2Row);
                    this.core0.setData(this.getCacheL2Data(this.memMiss));
                }
            }else{
                this.core0.setData(this.getCacheL2Data(this.memMiss));
            }
            this.core0.setCacheL1Miss(false);

        }else if (core1.missL1() == true){
            logging.newInfo("Searching on first L2");
            this.memMiss = core1.getMemDir();
            //Check if data is on cache
            if (checkOnCache(this.memMiss) == false){
                logging.newInfo("Data from chip " + chip_id + " core " + core1.getCoreId() + " not found in L2");
                this.missL2 = true;
                this.reqId = core1.getCoreId();
                logging.newInfo("Searching on second L2");
                while(this.missL2){
                    logging.newInfo("Writing on second L2");
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        logging.newInfo("Fail in L1 Controller:" + e.getMessage());
                    }
                }
                if(this.isFromL2){
                    this.core1.setData(this.getCacheL2Data(this.memMiss));
                }else{
                    logging.newInfo("Writing data in L2 from" + chip_id + " core " + core0.getCoreId());
                    this.writeOnL2(this.L2Row);
                    this.core1.setData(this.getCacheL2Data(this.memMiss));
                }
            }else{
                this.core1.setData(this.getCacheL2Data(this.memMiss));
            }
            this.core1.setCacheL1Miss(false);
        }

    }

    private void BusManage(){
        if(this.core0.BusWr()){
            this.writeonCache(core0.getMemDir(), core0.getData(), core0.getCoreId());
            core1.invalidCache(core0.getMemDir());
            this.memMiss = core0.getMemDir();
            this.dataMiss = core0.getData();
            this.busMiss = true;
            while(this.busMiss){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    logging.newInfo("Fail on Bus Manage" + e.getMessage());
            }
        }
        this.core0.setBusWr(false);
        }else if (this.core1.BusWr()){
            this.writeonCache(core1.getMemDir(), core1.getData(), core1.getCoreId());
            core0.invalidCache(core1.getMemDir());
            this.memMiss = core1.getMemDir();
            this.dataMiss = core1.getData();
            this.busMiss = true;
            while(this.busMiss){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    logging.newInfo("Fail on Bus2 Manage" + e.getMessage());
            }
        }
        this.core1.setBusWr(false);
    }
}
    

    /**
     * -----------------------------------------------MSI AUX------------------------------------------------------------------------
     */

    //Function to check value on Cache.
    public boolean checkOnCache(String direction){
        boolean result = false;
        for (int i=1; i < this.cacheL2.length; i++){
            if (direction.equals(cacheL2[i][3])){        //True if is in cache
                this.dataMiss = cacheL2[i][4];
                result =  true;
            }else{
                result = false;                         //False if is not
            }
        }
        return result;
    }

    
    public void setL2Value(int position, String direction, String value){
        for (int i = 1; i < this.cacheL2.length; i++) {
            if(direction.equals(this.cacheL2[i][3])){
                this.cacheL2[i][position] += value;
                break;
            }
        }
    }

    public String getL2Value(int position, String direction){
        String aux = "";
        for (int i = 1; i < this.cacheL2.length; i++) {
            if(direction.equals(this.cacheL2[i][3])){
                aux = this.cacheL2[i][position];
            }
        }
        return aux;
    }

    private String getCacheL2Data(String direction){
        for (int i=1; i < this.cacheL2.length; i++){
            if (direction.equals(cacheL2[i][3])){
                this.dataMiss = this.cacheL2[i][4];
                return dataMiss;
            }     
        }
        return this.dataMiss;
    }

    public String[] getRow(String memdir) {
        for (int i = 1; i < cacheL2.length; i++) {
            if(memdir.equals(this.cacheL2[i][3])){
                this.L2Row = this.cacheL2[i];
            }
        }
        return this.L2Row;
    }

    private void writeOnL2(String[] row){
        for (int i = 1; i < this.cacheL2.length; i++) {
            if(row[0].equals(cacheL2[i][0])){
                this.cacheL2[i] = row;
            }
        }
    }

    public String getL2Values(int pos, String dir){
        String value = "";
        for (int i = 1; i < this.cacheL2.length; i++) {
            if(dir.equals(this.cacheL2[i][3])){
                value = this.cacheL2[i][pos];
            }
        }
        return value;
    }

    private void writeonCache(String direction, String write_data, String core_id){
        String block = Integer.toString(Integer.parseInt(direction,2)%4);
        for (int i = 1; i < this.cacheL2.length; i++) {
            if(block.equals(this.cacheL2[i][0])){
                this.cacheL2[i][1] = "DM";
                this.cacheL2[i][2] = "P" + this.chip_id + ":" + core_id;
                this.cacheL2[i][3] = direction;
                this.cacheL2[i][4] = write_data;
                break;
            }
        }

    }

    
    public String getDataDir(String direction){
        String dir = "";
        for (int i = 1; i < cacheL2.length; i++) {
            if(direction.equals(this.cacheL2[i][3])){
                dir = this.cacheL2[i][4];
            }
        }
        return dir;
    }

    public void invalidCache(String direction){
        for (int i = 1; i < this.cacheL2.length; i++) {
            if(direction.equals(this.cacheL2[i][3])){
                this.cacheL2[i][1] = "DI";
                break;
            }
        }
        this.core0.invalidCache(direction);
        this.core1.invalidCache(direction);
    }

    
    /**
     * -----------------------------------------------RUN------------------------------------------------------------------------
     */

    public void run(){
        this.core0.start();
        this.core1.start();
        while(chipStatus == true){
            try {
                mutex.acquire();
            }catch (Exception e){
                logging.newInfo("Error on mutex" + e.getMessage());
            }
            this.BusManage();
            this.missL1Controller();
            mutex.release();
        }
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

    //Get if l2 miss
    public boolean missL2(){
        return missL2;
    }

    //Setter of the l1 miss
    public void setCacheL2Miss(boolean miss){
        missL2 = miss;
    }

    //Getter of L2
    public String[][] getL2(){
        return cacheL2;
    }

    //Getter of isFromL2
    public boolean isFromL2() {
        return isFromL2;
    }

    //Setter of isFromL2
    public void setFromL2(boolean isFromL2) {
        this.isFromL2 = isFromL2;
    }

    public String getMemDir() {
        return memMiss;
    }

    public void setMemDir(String memDir) {
        this.memMiss = memDir;
    }

    public String getCpuId(){
        return Integer.toString(chip_id);
    }

    //Getter of missData
    public String getMissData(){
        return this.dataMiss;
    }

    //Setter of missData
    public void setMissData(String data){
        this.dataMiss = data;
    }

    //Getter of getL2Row
    public String[] getL2Row() {
        return L2Row;
    }

    //Setter of getL2Row
    public void setL2Row(String[] l2Row) {
        L2Row = l2Row;
    }

    public boolean busMiss() {
        return busMiss;
    }

    public void setBusMiss(boolean miss) {
        busMiss = miss;
    }

    //Set chip on pause
    public void pauseChip(){
        this.chipStatus = false;
    }

    //Resume chip from pause
    public void resumeChip(){
        this.chipStatus = true;
    }

    public boolean chipStatus() {
        return chipStatus;
    }

    public void setChipStatus(boolean status) {
        this.chipStatus = status;
    }

    //Getter of the id 
    public String getReqId() {
        return reqId;
    }
    
}