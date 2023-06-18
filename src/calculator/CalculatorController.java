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
    
    //adds an operator to the expression
    public void insertOperator(String op){
        this.setExpression(this.getExpression() + " " + op + " ");
    }
    
    //opening parentheses needs special treatment
    public void insertParentheses(String paren){
        String exp = this.getExpression();
        int size = exp.length();
        
        if(exp.isEmpty()){
            setExpression(paren + " ");
        }
        else if(exp.charAt(size - 1) >= '0' && exp.charAt(size - 1) <= '9'){
            this.setExpression(this.getExpression() + " * " + paren + " ");
        }
        else{
            this.setExpression(this.getExpression() + paren + " ");
        }
    }
    
    //adds a number to the expression 
    public void insertNum(String num){
        this.setExpression(this.getExpression() + num);
    }
    
    //clear the expression
    public void clear(){
        this.setExpression("");
        this.setAnswer("");
    }
    
    //delete last inputted character
    public void deleteLast(){
        if(!this.getExpression().isEmpty()){
            StringBuilder text = new StringBuilder(this.getExpression());
            text.deleteCharAt(text.length() - 1);
            this.setExpression(text.toString());
        }
    }
    
    //evaluates expression after equals sign is pressed 
    public void evaluate(){
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String infix = this.getExpression();
        
        try {
            Object ans = engine.eval(infix);
            this.setAnswer(ans.toString());
        } 
        catch (ScriptException ex) {
            this.setAnswer("Error");
        }
    }
    
    //function for factorial which includes factorials for 
    public String fact(String n){
        double num = Double.parseDouble(n);
        double ans = num - Math.floor(num) + 1;
        
        for(;num>1; num-=1){
            ans *= num;
        }
        
        return Double.toString(ans);
    }
    
    //helper function for adjustments that are made when +/- button is pressed
    public StringBuilder plusMinusHelper(StringBuilder text, int index, char stop){
        for(;index>=0; index--){
            if(text.charAt(index) == stop){
                break;
            }
        }
        
        if(index < 0) index = 0;
        
        if(index == 0){
            text.reverse();
            text.append(" -");
            text.reverse();
        }
        else if(text.charAt(index - 1) == '+'){
            text.replace(index - 1, index + 1, "- ");
        }
        else if(text.charAt(index - 1) == '-'){ 
            text.replace(index - 1, index + 1, "+ ");
        }
        else if(text.charAt(index - 1) == '*' || text.charAt(index - 1) == '/'
                || text.charAt(index - 1) == '%'){ 
            text.replace(index, index + 1, " - ");
        }
        
        return text;
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
    
    public void addCalculation(){
        calculationHistory.add(this.getExpression() + " = " + this.getAnswer());
    }
    
    //handles all main functionality
    public void onMouseClick(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        String buttonText = button.getText();
        
        if(buttonText.charAt(0) >= '0' && buttonText.charAt(0) <= '9'
                || buttonText.equals(".")){
            insertNum(buttonText);
        }
        else if(buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("*")
                || buttonText.equals("/") || buttonText.equals(")")){
            insertOperator(buttonText);
        }
        else if(buttonText.equals("(")){
            insertParentheses(buttonText);
        }
        
        //these require slightly different steps
        if(buttonText.equals("pi")){
            insertNum("3.14");
        }
        if(buttonText.equals("e")){
            insertNum("2.71");
        }
        
        //implementing functionality
        if(buttonText.equals("mod")){
            insertOperator("%");
        }
        if(buttonText.equals("C")){
            this.clear();
        }
        if(buttonText.equals("=")){
            this.evaluate();
            this.addCalculation();
        }
        if(buttonText.equals("del")){
            this.deleteLast();
        }
        if(buttonText.equals("+/-")){
            StringBuilder text = new StringBuilder(this.getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '1' && text.charAt(index) <= '9'){
                this.setExpression(plusMinusHelper(text, index, ' ').toString());
            }
            else if(text.charAt(index - 1) == ')'){
                this.setExpression(plusMinusHelper(text, index - 1, '(').toString());
            }
        }
        if(buttonText.equals("n!")){
            StringBuilder text = new StringBuilder(this.getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                for(;index>=0; index--){
                    if(text.charAt(index) == ' '){
                        break;
                    }
                }
                
                String calculated = this.fact(text.substring(index + 1, text.length())); 
                text.replace(index + 1, text.length(), calculated);
                
                this.setExpression(text.toString());
            }
        }
        if(buttonText.equals("ln")){
            StringBuilder text = new StringBuilder(this.getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                for(;index>=0; index--){
                    if(text.charAt(index) == ' '){
                        break;
                    }
                }
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.log(Double.parseDouble(num))); 
                text.replace(index + 1, text.length(), calculated);
                
                this.setExpression(text.toString());
            }
        }
        if(buttonText.equals("log")){
            StringBuilder text = new StringBuilder(this.getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                for(;index>=0; index--){
                    if(text.charAt(index) == ' '){
                        break;
                    }
                }
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.log10(Double.parseDouble(num))); 
                text.replace(index + 1, text.length(), calculated);
                
                this.setExpression(text.toString());
            }
        }
        if(buttonText.equals("x^2")){
            StringBuilder text = new StringBuilder(this.getExpression());
            int index = text.length() - 1;
            
            if(text.charAt(index) >= '0' && text.charAt(index) <= '9'){
                for(;index>=0; index--){
                    if(text.charAt(index) == ' '){
                        break;
                    }
                }
                
                String num = text.substring(index + 1, text.length());
                String calculated = Double.toString(Math.pow(Double.parseDouble(num), 2)); 
                text.replace(index + 1, text.length(), calculated);
                
                this.setExpression(text.toString());
            }
        }
        if(buttonText.equals("ans")){
            this.setExpression(this.getAnswer());
        }
        if(buttonText.equals("hist")){
            this.openHistoryWindow();
        }
    }
}
