package shs216shreck;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.lang.Object;
import java.util.Date;



public class StoreManager{
    
    Connection con;
    Scanner in;
    
    public StoreManager(Connection con, Scanner in){
    	
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
			System.out.println("\nLoading Store Manager interface...\n");
			StoreManager s = new StoreManager(con,scanner);
			s.startInterface();
    }*/
    
    public void startInterface() throws IOException{
	        try {
	            Statement s = con.createStatement();            
	            System.out.print("Enter your Sephora employee ID number: ");
	            String storemanagerID = in.nextLine();                
	            ResultSet result = s.executeQuery("select * from Employee where Employee_ID = '" + storemanagerID + "'");
	            if(!result.next()){
	            	System.out.println("The employee ID entered does not exist in Sephora's records.\n"
	                        + "Please try again");
	            	startInterface();
	            }
	            else{
		            storemanagerID = result.getString(1);
		            System.out.print("\nWelcome " + result.getString("ename") + ". "
		                    + "\nYou are currently located in Sephora's store with Store ID number " + result.getString("Store_ID") + "."
		                    + "\nYour current phone number is " + result.getString("phone") + ". \n");
		            String storeID = result.getString("Store_ID");
		            boolean flag = false;
			    	while(!flag){
			            System.out.println("\nSelect an option from the menu:\n");
			            options("menu");
				    	String choice = in.nextLine().toLowerCase();
				    	while(!choice.equalsIgnoreCase("view inventory")&&!choice.equalsIgnoreCase("view orders")&&!choice.equalsIgnoreCase("edit price")&&!choice.equalsIgnoreCase("edit min")&&!choice.equalsIgnoreCase("edit phone")&&!choice.equalsIgnoreCase("leaving")){
			           		 //System.out.println("Incorrect input: Please enter an option from the menu");
				    		 //options("menu");
			           		 choice = in.nextLine().toLowerCase();
				    	}
				    	switch(choice){
			        		case "view inventory":
			        			System.out.println("--------------------------------------------------------------------------");
			        			viewInventory(storeID);
			        			System.out.println("--------------------------------------------------------------------------");
			        			System.out.println("");
			        			
			        			break;
			        			
			        		case "view orders":
			        			viewOrderHistory(storeID);
			        			System.out.println("");
			        			//options("menu");
			        			break;	
			        		
			        		case "edit price":
			        			editProductPrice(storeID);
			        			System.out.println("");
			        			//options("menu");
			        			break;
			        				        		
			        		case "edit min":
			        			editInventoryMin(storeID);
			        			System.out.println("");
			        			//options("menu");
			        			break;
			        			
			        		case "edit phone":
				            	result = s.executeQuery("select phone from Employee where Employee_ID = " + storemanagerID);
				            	result.next();
				            	System.out.print("\nYour current phone number is " + result.getString("phone") + " \n");
	                            editPhone(storemanagerID);
	                            result = s.executeQuery("select phone from Employee where Employee_ID =" + storemanagerID);
				            	result.next();
				            	System.out.print("\nYour new phone number is " + result.getString("phone") + " \n");
				            	System.out.println("");
				            	break; 
				            	
			        		case "leaving":
			        			  System.out.println("\nThank you for working at Sephora. Have a B-E-A-Utiful rest of the day!");
	                                flag = true;
	                                con.close();
	                                in.close();
	                                try {
	                                    con.close();
	                                    System.exit(0);
	                                }catch (SQLException ex) {
	                                    System.out.println("Error. Exiting...");
	                                    System.exit(1);
	                                }
	                                break;
			    	}
	            }
	        
	        }
	        }catch (SQLException e) {
	        	e.printStackTrace();
	    	}
    }
    
