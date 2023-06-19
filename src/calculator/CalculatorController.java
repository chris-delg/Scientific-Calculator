/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;


import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


public class CalculatorController {
    
    @FXML
    private Label expression;
    
    @FXML
    private Label answer;
    
    private ArrayList<String> calculationHistory = new ArrayList<>();
    
    //setter used to do calculations before equals is pressed  
    public void setExpression(String exp){
        expression.setText(exp);
    }
    
    //setter for answer
    public void setAnswer(String exp){
        answer.setText(exp);
    }
    
    //getter functions
    public String getAnswer(){
        return answer.getText();
    }
    
    public String getExpression(){
        return expression.getText();
    }
    
    //inserts number or operation into the expression
    public void insertChar(String ch){
        setExpression(getExpression() + ch);
    }
    
    //opening parentheses needs special treatment
    public void insertParentheses(String paren){
        String exp = getExpression();
        int size = exp.length();
        
        if(exp.isEmpty()){
            setExpression(paren);
        }
        else if(exp.charAt(size - 1) >= '0' && exp.charAt(size - 1) <= '9'){
            setExpression(getExpression() + "*" + paren);
        }
        else{
            setExpression(getExpression() + paren);
        }
    }
    
    //clear the expression
    public void clear(){
        setExpression("");
        setAnswer("");
    }
    
    //delete last inputted character
    public void deleteLast(){
        if(!getExpression().isEmpty()){
            StringBuilder text = new StringBuilder(getExpression());
            text.deleteCharAt(text.length() - 1);
            setExpression(text.toString());
        }
    }
    
    //evaluates expression after equals sign is pressed 
    public void evaluate(){
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String infix = getExpression();
        
        try {
            Object ans = engine.eval(infix);
            setAnswer(ans.toString());
        } 
        catch (ScriptException ex) {
            setAnswer("Error");
        }
    }
    
    //used to calculate expression inside of a parenthesis for simplifying expression
    public String calcParenthesis(String exp){
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        
        try {
            Object ans = engine.eval(exp);
            return ans.toString();
        } 
        catch (ScriptException ex) {
            setAnswer("Error");
        }
        
        return exp;
    }
    
    //helper function for the "+/-" button
    public void plusMinusHelper(StringBuilder text, int index){
        String num = text.substring(index, text.length());
                
        if(index == 0){
            setExpression("-" + getExpression());
        }
        else if(text.charAt(index - 1) == '-'){
            text.replace(index - 1, index, "+");
            setExpression(text.toString());
        }
        else if(text.charAt(index - 1) == '+'){
            text.replace(index - 1, index, "-");
            setExpression(text.toString());
        }
        else{
            setExpression(text.substring(0, index) + "-" + num);
        }
    }
    
    //helper function that finds index of where a number starts
    public int getNumIndex(StringBuilder text, int index){
        for(;index>=0; index--){
            if(text.charAt(index) == '+' || text.charAt(index) == '-' || 
                    text.charAt(index) == '/' || text.charAt(index) == '+' || 
                    text.charAt(index) == '%' || text.charAt(index) == '*'){
                break;
            }
        }

        return index;
    }
    
    //helper function that finds index of contents inside parenthesis
    public int getParenIndex(StringBuilder text, int index){
        for(;index>=0; index--){
            if(text.charAt(index) == '('){
                break;
            }
        }

        return index;
    }
    
