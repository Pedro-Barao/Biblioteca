/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package biblioteca;

public enum Status {
    RETIRADO,   
    ESTOQUE,      
    ATRASADO;  

    public static Status fromCodigo(String status) 
    {
        
        if (status == null) 
        {
            
            return null;
            
        }
        
        status = status.trim();
        
        switch (status) 
        {
            
            case "1": 
                
                return RETIRADO;
                
            case "2": 
                
                return ESTOQUE;
                
            case "3": 
                
                return ATRASADO;
                
            default:  
                
                return null;
            
        }
        
    }

    public String codigo() 
    {
        
        switch (this) 
        {
            
            case RETIRADO: 
                
                return "1";
                
            case ESTOQUE:  
                
                return "2";
                
            case ATRASADO: 
                
                return "3";
            
        }
        
        return "?";
        
    }
    
}
