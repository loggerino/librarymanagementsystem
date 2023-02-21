/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package managebooksinfo;

import java.util.List;
import managebooks.librarybooks;

/**
 *
 * @author chyis
 */
public interface booksDAO {
    public void save(librarybooks Books);
    public void update(librarybooks Books);
    public void delete(librarybooks Books);
    public librarybooks get(int book_id);
    public List<librarybooks> list();
    
    
    
    
    
}
