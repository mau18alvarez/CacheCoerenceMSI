package app;

public class cacheL1 {
    private String block;
    private String coherence; 
    private String memDir;
    private String data;
  
    public cacheL1(String block, String coherence, String memDir, String data) {
      this.block = block;
      this.coherence = coherence;
      this.memDir = memDir;
      this.data = data;
    }
  
    public String getblock() {
      return block;
    }
  
    public void setblock(String block) {
      this.block = block;
    }
  
    public String getcoherence() {
      return coherence;
    }
  
    public void setcoherence(String coherence) {
      this.coherence = coherence;
    }
  
    public String getMemDir() {
      return memDir;
    }
  
    public void setMemDir(String memDir) {
      this.memDir = memDir;
    }
  
    public String getdata() {
      return data;
    }
  
    public void setdata(String data) {
      this.data = data;
    }
  }