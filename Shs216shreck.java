package shs216shreck;

import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Shs216shreck {

    public static void main(String[] args) throws IOException {         
        
    	Connection con = null;
        try{
            Class.forName ("oracle.jdbc.driver.OracleDriver");
        }
        catch(ClassNotFoundException ex){
            System.out.println("Error: There is an internal error. JDBC driver was not found. "
                    + "Please include the driver and restart the interface.");
            System.exit(1);
        }
        Boolean notDone = true;
        while (notDone) {
	        try{
	        	Scanner in = new Scanner(System.in);
	        	String password;
	            System.out.print ("Enter password: ");
	            password = in.nextLine();
	            con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","shs216",password);
	            Statement s = con.createStatement();
	            notDone = false;
	        }catch(Exception e){
	          System.out.println ("Login Error: ");
	          System.out.println (e.getMessage());
	        }
        }
	        try{
	            Scanner scanner = new Scanner(System.in);
	            System.out.println("\n\n");
	            System.out.println("\t\t\tWELCOME TO SEPHORA\n");
	            
	            System.out.println("Please tell us which interface you would like to use.\n"
	                    + "Choose an option from the list below:");
	            options();
	            while(true){
	                boolean validOption = false;
	                while(!validOption){
	                    String choice;
	                    try{
	                        choice = scanner.nextLine().toLowerCase();
		                        switch(choice){
		                            case "customer":
		                                //opens Customer interface
		                                System.out.println("\nLoading Customer interface...\n");
		                                Customer c = new Customer(con, scanner);
		                                c.startInterface();
		                                validOption=false;
		                                break;
		                                
		                            case "storemanager":
		                                //opens Store Manager interface
		                                System.out.println("\nLoading Store Manager interface...\n");
		                                StoreManager s = new StoreManager(con,scanner);
		                                try {
		                                	s.startInterface();
		                                }catch (IOException e) {
		                                	e.printStackTrace();
		                                }
		                                validOption=false;
		                                break;
		                            
		                            case "marketer":
		                                //opens Marketer interface
		                                System.out.println("\nLoading Marketer interface...\n");
		                                Marketer m = new Marketer(con,scanner);
		                                try {
		                                	m.startInterface();
		                                }catch (IOException e) {
		                                	e.printStackTrace();
		                                }
		                                validOption=false;
		                                break;
		                                
		                            case "quit":
		                                //quits the program
		                                System.out.println("\nThank you for working at Sephora. Have a B-E-A-Utiful rest of the day!");
		                                validOption = true;
		                                con.close();
		                                scanner.close();
		                                try {
		                                    con.close();
		                                    System.exit(0);
		                                }catch (SQLException ex) {
		                                    System.out.println("Error. Exiting...");
		                                    System.exit(1);
		                                }
		                                break;
		                                
		                            default:
		                                System.out.println("Invalid interface. Enter a valid interface: ");
		                                break;   	
		                        }
	                    }catch(InputMismatchException ex){
	                    	validOption = false;
	                    	System.out.println("Invalid interface. InputMisMatchException caught.");
	                        scanner.next();
	                    }catch(IllegalStateException ex){
	                        scanner = new Scanner(System.in);
	                        System.out.println("Internal Error. Scanner closed.\nReinitializing...\n"
	                                + "Enter a valid interface:\n");
	                        validOption = false;
	                    }
	                }
	                validOption = true;
                    con.close();
                    scanner.close();
	            }
	        }catch(SQLException ex){
	        	System.out.println("Error: There is an internal error. We could not connect to "
                    + "Edgar1. Check your internet connection and try again.");
	        	System.exit(1);
            }
        }
    
        public static void options(){
            System.out.println("<customer> Customer");
            System.out.println("<storemanager> Store Manager");
            System.out.println("<marketer> Marketer");
            System.out.println("<quit> Quit the program");    
            
        }
}
            
            
            
            
            
            