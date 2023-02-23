/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package jframe;

import controller.StudentManager;
import controller.booksDAOimpl;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.Student;
import model.librarybooks;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author chyis
 */
public class issuebook extends javax.swing.JFrame {

    /**
     * Creates new form issuebook
     */
    public issuebook() {
        initComponents();
    }

    private librarybooks getBookDetails() {
        librarybooks books = new librarybooks();
        int bookID = Integer.parseInt(txt_bookid.getText());
        booksDAOimpl dao = new booksDAOimpl();
        books = dao.get(bookID);

        if (books != null) {
            lbl_bookid.setText(Integer.toString(books.getBook_id()));
            lbl_title.setText(books.getBook_name());
            lbl_author.setText(books.getAuthor());
            lbl_quantity.setText(Integer.toString(books.getQuantity()));
        } else {
            JOptionPane.showMessageDialog(this, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return books;
    }

    private Student getStudentDetails() {
        Student students = new Student();
        int studentID = Integer.parseInt(txt_studentid.getText());
        StudentManager dao = new StudentManager();
        students = dao.get(studentID);

        if (students != null) {
            lbl_studentid.setText(Integer.toString(students.getStudentID()));
            lbl_name.setText(students.getName());
            lbl_course.setText(students.getCourse());
            lbl_faculty.setText(students.getFaculty());
        } else {
            JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return students;
    }

    private DateFormat[] formats = new DateFormat[]{
        new SimpleDateFormat("yyyy-MM-dd")
    };

    private Date getIssueDate() throws ParseException {
        String issueDateStr = txt_issuedate.getText();
        Date issueDate = null;
        for (DateFormat format : formats) {
            try {
                issueDate = format.parse(issueDateStr);
                break;
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid dates in the format yyyy-MM-dd");
            }
        }
        if (issueDate == null) {
            throw new ParseException("Unparseable date: " + issueDateStr, 0);

        }
        return issueDate;
    }

    private Date getReturnDate() throws ParseException {
        String returnDateStr = txt_returndate.getText();
        Date returnDate = null;
        for (DateFormat format : formats) {
            try {
                returnDate = format.parse(returnDateStr);
                break;
            } catch (ParseException e) {
                // Do nothing, try next format
            }
        }
        if (returnDate == null) {
            throw new ParseException("Unparseable date: " + returnDateStr, 0);
        }
        return returnDate;
    }

    public int calculateDuration() {
        int durationInDays = 0;
        try {
            Date issueDate = getIssueDate();
            Date returnDate = getReturnDate();

            long durationInMillis = returnDate.getTime() - issueDate.getTime();
            durationInDays = (int) TimeUnit.MILLISECONDS.toDays(durationInMillis);

            lbl_duration.setText(Long.toString(durationInDays) + " days");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid dates in the format yyyy-MM-dd");
            e.printStackTrace();
        }
        return durationInDays;
    }

    public boolean issueBook() throws ParseException {
        boolean isIssued = false;
        int bookId = Integer.parseInt(txt_bookid.getText());
        int studentId = Integer.parseInt(txt_studentid.getText());

        Date uIssueDate = getIssueDate();
        Date uDueDate = getReturnDate();

        Long l1 = uIssueDate.getTime();
        long l2 = uDueDate.getTime();

        int duration = calculateDuration();

        java.sql.Date sIssueDate = new java.sql.Date(l1);
        java.sql.Date sDueDate = new java.sql.Date(l2);

        try {
            Connection connection = DBConnection.getConnection();
            String sql = "insert into issuebook(book_id,studentID,issue_date,due_date,duration,status) values(?,?,?,?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            pst.setDate(3, sIssueDate);
            pst.setDate(4, sDueDate);
            pst.setInt(5, duration);
            pst.setString(6, "pending");
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) {
                isIssued = true;
            } else {
                isIssued = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Other code for issuing the book
        return isIssued;
    }

    public void updateBookCount() {
        int bookID = Integer.parseInt(txt_bookid.getText());
        booksDAOimpl dao = new booksDAOimpl();
        librarybooks book = dao.get(bookID);
        if (book.getQuantity() > 0) {
            book.setQuantity(book.getQuantity() - 1);
            dao.update(book);
            lbl_quantity.setText(String.valueOf(book.getQuantity()));

        } else {
            JOptionPane.showMessageDialog(this, "Book is not available");
        }
    }

    public boolean isAlreadyIssued() {

        boolean isAlreadyIssued = false;
        int bookId = Integer.parseInt(txt_bookid.getText());
        int studentId = Integer.parseInt(txt_studentid.getText());

        try {
            Connection con = DBConnection.getConnection();
            String sql = "select * from issuebook where book_id = ? and studentID = ? and status = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            pst.setString(3, "pending");

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                isAlreadyIssued = true;
            } else {
                isAlreadyIssued = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAlreadyIssued;

    }

    public void displayReceipt(Student student, librarybooks book) throws ParseException {

        Date issueDate = getIssueDate();
        Date returnDate = getReturnDate();
        int duration = calculateDuration();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String issueDateStr = dateFormat.format(issueDate);
        String returnDateStr = dateFormat.format(returnDate);

        String message = "Book Issued:\n\n"
                + "Title: " + book.getBook_name() + "\n"
                + "Author: " + book.getAuthor() + "\n"
                + "Book ID: " + book.getBook_id() + "\n\n"
                + "Student Details:\n\n"
                + "Name: " + student.getName() + "\n"
                + "Student ID.: " + student.getStudentID() + "\n"
                + "Course: " + student.getCourse() + "\n"
                + "Faculty: " + student.getFaculty() + "\n\n"
                + "Issue Date: " + issueDateStr + "\n"
                + "Return Date: " + returnDateStr + "\n"
                + "Duration: " + duration + " days";
        JOptionPane.showMessageDialog(null, message, "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lbl_quantity = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbl_title = new javax.swing.JLabel();
        lbl_author = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lbl_bookid = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lbl_studentid = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lbl_name = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lbl_course = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lbl_faculty = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_studentid = new javax.swing.JTextField();
        txt_bookid = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_issuedate = new javax.swing.JTextField();
        txt_returndate = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        lbl_duration = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 153, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 255));
        jLabel7.setText("Book Details");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 150, 40));

        lbl_quantity.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_quantity.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 340, 160, 40));

        jLabel8.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 51, 255));
        jLabel8.setText("Author");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 290, 50, 40));

        jLabel6.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 255));
        jLabel6.setText("Quantity");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 340, 70, 40));

        jLabel10.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 51, 255));
        jLabel10.setText("Book Title");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, 80, 40));

        lbl_title.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_title.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_title, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, 160, 40));

        lbl_author.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_author.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_author, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 290, 160, 40));

        jLabel13.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 51, 255));
        jLabel13.setText("Book ID");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 190, 80, 40));

        lbl_bookid.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_bookid.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_bookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 160, 40));

        jLabel16.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 51, 255));
        jLabel16.setText("Student ID");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 190, 80, 40));

        lbl_studentid.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_studentid.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_studentid, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 190, 180, 40));

        jLabel18.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 51, 255));
        jLabel18.setText("Name");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 240, 50, 40));

        lbl_name.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_name.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 240, 180, 40));

        jLabel20.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 51, 255));
        jLabel20.setText("Course");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 290, 50, 40));

        lbl_course.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_course.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_course, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 290, 180, 40));

        jLabel22.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 51, 255));
        jLabel22.setText("Faculty");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 340, 60, 40));

        lbl_faculty.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_faculty.setForeground(new java.awt.Color(0, 51, 255));
        jPanel1.add(lbl_faculty, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 340, 180, 40));

        jPanel2.setBackground(new java.awt.Color(255, 51, 51));

        jLabel1.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Exit_26px_2.png"))); // NOI18N
        jLabel1.setText("BACK");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 50));

        jLabel17.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 51, 255));
        jLabel17.setText("Student Details");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 130, 150, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 760, 720));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 51, 255));
        jLabel19.setText("Issue Books");
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 150, 40));

        jLabel14.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 51, 255));
        jLabel14.setText("Book ID");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 80, 40));

        jLabel21.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 51, 255));
        jLabel21.setText("Student ID");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 80, 40));

        txt_studentid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_studentidFocusLost(evt);
            }
        });
        jPanel3.add(txt_studentid, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 240, 230, 40));

        txt_bookid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_bookidFocusLost(evt);
            }
        });
        txt_bookid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_bookidActionPerformed(evt);
            }
        });
        jPanel3.add(txt_bookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 190, 230, 40));

        jLabel15.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 51, 255));
        jLabel15.setText("Issue Date");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 290, 80, 40));

        txt_issuedate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_issuedateFocusLost(evt);
            }
        });
        txt_issuedate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_issuedateActionPerformed(evt);
            }
        });
        jPanel3.add(txt_issuedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 290, 230, 40));

        txt_returndate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_returndateFocusLost(evt);
            }
        });
        txt_returndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_returndateActionPerformed(evt);
            }
        });
        jPanel3.add(txt_returndate, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, 230, 40));

        jLabel23.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 51, 255));
        jLabel23.setText("Duration");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 400, 80, 40));

        lbl_duration.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        lbl_duration.setForeground(new java.awt.Color(0, 51, 255));
        jPanel3.add(lbl_duration, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 400, 180, 40));

        jButton1.setBackground(new java.awt.Color(0, 0, 255));
        jButton1.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Issue");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 450, -1, -1));

        jLabel24.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 51, 255));
        jLabel24.setText("Return Date");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 340, 80, 40));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 0, 520, 720));

        setSize(new java.awt.Dimension(1294, 757));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        home Homepage = new home();
        Homepage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void txt_bookidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_bookidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bookidActionPerformed

    private void txt_bookidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookidFocusLost
        // TODO add your handling code here:
        getBookDetails();
    }//GEN-LAST:event_txt_bookidFocusLost

    private void txt_studentidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_studentidFocusLost
        // TODO add your handling code here:
        getStudentDetails();
    }//GEN-LAST:event_txt_studentidFocusLost

    private void txt_issuedateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_issuedateFocusLost
        try {
            // TODO add your handling code here:
            getIssueDate();
        } catch (ParseException ex) {
            Logger.getLogger(issuebook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txt_issuedateFocusLost

    private void txt_issuedateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_issuedateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_issuedateActionPerformed

    private void txt_returndateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_returndateFocusLost
        try {
            // TODO add your handling code here:
            getReturnDate();
        } catch (ParseException ex) {
            Logger.getLogger(issuebook.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculateDuration();
    }//GEN-LAST:event_txt_returndateFocusLost

    private void txt_returndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_returndateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_returndateActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            if (lbl_quantity.getText().equals("0")) {
                JOptionPane.showMessageDialog(this, "Book is not available");
            } else {
                if (isAlreadyIssued() == false) {
                    Student student = getStudentDetails();
                    librarybooks books = getBookDetails();
                    if (books != null && student != null) {
                        if (issueBook() == true) {
                            JOptionPane.showMessageDialog(this, "Book issued");
                            updateBookCount();
                            displayReceipt(student, books);
                        } else {
                            JOptionPane.showMessageDialog(this, "Book issue failed");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book has already been issued to this student");
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(issuebook.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(issuebook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(issuebook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(issuebook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(issuebook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new issuebook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lbl_author;
    private javax.swing.JLabel lbl_bookid;
    private javax.swing.JLabel lbl_course;
    private javax.swing.JLabel lbl_duration;
    private javax.swing.JLabel lbl_faculty;
    private javax.swing.JLabel lbl_name;
    private javax.swing.JLabel lbl_quantity;
    private javax.swing.JLabel lbl_studentid;
    private javax.swing.JLabel lbl_title;
    private javax.swing.JTextField txt_bookid;
    private javax.swing.JTextField txt_issuedate;
    private javax.swing.JTextField txt_returndate;
    private javax.swing.JTextField txt_studentid;
    // End of variables declaration//GEN-END:variables
}
