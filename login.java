package NLP;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTextField;
import javax.swing.JButton;


public class login extends JFrame  implements ActionListener {

	private static final Connection NULL = null;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//1. Connection
		 Connection conn = NULL;
		 String myUrl = "jdbc:mysql://localhost:3306/MySQL";
	      try{
	      conn = DriverManager.getConnection(myUrl, "root", "root");
	      if (conn != null) {
				System.out.println("You made it, take control your database now!");
			} else {
				System.out.println("Failed to make connection!");
			}
	      
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }		    

	      //Design
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login frame = new login();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
		public  void insertdata() throws SQLException
		{
			Connection conn = null;
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MySQL", "root", "root");
			String query = " insert into Login (Uname,passwd)"
			        + " values (?, ?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString (1, textField.getText());
		      	preparedStmt.setString (2, textField_1.getText());
		      // execute the preparedstatement
			 preparedStmt.execute();
			 conn.close();
		}
		
		
		/**
		 * Create the frame.
		 */
		
		public login() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 450, 426);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			GridBagLayout gbl_contentPane = new GridBagLayout();
			gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			contentPane.setLayout(gbl_contentPane);
			
			JLabel lblLogin = new JLabel("Login");
			GridBagConstraints gbc_lblLogin = new GridBagConstraints();
			gbc_lblLogin.insets = new Insets(0, 0, 5, 5);
			gbc_lblLogin.gridx = 6;
			gbc_lblLogin.gridy = 2;
			contentPane.add(lblLogin, gbc_lblLogin);
			
			JLabel lblUsername = new JLabel("Username");
			GridBagConstraints gbc_lblUsername = new GridBagConstraints();
			gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
			gbc_lblUsername.gridx = 5;
			gbc_lblUsername.gridy = 4;
			contentPane.add(lblUsername, gbc_lblUsername);
			
			textField = new JTextField();
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 5, 5);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 7;
			gbc_textField.gridy = 4;
			contentPane.add(textField, gbc_textField);
			textField.setColumns(10);
			
			JLabel lblPassword = new JLabel("Password");
			GridBagConstraints gbc_lblPassword = new GridBagConstraints();
			gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
			gbc_lblPassword.gridx = 5;
			gbc_lblPassword.gridy = 6;
			contentPane.add(lblPassword, gbc_lblPassword);
			
			textField_1 = new JPasswordField();
			GridBagConstraints gbc_textField_1 = new GridBagConstraints();
			gbc_textField_1.insets = new Insets(0, 0, 5, 5);
			gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_1.gridx = 7;
			gbc_textField_1.gridy = 6;
			contentPane.add(textField_1, gbc_textField_1);
			textField_1.setColumns(10);
			
			JButton btnNewButton = new JButton("Submit");
			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
			gbc_btnNewButton.gridx = 6;
			gbc_btnNewButton.gridy = 8;
			contentPane.add(btnNewButton, gbc_btnNewButton);
			btnNewButton.addActionListener((ActionListener) this);
		}

			
			@SuppressWarnings("unused")
			public  void actionPerformed(ActionEvent e)
		    {
			    String databaseUsername = "";
			    String databasePassword = "";
			    Connection conn = null;
			    try{
	            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MySQL", "root", "root");
			    // Check Username and Password
			     String name = textField.getText();
			    String password = textField_1.getText();

			            // Create SQL Query
			    Statement stmt = conn.createStatement();
			    String SQL = "SELECT * FROM login WHERE Uname='" + name + "' && passwd='" + password+ "'";
			    ResultSet rs = stmt.executeQuery(SQL);
			    
			            // Check Username and Password
			    while (rs.next()) {
			        databaseUsername = rs.getString("Uname");
			        databasePassword = rs.getString("passwd");
			    }

			    if (name.equals(databaseUsername) && password.equals(databasePassword)) 
			    {
			        JOptionPane.showMessageDialog(null, "Successful Login!");
			        NLParserRunner2 NLPR = new NLParserRunner2();  
			        NLPR.setVisible(true);
			    } 
			    else 
			    {
			    	/*if(name != databaseUsername)
				    {
				    	try {
							insertdata();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				    	JOptionPane.showMessageDialog(null, "You have successfully logged in!");
				    }*/
				    	//else
				    	{
				    		JOptionPane.showMessageDialog(null, "Incorrect Password!");
				    		textField.setText("");
				    		textField_1.setText("");
				    	}
			    	
			    }
			   
			    }
			    catch (Exception e1)
		        {
		            JOptionPane.showMessageDialog(null, e1);
		        }
				
}
}
		
		  