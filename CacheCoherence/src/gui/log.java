package gui;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Creation of Log File
 * Tomado de StackOverflow
 * How to write logs in text file when using java.util.logging.Logger
 */

public class log {
    
    private String fileName;
    private Logger logger;
    private FileHandler fh;
  
    public log(String fileName) {
      this.fileName = fileName;
      try {
        // This block configure the logger with handler and formatter  
        this.logger = Logger.getLogger(this.fileName);
        this.fh = new FileHandler("logFiles\\" + fileName + ".log");
        this.logger.addHandler(this.fh);
        SimpleFormatter formatter = new SimpleFormatter();
        this.fh.setFormatter(formatter);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  
    //Methods
    public void newInfo(String msg){
      this.logger.info(msg +  "\n");
    }
  
    public void newWarn(String msg){
      this.logger.warning(msg);
    }
  }