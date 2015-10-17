package shs216shreck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Customer {
    
	Connection con;
    Scanner in;
    String token = "";

    public Customer (){
    	
    }
    
    public Customer(Connection con, Scanner in){
        
    	this.con = con;
        this.in = in;

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
	        try{
	            Scanner scanner = new Scanner(System.in);
	            System.out.println("\n\n");
	            System.out.println("\t\t\tWELCOME TO SEPHORA\n");
	            System.out.println("\nLoading Customer interface...\n");
                Customer c = new Customer(con, scanner);
                c.startInterface();
	        
	        }catch(SQLException ex){
	        	System.out.println("Error: There is an internal error. We could not connect to "
	                    + "Edgar1. Check your internet connection and try again.");
		        	System.exit(1);
	        }
    }*/
	            
    public void startInterface() throws SQLException, IOException{
    	try{
	    	System.out.print("Welcome to Sephora! Are you purchasing from our online store ");
			System.out.print("or from one of our store fronts?\n");
			System.out.println("<online> Online");
			System.out.println("<physical> Physical");
			System.out.println("<leaving> Leaving Store");  
			
			boolean flag = false;
			while(!flag){
			    String storeType = in.next().toLowerCase();
			    switch(storeType){
			        case "online":
			        	System.out.println("");
			            onlineStore();
			            flag = true;
			            break;
	
			        case "physical":
			        	System.out.println("");
			            physicalStore();
			            flag = true;
			            break;    
	
			        case "leaving":
	        			  System.out.println("\nThank you for entering Sephora. Have a B-E-A-Utiful rest of the day!");
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
                        System.out.println("\nInvalid store. Enter a valid store: ");
                        break;
			        }
			}
    	}catch(InputMismatchException ex){
        	System.out.println("\nInvalid store. Enter a valid store: ");
            in.next();
        }catch (Exception e) {
            System.err.println("\nInvalid store.");
        }
    }

	void physicalStore() throws Exception {
		Statement s;
		s = con.createStatement();
		ResultSet result;
		boolean flag = false;
		while (!flag){
			try{
				result = s.executeQuery("select max(Customer_ID) from Customer");
	            result.next();
	            int custID = result.getInt("max(Customer_ID)") + 1;
	            result = s.executeQuery("insert into Customer values ("+ custID +")");
				System.out.println("Please choose the Sephora store you are currently shopping at.");
				System.out.println("Here are a list of Sephora locations and Store IDs..\n");
				result = s.executeQuery("select Store_ID, address from Store where address is NOT NULL");
				System.out.println("---------------------------------------------------------");
				System.out.printf("%-10s %45s\n", "Store ID", "Addresses");
				System.out.printf("%-10s %45s\n", "------- ", "----------------------------------------------");
				result.next();
	            do {
	            	String store_ID = result.getString(1);
	                String address = result.getString(2);
	                System.out.printf("%-10s %35s\n", store_ID, address);
	            } while (result.next());
	            System.out.println("---------------------------------------------------------");
	               System.out.println("\nSelect the Store ID you are currently shopping at:"); 
	               String choice = in.nextLine().toLowerCase();
	               while(!isNum(choice)){
	            	   //System.out.println("Invalid store ID. Please enter a valid Store ID:");
	            	   choice = in.nextLine();
	               }
	               result = s.executeQuery("select Store_ID from Store where Store_ID = '"+choice+"'");
	               while(!result.next()){
	            	   System.out.println("Invalid store ID. Please enter a valid Store ID:");
	            	   choice = in.nextLine();
	                   while(!isNum(choice)){
	                	   System.out.println("Invalid store ID. Please enter a valid Store ID:");
	                	   choice =in.nextLine();
	                   }
	               }
	                   result = s.executeQuery("select address from Store where Store_ID = '"+choice+"'"); 
	                   result.next();
	                   String location = result.getString(1);
	                   System.out.println("\nWelcome to Sephora! You specified you are currently shopping");
	                   System.out.println("at "+ location+ ".");
	                   //s.close();
	                   //flag = true;
	                   token = "customer";
	                   String storeID = choice;
	                   result = s.executeQuery("insert into ShopsAt values ("+ custID +", '"+storeID+"')");
	                   options(token);
	                   
	                   try{
	                	   flag = false;
	                	   while(!flag){
	                           String option = in.nextLine().toLowerCase();
	                           switch(option){
	                            case "view products":
	                                System.out.println("\nOpening Inventory....");                                
	                                viewProducts(custID, storeID);
	                                flag = false;
	                                options(token);
	                                break;
	                            
	                            case "open cart":
	                            	result = s.executeQuery("select Cart_ID from Cart where Customer_ID = "+ custID +" and Store_ID = '"+ storeID +"'");
	   	                    	 	result.next();
	                            	int cartID = result.getInt("Cart_ID");
	   	                    	 	if(cartID >= 0) {
	   	                    	 		System.out.println("\nOpening Shopping Cart....");
	   	                    	 		openCart(cartID, custID, storeID);
	   	                    	 	}	
	   	                    	 	flag = false;
	                    	 		options(token);
	                                break;

	                            case "view transactions":
	                                System.out.println("\nOpening Transactions....");
	                                viewTransactions(custID, storeID);
	                                flag = false;
	                                options(token);
	                                break;
	                                
	                            /*default:
	                                System.out.println("\nInvalid request. Enter a valid request: ");
	                                break;*/
	        			            
	                           }	                       
	                	   }
	                   }catch(InputMismatchException ex){
                      		System.out.println("\nInvalid request. Enter a valid request: ");
                      		in.next();
                      }
			}catch (SQLException e){
                e.printStackTrace();
         	   flag = true; 
			}
		}
	}
	
	void onlineStore() throws Exception {
		try{	
			int custID = -1;
			String storeID = "10000";
			System.out.println("Welcome to Sephora! You are currently shopping");
			System.out.println("on our online store with Store ID, "+ storeID +".");
			System.out.print("\n");
			System.out.println("To purchase any items, you must be a part of our loyalty program.");
			System.out.println("Are you a Beauty Insider or a new member?");
		    System.out.println("");
		    System.out.println("<beautyinsider> Beauty Insider");
		    System.out.println("<new> New Member"); 
		    boolean flag = false;
			while (!flag){	        
	        	String custType = in.nextLine().toLowerCase();
		        switch(custType){
			        case "beautyinsider":
			        	beautyInsider(custID, storeID);
			            flag = true;
			            break;
	
			        case "new":
			             newMember();
			             flag = false;
			             break;
			        
/*			        default:
	                    System.out.println("\nInvalid request. Enter a valid request: ");
	                    break; */  
               }		        
           }
		}catch(InputMismatchException ex){
          		System.out.println("\nInvalid request. InputMisMatchException caught.");
          		in.next();
		}
	}

	void openCart(int cartID, int custID, String storeID){
		try{
			boolean flag = false;
			while(!flag){
				 Statement s=con.createStatement();
			     ResultSet result;
			     token = "cart";
			     options(token);
	             String option = in.nextLine().toLowerCase(); 
	                 switch(option){
	                     case "view cart":
	                         System.out.println("\nView Shopping Cart....");
	                         System.out.println("-----------------------------------------------");
	                         viewCart(cartID, custID, storeID);
	                         System.out.println("-----------------------------------------------");
	                         flag = false;
	                         break;
	                         
	                     case "edit cart":
	                         System.out.println("\nEdit Shopping Cart....");  
	                         editCart(cartID, custID, storeID);
	                         flag = false;
	                         break;
	
	                     case "exit":
	                    	 token = "customer";
	                         flag = true;
	                         System.out.println("\nReturning to Customer Menu...\n");
	                         break;	                     

						default:
							System.out.println("\nInvalid request. Enter a valid request: ");
							break;
	                 }
							
			}
		}catch(InputMismatchException ex){
			System.out.println("\nInvalid input. Enter a valid input:");
			in.next();
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
	}
	
	void viewCart(int cartID, int custID, String storeID) {
		try{
    		Statement s=con.createStatement();
		      ResultSet result;
		      result = s.executeQuery("select distinct Cart.Product_ID, quantity, total_price from Cart, Inventory where Cart_ID = "+ cartID +" and Cart.Store_ID = '"+ storeID +"' and total_price = quantity*Item_Price");
		      System.out.printf("%-10s %15s %20s\n", "Product ID", "Quantity", "Total Price");
		      System.out.printf("%-10s %15s %20s\n", "----------", "--------", "-----------");
	        while (result.next()) {
	            String productID = result.getString(1);
	            int quantity = result.getInt(2);
	            double totalPrice = result.getDouble(3);
	            System.out.printf("%-10s %15s %20s\n", productID, quantity, totalPrice);
	        }
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
    }

	void editCart(int cartID, int custID, String storeID) {
		try{
    		ResultSet result;
    		Statement s = con.createStatement();
    		boolean flag = false;
    		while(!flag){
    			System.out.println("\nEdit Shopping Cart Menu\nBelow is a list of current products in your cart.");
    			System.out.println("-----------------------------------------------");
    			viewCart(cartID, custID, storeID);
    			System.out.println("-----------------------------------------------");
    			System.out.println("");
    			System.out.println("Would you like to adjust the quantity to an exisiting product or delete a product?\n\n<adjust> Adjust Quantity\n<delete> Delete Product\n<exit> Return to Shopping Cart Menu");
    			String choice = in.nextLine().toLowerCase(); 	
    			while(!choice.equalsIgnoreCase("adjust")&&!choice.equalsIgnoreCase("delete")&&!choice.equalsIgnoreCase("exit")){
    				choice = in.nextLine().toLowerCase();
    			}
    			if(choice.equalsIgnoreCase("adjust")){
    				System.out.print("Enter the Product ID: ");
    				String productID = in.nextLine();
    				result = s.executeQuery("select Product_ID from Cart where Product_ID = '"+ productID +"' and Cart_ID = "+ cartID);
    				if (!result.next() && !isNum(productID)){
    				    System.out.println("Invalid product ID. Please enter a valid Product ID: ");
    				    productID = in.nextLine();
    				}else {
    				    productID = result.getString(1);
    				    result = s.executeQuery("select Items_In_Stock from Inventory where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
    				    result.next();
    				    int inStock = result.getInt("Items_In_Stock");
    				    result = s.executeQuery("select Item_Price from Inventory where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
    				    result.next();
    				    double itemPrice = result.getDouble("Item_Price");
    				    checkInventory(inStock, storeID, productID);
    				    result = s.executeQuery("select quantity from Cart where Product_ID = '"+productID+"' and Cart_ID = "+cartID);
    				    result.next();
    				    int quantity = result.getInt("quantity");
    				    System.out.println("The original quantity of the item with Product ID " + productID + " is " + quantity +".");
    				    System.out.println("");
    				    System.out.println("Enter a new quantity for the item selected: ");
    				    int newQuantity = in.nextInt();
    				    if(newQuantity == quantity && newQuantity == 0 && newQuantity > inStock){
    				      System.out.print("Invalid quantity. Please enter a valid quantity: ");
    				      newQuantity = in.nextInt();
    				    }else{
    				      System.out.println("\nThe new quantity of the item with Product ID " + productID + " is " + newQuantity +".");
    				      result = s.executeQuery("delete from Cart where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"' and quantity = "+quantity);
    				      double totalPrice = newQuantity*itemPrice;
    				      result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+productID+"', "+newQuantity+", "+totalPrice+")");
    				      //result = s.executeQuery("update Cart set quantity = "+ newQuantity +" where Product_ID = '"+productID+"' and Cart_ID = "+cartID);   				      
    				      int quantityDiff = newQuantity - quantity; 
    				      int updateInStock = inStock - Math.abs(quantityDiff);
    				      result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"'");
    				      checkInventory(updateInStock, storeID, productID);
    				    }
    				}
    			}
    			if(choice.equalsIgnoreCase("delete")){
    				System.out.print("Enter the Product ID you wish to delete: ");
    				String productID = in.nextLine();
    				result = s.executeQuery("select Product_ID from Cart where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"'");
    				if (!result.next() && !isNum(productID)){
    	            	System.out.println("Invalid product ID. Please enter a valid Product ID: ");
    	            	productID = in.nextLine();
    				}
    				else {
    					productID = result.getString(1);    					
        				result = s.executeQuery("select quantity from Cart where Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"'");
        				result.next();
        				int quantity = result.getInt("quantity");
        				result = s.executeQuery("select Items_In_Stock from Inventory where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");
	    				result.next();
	    				int inStock = result.getInt("Items_In_Stock");
        				int updateInStock = inStock + quantity;
	    				result = s.executeQuery("update Inventory set Items_In_Stock = "+updateInStock+" where Product_ID = '"+productID+"' and Store_ID = '"+storeID+"'");	  				
	    				checkInventory(updateInStock, storeID, productID);
	    				result = s.executeQuery("delete from Cart where Customer_ID = "+custID+" and Product_ID = '"+ productID +"' and Store_ID = '"+ storeID +"' and quantity = "+quantity);
    				}
    			}
    			if(choice.equalsIgnoreCase("exit")){
    				System.out.println("Returning to Shopping Cart Menu....");
    				break;
    			}
    			if(!choice.equalsIgnoreCase(choice)){
    				System.out.println("Invalid request. Enter a valid request: ");
    				choice =in.nextLine();
    			}
    		}
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
	} 
	
	void checkInventory(int inStock, String storeID, String productID) { 
		try {
			ResultSet result;
			Statement s = con.createStatement();
			result = s.executeQuery("select Min_Level from Inventory where Store_ID = '"+storeID+"'");
			result.next();
			int minLevel = result.getInt("Min_Level");
			if (inStock == 0){
	           	System.out.println("\nOUT OF STOCK\n");
	           	reOrderProduct(inStock, storeID, productID);
	           }
			else if (inStock <= minLevel){
	           	//System.out.println("\nNeed to reorder product.\n");
	           	reOrderProduct(inStock, storeID, productID);
	          }
			else {
				//System.out.println("\nNo need to reorder product.\n");
			}
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
        }
	}

	void reOrderProduct(int inStock, String storeID, String productID) {
		try {
			ResultSet result;
    		Statement s = con.createStatement();
    		result = s.executeQuery("select Vendor_ID from (select Vendor_ID from Vendor order by dbms_random.value) where rownum = 1");
    		result.next();
    		int vendorID = result.getInt("Vendor_ID");
    		result = s.executeQuery("select Item_Price from Inventory where Product_ID = '"+ productID +"'");
    		result.next();
    		double itemPrice = result.getInt("Item_Price");
    		int reorderAmount = 50;
    		result = s.executeQuery("insert into supplies (Vendor_ID, Store_ID, Reorder_Amount, Cost_Per_Item) values ("+vendorID+", '"+storeID+"', "+reorderAmount+", "+itemPrice+")");
    		orderProduct(inStock, storeID, productID, vendorID);
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
        }
	}
	
	void orderProduct(int inStock, String storeID, String productID, int vendorID) {
		try {
			ResultSet result;
    		Statement s = con.createStatement();
    		result = s.executeQuery("select Product_ID from Order_History where Store_ID = '"+ storeID +"' and Product_ID = '"+ productID +"'");
    		if(result.next()){
    			String productIDOrdered = result.getString("Product_ID"); 
	    		if (productID == productIDOrdered){
	    			//System.out.println("\nProduct is already being reordering.\n");
	    		}else{
		    		result = s.executeQuery("select max(Order_ID)+1 from Order_History");
		    		result.next();
		    		int orderID = result.getInt("max(Order_ID)+1");
		    		result = s.executeQuery("select Cost_Per_Item from supplies where Store_ID = '"+ storeID +"'");
		    		result.next();
		    		double costPer = result.getInt("Cost_Per_Item");
		    		result = s.executeQuery("select Reorder_Amount from supplies where Store_ID = '"+ storeID +"'");
		    		result.next();
		    		int reorderAmount = result.getInt("Reorder_Amount");
		    		double costTotal = reorderAmount*costPer;
		    		result = s.executeQuery("insert into Order_History (Order_ID, Vendor_ID, Store_ID, Product_ID, Date_Ordered, Date_Supplied, Reorder_Amount, cost) values ("+orderID+", "+vendorID+", '"+storeID+"', '"+productID+"', sysdate, sysdate+7, "+reorderAmount+", "+costTotal+")");
	    		}
    		}else if(!result.next()){
	    		result = s.executeQuery("select max(Order_ID)+1 from Order_History");
	    		result.next();
	    		int orderID = result.getInt("max(Order_ID)+1");
	    		result = s.executeQuery("select Cost_Per_Item from supplies where Store_ID = '"+ storeID +"'");
	    		result.next();
	    		double costPer = result.getInt("Cost_Per_Item");
	    		result = s.executeQuery("select Reorder_Amount from supplies where Store_ID = '"+ storeID +"'");
	    		result.next();
	    		int reorderAmount = result.getInt("Reorder_Amount");
	    		double costTotal = reorderAmount*costPer;
	    		result = s.executeQuery("insert into Order_History (Order_ID, Vendor_ID, Store_ID, Product_ID, Date_Ordered, Date_Supplied, Reorder_Amount, cost) values ("+orderID+", "+vendorID+", '"+storeID+"', '"+productID+"', sysdate, sysdate+7, "+reorderAmount+", "+costTotal+")");		
    		}
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
        }
	}

	void viewTransactions(int custID, String storeID) throws Exception{
		try {
			boolean flag = false;
        	while(!flag){       	 
				int creditCardNum = 0;
	            token = "transactions";
	    		options(token);
	            ResultSet result;
	    		Statement s = con.createStatement();
	    		result = s.executeQuery("select Cart_ID from Cart where Customer_ID = " + custID + " and Store_ID = '" + storeID +"'");
	            result.next();
	    		int cartID = result.getInt("Cart_ID");
	            String option = in.nextLine().toLowerCase();
	                switch(option){
	                    case "balance":
	                    	System.out.println("\nDisplaying Subtotal...."); 
	                        viewBalance(cartID);
	                        flag = false;
	                        break;
	                        
	                    case "checkout":
	                    	System.out.println("\nProceed to Checkout...."); 
	                        checkOut(cartID, storeID);
	                        flag = true;
	                        break;
	                        
	                    case "exit":
	                        System.out.println("\nReturning to Customer Menu....");
	                        flag = true;
	                        break;
	                    
	                    default:
							System.out.println("\nInvalid request. Enter a valid request: ");
							break;    	                        
                    }
        	}
		}catch(InputMismatchException ex){
                System.out.println("\nInvalid request. Enter a valid request:");
                in.next();
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
        		ex.printStackTrace();
		}
	}
	
	void viewBalance(int cartID) {
		try{
    		Statement s=con.createStatement();
		      ResultSet result;
		      result =s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
		      result.next();
		      double balance = result.getDouble("sum(total_price)");
		      System.out.println("Balance of your Shopping Cart: "+ balance +"\n");
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
	}

	void checkOut(int cartID, String storeID) throws Exception {
		try {  
            ResultSet result;
    		Statement s = con.createStatement();
            boolean flag = false;
            while(!flag){
            	if (storeID.equals("10000")){ //online store
            		System.out.println("Re-Enter your Beauty Insider Customer ID: ");
            		int custID = in.nextInt();
            		result = s.executeQuery("select Customer_ID from BeautyInsider where Customer_ID = " + custID);
            		result.next();
            		custID = result.getInt("Customer_ID");
                    result = s.executeQuery("update Cart set Customer_ID = "+ custID +" where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
                    result = s.executeQuery("select max(Transaction_ID)+1 from Transaction"); //generate transaction ID
		   	        result.next();
		   	        int transID = result.getInt("max(Transaction_ID)+1");
		   	        result = s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
		   	        result.next();
		   	        double subtotal = result.getDouble("sum(total_price)");
		   	        result = s.executeQuery("insert into Transaction (Transaction_ID, Customer_ID, subtotal, Date_Purchased, Store_ID) values ("+transID+", "+custID+", "+subtotal+", sysdate, '"+storeID+"')");
		   	        System.out.println("\nThank you for shopping at Sephora. Have a B-E-A-Utiful rest of the day!");
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
            	}
            	else {
            		System.out.println("How will you be paying today?\n\n<cash> Cash\n<credit> Credit Card\n<beautyinsider> Beauty Insider");
        			String choice = in.nextLine();
        			while(!choice.equalsIgnoreCase("cash")&&!choice.equalsIgnoreCase("credit")&&!choice.equalsIgnoreCase("beautyinsider")&&!choice.equalsIgnoreCase("exit")){
        				//System.out.println("Invalid input. Enter a valid input: ");
        				choice =in.nextLine();
        			}
        			if(choice.equalsIgnoreCase("cash")){
        				result = s.executeQuery("select Customer_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
        	            result.next();
        	            int custCashID = result.getInt("Customer_ID");
        	            result = s.executeQuery("insert into CustomerCash values ("+ custCashID +")");
        	            result = s.executeQuery("select max(Transaction_ID)+1 from Transaction");
    		   	        result.next();
    		   	        int transID = result.getInt("max(Transaction_ID)+1");
    		   	        result = s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
    		   	        result.next();
    		   	        double subtotal = result.getDouble("sum(total_price)");
    		   	        result = s.executeQuery("insert into Transaction (Transaction_ID, Customer_ID, subtotal, Date_Purchased, Store_ID) values ("+transID+", "+custCashID+", "+subtotal+", sysdate, '"+storeID+"')");
    		   	        System.out.println("\nYou have paid with cash.");
    		   	        System.out.println("\nThank you for shopping at Sephora. Have a B-E-A-Utiful rest of the day!");
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
        			}
        			if(choice.equalsIgnoreCase("credit")){
        				System.out.println("If you have shopped here before and used your credit card,");
        				System.out.println("Please Enter your Credit Card Number XXXXXXXXXXXXXXXX: ");
        				long creditCardNum = in.nextLong();
        				if(creditCardNum >= 9999999999999999L){
        	                System.out.println("You typed in too many numbers to be a valid card number.");
        	                System.out.println("Please type correctly: ");
        	                creditCardNum = in.nextLong();
        	            }
        	            result = s.executeQuery("select CreditCardNumber from CustomerCard where CreditCardNumber = " + creditCardNum);  
        	            if(!result.next()){
        	            	System.out.println("The credit card number entered does not exist in Sephora's records.");
            				result = s.executeQuery("select Customer_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
            	            result.next();
            	            int custCreditID = result.getInt("Customer_ID");
    	    	            System.out.println("Enter your credit card number: ");
    	    	            creditCardNum = in.nextLong();
    	    	            if(creditCardNum >= 9999999999999999L){
    	    	            	System.out.println("Invalid credit card number. Enter a valid credit card number: ");
    	    	            	creditCardNum = in.nextLong();
    	    	            }
        	    	            result = s.executeQuery("insert into CustomerCard values ("+ custCreditID +", "+ creditCardNum +")"); 
    	    	            	result = s.executeQuery("select max(Transaction_ID)+1 from Transaction");
    	    	            	result.next();
    	    	            	int transID = result.getInt("max(Transaction_ID)+1");
    	    	            	result = s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
    	    	            	result.next();
    	    	            	double subtotal = result.getDouble("sum(total_price)");
    	    	            	result = s.executeQuery("insert into Transaction (Transaction_ID, Customer_ID, subtotal, Date_Purchased, Store_ID) values ("+transID+", "+custCreditID+", "+subtotal+", sysdate, '"+storeID+"')");
            		   	        System.out.println("\nYou have paid with your credit card that you just entered into our system.");
    	    	            	System.out.println("\nThank you for shopping at Sephora. Have a B-E-A-Utiful rest of the day!");
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
        	            }else{ 
        	            	creditCardNum = result.getLong(1);
        	            	result = s.executeQuery("select Customer_ID from CustomerCard where CreditCardNumber = "+ creditCardNum);
            	            result.next();
            	            int custCreditID = result.getInt("Customer_ID");        	            	
        	            	/*result = s.executeQuery("select Customer_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
            	            result.next();
            	            int custCreditID = result.getInt("Customer_ID");*/
    	    	            result = s.executeQuery("update Cart set Customer_ID = "+ custCreditID +" where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'"); 
    	    	            result = s.executeQuery("select max(Transaction_ID)+1 from Transaction");
    			   	        result.next();
    			   	        int transID = result.getInt("max(Transaction_ID)+1");
    			   	        result = s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
    			   	        result.next();
    			   	        double subtotal = result.getDouble("sum(total_price)");
    			   	        result = s.executeQuery("insert into Transaction (Transaction_ID, Customer_ID, subtotal, Date_Purchased, Store_ID) values ("+transID+", "+custCreditID+", "+subtotal+", sysdate, '"+storeID+"')");
        		   	        System.out.println("\nYou have paid with your credit card that is in our Sephora Database.");
    			   	        System.out.println("\nThank you for shopping at Sephora. Have a B-E-A-Utiful rest of the day!");
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
        	            } 
        			}
        			if(choice.equalsIgnoreCase("beautyinsider")){
        				System.out.println("To access your Beauty Insider information, Re-Enter your Customer ID: ");
        				int custBeautyID;
        	            custBeautyID = in.nextInt();
        	            result = s.executeQuery("select Customer_ID from BeautyInsider where Customer_ID = " + custBeautyID);
        	            if(!result.next()){
        	                System.out.println("The customer ID you entered does not exist in our records.");
        	                System.out.println("You will have to pay with cash today.");
        	                result = s.executeQuery("select Customer_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
            	            result.next();
            	            int custCashID = result.getInt("Customer_ID");
            	            result = s.executeQuery("insert into CustomerCash values ("+ custCashID +")");
            	            result = s.executeQuery("select max(Transaction_ID)+1 from Transaction");
        		   	        result.next();
        		   	        int transID = result.getInt("max(Transaction_ID)+1");
        		   	        result = s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
        		   	        result.next();
        		   	        double subtotal = result.getDouble("sum(total_price)");
        		   	        result = s.executeQuery("insert into Transaction (Transaction_ID, Customer_ID, subtotal, Date_Purchased, Store_ID) values ("+transID+", "+custCashID+", "+subtotal+", sysdate, '"+storeID+"')");
        		   	        System.out.println("\nYou have paid cash.");
        		   	        System.out.println("\nThank you for shopping at Sephora. Have a B-E-A-Utiful rest of the day!");
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
        	            }else{
        	            	custBeautyID = result.getInt(1);
        	            	result = s.executeQuery("update Cart set Customer_ID = "+ custBeautyID +" where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
                            result = s.executeQuery("select max(Transaction_ID)+1 from Transaction"); //generate transaction ID
        		   	        result.next();
        		   	        int transID = result.getInt("max(Transaction_ID)+1");
        		   	        result = s.executeQuery("select sum(total_price) from Cart where Cart_ID = " + cartID);
        		   	        result.next();
        		   	        double subtotal = result.getDouble("sum(total_price)");
        		   	        result = s.executeQuery("insert into Transaction (Transaction_ID, Customer_ID, subtotal, Date_Purchased, Store_ID) values ("+transID+", "+custBeautyID+", "+subtotal+", sysdate, '"+storeID+"')");
        		   	        result = s.executeQuery("select name from BeautyInsider where Customer_ID = " + custBeautyID);
        		   	        result.next();
        		   	        String name = result.getString("name");   		   	        
        		   	        System.out.println("Welcome back "+name+".");
        		   	        System.out.println("\nYou have paid with your credit card that is in your Beauty Insider Account Records.");
        		   	        System.out.println("\nThank you for shopping at Sephora. Have a beautiful day!");
        		   	        flag = true;
     		   	        	con.close();
     		   	        	in.close();
     		   	        	try {
     		   	        		con.close();
     		   	        		System.exit(0);
     		   	        	}catch (SQLException ex) {
     		   	        		System.out.println("\nrror. Exiting...");
     		   	        		System.exit(1);
     		   	        	}
        	            	}
        			}
        			if(!choice.equalsIgnoreCase(choice)){
        				System.out.println("\nInvalid request. Enter a valid request: ");
        				choice = in.nextLine();
        			}
            	}
            	
            }
		}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
            ex.printStackTrace();
        }
	}

	void beautyInsider(int custID, String storeID) throws SQLException, Exception {
		custID = -1;
        String name = "";
        try{    
            System.out.print("\nEnter your Customer ID: ");
            custID = in.nextInt();
        }catch(InputMismatchException ex){
            System.out.println("Invalid customer ID. Enter valid Customer ID: ");
            in.nextLine();
            beautyInsider(custID, storeID);
        }
        try{
            Statement validateID = con.createStatement();
            ResultSet validResult;
            validResult = validateID.executeQuery("select * from BeautyInsider where Customer_ID = " + custID);
            if(!validResult.next()){
                System.out.println("The customer ID you entered does not exist in our records.\n"
                        + "Please try again or create a new account.");
                System.out.println("<try again> Try Again\n<create> Create Account\n");
                String choice;
                boolean word = false;
                while(word == true){
                    choice =in.nextLine().toLowerCase();                  
	                    switch(choice){
	                        case "try again":
	                            word = false;
	                            beautyInsider(custID, storeID);                            
	                            break;
	                    
	                        case "create":
	                            word = false;
	                            newMember();
	                            break;	                   
	                        
	                        default:
								System.out.println("\nInvalid request. Enter a valid request: ");
								break;
	                    }
                }                    
            }else{
                token = "beautyinsider";
                name = validResult.getString(2);
                //validateID.close();
                System.out.println("\nWelcome "+ name + "!\n");
                System.out.println("Your Customer ID is: "+ custID);
                System.out.println("\nSelect an option from the list below");
                options(token);
                Statement s = con.createStatement();
                ResultSet result;
                boolean flag = false;
                while(!flag){
                    try{
                    	String option = in.nextLine().toLowerCase(); 
                        if (option.equalsIgnoreCase("customer")){
                        	token = "customer";
                        	System.out.println("\nOpening Customer Main Menu....");
                        	options(token);
                        	try{
                        		flag = false;
                            	while(!flag){
                                	String input = in.nextLine().toLowerCase();
                                    switch(input){
                                     case "view products":
                                         System.out.println("\nOpening Inventory....");                                
                                         viewProducts(custID, storeID);
                                         flag = false;
                                         token = "customer";
                                         options(token);
                                         break;
                                     
                                     case "open cart":
                                         result = s.executeQuery("select Cart_ID from Cart where Customer_ID = "+ custID +" and Store_ID = '"+ storeID +"'");
                                         result.next();
                                         int cartID = result.getInt("Cart_ID");
                                         if(cartID >= 0) {
                                             System.out.println("\nOpening Shopping Cart....");
                                             openCart(cartID, custID, storeID);
                                         }   
                                         flag = false;
                                         token = "customer";
                                         options(token);
                                         break;

                                     case "view transactions":
                                    	 result = s.executeQuery("select Cart_ID from Cart where Customer_ID = "+ custID +" and Store_ID = '"+ storeID +"'");
                                         result.next();
                                         cartID = result.getInt("Cart_ID");
                                         if(cartID >= 0) {
                                             System.out.println("\nOpening Transactions....");
                                             viewTransactions(custID, storeID);
                                         }   
                                         flag = false;
                                         token = "customer";
                                         options(token);
                                         break;
                                     
                                     case "exit":
                                         System.out.println("\nReturning to Customer Menu....");
                                         flag = true;
                                         break;
                                     
                                     default:
         								System.out.println("\nInvalid request. Enter a valid request: ");
         								break;
                                    }
                            	}
                        	}catch(InputMismatchException ex){
                                System.out.println("\nInvalid request. Enter a valid request: ");
                                in.next();
                        	}catch(Exception ex){
                        		System.out.println("\nInvalid request. Enter a valid request: ");
                        		flag = false;
                        	}
                        }
                        if (option.equalsIgnoreCase("manage")){ 
                        	System.out.println("Opening Account Management....");
	                        flag = false;
	                        acctManagement(custID);
	                        break;
                        }
                    }catch(InputMismatchException ex){
                        System.out.println("\nInvalid request. Enter a valid request: ");
                        flag = false;
                    }
                 }
             }
        }catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
            ex.printStackTrace();
        }	
	}
	
	void newMember() throws Exception {
		in.nextLine();
		System.out.println("Enter your first and last name: ");
		String name = in.nextLine();
		System.out.print("Enter your phone number (XXX)XXX-XXXX: ");
		String phoneNumber = "";
		try{
			phoneNumber = in.nextLine();
			while(phoneNumber.length()!=13 ){
				System.out.println("Error: Invalid Phone number");
				phoneNumber = in.nextLine();
				}
			}catch(Exception ex){
				System.out.println("Error: Invalid Phone number");
				}
		System.out.println("Enter your email address: ");
	    String email = in.nextLine();
	    System.out.println("Enter a valid Credit Card Number for your Sephora account XXXXXXXXXXXXXXXX: ");
	    long creditCardNum = in.nextLong();
	    if(creditCardNum >= 9999999999999999L){
	    	System.out.println("Invalid credit card number. Enter a valid credit card number: ");
	    	creditCardNum = in.nextLong();
        }
	    boolean flag = false;
	    while (!flag){
	    	int custID = -1;
	        try{
	        	Statement s = con.createStatement();
	        	ResultSet result = s.executeQuery("select max(Customer_ID)+1 from Customer");
	            result.next();
	            custID = result.getInt("max(Customer_ID)+1");
	            result = s.executeQuery("insert into Customer values ("+ custID +")");
	            result = s.executeQuery("select max(Customer_ID) from Customer");
	            result.next();
	            custID = result.getInt("max(Customer_ID)");
	            result = s.executeQuery("insert into BeautyInsider values (" 
	                       + custID + ", '" + name + "', '" + phoneNumber + "', '" + email + "', " + creditCardNum + ")");
	            System.out.println("Welcome " + name +", your customer ID is "+ custID + " and your phone number is " + phoneNumber+".");
	            System.out.println("Your email address is " + email + " and your credit card on file is " + creditCardNum+".");
	            s.close();
	            flag = true;
	            onlineStore();
	            }catch (SQLException e){
	        		System.out.println("Internal Database Error. Close connection and start up program again.");
	            	e.printStackTrace();
	            	flag = true; 
	            }
	        }
	    }
	
	void acctManagement(int custID) throws SQLException{
    	try{
    		Statement s = con.createStatement();
            ResultSet result;
            result = s.executeQuery("select * from BeautyInsider where Customer_ID = "+ custID);
            result.next();
    		boolean flag = false;
    		while(!flag){
        		System.out.println("\nSelect an option from the menu");
        		options("manage");
        		String choice = in.nextLine().toLowerCase();              
	    		switch(choice){
	    			case "view purchases":
	    				System.out.println("\nOpening Previous Purchases....");
	    				viewTransactionsHistory(custID);
	    				flag = false;
	    				break;
	    				
	    			case "view email":
                        System.out.println("\nOpening Email Address....");
                        System.out.println("");
                        flag = false;
                        viewEmail(custID);
                        break;

                    case "edit phone":
                        System.out.print("\nYour current phone number is " + result.getString("phone_number") + " \n");
                        flag = false;
                        editPhone(custID);
                        break;
                        
                    case "edit card":
                    	System.out.print("\nYour current credit card number is " + result.getString("creditCardNumber") + " \n");
                        flag = false;
                        editCard(custID);
                        break;    
                        
	    			case "exit":
	    				flag = true;
	    				break;	
	    			
	    			default:
						System.out.println("\nInvalid request. Enter a valid request: ");
						break;		
	    		}
    		}
    	}catch(InputMismatchException ex){
			System.out.println("\nInvalid request. Enter a valid request:");
			System.out.println("Invalid account management.");
    	}catch(Exception ex){
			System.out.println("\nInvalid request. Enter a valid request:");
			System.out.println("Invalid account management.");
    	}
    }
    
	void viewTransactionsHistory(int custID){
		try{
    		Statement s=con.createStatement();
		    ResultSet result;
		    System.out.println("\nBeauty Insider Transaction History Menu");
		    System.out.println("----------------------------------------------------------------------------------------");		    
		    result = s.executeQuery("select Transaction_ID, subtotal, Date_Purchased, Store_ID from Transaction where Customer_ID = "+custID);
		    System.out.printf("%-10s %20s %25s %26s\n", "Transaction ID", "Subtotal", "Date Purchased", "Store ID");
		    System.out.printf("%-10s %20s %25s %26s\n", "--------------", "--------", "--------------", "--------");
	        while (result.next()){
	            String transactionID = result.getString(1);
	            double subtotal = result.getDouble(2);
	            Date datePurchased = result.getDate(3);
	            String storeID = result.getString(4);
	            System.out.printf("%-10s %20s %25s %26s\n", transactionID, subtotal, datePurchased, storeID);
	        }
	        System.out.println("----------------------------------------------------------------------------------------");
	        System.out.println("");
	        System.out.println("---------------------------------------------------------------------");
	        result = s.executeQuery("select Product_ID, quantity, total_price, Store_ID from Cart where Customer_ID = "+custID);
		    System.out.printf("%-10s %17s %19s %20s\n", "Product ID", "Quantity", "Total Price", "Store ID");
		    System.out.printf("%-10s %17s %19s %20s\n", "----------", "--------", "-----------", "--------");
	        while (result.next()){
	            String productID = result.getString(1);
	            int quantity = result.getInt(2);
	            double totalPrice = result.getDouble(3);
	            String storeID = result.getString(4);
	            System.out.printf("%-10s %17s %19s %20s\n", productID, quantity, totalPrice, storeID);
	        }
	        System.out.println("---------------------------------------------------------------------");
    	}catch(SQLException ex){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
    		ex.printStackTrace();
		}
	}
	
	void viewEmail(int custID) throws SQLException{
		Statement s = con.createStatement();
        ResultSet result = s.executeQuery("select email from BeautyInsider where Customer_ID = " + custID);
        result.next();
        String email = result.getString("email");
		System.out.println("Your current email address is: " + email);
	}	
		
	void editPhone(int custID){
        try{
        	ResultSet result;
        	Statement s = con.createStatement();
            System.out.println("Enter your new phone number (XXX)XXX-XXXX: ");
            String phoneNumber = in.nextLine();
            if(phoneNumber.length()!=13){
                throw new Exception();
            }
            else{
                result = s.executeQuery("update BeautyInsider set phone_number = '"+ phoneNumber +"' where Customer_ID = '" + custID + "'");
            }
        }catch(Exception ex){
             System.out.println("Invalid phone number. Try editing your phone number again.\n");
             editPhone(custID); 
         }
    }
	
	void editCard(int custID){
        try{
        	ResultSet result;
        	Statement s = con.createStatement();
            System.out.println("Enter your new credit card number: ");
            int creditCardNum = in.nextInt();
            if(creditCardNum >= 9999999999999999L){
                throw new Exception();
            }
            else{
                result = s.executeQuery("update BeautyInsider set CreditCardNumber = '"+ creditCardNum +"' where Customer_ID = '" + custID + "'");
            }
        }catch(Exception ex){
        	 System.out.println("Invalid credit card number. Try editing your card number again.\n");
             editCard(custID); 
         }
    }
	
	void viewProducts (int custID, String storeID){
    	try{
    		boolean flag = false;
    		while(!flag){
        		System.out.println("\nWhat are you looking for?");
        		options("products");
        		String choice = in.nextLine().toLowerCase();               
	    		switch(choice){
		    		case "makeup":
		    			System.out.println("");
	    				viewMakeup(custID, storeID);
	    				break;
	    				
	    			case "skin":
	    				System.out.println("");
	    				viewSkin(custID, storeID);
	    				break;
	    			
	    			case "fragrance":
	    				System.out.println("");
	    				viewFragrance(custID, storeID);
	    				break;
	    			
	    			case "exit":
	    				flag = true;
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
            System.out.println("\nInvalid product.");            
        }
	}
	
	public void viewMakeup (int custID, String storeID){
		boolean flag;
		try{
    		ResultSet result;
    		Statement s = con.createStatement();
    		flag = false;
    		while(!flag){
    			System.out.println("Here is a list of types of Makeup products.\n");
				result = s.executeQuery("select distinct product_subtype from Product natural join Makeup natural join Inventory where Store_ID = '"+ storeID +"'");
				while (result.next()) {
		        	String product_subtype = result.getString("product_subtype");
		            System.out.println("<"+product_subtype+"> " + product_subtype);
		        } 
				System.out.println("<exit> Exit");
		        System.out.println("\nWhat type of Makeup product are you looking for? "); 
		        String choice = in.nextLine();
		        while(!choice.equals("Nail") && !choice.equals("Eye") && !choice.equals("Face") && !choice.equals("Lip") && !choice.equalsIgnoreCase("exit")){
		        	//System.out.println("Invalid type of product. Please enter a valid product type:");
		        	choice = in.nextLine();
		        }
		        if (choice.equalsIgnoreCase("exit")){
		        	System.out.println("Returning to Product Menu....");
    				break;
		        }
		        result = s.executeQuery("select distinct product_subtype from Product natural join Makeup natural join Inventory where Store_ID = '"+ storeID +"'");
		        while(result.next()==false){
		        	System.out.println("Invalid type of product. Please enter a valid product type:");
		        	choice = in.nextLine();
		        	while(!choice.equals(choice)){
		        		System.out.println("Invalid type of product. Please enter a valid product type:");
		        		choice =in.nextLine();
		        	}
    			}
		        System.out.println("");
		        String productSubtype = choice;
		        result = s.executeQuery("select Product_ID, Brand_Name, product_size, product_descrip, (Item_Price) as price from Product natural join Makeup natural join Inventory where product_subtype ='"+productSubtype+"' and Store_ID = '"+storeID+"'");
                System.out.println("-----------------------------------------------------------------------------------------------------------");
		        System.out.printf("%-10s %25s %20s %23s %23s\n", "Product ID",  "Brand Name", "Product Size",  "Product Description", "Price");
                System.out.printf("%-10s %25s %20s %23s %23s\n", "----------", "-------------------", "------------", "-------------------", "--------");
   	            while(result.next()) {
   	            	String product_ID = result.getString(1);
   	                String brandName = result.getString(2);
   	                String productSize = result.getString(3);
   	                String productDescrip = result.getString(4);
   	                double price = result.getDouble(5);
   	                System.out.printf("%-10s %25s %20s %23s %23s\n", product_ID, brandName, productSize, productDescrip, price);
   	            }
                System.out.println("-----------------------------------------------------------------------------------------------------------");
   	            System.out.println("\nDo you have a Cart ID for your Shopping Cart?\n\n<yes> Yes\n<no> No");
            	String select;
            	select = in.nextLine().toLowerCase();
            	while(!select.equalsIgnoreCase("yes")&&!select.equalsIgnoreCase("no")){
            		//System.out.println("Invalid input. Please enter a valid input:");
  	            	select =in.nextLine();
  	            }
            	if(select.equalsIgnoreCase("yes")){
  	            	System.out.println("Enter Cart ID: ");
  	            	int cartID = in.nextInt();
  	            	result = s.executeQuery("select Cart_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
  	            	result.next();
   		   	       if(cartID != result.getInt("Cart_ID")){
   		   	    	   System.out.println("The Cart ID you entered does not match our records.\n");
   		   	    	   System.out.println("You will be given a new Cart ID.");
   		   	    	   result = s.executeQuery("select max(Cart_ID)+1 from Cart");
		   	           result.next();
   		   	           cartID = result.getInt("max(Cart_ID)+1");
   		   	       }
		   	       	System.out.println("\nYour Cart ID is "+ cartID+ ".\n");
	            	System.out.println("Would you like to add a Makeup Product to your Shopping Cart?\n\n<yes> Yes\n<no> No\n");
	   	            String option;
	   	            option = in.nextLine().toLowerCase();
	   	            while(!option.equalsIgnoreCase("yes")&&!option.equalsIgnoreCase("no")){
	   	            	//System.out.println("Invalid input. Please enter a valid input:");
	   	            	option =in.nextLine();
	   	            }
	   	            if(option.equalsIgnoreCase("yes")){
	   	            	System.out.println("\nSelect the Product ID you want to add to cart:"); 
		   	            String addProdMake = in.nextLine();
		   	            while(!isNum(addProdMake)){
		   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
			            	addProdMake = in.nextLine();
			               }
		   	            result = s.executeQuery("select Product_ID from Product natural join Makeup natural join Inventory where product_subtype = '"+productSubtype+"' and Store_ID = '"+storeID+"'");
		   	            while(result.next()==false){
		   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
		   	            	addProdMake = in.nextLine();
			                while(!isNum(addProdMake)){
			                	System.out.println("Invalid Product. Please enter a valid Product ID:");
			                	addProdMake =in.nextLine();
			                }
		   	            }
		   	            result = s.executeQuery("select Items_In_Stock, Item_Price from Inventory natural join Product where Product_ID ='"+addProdMake+"' and Store_ID = '"+storeID+"'"); 
		   	            result.next();
		   	            int itemsInStock = result.getInt("Items_In_Stock");
		   	            double itemPrice = result.getDouble("Item_Price");
		   	            checkInventory(itemsInStock, storeID, addProdMake);
		   	            System.out.println("Enter the quantity of the amount you want: ");
			   	        int quantityWanted = in.nextInt(); 
			   	        while (quantityWanted == 0){
			   	            	System.out.println("Invalid quantity. Enter a valid quantity: ");
			   	            	quantityWanted = in.nextInt();
			   	        }
			   	        while(quantityWanted > itemsInStock){
			   	            	System.out.println("At this time, Sephora cannot fill such a large order.");
	   		   	            	System.out.print("\nEnter a valid quantity: ");
			   	            	quantityWanted = in.nextInt();
			   	        }
			   	        double totalPrice = quantityWanted*itemPrice;
	  		   	        result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+addProdMake+"', "+quantityWanted+", "+totalPrice+")");
	   		   	        System.out.println("Item added to Cart.\n");
	   		   	        int updateInStock = itemsInStock - quantityWanted;
	   		   	        result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ addProdMake +"' and Store_ID = '"+ storeID +"'");
	   		   	        checkInventory(updateInStock, storeID, addProdMake);
		   	        }
	   	            if(option.equalsIgnoreCase("no")){
	   	            	System.out.println("Returning to Makeup Menu....");
	    				break;
	   	            }
	   	            if(!option.equalsIgnoreCase(option)){
	   	            	System.out.println("Invalid input. Enter a valid input: ");
	    				option =in.nextLine();
	   	            }
            	}
            	if(select.equalsIgnoreCase("no")){
            		result = s.executeQuery("select max(Cart_ID)+1 from Cart");
		   	        result.next();
		   	        int cartID = result.getInt("max(Cart_ID)+1");
	   	       	  	System.out.println("\nYour Cart ID is "+ cartID+ ".\n");
	            	System.out.println("Would you like to add a Makeup Product to your Shopping Cart?\n\n<yes> Yes\n<no> No\n");
	   	            String option;
	   	            option = in.nextLine().toLowerCase();
	   	            while(!option.equalsIgnoreCase("yes")&&!option.equalsIgnoreCase("no")){
	   	            	//System.out.println("Invalid input. Please enter a valid input:");
	   	            	option =in.nextLine();
	   	            }
	   	            if(option.equalsIgnoreCase("yes")){
	   	            	System.out.println("\nSelect the Product ID you want to add to cart:"); 
		   	            String addProdMake = in.nextLine();
		   	            while(!isNum(addProdMake)){
		   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
			            	addProdMake = in.nextLine();
			               }
		   	            result = s.executeQuery("select Product_ID from Product natural join Makeup natural join Inventory where product_subtype = '"+productSubtype+"' and Store_ID = '"+storeID+"'");
		   	            while(result.next()==false){
		   	            	System.out.println("Invalid Product ID. Please enter a valid Product ID:");
		   	            	addProdMake = in.nextLine();
			                while(!isNum(addProdMake)){
			                	System.out.println("Invalid Product ID. Please enter a valid Product ID:");
			                	addProdMake =in.nextLine();
			                }
		   	            }
		   	            result = s.executeQuery("select Items_In_Stock, Item_Price from Inventory natural join Product where Product_ID ='"+addProdMake+"' and Store_ID = '"+storeID+"'"); 
		   	            result.next();
		   	            int itemsInStock = result.getInt("Items_In_Stock");
		   	            double itemPrice = result.getDouble("Item_Price");
		   	            checkInventory(itemsInStock, storeID, addProdMake);
			   	            System.out.println("Enter the quantity of the amount you want: ");
			   	            int quantityWanted = in.nextInt();
			   	            while (quantityWanted == 0){
			   	            	System.out.println("Invalid quantity. Enter a valid quantity: ");
			   	            	quantityWanted = in.nextInt();
			   	            }
			   	            while(quantityWanted > itemsInStock){
			   	            	System.out.println("At this time, Sephora cannot fill such a large order.");
	   		   	            	System.out.print("\nEnter a valid quantity: ");
			   	            	quantityWanted = in.nextInt();
			   	            }
			   	            double totalPrice = quantityWanted*itemPrice;
	  		   	          	result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+addProdMake+"', "+quantityWanted+", "+totalPrice+")");
	   		   	            System.out.println("Item added to Cart.\n");
	   		   	            int updateInStock = itemsInStock - quantityWanted;
	   		   	            result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ addProdMake +"' and Store_ID = '"+ storeID +"'");
	   		   	            checkInventory(updateInStock, storeID, addProdMake);
		   	        }
	   	            if(option.equalsIgnoreCase("no")){
	   	            	System.out.println("Returning to Makeup Menu....");
	    				break;
	   	            }
	   	            if(!option.equalsIgnoreCase(option)){
	   	            	System.out.println("Invalid input. Enter a valid input: ");
	    				option =in.nextLine();
	   	            }
            	}
            	if(!select.equalsIgnoreCase(choice)){
    				//System.out.println("Invalid request. Enter a valid request: ");
    				select =in.nextLine();
    			}
            flag = false;	
    		}
		}catch (SQLException e){
    		System.out.println("Internal Database Error. Close connection and start up program again.");
            e.printStackTrace();
     	   	flag = true; 
        }
	}
	
	public void viewSkin (int custID, String storeID){
		boolean flag;
		try{
    		ResultSet result;
    		Statement s = con.createStatement();
    		flag = false;
    		while(!flag){
    			System.out.println("Here is a list of types of Skin products.\n");
				result = s.executeQuery("select distinct product_subtype from Product natural join Skin natural join Inventory where Store_ID = '"+ storeID +"'");
				while (result.next()) {
		        	String product_subtype = result.getString("product_subtype");
		            System.out.println("<"+product_subtype+"> " + product_subtype);
		        }
				   System.out.println("<exit> Exit");
		           System.out.println("\nWhat type of Skin product are you looking for?"); 
		           String choice = in.nextLine();
		           while(!choice.equals("Cleanse") && !choice.equals("Treat") && !choice.equals("Moisturize") && !choice.equalsIgnoreCase("exit")){
		        	   //System.out.println("Invalid type of product. Please enter a valid product type: ");
		        	   choice = in.nextLine();
		           }
		           if(choice.equalsIgnoreCase("exit")){
		        	   System.out.println("Returning to Product Menu....");
		        	   break;
		           }
		           result = s.executeQuery("select distinct product_subtype from Product natural join Skin natural join Inventory where Store_ID = '"+ storeID +"'");
		           while(result.next()==false){
		        	   System.out.println("Invalid type of product. Please enter a valid product type:");
		        	   choice = in.nextLine();
		               while(!choice.equals(choice)){
		            	   System.out.println("Invalid type of product. Please enter a valid product type:");
		            	   choice =in.nextLine();
		               }
		           }
		           System.out.println("");
		           String productSubtype = choice;
		           result = s.executeQuery("select Product_ID, Brand_Name, product_size, product_descrip, (Item_Price) as price from Product natural join Skin natural join Inventory where product_subtype ='"+productSubtype+"' and Store_ID = '"+storeID+"'");
                   System.out.println("-----------------------------------------------------------------------------------------------------------");
		           System.out.printf("%-10s %25s %20s %23s %23s\n", "Product ID",  "Brand Name", "Product Size",  "Product Description", "Price");
                   System.out.printf("%-10s %25s %20s %23s %23s\n", "----------", "-------------------", "------------", "-------------------", "--------");
                   while(result.next()) {
   	            	String product_ID = result.getString(1);
   	                String brandName = result.getString(2);
   	                String productSize = result.getString(3);
   	                String productDescrip = result.getString(4);
   	                double price = result.getDouble(5);
   	                System.out.printf("%-10s %25s %20s %23s %23s\n", product_ID, brandName, productSize, productDescrip, price);
   	                } 
                   	System.out.println("-----------------------------------------------------------------------------------------------------------");
   	            	System.out.println("\nDo you have a Cart ID for your Shopping Cart?\n\n<yes> Yes\n<no> No");
   	            	String select;
   	            	select = in.nextLine().toLowerCase();
      	            while(!select.equalsIgnoreCase("yes")&&!select.equalsIgnoreCase("no")){
      	            	//System.out.println("Invalid input. Please enter a valid input:");
      	            	select =in.nextLine();
      	            }
      	            if(select.equalsIgnoreCase("yes")){
      	            	System.out.println("Enter Cart ID: ");
      	            	int cartID = in.nextInt();
      	            	result = s.executeQuery("select Cart_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
      	            	result.next();
       		   	       if(cartID != result.getInt("Cart_ID")){
       		   	    	   System.out.println("The Cart ID you entered does not match our records.\n");
       		   	    	   result = s.executeQuery("select max(Cart_ID)+1 from Cart");
       		   	    	   System.out.println("You will be given a new Cart ID.");
    		   	           result.next();
       		   	           cartID = result.getInt("max(Cart_ID)+1");
       		   	       }
  		   	       	    System.out.println("\nYour Cart ID is "+ cartID+ ".\n");
	   	            	System.out.println("Would you like to add a Skin Product to your Shopping Cart?\n\n<yes> Yes\n<no> No");
	      	            String option;
	      	            option = in.nextLine().toLowerCase();
	      	            while(!option.equalsIgnoreCase("yes")&&!option.equalsIgnoreCase("no")){
		   	            	//System.out.println("Invalid input. Please enter a valid input:");
		   	            	option =in.nextLine();
		   	            }
	      	            if(option.equalsIgnoreCase("yes")){
	      	            	System.out.println("\nSelect the Product ID you want to add to cart:"); 
	   	   	            	String addProdSkin = in.nextLine();
		   	   	            while(!isNum(addProdSkin)){
		   	   	            	System.out.println("Invalid product ID. Please enter a valid Product ID:");
		   		            	addProdSkin = in.nextLine();
		   		               }
		   	   	            result = s.executeQuery("select Product_ID from Product natural join Skin natural join Inventory where product_subtype = '"+productSubtype+"' and Store_ID = '"+storeID+"'");
		   	   	            while(result.next()==false){
		   	   	            	System.out.println("Invalid product ID. Please enter a valid Product ID:");
		   	   	            	addProdSkin = in.nextLine();
		   		                while(!isNum(addProdSkin)){
		   		                	System.out.println("Invalid product ID. Please enter a valid Product ID:");
		   		                	addProdSkin =in.nextLine();
		   		                }
		   	   	            }
		   	   	            result = s.executeQuery("select Items_In_Stock, Item_Price from Inventory natural join Product where Product_ID = '"+addProdSkin+"' and Store_ID = '"+storeID+"'"); 
		   	   	            result.next();
		   	   	            int itemsInStock = result.getInt("Items_In_Stock");
		   	   	            double itemPrice = result.getDouble("Item_Price");
		   	   	            checkInventory(itemsInStock, storeID, addProdSkin);
		   	   	          
		   		   	            System.out.println("Enter the quantity of the amount you want: ");
		   		   	            int quantityWanted = in.nextInt();
		   		   	            while (quantityWanted == 0){
		   		   	            	System.out.println("Invalid quantity. Enter a valid quantity: ");
		   		   	            	quantityWanted = in.nextInt();
		   		   	            }
		   		   	            while(quantityWanted > itemsInStock){
		   		   	            	System.out.println("At this time, Sephora cannot fill such a large order.");
		   		   	            	System.out.print("\nEnter a valid quantity: ");
		   		   	            	quantityWanted = in.nextInt();
		   		   	            }
		   		   	            double totalPrice = quantityWanted*itemPrice;
		  		   	          	result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+addProdSkin+"', "+quantityWanted+", "+totalPrice+")");
		   		   	            System.out.println("Item added to Cart.\n");
		   		   	            int updateInStock = itemsInStock - quantityWanted;
		   		   	            result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ addProdSkin +"' and Store_ID = '"+ storeID +"'");
		   		   	            checkInventory(updateInStock, storeID, addProdSkin);
	   	   	            }
	      	            if(option.equalsIgnoreCase("no")){
	     	            	System.out.println("\nReturning to Skin Menu....");
	     	            	break;
	     	            }
	      	            if(!option.equalsIgnoreCase(option)){
		   	            	System.out.println("Invalid request. Enter a valid request: ");
		    				option =in.nextLine();
		   	            }
      	            }
      	          if(select.equalsIgnoreCase("no")){
      	        	  result = s.executeQuery("select max(Cart_ID)+1 from Cart");
		   	          result.next();
		   	          int cartID = result.getInt("max(Cart_ID)+1");
		   	       	  System.out.println("\nYour Cart ID is "+ cartID+ ".\n");
		   	          System.out.println("Would you like to add a Skin Product to your Shopping Cart?\n\n<yes> Yes\n<no> No");
		   	          String option;
		   	          option = in.nextLine().toLowerCase();
		   	          while(!option.equalsIgnoreCase("yes")&&!option.equalsIgnoreCase("no")){
	   	            	//System.out.println("Invalid input. Please enter a valid input:");
	   	            	option =in.nextLine();
		   	          }
	     	          if(option.equalsIgnoreCase("yes")){
     	            	System.out.println("\nSelect the Product ID you want to add to cart:"); 
  	   	            	String addProdSkin = in.nextLine();
	   	   	            while(!isNum(addProdSkin)){
	   	   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
	   		            	addProdSkin = in.nextLine();
	   		               }
	   	   	            result = s.executeQuery("select Product_ID from Product natural join Skin natural join Inventory where product_subtype ='"+productSubtype+"' and Store_ID = '"+storeID+"'");
	   	   	            while(result.next()==false){
	   	   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
	   	   	            	addProdSkin = in.nextLine();
	   		                while(!isNum(addProdSkin)){
	   		                	System.out.println("Invalid Product. Please enter a valid Product ID:");
	   		                	addProdSkin =in.nextLine();
	   		                }
	   	   	           }
	   	   	            result = s.executeQuery("select Items_In_Stock, Item_Price from Inventory natural join Product where Product_ID ='"+addProdSkin+"' and Store_ID = '"+storeID+"'"); 
	   	   	            result.next();
	   	   	            int itemsInStock = result.getInt("Items_In_Stock");
	   	   	            double itemPrice = result.getDouble("Item_Price");
	   	   	            checkInventory(itemsInStock, storeID, addProdSkin);
	   		   	        System.out.println("Enter the quantity of the amount you want: ");
	   		   	        int quantityWanted = in.nextInt();
	   		   	        while (quantityWanted == 0){
	   		   	            System.out.println("Invalid quantity. Enter a valid quantity: ");
	   		   	            quantityWanted = in.nextInt();
	   		   	        }
	   		   	        while(quantityWanted > itemsInStock){
	   		   	            System.out.println("At this time, Sephora cannot fill such a large order.");
	   		   	            System.out.print("\nEnter a valid quantity: ");
	   		   	            quantityWanted = in.nextInt();
	   		   	        }
	   		   	        double totalPrice = quantityWanted*itemPrice;
	  		   	        result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+addProdSkin+"', "+quantityWanted+", "+totalPrice+")");
	   		   	        System.out.println("Item added to Cart.\n");
	   		   	        int updateInStock = itemsInStock - quantityWanted;
	   		   	        result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ addProdSkin +"' and Store_ID = '"+ storeID +"'");
	   		   	        checkInventory(updateInStock, storeID, addProdSkin);
  	   	            }
     	            if(option.equalsIgnoreCase("no")){
    	            	System.out.println("\nReturning to Skin Menu....");
    	            	break;
    	            }
     	            if(!option.equalsIgnoreCase(option)){
	   	            	System.out.println("Invalid input. Enter a valid input: ");
	    				option =in.nextLine();
	   	            }
      	          }
      	          if(!select.equalsIgnoreCase(select)){
   	            	//System.out.println("Invalid request. Enter a valid request: ");
    				select =in.nextLine();
      	          }
      	          flag = false;
      	        }
			}catch (SQLException e){
	    		System.out.println("Internal Database Error. Close connection and start up program again.");
	    		e.printStackTrace();
				flag = true; 
			}
		}
	
	public void viewFragrance (int custID, String storeID){
		boolean flag;
		try{
    		flag = false;
    		while(!flag){
        		Statement s = con.createStatement();
    			System.out.println("Here is a list of types of Fragrance products.\n");
				ResultSet result = s.executeQuery("select distinct product_subtype from Product natural join Fragrance natural join Inventory where Store_ID = '"+ storeID +"'");
				while (result.next()) {
		        	String product_subtype = result.getString("product_subtype");
		            System.out.println("<"+product_subtype+"> " + product_subtype);
		        }
				   System.out.println("<exit> Exit");
		           System.out.println("\nWhat type of Fragrance product are you looking for?"); 
		           String choice = in.nextLine();
		           while(!choice.equals("Men") && !choice.equals("Women") && !choice.equalsIgnoreCase("exit")){
		        	   //System.out.println("Invalid type of product. Please enter a valid product type:");
		        	   choice = in.nextLine();
		           }		           
		           if(choice.equalsIgnoreCase("exit")){
		        	   System.out.println("Returning to Product Menu....");
		        	   break;
		           }
		           result = s.executeQuery("select distinct product_subtype from Product natural join Fragrance natural join Inventory where Store_ID = '"+ storeID +"'");
		           while(result.next()==false){
		        	   System.out.println("Invalid type of product. Please enter a valid product type:");
		        	   choice = in.nextLine();
		               while(!choice.equalsIgnoreCase(choice)){
		            	   System.out.println("Invalid type of product. Please enter a valid product type:");
		            	   choice =in.nextLine();
		               }
		           }
		           System.out.println("");
		           String productSubtype = choice;
		           result = s.executeQuery("select Product_ID, Brand_Name, product_size, product_descrip, (Item_Price) as price from Product natural join Fragrance natural join Inventory where product_subtype ='"+productSubtype+"' and Store_ID = '"+storeID+"'");
                   System.out.println("-----------------------------------------------------------------------------------------------------------");
		           System.out.printf("%-10s %25s %20s %23s %23s\n", "Product ID",  "Brand Name", "Product Size",  "Product Description", "Price");
                   System.out.printf("%-10s %25s %20s %23s %23s\n", "----------", "-------------------", "------------", "-------------------", "--------");
                   while(result.next()) {
	   	            	String product_ID = result.getString(1);
	   	                String brandName = result.getString(2);
	   	                String productSize = result.getString(3);
	   	                String productDescrip = result.getString(4);
	   	                double price = result.getDouble(5);
	   	                System.out.printf("%-10s %25s %20s %23s %23s\n", product_ID, brandName, productSize, productDescrip, price);
   	               }
                   System.out.println("-----------------------------------------------------------------------------------------------------------");
                   System.out.println("\nDo you have a Cart ID for your Shopping Cart?\n\n<yes> Yes\n<no> No");
                   String select;
                   select = in.nextLine().toLowerCase();
     	           while(!select.equalsIgnoreCase("yes")&&!select.equalsIgnoreCase("no")){
     	            	//System.out.println("Invalid input. Please enter a valid input:");
     	            	select =in.nextLine();
     	           }
     	           if(select.equalsIgnoreCase("yes")){
     	            	System.out.println("Enter Cart ID: ");
      	            	int cartID = in.nextInt();
      	            	result = s.executeQuery("select Cart_ID from Cart where Cart_ID = "+ cartID +" and Store_ID = '"+ storeID +"'");
      	            	result.next();
       		   	       if(cartID != result.getInt("Cart_ID")){
       		   	    	   System.out.println("The Cart ID you entered does not match our records.\n");
       		   	    	   result = s.executeQuery("select max(Cart_ID)+1 from Cart");
       		   	    	   System.out.println("You will be given a new Cart ID.");
    		   	           result.next();
       		   	           cartID = result.getInt("max(Cart_ID)+1");
       		   	       }
       		   	       System.out.println("\nYour Cart ID is "+ cartID +".\n");
       		   	       System.out.println("Would you like to add a Fragrance Product to your Shopping Cart?\n\n<yes> Yes\n<no> No");
	      	           String option;
	      	           option = in.nextLine().toLowerCase();
	      	           while(!option.equalsIgnoreCase("yes")&&!option.equalsIgnoreCase("no")){
	      	            	//System.out.println("Invalid input. Please enter a valid input:");
	      	            	option =in.nextLine();
	      	           }
	      	           if(option.equalsIgnoreCase("yes")){
	      	            	System.out.println("\nSelect the Product ID you want to add to cart:"); 
	   	   	            	String addProdFrag = in.nextLine();
		   	   	            while(!isNum(addProdFrag)){
		   	   	            	System.out.println("Invalid product ID. Please enter a valid Product ID:");
		   	   	            	addProdFrag = in.nextLine();
		   	   	            }
		   	   	            result = s.executeQuery("select Product_ID from Product natural join Fragrance natural join Inventory where product_subtype = '"+productSubtype+"' and Store_ID = '"+storeID+"'");
		   	   	            while(result.next()==false){
		   	   	            	System.out.println("Invalid product ID. Please enter a valid Product ID:");
		   	   	            	addProdFrag = in.nextLine();
		   		                while(!isNum(addProdFrag)){
		   		                	System.out.println("Invalid product ID. Please enter a valid Product ID:");
		   		                	addProdFrag =in.nextLine();
		   		                }
		   	   	            }
		   	   	            result = s.executeQuery("select Items_In_Stock, Item_Price from Inventory natural join Product where Product_ID = '"+addProdFrag+"' and Store_ID = '"+storeID+"'"); 
		   	   	            result.next();
		   	   	            int itemsInStock = result.getInt("Items_In_Stock");
		   	   	            double itemPrice = result.getDouble("Item_Price");
		   	   	            checkInventory(itemsInStock, storeID, addProdFrag);
		   		   	        System.out.println("Enter the quantity of the amount you want: ");
		   		   	        int quantityWanted = in.nextInt();
		   		   	        while (quantityWanted == 0){
		   		   	            System.out.println("Invalid quantity. Enter a valid quantity: ");
		   		   	            quantityWanted = in.nextInt();
		   		   	        }
		   		   	        while(quantityWanted > itemsInStock){
		   		   	            System.out.println("At this time, Sephora cannot fill such a large order.");
		   		   	            System.out.print("\nEnter a valid quantity: ");
		   		   	            quantityWanted = in.nextInt();
		   		   	        }
		   		   	        double totalPrice = quantityWanted*itemPrice;
		  		   	        result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+addProdFrag+"', "+quantityWanted+", "+totalPrice+")");
		   		   	        System.out.println("Item added to Cart.\n");
		   		   	        int updateInStock = itemsInStock - quantityWanted;
		   		   	        result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ addProdFrag +"' and Store_ID = '"+ storeID +"'");
		   		   	        checkInventory(updateInStock, storeID, addProdFrag);		   	   	            
	   	   	            }
	      	            if(option.equalsIgnoreCase("no")){
	     	            	System.out.println("\nReturning to Fragrance Menu....");
	     	            	break;
	     	            }
	      	            if(!option.equalsIgnoreCase(option)){
		   	            	System.out.println("Invalid request. Enter a valid request: ");
		    				option =in.nextLine();
		   	            }
     	            }
     	          if(select.equalsIgnoreCase("no")){
     	        	  result = s.executeQuery("select max(Cart_ID)+1 from Cart");
		   	          result.next();
		   	          int cartID = result.getInt("max(Cart_ID)+1");
		   	          System.out.println("\nYour Cart ID is "+ cartID+ ".\n");
		   	          System.out.println("Would you like to add a Fragrance Product to your Shopping Cart?\n\n<yes> Yes\n<no> No");
		   	          String option;
		   	          option = in.nextLine().toLowerCase();
	     	          while(!option.equalsIgnoreCase("yes")&&!option.equalsIgnoreCase("no")&&!option.equalsIgnoreCase("exit")){
	     	        	  	//System.out.println("Invalid input. Please enter a valid input: ");
	     	            	option =in.nextLine();
	     	          }
	     	          if(option.equalsIgnoreCase("yes")){
	    	            	System.out.println("\nSelect the Product ID you want to add to cart:"); 
	 	   	            	String addProdFrag = in.nextLine();
		   	   	            while(!isNum(addProdFrag)){
		   	   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
		   	   	            	addProdFrag = in.nextLine();
		   	   	            }
		   	   	            result = s.executeQuery("select Product_ID from Product natural join Fragrance natural join Inventory where product_subtype ='"+productSubtype+"' and Store_ID = '"+storeID+"'");
		   	   	            while(result.next()==false){
		   	   	            	System.out.println("Invalid Product. Please enter a valid Product ID:");
		   	   	            	addProdFrag = in.nextLine();
		   		                while(!isNum(addProdFrag)){
		   		                	System.out.println("Invalid Product. Please enter a valid Product ID:");
		   		                	addProdFrag =in.nextLine();
		   		                }
		   	   	            }
		   	   	            result = s.executeQuery("select Items_In_Stock, Item_Price from Inventory natural join Product where Product_ID ='"+addProdFrag+"' and Store_ID = '"+storeID+"'"); 
		   	   	            result.next();
		   	   	            int itemsInStock = result.getInt("Items_In_Stock");
		   	   	            double itemPrice = result.getDouble("Item_Price");
		   	   	            checkInventory(itemsInStock, storeID, addProdFrag);	   	   	         
		   		   	        System.out.println("Enter the quantity of the amount you want: ");
		   		   	        int quantityWanted = in.nextInt();
		   		   	        while (quantityWanted == 0){
		   		   	            System.out.println("Invalid quantity. Enter a valid quantity: ");
		   		   	            quantityWanted = in.nextInt();
		   		   	        }
		   		   	        while(quantityWanted > itemsInStock){
		   		   	            System.out.println("At this time, Sephora cannot fill such a large order.");
		   		   	            System.out.print("\nEnter a valid quantity: ");
		   		   	            quantityWanted = in.nextInt();
		   		   	        }
		   		   	        double totalPrice = quantityWanted*itemPrice;
		  		   	        result = s.executeQuery("insert into Cart (Cart_ID, Customer_ID, Store_ID, Product_ID, quantity, total_price) values ("+cartID+", "+custID+", '"+storeID+"', '"+addProdFrag+"', "+quantityWanted+", "+totalPrice+")");
		   		   	        System.out.println("Item added to Cart.\n");
		   		   	        int updateInStock = itemsInStock - quantityWanted;
		   		   	        result = s.executeQuery("update Inventory set Items_In_Stock = "+ updateInStock +" where Product_ID = '"+ addProdFrag +"' and Store_ID = '"+ storeID +"'");
		   		   	        checkInventory(updateInStock, storeID, addProdFrag);	   	   	            
	     	          }
	     	          if(option.equalsIgnoreCase("no")){
	    	            	System.out.println("\nReturning to Fragrance Menu....");
	    	            	break;
	     	          }
	     	          if(!option.equalsIgnoreCase(option)){
		   	            	System.out.println("Invalid input. Enter a valid input: ");
		    				option =in.nextLine();
	     	          }
     	          }
     	         if(!select.equalsIgnoreCase(select)){
	   	            //System.out.println("Invalid request. Enter a valid request: ");
	    			select =in.nextLine();
     	         }
     	         flag = false;
    		}
			}catch (SQLException e){
	    		System.out.println("Internal Database Error. Close connection and start up program again.");
				e.printStackTrace();
				flag = true; 
			}
	}
	
    public boolean isNum(String s){
    	try { 
            Double.parseDouble(s); 
            return true;
        } catch(NumberFormatException e) { 
            return false; 
        }
    }
        
    public static void options(String token){
        switch(token){
        	case "customer":
        		System.out.println("\nCustomer Main Menu");
                System.out.println("<view products> View Products");
                System.out.println("<open cart> Open Shopping Cart");
                System.out.println("<view transactions> View Transactions");
                break;
        		
        	case "beautyinsider":
                System.out.println("\nBeauty Insider Main Menu");
                System.out.println("<customer> Open Customer Main Menu");
                System.out.println("<manage> Account Management");
                break;
                
            case "new":
            	System.out.println("\nNew Customer Main Menu");
                System.out.println("<join beauty insider> Join Beauty Insider");
                System.out.println("<exit> Exit to Customer menu");
                break;            
            
            case "cart":
            	System.out.println("\nShopping Cart Main Menu");
            	System.out.println("<view cart> View Shopping Cart");
            	System.out.println("<edit cart> Edit Shopping Cart");
                System.out.println("<exit> Return to Customer Menu");
                break;  
                
            case "transactions":
            	System.out.println("\nTransactions Main Menu");
            	System.out.println("<balance> View Subtotal");
            	System.out.println("<checkout> Proceed to Checkout");
                System.out.println("<exit> Return to Customer Menu");
                break;
           
            case "products":
            	System.out.println("\nProducts Main Menu");
                System.out.println("<makeup> Makeup Products");
                System.out.println("<skin> Skin Products");
                System.out.println("<fragrance> Fragrance Products");
                System.out.println("<exit> Return to Customer Menu");
                break;
            
            case "manage":
            	System.out.println("\nManage Main Menu");
                System.out.println("<view purchases> View Previous Purchases");
                System.out.println("<view email> View Current Email Address");
                System.out.println("<edit phone> Edit Phone Number");
                System.out.println("<edit card> Edit Credit Card");
                System.out.println("<exit> Return to Beauty Insider Menu");
                break;
        }
  
    }  
}