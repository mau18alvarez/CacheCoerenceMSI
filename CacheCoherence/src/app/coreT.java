package app;

import java.util.Random;

import sun.security.util.PropertyExpander.ExpandException;


public class coreT extends Thread{

    Thread t;
    public boolean coreStatus;            //Core pause or running
    public boolean writeBusMiss;          //Write bus status 
    public boolean readBusMiss;           //Read bus status

    private int core_id;                  //Id of the core
    private int chip_id;                  //Id of the chip father
    private String instruction_type;      //1->Read - 2->Write - 3->Calc
    private String direction;             //Memory Direction
    private String write_data;            //Set data to write
    private String coherence_status;      //Set coherence status
    private String final_instruction;     //Set the final struction
    private String parse_instruction;     //Set the final struction for parse
    private String[][] cacheL1 = {{"Block", "Coherence", "Memory Dir", "Data"},{"","","",""},{"","","",""}};
    
    /**
     * Constructor of the CORE.
     */
    public coreT(int core_id, int chip_id) {
        this.core_id = core_id;         //set initial id
        this.chip_id = chip_id;         //set initial processor father
        this.instruction_type = "";     //set initial instruction type
        this.direction = "";            //set initial direction
        this.write_data = "";           //set initial data
        this.final_instruction = "";    //set initial final instruction
        this.parse_instruction = "";    //set initial final instruction
        this.coherence_status = "I";    //set initial value for coherence status
        this.coreStatus = true;         //set initial value for Status ->TRUE ON    ->FALSE OFF
    }


    public void run () {
        try{
            while(coreStatus == true){
                Thread.sleep(1000);                  //Generate requests every second
                this.generate_instruction();         //set the instruction
                this.generate_final_inst(chip_id, core_id, instruction_type, direction, write_data); //set the final instruction
                this.generate_parse_inst(chip_id, core_id, instruction_type, direction, write_data); //set the final instruction for parse
                this.MSI(instruction_type, direction, write_data);
                //Set the values to print on Interface
                //this.cacheL1[1][0] = Integer.toString(core_id);
                //this.cacheL1[1][1] = instruction_type;
                //this.cacheL1[1][2] = direction;
                //this.cacheL1[1][3] = write_data;
                //System.out.println(parse_instruction); 
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
    public void generate_mem_dir( ){
        int random = Binomial(15, 0.375);
        String result =  Integer.toBinaryString(random);

        if (random == 0 || random == 1){
            this.direction = "000" + result;
        }
        if (random == 2 || random == 3){
            this.direction = "00" + result;
        }
        if (random >= 4 && random <= 7){
            this.direction = "0" + result;
        }
        else{
            this.direction = result;
        }
    }

     /**
     * Creation of a new instruction and set the data.
     */
    public void generate_instruction( ){
        int random = Binomial(3, 0.34);

        if (random == 0){
            this.instruction_type = "READ";
            this.write_data = "";
            generate_mem_dir();             //set the direction of the instruction
        }else if (random == 1){
            this.instruction_type = "WRITE";
            generate_data();
            generate_mem_dir();             //set the direction of the instruction
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

    //Function to write Cache.
    private void writeOnCache(String direction, String write_data){
        for (int i=1; i < 3; i++){
            if (direction.equals(cacheL1[i][2])){              //If match the direction
                this.coherence_status = "M";
                this.cacheL1[i][1] = this.coherence_status;    //State Modified
                this.cacheL1[i][2] = direction;                //Write direction
                this.cacheL1[i][3] = write_data;               //Write data
            } 
        }
    }

    private void MSI(String instruction_type, String direction, String write_data, String coherence_status){
  
        if (instruction_type.equals("WRITE")){
            //EXCLUSIVE (M)
            if(coherence_status.equals("M")){
                if (checkOnCache(direction) == true){
                    //Cpu write hit I -> M
                    this.writeBusMiss = false; //busmiss
                    this.writeOnCache(direction, write_data);
                    System.out.println("Write Hit"); 
                }else{
                    //CPU write miss
                    //Write on memory
                    int i = cacheMap(direction);
                    this.coherence_status = "M";
                    this.cacheL1[i][1] = this.coherence_status;
                    this.cacheL1[i][2] = direction;
                    this.cacheL1[i][3] = write_data;
                    //Place write miss
                    this.writeBusMiss = true;
                    System.out.println("Write Miss"); 
                }
            }else if (coherence_status.equals("S")){
                if (checkOnCache(direction) == true){
                    //Cpu write hit I -> M
                    this.writeBusMiss = false; //busmiss
                    this.writeOnCache(direction, write_data);
                }
            }




        }else if (instruction_type.equals("READ")){
            //Cpu read hit
            if (checkOnCache(direction) == true){
                try{
                    Thread.sleep(500);
                    System.out.println("Read Hit"); 
                }catch(Exception e){
                    System.out.println("Fail in Read Thread:" + e.getMessage());
                }
            }else{
                //READ miss
                this.readBusMiss = true;    //Place ReadMiss on bus
                System.out.println("Read Miss"); 
                //Escribir a memoria
            }

        }else if (instruction_type.equals("CALC")){
            try{
                Thread.sleep(500);
                System.out.println("Calc"); 
            }catch(Exception e){
                System.out.println("Fail in Calc Thread:" + e.getMessage());
            }
        }
    }
    
     /**
     * -----------------------------------------------GETTERS AND SETTERS------------------------------------------------------------------------
     */
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

    //Getter of the final instrucion
    public String getFinalInstr() {
        return final_instruction;
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

