package app;

import java.util.Random;


public class core extends Thread{

    Thread t;
    public boolean pause;                 //Core pause or running
    private int core_id;                  //Id of the core
    private int chip_id;                  //Id of the chip father
    private String instruction_type;      //1->Read - 2->Write - 3->Calc
    private String direction;             //Memory Direction
    private String write_data;            //Set data to write
    private String final_instruction;     //Set the final struction
    private String[][] L1 = {{"Bloque", "Coherencia", "Memoria", "Dato"},{"0","","",""},{"1","","",""}};
    
    /**
     * Constructor of the CORE.
     */
    public core(int core_id, int chip_id) {
        this.core_id = core_id;         //set initial id
        this.chip_id = chip_id;         //set initial processor father
        this.instruction_type = "";     //set initial instruction type
        this.direction = "";            //set initial direction
        this.write_data = "";           //set initial data
        this.final_instruction = "";    //set initial final instruction
        this.pause = false;             //set initial value for pause
    }


    public void run () {
        try{
            while(pause == false){
                Thread.sleep(1000);             //Generate requests every second
                generate_instruction();         //set the instruction
                generate_final_inst(chip_id, core_id, instruction_type, direction, write_data); //set the final instruction
                System.out.println(final_instruction); 
            }
        }catch (InterruptedException e){
            System.out.println("Sleep Error");
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

    //Random Generator
    //n numero de posibilades, p=probabilidad
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
        int random = Binomial(15, 0.625);
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
        return L1;
    }

    //Set core on pause
    public void pauseCore(){
        this.pause = true;
    }

    //Resume core from pause
    public void resumeCore(){
        this.pause = false;
    }
}

