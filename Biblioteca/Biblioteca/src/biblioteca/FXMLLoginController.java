/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package biblioteca;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class FXMLLoginController implements Initializable {
    
    
    @FXML private TextField nomeCampo;
    @FXML private PasswordField senhaCampo;
    @FXML private Button entrarBtn;
    @FXML private Label saida;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        entrarBtn.disableProperty().bind(
                nomeCampo.textProperty().isEmpty()
                    .or(senhaCampo.textProperty().isEmpty())
                
        );
        
        
        nomeCampo.setOnAction(this::entrar);
        senhaCampo.setOnAction(this::entrar);
        
    }    
    
    
    @FXML
    private void entrar(ActionEvent e) {
       
            
        String login = nomeCampo.getText();
        String verificador_senha = senhaCampo.getText();
        String nomes = "Teste";
        String senhas ="123";
        
        
        if(!nomes.toUpperCase().contains(login.toUpperCase()) || !senhas.contains(verificador_senha))
        {
            
            saida.setStyle("-fx-text-fill: #b00020");
            saida.setText("Usuário não encontrado ou Senha inválida");
            
        }
        
        else
        {
            
            saida.setStyle("-fx-text-fill: #32CD32");
            saida.setText("Usuário encontrado com Sucesso!");
            
            Navigator.goTo((Node) e.getSource(), "/biblioteca/FXMLDecisao_de_CRUD.fxml");
            
        }
        
        
    }
    
    
}