    //used to pull up history
    public void openHistoryWindow(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistoryDesign.fxml"));
            Parent root = loader.load();
            Calculator.getHistoryStage().setScene(new Scene(root));
            
            HistoryController historyController = loader.getController();
            historyController.initCalculations(calculationHistory);
            
            Calculator.getHistoryStage().show();
        }
        catch(IOException ex){
            System.out.println(ex);
        }
    }
    
    //used to display in history window later on
    public void addCalculation(){
        calculationHistory.add(getExpression() + " = " + getAnswer());
    }
    
    //handles all main functionality
    public void onMouseClick(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        String buttonText = button.getText();
        
        if(buttonText.charAt(0) >= '0' && buttonText.charAt(0) <= '9'
                || buttonText.equals(".")){
            insertChar(buttonText);
        }
        else if(buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("*")
                || buttonText.equals("/") || buttonText.equals(")")){
            insertChar(buttonText);
        }
        else if(buttonText.equals("(")){
            insertParentheses(buttonText);
        }
        
        //these require slightly different steps
        if(buttonText.equals("pi") || buttonText.equals("e")){
            String exp = getExpression();
            
            if(exp.isEmpty()){
                insertChar(buttonText.equals("pi") ? "3.14" : "2.71");
            }
            else if(exp.charAt(exp.length() - 1) == ')' ||
                    exp.charAt(exp.length() - 1) >= '0' && exp.charAt(exp.length() - 1) <= '9'){
                insertChar(buttonText.equals("pi") ? "*3.14" : "*2.71");
            }
            else{
                insertChar(buttonText.equals("pi") ? "3.14" : "2.71");
            }
        }
        
        //implementing functionality
        if(buttonText.equals("mod")){
            insertChar("%");
        }
        if(buttonText.equals("C")){
            clear();
        }
        if(buttonText.equals("=")){
            evaluate();
            addCalculation();
        }
        if(buttonText.equals("del")){
            deleteLast();
        }
        if(buttonText.equals("ans")){
            setExpression(getAnswer());
        }
        if(buttonText.equals("hist")){
            openHistoryWindow();
        }
        if(buttonText.equals("+/-")){
            StringBuilder text = new StringBuilder(getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                index = getNumIndex(text, index) + 1;
                plusMinusHelper(text, index);
            }
            else if(text.charAt(index) == ')'){
                index = getParenIndex(text, index);
                plusMinusHelper(text, index);
            }
        }
        
        //these next functions follow similar logic
        //if the last character added to the expression was a number just calculate
        //if the last character was a ')' then calculate inside parenthesis
        if(buttonText.equals("ln")){
            StringBuilder text = new StringBuilder(getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                index = getNumIndex(text, index);
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.log(Double.parseDouble(num)));
        
                text.replace(index + 1, text.length(), calculated);
                setExpression(text.toString());
            }
            else if(text.charAt(index) == ')'){
                index = getParenIndex(text, index);
                
                String num = text.substring(index + 1, text.length() - 1);
                num = calcParenthesis(num);
                String calculated = Double.toString(Math.log(Double.parseDouble(num)));
        
                text.replace(index + 1, text.length() - 1, calculated);
                setExpression(text.toString());
            }
        }
        if(buttonText.equals("log")){
            StringBuilder text = new StringBuilder(getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                index = getNumIndex(text, index);
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.log10(Double.parseDouble(num))); 
                
                text.replace(index + 1, text.length(), calculated);
                setExpression(text.toString());
            }
            else if(text.charAt(index) == ')'){
                index = getParenIndex(text, index);
                
                String num = text.substring(index + 1, text.length() - 1);
                num = calcParenthesis(num);
                String calculated = Double.toString(Math.log10(Double.parseDouble(num)));
        
                text.replace(index + 1, text.length() - 1, calculated);
                setExpression(text.toString());
            }
        }
        if(buttonText.equals("x^2")){
            StringBuilder text = new StringBuilder(getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                index = getNumIndex(text, index);
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.pow(Double.parseDouble(num), 2));
                
                text.replace(index + 1, text.length(), calculated);
                setExpression(text.toString());
            }
            else if(text.charAt(index) == ')'){
                index = getParenIndex(text, index);
                
                String num = text.substring(index + 1, text.length() - 1);
                num = calcParenthesis(num);
                String calculated = Double.toString(Math.pow(Double.parseDouble(num), 2));
        
                text.replace(index + 1, text.length() - 1, calculated);
                setExpression(text.toString());
            }
        }
        if(buttonText.equals("x^10")){
            StringBuilder text = new StringBuilder(getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                index = getNumIndex(text, index);
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.pow(Double.parseDouble(num), 10));
                
                text.replace(index + 1, text.length(), calculated);
                setExpression(text.toString());
            }
            else if(text.charAt(index) == ')'){
                index = getParenIndex(text, index);
                
                String num = text.substring(index + 1, text.length() - 1);
                num = calcParenthesis(num);
                String calculated = Double.toString(Math.pow(Double.parseDouble(num), 10));
        
                text.replace(index + 1, text.length() - 1, calculated);
                setExpression(text.toString());
            }
        }
    }
}
