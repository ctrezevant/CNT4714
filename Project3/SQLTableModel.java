/*
	Name: Charlton Trezevant
	Course: CNT 4714 Fall 2020
	Assignment title: Project 23 - Two-Tier Client-Server Application Development With MySQL and JDBC
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.AbstractTableModel;

public class SQLTableModel extends AbstractTableModel {
	private Connection conn;
	private Statement cmd;
	private ResultSet results;
	private ResultSetMetaData resMeta;
	private int numRows;
	private boolean connected = false;

	public SQLTableModel(String query, Connection c) throws SQLException, ClassNotFoundException, IOException {
		try {
			this.conn = c;
			if(this.conn.isClosed())
				return;
				
			connected = true;
			cmd = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			if(query.toLowerCase().contains("select")){
				try {
					setQuery(query);
				} catch (IllegalStateException | SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			} else {
				try {
					setUpdate(query);
				} catch (IllegalStateException | SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
			
			
		} catch (IllegalStateException | SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public Class getColumnClass(int c) throws IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
		
		try {
			String name = resMeta.getColumnClassName(c + 1);
			return Class.forName(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Object.class;
	}

	public int getColumnCount() throws IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
			
		try {
			return resMeta.getColumnCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}


	public String getColumnName(int c) throws IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
			
		try {
			return resMeta.getColumnName(c + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return "";
	}

	public int getRowCount() throws IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
			
		return numRows;
	}
	
	public Object getValueAt(int r, int c) throws IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
			
		try {
			results.next();
			results.absolute(r + 1);
			return results.getObject(c + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return "";
	}
	
	public void setQuery(String q) throws SQLException, IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
		
		this.results = cmd.executeQuery(q);
		this.resMeta = results.getMetaData();
		results.last();
		this.numRows = results.getRow();
		fireTableStructureChanged();
	}
	
	public void setUpdate(String q) throws SQLException, IllegalStateException {
		if(!connected)
			throw new IllegalStateException("Database not connected.");
		
		cmd.executeUpdate(q);
		fireTableStructureChanged();
	}
}