    void editProductPrice(String storeID){
    	try{
    		ResultSet result;
    		Statement s = con.createStatement();
    		boolean flag = false;
    		while(!flag){
    			System.out.println("\nProduct Price Menu\nBelow is a list of current products on shelf.");
    			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
    			viewProducts(storeID);
    			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
    			System.out.println("");
    			System.out.println("Would you like to increase or decrease the price of a Product?\n\n<increase> Increase\n<decrease> Decrease\n<exit> Exit");
    			String choice;
    			choice = in.nextLine();
    			while(!choice.equalsIgnoreCase("increase")&&!choice.equalsIgnoreCase("decrease")&&!choice.equalsIgnoreCase("exit")){
    				System.out.println("Sorry, I didn't catch that. Please type again.");
    				choice =in.nextLine();
    			}
    			if(choice.equalsIgnoreCase("increase")){
    				System.out.print("Enter the Product ID you wish to increase to price of: ");
    				String productID = in.nextLine();
    				result = s.executeQuery("select distinct Product_ID from Inventory natural join Product where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"'");
    				if (!result.next() && !isNum(productID)){
    	            	System.out.println("Invalid input. Please enter a valid Product ID: ");
    	            	productID = in.nextLine();
    				}
    				else {
    					productID = result.getString(1);
	    				result = s.executeQuery("select Item_Price from Inventory where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
	    				result.next();
	    				double price = result.getDouble("Item_Price");
	    				System.out.println("The original price of the item with Product ID " + productID + " is " + price +".");
	    				System.out.println("");
	    				System.out.println("Enter a new price for the item selected: ");
	    				double newINCPrice = in.nextDouble();
	    				while(newINCPrice <= price){
	    					System.out.print("Invalid input. Please enter a valid price: ");
	    					newINCPrice = in.nextDouble();
	    				}
	    				System.out.println("\nThe new price of the item with Product ID " + productID + " is " + newINCPrice +".");
	    				result = s.executeQuery("update Inventory set Item_Price = '"+ newINCPrice +"' where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
    				}
    			}
    			if(choice.equalsIgnoreCase("decrease")){
    				System.out.print("Enter the Product ID you wish to decrease to price of: ");
    				String productID = in.nextLine();
    				result = s.executeQuery("select distinct Product_ID from Inventory natural join Product where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"'");
    				if (!result.next() && !isNum(productID)){
    	            	System.out.println("Invalid input. Please enter a valid Product ID: ");
    	            	productID = in.nextLine();
    				}
    				else {
    					productID = result.getString(1);
	    				result = s.executeQuery("select Item_Price from Inventory where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
	    				result.next();
	    				double price = result.getDouble("Item_Price");
	    				System.out.println("The original price of the item with Product ID " + productID + " is " + price +".");
	    				System.out.println("");
	    				System.out.println("Enter a new price for the item selected: ");
	    				double newDECPrice = in.nextDouble();
	    				while(newDECPrice >= price){
	    					System.out.print("Invalid input. Please enter a valid price: ");
	    					newDECPrice = in.nextDouble();
	    				}
	    				System.out.println("\nThe new price of the item with Product ID " + productID + " is " + newDECPrice +".");
	    				result = s.executeQuery("update Inventory set Item_Price = '"+ newDECPrice +"' where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
	    				}
    				}
    			if(choice.equalsIgnoreCase("exit")){
    				System.out.println("Returning to Store Manager Menu....");
    				break;
    			}
    		}
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Please try again.");
    		ex.printStackTrace();
		}
    		
    }
    
    void editInventoryMin(String storeID){
    	try{
    		ResultSet result;
    		Statement s = con.createStatement();
    		boolean flag = false;
    		while(!flag){
    			System.out.println("\nMinimum Level Decription Menu\nBelow is a list of current products on shelf.");
    			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
    			viewProducts(storeID);
    			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
    			System.out.println("");
    			System.out.println("Would you like to edit the minimum level of a product?\n\n<yes> Yes\n<no> No\n");
    			String choice;
    			choice = in.nextLine();
    			while(!choice.equalsIgnoreCase("yes")&&!choice.equalsIgnoreCase("no")&&!choice.equalsIgnoreCase("exit")){
    				System.out.println("Sorry, I didn't catch that. Please type again.");
    				choice =in.nextLine();
    			}
    			if(choice.equalsIgnoreCase("yes")){
    				System.out.print("Enter the Product ID you wish to edit to minimum level of: ");
    				String productID = in.nextLine();
    				result = s.executeQuery("select distinct Product_ID from Inventory natural join Product where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"'");
    				if (!result.next() && !isNum(productID)){
    	            	System.out.println("Invalid input. Please enter a valid Product ID: ");
    	            	productID = in.nextLine();
    				}
    				else {
    					productID = result.getString(1);
	    				result = s.executeQuery("select Min_Level from Inventory where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
	    				result.next();
	    				int minLevel = result.getInt("Min_Level");
	    				System.out.println("The original minimum level of the item with Product ID " + productID + " is " + minLevel +".");
	    				System.out.println("");
	    				System.out.println("Enter a new minimum level for the item selected: ");
	    				int newMinLevel = in.nextInt();
	    				while(newMinLevel != minLevel && newMinLevel != 0){
	    					System.out.print("Invalid input. Please enter a valid minimum level: ");
	    					newMinLevel = in.nextInt();
	    				}
	    				System.out.println("\nThe new minimum level of the item with Product ID " + productID + " is " + newMinLevel +".");
	    				result = s.executeQuery("update Inventory set Min_Level = '"+ newMinLevel +"' where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
    					}
    				}
    			if(choice.equalsIgnoreCase("no")){
    				System.out.println("Returning to Store Manager Menu....");
    				break;
    			}
    		}
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Please try again.");
    		ex.printStackTrace();
		}
    }
    
    void viewInventory(String storeID){
    	try{
    		Statement s=con.createStatement();
		      ResultSet result;
		      result =s.executeQuery("select Product_ID, Item_Price, Items_In_Stock, Min_Level from Inventory where Store_ID = '"+storeID+"'");
		      System.out.printf("%-10s %15s %20s %25s\n", "Product ID", "Item Price", "In Stock", "Min Level");
		      System.out.printf("%-10s %15s %20s %25s\n",  "---------","----------", " --------", "---------");
		    while (result.next()){
	            String productID = result.getString(1);
	            double itemPrice = result.getDouble(2);
	            int inStock = result.getInt(3);
	            int minLevel = result.getInt(4);
	            System.out.printf("%-10s %15s %20s %25s\n", productID, itemPrice, inStock, minLevel);
	        }
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Please try again");
    		ex.printStackTrace();
		}
    }
    
    void viewOrderHistory(String storeID){
    	//boolean flag = false;
		//while(!flag){
	    	try{
	        	Statement s=con.createStatement();
	        	ResultSet result;
	        	result =s.executeQuery("select Order_ID, Vendor_ID, Product_ID, Date_Ordered, Date_Supplied, Reorder_Amount, cost from Order_History where Store_ID = '"+storeID+"'");
	        	System.out.println("");
    			System.out.println("--------------------------------------------------------------------------------------------------------------------------");
	    		System.out.printf("%-10s %10s %15s %17s %19s %21s %24s\n", "Order ID", "Vendor ID", "Product ID", "Date Ordered", "Date Supplied", "Reorder Amount", "Cost");
	    		System.out.printf("%-10s %10s %15s %17s %19s %21s %24s\n", "--------", "---------", "----------", "------------", "--------------", "--------------", "----");
	    		while (result.next()){
	    	    	int orderID = result.getInt(1);
	    	        String vendorID = result.getString(2);
	    	        String productID = result.getString(3);
	    	        Date dateOrdered = result.getDate(4);
	    	        Date dateSupplied = result.getDate(5);
	    	        int amount = result.getInt(6);
	    	        double cost = result.getDouble(7);
	    	        System.out.printf("%-10s %10s %15s %17s %19s %21s %24s\n", orderID, vendorID, productID, dateOrdered, dateSupplied, amount, cost);
	    	    }
    			System.out.println("--------------------------------------------------------------------------------------------------------------------------");
	    	    System.out.println("\nWould you like to check on the shipment to see if the reorder arrived with the expected date?\n\n<yes> Yes\n<no> No");
	    	    String choice;
				choice = in.nextLine();
				while(!choice.equalsIgnoreCase("yes")&&!choice.equalsIgnoreCase("no")){
					System.out.println("Invalid input. Enter a valid input: ");
					choice =in.nextLine();
				}
				if(choice.equalsIgnoreCase("yes")){
					System.out.print("Enter the Order ID: ");
					int orderID = in.nextInt();
					result = s.executeQuery("select distinct Order_ID from Order_History where Store_ID = '"+ storeID +"'");
					if (!result.next()){
		            	System.out.println("Invalid order ID. Please enter a valid Order ID: ");
		            	orderID = in.nextInt();
					}
					else {
						orderID = result.getInt(1);
						result = s.executeQuery("select distinct Product_ID from Order_History where Order_ID = "+ orderID);
						result.next();
						String productID = result.getString(1);
						result = s.executeQuery("select Date_Supplied from Order_History where Order_ID = "+ orderID);
						result.next();
						Date dateSupplied = result.getDate(1);
						result = s.executeQuery("select sysdate from dual");
						result.next();
						Date currentDate = result.getDate("sysdate");
						result =s.executeQuery("select Items_In_Stock from Inventory where Store_ID = '"+storeID+"' and Product_ID = '"+productID+"'");
						result.next();
						int inStock = result.getInt(1);
						result =s.executeQuery("select Min_Level from Inventory where Store_ID = '"+storeID+"' and Product_ID = '"+productID+"'");
						result.next();
						int minLevel = result.getInt(1);
						if (isSameDay(dateSupplied,currentDate)){
							if(inStock <= minLevel){
								result =s.executeQuery("select Reorder_Amount from Order_History where Order_ID = "+orderID+" and Store_ID = '"+storeID+"' and Product_ID = '"+productID+"'");
								result.next();
								int reorderAmount = result.getInt(1);
								int updatedStock = inStock + reorderAmount;
			    				result = s.executeQuery("update Inventory set Items_In_Stock = "+ updatedStock +" where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
			    				System.out.println("The shipment has arrived. The product has been restocked.");
			    				System.out.println("You can review the inventory to check that it updated with the reorder.");		   
							}else{
								System.out.println("The shipment has arrived. The product have already been restocked.");
			    				System.out.println("You can review the inventory to check that it updated with the reorder.");
							}
						}else{
							System.out.println("The shipment hasn't arrived yet. Check again soon.");
							System.out.println("The expected date of the shipment is "+ dateSupplied+".");
						}
						
					}
				}
				if(choice.equalsIgnoreCase("no")){
					System.out.println("Returning to Store Manager Menu....");
					//break;
				}
	        }catch(SQLException ex){
	    	    	System.out.println("Internal Database Error. Please try again");
	    	    	ex.printStackTrace();
	        }
		
    }

    public static boolean isSameDay(Date date1, Date date2) {
    	if (date1 == null || date2 == null) {
    		throw new IllegalArgumentException("The date must not be null");
    	}
    	Calendar cal1 = Calendar.getInstance();
    	cal1.setTime(date1);
    	Calendar cal2 = Calendar.getInstance();
    	cal2.setTime(date2);
    	return isSameDay(cal1, cal2);
       }
    
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
    	if (cal1 == null || cal2 == null) {
    		throw new IllegalArgumentException("The date must not be null");
    	}
    	return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
    			cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
    	       cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    
    void viewProducts(String storeID){
    	try{
    		Statement s=con.createStatement();
    		ResultSet result;
		      result = s.executeQuery("select distinct Product_ID, Brand_Name, product_size, product_descrip, Item_Price, Items_In_Stock, Min_Level from Inventory natural join Product where Store_ID = '"+ storeID +"'");
		      System.out.printf("%-10s %15s %20s %23s %24s %25s %26s\n", "Product ID", "Brand Name", "Product Size", "Description", "Price", "In Stock", "Minimum Level");
		      System.out.printf("%-10s %15s %20s %23s %24s %25s %26s\n",  "---------","-------------", " ------------", "-------------", "-------", "--------", "------------");
		      while (result.next()){
	            String productID = result.getString(1);
	            String brandName = result.getString(2);
	            String productSize = result.getString(3);
	            String productDescrip = result.getString(4);
	            double price = result.getDouble(5);
	            int inStock = result.getInt(6);
	            int minLevel = result.getInt(7);
	            System.out.printf("%-10s %15s %20s %23s %24s %25s %26s\n", productID, brandName, productSize, productDescrip, price, inStock, minLevel);
	        } 
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Please try again");
    		ex.printStackTrace();
		}
    }
    
    void editPhone(String storemanagerID){
        try{
        	Statement s = con.createStatement();
        	ResultSet result;
            System.out.println("Enter your new phone number (XXX)XXX-XXXX: ");
            String phoneNumber = in.nextLine();
            if(phoneNumber.length()!=13){
                throw new Exception();
            }
            else{
                result = s.executeQuery("update Employee set phone = '"+ phoneNumber +"' where Employee_ID = '" + storemanagerID + "'");
            }
        }
         catch(Exception ex){
             System.out.println("Invalid input. Try again");
             editPhone(storemanagerID); 
         }
    }
    
    public boolean isNum(String s){
    	try { 
            Double.parseDouble(s); 
            return true;
        } catch(NumberFormatException e) { 
            return false; 
        }
        //return true;
    }
    
    public static void options(String token){
        switch(token){
        	case "menu":
        		System.out.println("Store Manager Main Menu");
                System.out.println("<view inventory> View Inventory");
                System.out.println("<view orders> View Order History");
                System.out.println("<edit price> Edit Prices of Products");
                System.out.println("<edit min> Edit Minimum Levels in Inventory");
                System.out.println("<edit phone> Edit Phone");
                System.out.println("<leaving> Leaving Store");
                break;
        }
    }
}	
			    	
			    	
			    	
			    	
			    	
			    	
			    	
			    	