/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Student;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author chyis
 */
public class StudentManager {
    public void save(Student students){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "insert into student(name,course,faculty) values (?,?,?)";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setString(1, students.getName());
            prepStatement.setString(2, students.getCourse());
            prepStatement.setString(3, students.getFaculty());
            int updatedRowCount = prepStatement.executeUpdate();
            
            if(updatedRowCount > 0){
                JOptionPane.showMessageDialog(null, "Successful Record");
            } else{
                JOptionPane.showMessageDialog(null, "Record Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void delete(Student students){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "delete from student WHERE studentID=?";
            PreparedStatement ps = connection.prepareStatement(sql);  
            ps.setInt(1, students.getStudentID());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Deleted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void update(Student students){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "UPDATE student SET name=?,course=?,faculty=? WHERE studentID=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, students.getName());
            ps.setString(2, students.getCourse());
            ps.setString(3, students.getFaculty());
            ps.setInt(4, students.getStudentID());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Updated!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Update Failed");
        }
    }
    
    public Student get(int studentID){
        Student students = new Student();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "SELECT * FROM student WHERE studentID=?";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setInt(1, studentID);
            ResultSet rs = prepStatement.executeQuery();
            if(rs.next()){
                
                students.setStudentID(rs.getInt("studentID"));
                students.setName(rs.getString("name"));
                students.setCourse(rs.getString("course"));
                students.setFaculty(rs.getString("faculty"));
 
            } else {
                JOptionPane.showMessageDialog(null, "No student with ID " + studentID + " found.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
    
    public List<Student> list(){
        
        List<Student> list = new ArrayList<Student>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms","root","");
            String sql = "SELECT * FROM student ";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            ResultSet rs = prepStatement.executeQuery();
            
            while(rs.next()){
                Student students = new Student();
                students.setStudentID(rs.getInt("studentID"));
                students.setName(rs.getString("name"));
                students.setCourse(rs.getString("course"));
                students.setFaculty(rs.getString("faculty"));
 
                list.add(students);
            }
        } catch (Exception e) {
        }
        return list;
    }
}
