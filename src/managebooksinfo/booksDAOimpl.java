/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package managebooksinfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import javax.swing.JOptionPane;
import managebooks.librarybooks;

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
    }

    @Override
    public void delete(librarybooks Books) {
    }

    @Override
    public librarybooks get(int book_id) {
        return null;
    }

    @Override
    public List<librarybooks> list() {
        return null;
    }
    
}
