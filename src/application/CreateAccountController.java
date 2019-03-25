package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.derby.drda.NetworkServerControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CreateAccountController {

	 @FXML
	 private TextField tf_username;

	 @FXML
	 private PasswordField tf_password;

	 @FXML
	 private TextField tf_email;

	 @FXML
	 private Button btn_serverStart;

	 @FXML
	 private Button btn_serverShut;

	 @FXML
	 private Button btn_tableCreate;

	 @FXML
	 private Button btn_getIP;

	 @FXML
	 private Button btn_exit;

	 @FXML
	 private Button btn_create4;

    @FXML
    void create(ActionEvent event) {
    	
    	String user = tf_username.getText();
    	String pass = tf_password.getText();
    	String email = tf_email.getText();
    	
    	try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/srmsDB;create=true");
			PreparedStatement ps = connection.prepareStatement("SELECT ID FROM ACCOUNT.USERS ORDER BY ID DESC ");
			ResultSet rs = ps.executeQuery();
			int id = 1;
			//check for password strength
			boolean checkUpperCase = !pass.equals(pass.toLowerCase());
			boolean checkLowerCase = !pass.equals(pass.toUpperCase());
			boolean checkhasNumbers = pass.matches(".*\\d.*");
			int len = pass.length();

			if(checkUpperCase == true && checkLowerCase == true && checkhasNumbers == true && len > 8 && len < 20) {
				if(rs.next()) {
					id = Integer.parseInt(rs.getString("ID")) + 1;
				}
			
			ps = connection.prepareStatement("INSERT INTO ACCOUNT.USERS (ID,USERNAME,PASSWORD,EMAIL) VALUES (?, ?, ?, ?)");
			ps.setString(1, Integer.toString(id));
			ps.setString(2, user);
			ps.setString(3,encrypt(pass));
			ps.setString(4, email);
			
			ps.executeUpdate();
			
			Parent root = (Parent) FXMLLoader.load(getClass().getResource("alert.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage primaryStage = new Stage();
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.showAndWait();
			
			}else {
				//replace with error page
				System.out.println("n");
			}	
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void exit(ActionEvent event) {
    	Stage stage = (Stage)btn_exit.getScene().getWindow();
    	stage.close();
    }
    
   
    	
    private static int[]  keys = {216, 1512, 184, 586, 15,49,432,23,41,534,564,646,1476,245,235,523,52342,123425,645743,356};
    public static String encrypt(String pass) {
    	String newPass = "";
    	char ch;
    	int key = 0;
    	for(int i = 0; i < pass.length(); i++) {
    		if(key >= keys.length - 1) {
    			key = 0;
    		}
    		ch = pass.charAt(i);
    		ch += keys[key];
    		newPass += ch;
    	}
    	return newPass;
    }
    
    public static String decrypt(String pass) {
    	String newPass = "";
    	char ch;
    	int key = 0;
    	for(int i = 0; i < pass.length(); i++) {
    		if(key >= keys.length - 1) {
    			key = 0;
    		}
    		ch = pass.charAt(i);
    		ch -= keys[key];
    		newPass += ch;
    	}
    	return newPass;		
    }
    
    @FXML
    void createTable(ActionEvent event) {try {
		Class.forName("org.apache.derby.jdbc.ClientDriver");
		Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/srmsDB;create=true");
		PreparedStatement ps = null;
		ps = connection.prepareStatement("SELECT  * FROM    SYS.SYSSCHEMAS WHERE   SCHEMANAME = 'ACCOUNT'");
		ResultSet rs = ps.executeQuery();
		
		if(rs.next() == false) {
		ps = connection.prepareStatement("create SCHEMA account");
		ps.executeUpdate();
		}
		
		
		ps = connection.prepareStatement("SELECT  * FROM    SYS.SYSTABLES WHERE   TABLENAME = 'ORDERS'");
		rs = ps.executeQuery();
		if(!rs.next()) {
		ps = connection.prepareStatement("    	CREATE TABLE orders (\r\n" + 
				"    			OID INTEGER NOT NULL,\r\n" + 
				"    			STUDENT_NUMBER VARCHAR(20) NOT NULL,\r\n" + 
				"    			LAST_NAME VARCHAR(30) NOT NULL,\r\n" + 
				"    			FIRST_NAME VARCHAR(30) NOT NULL,\r\n" + 
				"    			ROOM_RENTED VARCHAR(10) NOT NULL,\r\n" + 
				"    			DATE_RENTED VARCHAR(20) NOT NULL,\r\n" + 
				"    			TIME_RENTED VARCHAR(4) NOT NULL,\r\n" + 
				"    			CANCELLED VARCHAR(5) NOT NULL,\r\n" + 
				"    			PAYMENT INTEGER NOT NULL,\r\n" + 
				"    			PRIMARY KEY (OID)\r\n" + 
				"    		)");
		ps.executeUpdate();
		}
		
		ps = connection.prepareStatement("SELECT  * FROM    SYS.SYSTABLES WHERE   TABLENAME = 'USERS'");
		rs = ps.executeQuery();
		if(!rs.next()) {
		ps = connection.prepareStatement("    	create TABLE account.users(\r\n" + 
				"    			id integer not NULL,\r\n" + 
				"    			username VARCHAR(120) not NULL,\r\n" + 
				"    			password VARCHAR(300) not NULL,\r\n" + 
				"    			email VARCHAR(120) not NULL,\r\n" + 
				"    			PRIMARY KEY (id)\r\n" + 
				"    		)");
		ps.executeUpdate();
		}
		
		ps = connection.prepareStatement("SELECT  * FROM    SYS.SYSTABLES WHERE   TABLENAME = 'SCHEDULED_RENT'");
		rs = ps.executeQuery();
		if(!rs.next()) {
		ps = connection.prepareStatement("create table scheduled_rent (\r\n" + 
				"	DAY_RENTED VARCHAR(20) NOT NULL,\r\n" + 
				"	NAME VARCHAR(30) NOT NULL,\r\n" + 
				"	ROOM_RENTED VARCHAR(10) NOT NULL,\r\n" + 
				"	TIME_RENTED VARCHAR(10) NOT NULL\r\n" + 
				")");
		ps.executeUpdate();
		}
//		ps = connection.prepareStatement("INSERT INTO ACCOUNT.USERS (ID,USERNAME,PASSWORD,EMAIL) VALUES (?, ?, ?, ?)");
//		
//		ps.executeUpdate();
		
		rs.close();
		ps.close();
		connection.close();
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	}





    }
    
    @FXML
    void getIP(ActionEvent event) {
    	try {
			System.out.println(InetAddress.getLocalHost().getClass());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void serverShut(ActionEvent event) {
    	try {
			NetworkServerControl server = new NetworkServerControl();
			server.shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    NetworkServerControl server;
    
    @FXML
    void serverStart(ActionEvent event) {
    	try {
			server = new NetworkServerControl();
			server.start(null);
			System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
