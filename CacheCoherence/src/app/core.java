package app;

import java.util.Random;
import gui.log;


public class core extends Thread{

    Thread t;
    public boolean coreStatus;            //Core pause or running
    private boolean writeBusMiss;          //Write bus status 
    private boolean cacheL1Miss;           //cache miss 

    private int core_id;                  //Id of the core
    private int chip_id;                  //Id of the chip father
    private String instruction_type;      //1->Read - 2->Write - 3->Calc
    private String direction;             //Memory Direction
    private String write_data;            //Set data to write
    private String final_instruction;     //Set the final struction
    private String parse_instruction;     //Set the final struction for parse
    private String memDir = "";           //Aux String for memory
    private log logging;
    private String[][] cacheL1 = {{"Block", "Coherence", "Memory Dir", "Data"},{"0","","",""},{"1","","",""}};
    
    /**
     * Constructor of the CORE.
     */
    public core(String msg, int core_id, int chip_id) {
        super(msg);
        this.core_id = core_id;         //set initial id
        this.chip_id = chip_id;         //set initial processor father
        this.instruction_type = "";     //set initial instruction type
        this.direction = "";            //set initial direction
        this.write_data = "";           //set initial data
        this.final_instruction = "";    //set initial final instruction
        this.parse_instruction = "";    //set initial final instruction
        this.coreStatus = true;         //set initial value for Status ->TRUE ON    ->FALSE OFF
        
        //Set booleans status
        this.writeBusMiss = false;      //Write bus status 
        this.cacheL1Miss = false;       //cache miss 
    }

    public void run () {
        try{
            while(coreStatus == true){
                this.generate_instruction();         //set the instruction
                this.generate_final_inst(chip_id, core_id, instruction_type, direction, write_data); //set the final instruction
                this.generate_parse_inst(chip_id, core_id, instruction_type, direction, write_data); //set the final instruction for parse
                this.MSI(core_id, chip_id, instruction_type, direction, write_data);
                Thread.sleep(1000);                  //Generate requests every second
                //Set the values to print on Interface
                //this.cacheL1[1][0] = Integer.toString(core_id);
                //this.cacheL1[1][1] = instruction_type;
                //this.cacheL1[1][2] = direction;
                //this.cacheL1[1][3] = write_data;
                System.out.println(this.final_instruction); 
            }
        }catch (InterruptedException e){
            System.out.println("Fail in Core Main Thread:" + e.getMessage());
        }
}

    /**
     * Initiate the thread
     */
    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

        
     /**
     * -----------------------------------------------INSTRUCTION GENERATION--------------------------------------------------------------
     */

    /**
    Random Binomial Number Generator
    Taken from Stack Overflow: A efficient binomial random number generator code in Java
    n=Chances, p=Probability
     */
    private int Binomial(final int n, final double p){
        final double log_q = Math.log(1.0 - p);
        int x = 0;
        double sum = 0;
        for(;;) { //infinity loop
            sum += Math.log(Math.random()) / (n - x);
            if(sum < log_q) {
                return x;
            }
            x++;
        }
    }

    /**
     *Generate RANDOM data in case of a Write.
     */
    public void generate_data( ){
        Random rand = new Random();
        int new_data = rand.nextInt(65535);
        this.write_data = Integer.toHexString(new_data);
    }

    /**
     * Creation of a new memory direction.
     */
    private String generate_mem_dir( ){
        //int random = Binomial(16, 0.1);
        Random rand = new Random();
        int random = rand.nextInt(16);
        String result =  Integer.toBinaryString(random);

        if (random == 0 || random == 1){
            return  "000" + result;
        }
        if (random == 2 || random == 3){
            return  "00" + result;
        }
        if (random >= 4 && random <= 7){
            return  "0" + result;
        }
        else{
            return result;
        }
    }

     /**
     * Creation of a new instruction and set the data.
     */
    public void generate_instruction( ){
        System.out.println("Generando instuccion");
        int random = Binomial(3, 0.34);

        if (random == 0){
            this.instruction_type = "READ";
            this.write_data = "";
            this.direction = generate_mem_dir();             //set the direction of the instruction
        }else if (random == 1){
            this.instruction_type = "WRITE";
            generate_data();
            this.direction = generate_mem_dir();             //set the direction of the instruction
        }else{
            this.instruction_type = "CALC";
            this.write_data = "";
            this.direction = "";
        }
    }


    public void generate_final_inst(int chip_id, int core_id, String instruction_type, String direction, String write_data){
        this.final_instruction = "P" + Integer.toString(chip_id) + ", " + Integer.toString(core_id) + ": " + instruction_type + " " + direction + "; " + write_data;  
    } 
    
    public void generate_parse_inst(int chip_id, int core_id, String instruction_type, String direction, String write_data){
        switch (instruction_type) {
            case "READ":
                this.parse_instruction = "P"+Integer.toString(chip_id) + ";" + Integer.toString(core_id) + ";" + instruction_type + ";" + direction;
                break;
            case "CALC":
                this.parse_instruction = "P" + Integer.toString(chip_id) + ";" + Integer.toString(core_id) + ";" + instruction_type;
                break;
            case "WRITE":
                this.parse_instruction = "P" + Integer.toString(chip_id) + ";" + Integer.toString(core_id) + ";" + instruction_type + ";" + direction + ";" + write_data;  
                break;
        }
    }

        
     /**
     * -----------------------------------------------MSI STATE MACHINE FUNCTIONS----------------------------------------------------------------
     */

