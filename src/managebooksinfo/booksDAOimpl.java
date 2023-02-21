/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package managebooksinfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import managebooks.librarybooks;
import java.sql.ResultSet;


/**
 *
 * @author chyis
 */
public class booksDAOimpl implements booksDAO{

    @Override
    public void save(librarybooks book_details) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "insert into book_details(book_name,author,quantity) values (?,?,?)";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setString(1, book_details.getBook_name());
            prepStatement.setString(2, book_details.getAuthor());
            prepStatement.setInt(3, book_details.getQuantity());
            int updatedRowCount = prepStatement.executeUpdate();
            
            if(updatedRowCount > 0){
                JOptionPane.showMessageDialog(null, "Successful Record");
            } else{
                JOptionPane.showMessageDialog(null, "Record Failed");
            }
            
        } catch (Exception e) {
        }
    }

    @Override
    public void update(librarybooks Books) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "UPDATE book_details SET book_name=?,author=?,quantity=? WHERE book_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, Books.getBook_name());
            ps.setString(2, Books.getAuthor());
            ps.setInt(3, Books.getQuantity());
            ps.setInt(4, Books.getBook_id());
            ps.executeUpdate();
 
        
            JOptionPane.showMessageDialog(null, "Updated!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error");
        }
    }

    @Override
    public void delete(librarybooks Books) {
        try {
          
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "delete from book_details  WHERE book_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);  
            ps.setInt(1, Books.getBook_id());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Deleted!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error");
        }
    }
    

    @Override
    public librarybooks get(int book_id) {
        librarybooks books = new librarybooks();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "SELECT * FROM book_details WHERE book_id=?";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setInt(1, book_id);
            ResultSet rs = prepStatement.executeQuery();
            if(rs.next()){
                
                books.setBook_id(rs.getInt("book_id"));
                books.setBook_name(rs.getString("book_name"));
                books.setAuthor(rs.getString("author"));
                books.setQuantity(rs.getInt("quantity"));
 
            } else {
                JOptionPane.showMessageDialog(null, "No book with ID " + book_id + " found.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error");
        }
        return books;
    }

    @Override
    public List<librarybooks> list() {
        
    List<librarybooks> list = new ArrayList<librarybooks>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "SELECT * FROM book_details ";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            ResultSet rs = prepStatement.executeQuery();
            
            
            
            while(rs.next()){
                librarybooks books = new librarybooks();
                books.setBook_id(rs.getInt("book_id"));
                books.setBook_name(rs.getString("book_name"));
                books.setAuthor(rs.getString("author"));
                books.setQuantity(rs.getInt("quantity"));
 
                list.add(books);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error");
        }
        return list;
  
    }
    
}
