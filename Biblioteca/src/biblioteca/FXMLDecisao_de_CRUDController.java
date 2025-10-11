/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package biblioteca;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FXMLDecisao_de_CRUDController implements Initializable {


    @FXML private Button livrosBtn;
    @FXML private Button sairBtn;


    @FXML private Node root;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
       

        if (sairBtn != null) 
        {
            
            sairBtn.setOnAction(this::sair);
            
        }
        
        if (livrosBtn != null) 
        {
            
            livrosBtn.setOnAction(this::abrirLivros);
            
        }
        
    }


    @FXML
    private void sair(ActionEvent e) 
    {
        
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLLogin.fxml");
        
    }

    @FXML
    private void abrirLivros(ActionEvent e) 
    {
        
        Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLLivro.fxml");
        
    }
    
}
