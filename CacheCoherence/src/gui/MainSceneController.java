package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainSceneController {

    //Declaration to render the tables
    public TableView <String[]> L1P00;
    public TableView <String[]> L1P01;
    public TableView <String[]> L1P10;
    public TableView <String[]> L1P11;
    public TableView <String[]> L2P0;
    public TableView <String[]> L2P1;
    public TableView<String[]> principalMem;


    //Function to populate the tables on the view
    public void populateTable(TableView<String[]> target, String[][] source){

    int numRows = source.length ;
    int numCols = source[0].length ;
    target.getItems().clear();
    target.getColumns().clear();

    for (int i = 0; i < numCols; i++) {
      TableColumn<String[], String> column = new TableColumn<>(source[0][i]);
      final int columnIndex = i;
      column.setCellValueFactory(cellData -> {
        String[] row = cellData.getValue();
        return new SimpleStringProperty(row[columnIndex]);
      });
      target.getColumns().add(column);
    }
    for (int i = 1; i < numRows; i++) {
      target.getItems().add(source[i]);
    }
    target.refresh();
  }  
}