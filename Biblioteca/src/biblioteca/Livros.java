/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca;


public class Livros {

    public Livros(String isbn, String nome, String ano, String autor, String genero, Status status) {
        
        this.isbn = isbn;
        this.nome = nome;
        this.ano = ano;
        this.autor = autor;
        this.genero = genero;
        this.retirada_entrega = status;
    }
    
    
    private String isbn;
    private String nome;
    private String ano;
    private String autor;
    private String genero;
    private Status retirada_entrega;

    
    public String getISBN()
    {
        
        return isbn;
        
    }
    
    public void setISBN(String isbn)
    {
     
        this.isbn = isbn;
        
    }
    
    
    public String getNome()
    {
        
        return nome;
        
    }
    
    public void setNome(String nome)
    {
     
        this.nome = nome;
        
    }
    
    
    public String getAno()
    {
        
        return ano;
        
    }
    
    public void setAno(String ano)
    {
     
        this.ano = ano;
        
    }
    
    
    public String getAutor()
    {
        
        return autor;
        
    }
    
    public void setAutor(String autor)
    {
     
        this.autor = autor;
        
    }
    
    
    public String getGenero()
    {
        
        return genero;
        
    }
    
    public void setGenero(String genero)
    {
     
        this.genero = genero;
        
    }
    
    
    public Status getRetirada_Entrega()
    {
        
        return retirada_entrega;
        
    }
    
    public void setRetirada_Entrega(Status retirada_entrega)
    {
        
        this.retirada_entrega = retirada_entrega;
        
    }

    

    public String resumo() 
    {
        
        String legivel;
        if (retirada_entrega == null) 
        {
            
            legivel = "?";
            
        }
        
        else 
        {
            
            switch (retirada_entrega) 
            {
                
                case RETIRADO: 
                    
                    legivel = "RETIRADO(1)"; 
                    break;
                    
                case ESTOQUE:  
                    
                    legivel = "ESTOQUE(2)";  
                    break;
                    
                case ATRASADO: 
                    
                    legivel = "ATRASADO(3)"; 
                    break;
                    
                default:       
                    
                    legivel = "?";
                    
            }
            
        }
        
        return String.format("%s | %s | %s | %s | %s | %s",
                isbn, nome, ano, autor, genero, legivel);
        
    }
    
}
