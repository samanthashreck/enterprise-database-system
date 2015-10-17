package shs216shreck;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.*;
import java.sql.*;

public class Marketer{
	
	Connection con;
    Scanner in;
	
    public Marketer (Connection con, Scanner in){
    	
        this.con=con;
        this.in=in;
        
    }
	
    /*public static void main(String[] args) throws IOException {         
        
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
	        Scanner scanner = new Scanner(System.in);
			System.out.println("\n\n");
			System.out.println("\t\t\tWELCOME TO SEPHORA\n");
			System.out.println("\nLoading Marketer interface...\n");
            Marketer m = new Marketer(con,scanner);
			m.startInterface();
    }*/
    
    public void startInterface() throws IOException{
        try {
            Statement s = con.createStatement(); 
            ResultSet result;
        	Scanner in = new Scanner(System.in);        	
        	boolean flag = false;
        	while(!flag){
        		System.out.println("\nSelect an option from the menu:\n");
                options();
        		String option = in.nextLine().toLowerCase();
                switch(option){
	                case "most used":
	                	System.out.println("\nTop Most Used Vendors");
	                	System.out.println("");
	                	System.out.println("--------------------------");
	                    rankVendor();
	                    System.out.println("--------------------------");
	                    flag = false;
	                    break;
	                    
	                case "stores":
	                	System.out.println("\nRanking of Sephora Stores");
	                	System.out.println("");
	                    rankStore();
	                    flag = false;
	                    break;
	                
	                case "customers":
	                	System.out.println("\nRanking of Sephora Customers"); 
	                	System.out.println("");
	                    rankCustomer();
	                    flag = false;
	                    break;
	                
	                case "products":
	                	System.out.println("\nRanking of Sephora Products");
	                	System.out.println("");
	                    rankProduct();
	                    flag = false;
	                    break;
	                    
	                case "leave":
	                	System.out.println("\nThank you for running reports at Sephora. Have a B-E-A-Utiful rest of the day!");
                        flag = true;
                        con.close();
                        in.close();
                        try {
                            con.close();
                            System.exit(0);
                        }catch (SQLException ex) {
                            System.out.println("\nError. Exiting...");
                            System.exit(1);
                        }
                        break;
	                
	                default:
						System.out.println("\nInvalid request. Enter a valid request: ");
						break; 
                }

        	}
        }catch(InputMismatchException ex){
            System.out.println("\nInvalid request. Enter a valid request:");
            in.next();
        }catch(Exception ex){
        	ex.printStackTrace();
        	
        }
    }
    
