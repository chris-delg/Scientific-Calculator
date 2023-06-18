/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


public class HistoryController {
    
    @FXML
    private ListView historyList;
    
    public void initCalculations(ArrayList<String> calculationHistory){
        calculationHistory.forEach((calculation) -> {
            historyList.getItems().add(calculation);
        });
    }
}