    //Function to map Cache-Direct Correspondence.
    private int cacheMap(String direction){
        int map = ((Integer.parseInt(direction, 2)) % 2) + 1;   //Parse String to INT and get in decimal. Module 2 -> two blocks is cache. +1 -> Inreface Matrix
        return map;
    }

    //Function to check value on Cache.
    private boolean checkOnCache(String direction){
        boolean result = false;
        for (int i=1; i < 3; i++){
            if (direction.equals(cacheL1[i][2])){        //True if is in cache
                result =  true;
            }else{
                result = false;                         //False if is not
            }
        }
        return result;
    }

    //Function to check Invalid cache
    private boolean checkInv(String direction){
        boolean result = false;
        for (int i=1; i < 3; i++){
            if (direction.equals(cacheL1[i][2])){        //True if is in cache
                if(cacheL1[i][1].equals("I")){
                    result =  false;
                }
            }
        }
        return result;
    }

        //Function to write Cache.
    private void writeBus(String direction){
        int aux = cacheMap(direction);
        for (int i=1; i < this.cacheL1.length; i++){
            if(this.cacheL1[i][0].equals(Integer.toString(aux))){
                this.cacheL1[i][1] = "S";
                this.cacheL1[i][2] = this.direction;
                this.cacheL1[i][3] = this.write_data;
            } 
        }
    }

    //Function to write Cache.
    private void writeOnCache(String direction, String write_data){
        for (int i=1; i < 3; i++){
            if (direction.equals(cacheL1[i][2])){        //If match the direction
                this.cacheL1[i][1] = "M";                //State Modified
                this.cacheL1[i][2] = direction;          //Write direction
                this.cacheL1[i][3] = write_data;         //Write data
            } 
        }
    }

    public void invalidCache(String direction){
        for (int i = 1; i < this.cacheL1.length; i++) {
            if(direction.equals(this.cacheL1[i][2])){
                this.cacheL1[i][1] = "I";
            }
        }
    }

    private void MSI(int core_id, int chip_id, String instruction_type, String direction, String write_data){
        
        if (instruction_type.equals("WRITE")){
            //Write on memory
            int i = cacheMap(direction);
            this.cacheL1[i][1] = "M";
            this.cacheL1[i][2] = direction;
            this.cacheL1[i][3] = write_data;
            //Place write miss
            System.out.println("WRITE on L1 from chip " + chip_id + " core " + core_id);
            this.memDir = direction;
            this.write_data = write_data;
            this.writeBusMiss = true;
            while(this.writeBusMiss){
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    System.out.println("Fail in Core MSI WRITE Thread:" + e.getMessage());
                }
            }
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                System.out.println("Fail in Core MSI WRITE2 Thread:" + e.getMessage());
            }
            
        }else if (instruction_type.equals("READ")){
            //Cpu read hit
            if (checkOnCache(direction) == true){
                System.out.println("READ on L1 from chip " + chip_id + " core " + core_id);
                if(this.checkInv(direction)){
                    this.direction = direction;
                    this.cacheL1Miss = true;
                    while(this.cacheL1Miss){
                        try{
                            Thread.sleep(1000);
                        }catch(Exception e){
                            System.out.println("Fail in Core MSI READ Thread:" + e.getMessage());
                        }
                    }
                    this.writeBus(this.direction);
                }   
                try{
                    Thread.sleep(500);
                }catch(Exception e){
                    System.out.println("Fail in Core MSI READ2 Thread:" + e.getMessage());
                }
            }else{
                //READ miss
                System.out.println("READ MISS on L1 from chip " + chip_id + " core " + core_id);
                this.cacheL1Miss = true;    //Place ReadMiss
                this.memDir = direction;
                System.out.println("Try to get data from L2 of " + chip_id + " core " + core_id);
                while(this.cacheL1Miss){
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        System.out.println("Fail in Core MSI READ L2 Thread:" + e.getMessage());
                    }
                }
                System.out.println("Updating L1 of " + chip_id + " core " + core_id + "with L2 data");
                this.writeBus(this.direction);
            }

        }else if (instruction_type.equals("CALC")){
            System.out.println("CALC operation in" + chip_id + " core " + core_id);
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                System.out.println("Fail in Core MSI CALC Thread:" + e.getMessage());
            }
        }
    }

     /**
     * -----------------------------------------------GETTERS AND SETTERS------------------------------------------------------------------------
     */

    public void newLog(String msg){
        this.logging.newInfo(msg);
    }

    //Getter of the instruction (READ,WRITE,CALC)
    public String getInstruction() {
        return instruction_type;
    }
    
    //Getter of the memory direction.(XXXX)
    public String getMemDir() {
        return direction;
    }

    //Getter of the data
    public String getData() {
        return write_data;
    }

    //Setter of the data
    public void setData(String data){
        this.write_data = data;
    }

    //Getter of the final instrucion
    public String getFinalInstr() {
        return final_instruction;
    }

    //Getter of the core id
    public String getCoreId(){
        return Integer.toString(this.core_id);
    }
    
    //Getter of the l1 miss
    public boolean getCacheL1Miss(){
        return cacheL1Miss;
    }

    public boolean missL1(){
        return cacheL1Miss;
    }

    public boolean BusWr() {
        return writeBusMiss;
    }

    public void setBusWr(boolean bus) {
        writeBusMiss = bus;
    }

    //Setter of the l1 miss
    public void setCacheL1Miss(boolean miss){
        cacheL1Miss = miss;
    }

    //Getter of the L1 memory
    public String[][] getL1() {
        return cacheL1;
    }

    //Set core on pause
    public void pauseCore(){
        this.coreStatus = false;
    }

    //Resume core from pause
    public void resumeCore(){
        this.coreStatus = true;
    }
}

