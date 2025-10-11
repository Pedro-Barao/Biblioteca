/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca;

import java.io.IOException;
import java.net.URL;            
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navigator 
{
    
    private Navigator(){};


    public static void goTo(Node sourceOnScene, String fxmlPath) 
    {
        
        try 
        {
            
            URL url = Navigator.class.getResource(fxmlPath);
            
            if (url == null) 
            {
                
                throw new IllegalArgumentException("FXML não encontrado: " + fxmlPath);
                
            }

            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) sourceOnScene.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } 
        
        catch (IOException ex) 
        {
            
            ex.printStackTrace(); 
            
        }
        
    }
    
}
