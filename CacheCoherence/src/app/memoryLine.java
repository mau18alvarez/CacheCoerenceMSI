package app;

public class memoryLine {

    private int idChip;         //Chip id
    private String data;       //Data to write on memory

    public memoryLine(int idChip, String data){

        this.idChip = idChip;
        this.data = data;
    }

    //Getters and Setters

    //Set Id of the Chip
    public void setMemIdChip(int idChip){
        this.idChip = idChip;
    }

    //Get Id of the Chip
    public int getMemIdChip(){
        return idChip;
    }

    //Set Data to the Memory
    public void setMemData(String data){
        this.data = data;
    }

    //Get Id of the Chip
    public String getMemData(){
        return data;
    }


  
}