    private void rankVendor() {
    	try{    		
            Statement s=con.createStatement();
            ResultSet result;
            result =s.executeQuery("select Vendor_ID, Most_Used from (select Vendor_ID, rank() over (order by sum(Vendor_ID) desc) as Most_Used from supplies group by Vendor_ID) where Most_Used <= 10");
            System.out.printf("%-10s %15s\n", "Vendor ID", "Most Used");
            System.out.printf("%-10s %15s\n", "---------", "---------");
            while (result.next()){
                  int vendorID = result.getInt(1);
                  int mostUsed = result.getInt(2);
                  System.out.printf("%-10s %15s\n", vendorID, mostUsed);
            }
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
	}

	private void rankProduct() {
		try{
			Statement s=con.createStatement();
            ResultSet result;
            System.out.println("Ranking of Products by Most Desired");
            System.out.println("----------------------------------------------");
            result = s.executeQuery("select Product_ID, sum(quantity) as Total_Purchases, dense_rank() over (order by sum(quantity) desc) as Ranking from Cart group by Product_ID order by Ranking");
            System.out.printf("%-10s %17s %17s\n", "Product ID", "Total Purchases", "Ranking");
            System.out.printf("%-10s %17s %17s\n", "----------", "---------------", "-------");
            while (result.next()){
                String productID = result.getString(1);
                int totalPurchares = result.getInt(2);
                int ranking = result.getInt(3);
                System.out.printf("%-10s %17s %17s\n", productID, totalPurchares, ranking);
            }
            System.out.println("----------------------------------------------");	
            System.out.println("\nRanking of Products by Least Desired");
            System.out.println("----------------------------------------------");
            result = s.executeQuery("select Product_ID, sum(quantity) as Total_Purchases, dense_rank() over (order by sum(quantity) asc) as Ranking from Cart group by Product_ID order by Ranking");
            System.out.printf("%-10s %17s %17s\n", "Product ID", "Total Purchases", "Ranking");
            System.out.printf("%-10s %17s %17s\n", "----------", "---------------", "-------");
            while (result.next()){
                String productID = result.getString(1);
                int totalPurchares = result.getInt(2);
                int ranking = result.getInt(3);
                System.out.printf("%-10s %17s %17s\n", productID, totalPurchares, ranking);
            }
            System.out.println("----------------------------------------------"); 
            
            
            
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
		
	}

	private void rankCustomer() {
		try{    		
            Statement s=con.createStatement();
            ResultSet result;
            System.out.println("Ranking of Customers by Most Purchases");
            System.out.println("-----------------------------------------------");
            result = s.executeQuery("select Customer_ID, sum(quantity) as Total_Purchases, rank() over (order by sum(quantity) desc) as Ranking from Cart group by Customer_ID order by Ranking");
            System.out.printf("%-10s %17s %17s\n", "Customer ID", "Total Purchases", "Ranking");
            System.out.printf("%-10s %17s %17s\n", "-----------", "---------------", "-------");
            while (result.next()){
                int customerID = result.getInt(1);
                int mostPurchares = result.getInt(2);
                int ranking = result.getInt(3);
                System.out.printf("%-10s %17s %17s\n", customerID, mostPurchares, ranking);
            }
            System.out.println("-----------------------------------------------");
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}		
	}

	private void rankStore() {
		try{    		
            Statement s=con.createStatement();
            ResultSet result;
            System.out.println("Ranking of Stores by Most Purchases");
            System.out.println("----------------------------------------------");
            result = s.executeQuery("select Store_ID, sum(quantity) as Total_Purchases, rank() over (order by sum(quantity) desc) as Ranking from Cart group by Store_ID order by Ranking");
            System.out.printf("%-10s %17s %17s\n", "Store ID", "Total Purchases", "Ranking");
            System.out.printf("%-10s %17s %17s\n", "--------", "---------------", "-------");
            while (result.next()){
                String storeID = result.getString(1);
                int mostPurchares = result.getInt(2);
                int ranking = result.getInt(3);
                System.out.printf("%-10s %17s %17s\n", storeID, mostPurchares, ranking);
            }
            System.out.println("----------------------------------------------");
            System.out.println("");
            System.out.println("Ranking of Stores by Most Revenue");
            System.out.println("----------------------------------------------");
            result = s.executeQuery("select Store_ID, sum(subtotal) as Revenue, rank() over (order by sum(subtotal) desc) as Ranking from Transaction group by Store_ID order by Ranking");
            System.out.printf("%-10s %17s %17s\n", "Store ID", "Total Revenue", "Ranking");
            System.out.printf("%-10s %17s %17s\n", "--------", "-------------", "-------");
            while (result.next()){
            	String storeID = result.getString(1);
                int mostRevenue = result.getInt(2);
                int ranking = result.getInt(3);
                System.out.printf("%-10s %17s %17s\n", storeID, mostRevenue, ranking);
            }
            System.out.println("----------------------------------------------");
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
		
	}

	public static void options(){
        System.out.println("<most used> Top Most Used Vendors");
        System.out.println("<stores> Ranking of Sephora Stores");
        System.out.println("<customers> Ranking of Sephora Customers");
        System.out.println("<products> Ranking of Sephora Products");
        System.out.println("<leave> Leave Sephora");  
    }
}