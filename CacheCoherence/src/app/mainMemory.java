package app;

public class mainMemory {

    //Render Memory
    public String[][] Render = { { "Dato" }, { "1" }, { "" }, { "" }, { "" }, { "" }, { "" }, { "" }, { "" },
                                            { "" }, { "" }, { "" }, { "" }, { "" }, { "" }, { "15" } };

    // We need 16 lines of Memory
    private memoryLine line0;
    private memoryLine line1;
    private memoryLine line2;
    private memoryLine line3;
    private memoryLine line4;
    private memoryLine line5;
    private memoryLine line6;
    private memoryLine line7;
    private memoryLine line8;
    private memoryLine line9;
    private memoryLine line10;
    private memoryLine line11;
    private memoryLine line12;
    private memoryLine line13;
    private memoryLine line14;
    private memoryLine line15;

    mainMemory() {
        // Set inicial values of the memory.
        this.line0 = new memoryLine(0, "");
        this.line1 = new memoryLine(0, "");
        this.line2 = new memoryLine(0, "");
        this.line3 = new memoryLine(0, "");
        this.line4 = new memoryLine(0, "");
        this.line5 = new memoryLine(0, "");
        this.line6 = new memoryLine(0, "");
        this.line7 = new memoryLine(0, "");
        this.line8 = new memoryLine(0, "");
        this.line9 = new memoryLine(0, "");
        this.line10 = new memoryLine(0, "");
        this.line11 = new memoryLine(0, "");
        this.line12 = new memoryLine(0, "");
        this.line13 = new memoryLine(0, "");
        this.line14 = new memoryLine(0, "");
        this.line15 = new memoryLine(0, "");
    }

    public void storeData(int dir, String data, int idChip) {
        switch (dir) {
            case 0:
                line0.setMemData(data);
                line0.setMemIdChip(idChip);
                break;
            case 1:
                line1.setMemData(data);
                line1.setMemIdChip(idChip);
                break;
            case 2:
                line2.setMemData(data);
                line2.setMemIdChip(idChip);
                break;
            case 3:
                line3.setMemData(data);
                line3.setMemIdChip(idChip);
                break;
            case 4:
                line4.setMemData(data);
                line4.setMemIdChip(idChip);
                break;
            case 5:
                line5.setMemData(data);
                line5.setMemIdChip(idChip);
                break;
            case 6:
                line6.setMemData(data);
                line6.setMemIdChip(idChip);
                break;
            case 7:
                line7.setMemData(data);
                line7.setMemIdChip(idChip);
                break;
            case 8:
                line8.setMemData(data);
                line8.setMemIdChip(idChip);
                break;
            case 9:
                line9.setMemData(data);
                line9.setMemIdChip(idChip);
                break;
            case 10:
                line10.setMemData(data);
                line10.setMemIdChip(idChip);
                break;
            case 11:
                line11.setMemData(data);
                line11.setMemIdChip(idChip);
                break;
            case 12:
                line12.setMemData(data);
                line12.setMemIdChip(idChip);
                break;
            case 13:
                line13.setMemData(data);
                line13.setMemIdChip(idChip);
                break;
            case 14:
                line14.setMemData(data);
                line14.setMemIdChip(idChip);
                break;
            case 15:
                line15.setMemData(data);
                line15.setMemIdChip(idChip);
                break;
        }
    }

    public String readData(int dir) {
        String data = "";
        switch (dir) {
            case 0:
                data = line0.getMemData();
                break;
            case 1:
                data = line1.getMemData();
                break;
            case 2:
                data = line2.getMemData();
                break;
            case 3:
                data = line3.getMemData();
                break;
            case 4:
                data = line4.getMemData();
                break;
            case 5:
                data = line5.getMemData();
                break;
            case 6:
                data = line6.getMemData();
                break;
            case 7:
                data = line7.getMemData();
                break;
            case 8:
                data = line8.getMemData();
                break;
            case 9:
                data = line9.getMemData();
                break;
            case 10:
                data = line10.getMemData();
                break;
            case 11:
                data = line11.getMemData();
                break;
            case 12:
                data = line12.getMemData();
                break;
            case 13:
                data = line13.getMemData();
                break;
            case 14:
                data = line14.getMemData();
                break;
            case 15:
                data = line15.getMemData();
                break;
        }
        return data;
    }

    // Getter of the L1 memory
    public String[][] getMemory() {
        return Render;
    }
}