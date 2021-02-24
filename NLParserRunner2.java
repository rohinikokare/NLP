package NLP;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;

public class NLParserRunner2 extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	NLParser parser = new NLParser();
	QueryRepresentation query = new QueryRepresentation();

	public NLParserRunner2() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblNlquery = new JLabel("NLQuery");
		GridBagConstraints gbc_lblNlquery = new GridBagConstraints();
		gbc_lblNlquery.insets = new Insets(0, 0, 5, 5);
		gbc_lblNlquery.gridx = 1;
		gbc_lblNlquery.gridy = 1;
		contentPane.add(lblNlquery, gbc_lblNlquery);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 5;
		gbc_textField.gridy = 1;
		contentPane.add(textField, gbc_textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		//textField_1.setText(query.generateSQL());
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 5;
		gbc_textField_1.gridy = 4;
		contentPane.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JButton btnTranslate = new JButton("Translate");
		GridBagConstraints gbc_btnTranslate = new GridBagConstraints();
		gbc_btnTranslate.insets = new Insets(0, 0, 5, 0);
		gbc_btnTranslate.gridx = 5;
		gbc_btnTranslate.gridy = 2;
		contentPane.add(btnTranslate, gbc_btnTranslate);
		btnTranslate.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			query = parser.translate(textField.getText());
			//System.out.println(query);
			String s = query.generateSQL();
			textField_1.setText(s);
			System.out.println(s);
			}
		});
		
		JLabel lblSqlquery = new JLabel("SQLQuery");
		GridBagConstraints gbc_lblSqlquery = new GridBagConstraints();
		gbc_lblSqlquery.insets = new Insets(0, 0, 5, 5);
		gbc_lblSqlquery.gridx = 1;
		gbc_lblSqlquery.gridy = 4;
		contentPane.add(lblSqlquery, gbc_lblSqlquery);
		
		
		
		JButton btnExecute = new JButton("Execute");
		GridBagConstraints gbc_btnExecute = new GridBagConstraints();
		gbc_btnExecute.gridx = 5;
		gbc_btnExecute.gridy = 5;
		contentPane.add(btnExecute, gbc_btnExecute);
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//results.loadData();
				LoadData();
				}

			private void LoadData() {
				// TODO Auto-generated method stub
				ArrayList columnNames = new ArrayList();
		        ArrayList data = new ArrayList();

		        //sql = textField.getText();

		        try{

		        Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/MySQL","root","root");
		        Statement stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery( textField_1.getText() );

		            ResultSetMetaData md = rs.getMetaData();
		            int columns = md.getColumnCount();

		            //  Get column names
		            for (int i = 1; i <= columns; i++)
		            {
		                columnNames.add( md.getColumnName(i) );
		            }
		            System.out.println(String.format("column count : %d",columns));
		            //  Get row data
		            while (rs.next())
		            {
		                ArrayList row = new ArrayList(columns);

		                for (int i = 1; i <= columns; i++)
		                {
		                    row.add( rs.getObject(i) );
		                }

		                data.add( row );
		                }
		            //System.out.println(data.add(row));
		}
		        catch (SQLException e)
		        {
		        	 System.out.println(e);
		        	// JOptionPane.showMessageDialog(null,"Error check syntax","Error" ,JOptionPane.ERROR_MESSAGE);
		        }

		        // Create Vectors and copy over elements from ArrayLists to them

		        Vector columnNamesVector = new Vector();
		        Vector dataVector = new Vector();

		        for (int i = 0; i < data.size(); i++)
		        {
		            ArrayList subArray = (ArrayList)data.get(i);
		            Vector subVector = new Vector();
		            for (int j = 0; j < subArray.size(); j++)
		            {
		                subVector.add(subArray.get(j));
		            }
		            dataVector.add(subVector);
		        }

		        for (int i = 0; i < columnNames.size(); i++ )
		            columnNamesVector.add(columnNames.get(i));

		        //  Create table with database data    
		       JTable table = new JTable(dataVector, columnNamesVector)
		        {
		            public Class getColumnClass(int column)
		            {
		                for (int row = 0; row < getRowCount(); row++)
		                {
		                    Object o = getValueAt(row, column);

		                    if (o != null)
		                    {
		                        return o.getClass();
		                    }
		                }
		                return Object.class;
		            }
		        };
		        JOptionPane.showMessageDialog(null, new JScrollPane(table));
		    }
			
			});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NLParserRunner2 frame = new NLParserRunner2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
