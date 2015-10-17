*************************************************
* Samantha Shreck								*
* shs216										*
* CSE 241										*
* Semester Project - "Sephora"					*
* 												*
* Run using the following command on the 		*
* command line:    								*
*												*
*    java -jar Shs216shreck.java           		*
*************************************************


Table of Contents

	1. Description
		a. Shs216shreck.java
		b. StoreManager.java
		c. Customer.java
		d. Marketer.java
	2. Usage
		a. Shs216shreck.java
		b. StoreManager.java
		c. Customer.java
		d. Marketer.java
	3. Other Notes
	
*************************
* 1. Description	*
*************************

	a. Shs216shreck.java

		This is the main class from which the program initializes from. The the two different 
		interfaces that I have implemented can be opened from here. There are two options: Customer or
		StoreManager, which are both interfaces that are initialized upon typing the appropriate commands.

	b. StoreManager.java

		This is the class for the store manager (employee of Sephora Store) interface. Users initially login by typing their Employee ID number. See the usage section of the README for appropriate ID numbers. In this interface, the user can view the store's Inventory (location specific to where the employee works), view the store's Order History (reorders and supplies from vendors), edit the prices of products in the store's Inventory, edit the minimum level of products in Inventory for reording of the store, and edit their phone number. Store Managers do not have much other function besides viewing and editing certain Inventory qualities and viewing Order History. The rest is left in the hands of the customers to handle their accounts. The Inventory involving vendors suppling the store with the products is an automatic system. Depending on certain values and levels in Inventory, reorders and shipment dates will be triggered to activate automatically without involving the Store Manager at all. 

	c. Customer.java

		This is the class for the customer interface. The user is initially prompted to enter their relationship to the store: Online Store or Physical Store Front. I thought about implementing a Customer ID instanteously when you access the customer interface, but after toying with the idea, I decided to hold off on initiating the Customer ID so early on. Acknowledging what store the customer is in is a more pressing matter at the time of programming the interface is in. My aim was to simulate commonly performed actions that customers of a simply store would perform if this were a real store. Common actions that are included in the interface include:

			-Choosing Online or Physical Store
				 Includes:
					-Online Store
						Includes:
							-Customer Menu
								Includes:
									- Viewing products online
									- Opening shopping cart
									- Viewing transactions
							-Account Management Menu
								Includes:
									- Viewing previous purchases
									- Viewing current email addresses
									- Editing phone number in Sephora's records
									- Editing credit card number in Sephora's records
					-Physical Store
						Includes:
							-Customer Menu
								Includes:
									- Viewing products online
									- Opening shopping cart
									- Viewing transactions
	d. Marketer.java

		This class initializes the data analytics and marketing research. This allows the markerter
		to run a reporta and then edit or refine it for more or less detail. 
		

*****************
* 2. Usage	*
*****************

	a. Initial Application Screen

		Upon being prompt, the user will be asked to enter the password to my 
		Enter the command to the option you want to choose. Each option takes you to the appropriate 
		interface 
            <customer>  --> Opens Customer Interface
            <storemanager> -->  Opens Store Manager Interface
            <marketer> --> Opens Marketer Interface
            <quit> -->  Quits the Program 


	b. StoreManager Interface

		Upon being prompted, an Employee ID between 30000 and 30011 can be entered in order to login.
		Each store has two Store Managers. An example of the display for login would be using Employee ID 30011 (in honor of Star Wars): 

			Welcome Han Solo.
			You are currently located in Sephora's store  with Store ID number 10005.
			Your current phone number is (471)321-3126.

		Enter the command corresponding to the option you want to choose. 
 				
                <view inventory> --> View the inventory of the store that the Store Manager works for
                <view orders> --> View the Order Histories of products of the store
                <edit price> --> Edit the prices of products on the shelves of the store the Store Manager works for
                <edit min> --> Edit minimum levels that are recorded in the store's inventory of each products
                <edit phone> --> Edit Phone to a more current phone number
                <exit> --> Exit Store Manager menu

            Within view orders, the user can check on individual shipments and their expected shipment dates to see if the reorder has arrived. If it has arrived and already been restocked, it automatically accounts for that, else the shipment will restock the shelves. However, if their is a reorder sent out, the Store Manager Interface is the ONLY interface can can initilize the restocking process.

	c. Customer Interface

		Upon being prompted, the customer will identify whether they are located at
		an Online Store or at one of Sephora's five Physical store fronts. 
		
		i.  Online Store
			 The user entering an online store can only be a loyalty member or a Beauty Insider. The Store ID for an online store is 10000.

                <beautyinsider> --> Beauty Insider
                <new> --> New Member                
			
					a. Beauty Insider

									<try again> --> Reinitialize Beauty Insider
						            <create> --> Become a New Member

					An example Customer ID to use is Customer ID 65. The Beauty Inisder Joe Average has been my test dummy though this whole process; however the first 1-66 Customer IDs are all Beauty Insiders. Not all of them have made purchases, but are all viable and ready. Beauty Insider Customer IDs that have been used for the already generated transactions are 4, 8, 30, 31, 49, 63, and 65.

					b. New Member 

									Prompts the customer for information needed to create a Beauty Insider account. It will generate a Customer ID that is EXTREMELY IMPORTANT!



				Once a Beauty Insider or loyalty member of Sephora, the customer has two menu options:

					c. Customer Menu

									<view products> -->  View Products
            						<open cart> --> Open Shopping Cart
            						<view transactions> -->  View Transactions

					d. Account Management
						 
						            <view purchase> --> View Previous Purchases 
						            <view email> --> View Current Email Address
						            <edit phone> --> Edit Phone Number
						            <edit card> --> Edit Credit Cart
					                <exit> --> Return to Beauty Insider Menu

		ii. Physical Store
			 Upon being prompted, the customer will choose which Physical store fronts of Sephora's five they are currently shopping at. It will pull up a list of locations and Store IDs for the user to select one. Once selected, the user is redirected to the Customer Main Menu. I relieved too late that Store ID has a glitch and that if the customer does not input the correct Store ID as seen on the screen, the customer will be stuck in an infinite loop. Hopefully, it will be fixed before submission, but keep that in mind when playing around with my code. THIS IS REALLY IMPORTANT TO NOTE.
			

			 Enter the command corresponding to the option you want to choose.

				<view products> -->  View Products
            	<open cart> --> Open Shopping Cart
            	<view transactions> -->  View Transactions
		
					a. View Products

							<makeup> --> Makeup Products
				            <skin> --> Skin Products
				            <fragrance> --> Fragrance Products
				            <exit> --> Return to Customer Menu
						
				        i. Makeup

				        	<Nail> --> Nail
				            <Lip> --> Lip
				            <Face> --> Face
				            <Eye> --> Eye
				            <exit> --> Return to Product Menu

				        ii. Skin

				        	<Cleanse> --> Cleanse
				            <Treat> --> Treat
				            <Moisturize> --> Moisturize
				            <exit> --> Return to Product Menu

				        iii. Fragrance

				        	<Men> --> Men
				            <Women> --> Women
				            <exit> --> Return to Product Menu

				        Choosing either Makeup, Skin or Fragrance will prompt a list of the subtypes of that type of product (as shown above). Once selected, the user will be prompted about a Cart ID and then adding products to the shopping cart.

					b. Open Cart

							<view cart> --> View Shopping Cart
			            	<edit cart> --> Edit Shopping Cart
			            	<exit> --> Return to Customer Menu
							
						Viewing shopping cart shows the customer a view of the products that are currently going to be purchased.
						Editting shooping cart allows the customer to either adjust the amount of a product already in the shopping cart or delete a product all together.

					c. View Transactions

					        <balance> --> View Subtotal
			            	<checkout> --> Proceed to Checkout
			            	<exit> --> Return to Customer Menu

			            Balance just shows the user a subtotal of the products that are currently in the shopping cart.
			            Within checkout, the user is prompted for payment type. The customer can either pay with cash, credit or Beauty Insider account information. Once you proceed with checkout, there is no going back into the store.
	
	d. Marketer Interface

		Upon being prompted, the user be given a menu to choose from.

		Enter the command corresponding to the option you want to choose.

				<most used> --> Top Ten Most Used Vendors
				<stores> --> Ranking of Sephora Stores
				<customers> --> Ranking of Sephora Customers
				<products> --> Ranking of Sephora Products
				<leave> --> Leave Sephora

		i.  Most Used
		
				This report analyzes the vendors. It gives rankings of the top ten most used vendors of the Sephora stores.

		ii. Stores

				This report analyzes the stores. It gives rankings of the stores with the most revenue and the most purchases.

		iii. Customers

				This report analyzes the customers. It givess rankings of the customers with the most purchases.

		iv. Products

				This report analyzes the products. It gives rankings of the products with the most reorders. It will also prompt the user for a product ID and will give reports on that product.


*************************
* 3. Other Notes	    *
*************************

The program is written in such way so that all menus are reprinted after a command is reprinted. For example after adding a product to a cart, the user is sent back to their investment account where they originally selected the option to buy stock.To go back menus, the user typically has the option of entering the <exit> command.The login screen contains the <quit> command to quit out of the program. I did my best to account for and handle any bad input such as negative numbers when entering amounts, invalid ID's, or garbage that might be entered in. When such instances of bad input occurs, the user is always re-prompted to enter valid input.I really tried using while statements and with my command reprinting prompts ignoring certain upper/lower case scenarios; however try to stick with what is inside the <...> command. 

  a. Case Sensitive 

  		In customer interface, when the user has viewed the products, then viewed either makeup, skin, or fragrance, the user is given a list of subtypes of the type of product the user further specified. Product subtype is very heavily case sensitive.The user should type the <...> command prompt as seen. 

  b. Cart ID

  		When the user is a returning customer and not loyalty member, 
  		the customer DOES NOT have the same Cart ID. However, online customers can
  		view preview purchases in account management. The Cart IDs from previous
  		transactions are stored there to be able to see the products the user
  		has purchased before. If a Beauty Insider is shopping Online and didn't finish
  		their transaction, the Card ID is still accessable if the customer restarts the
  		program.

  c. Typing Errors

  		In my program, the user might be prompted to re-type a valid request or input without receiving an error message. My code has some flaws. To get it running properly, I had to sacrifice a few error messsages. 

  d. Checkout

  		Within the customer interface, checkout automatically quits the program once you finish your transaction. An important thing to note is that when you are a customer who is paying with credit and you haven't shopped here yet, the first input for credit card could then just be a line of 1111111111111111. It would be a formality. To get your credit card and information into the system, the customer would then be prompted to enter their credit card information afterwards. 

  e. Transaction IDs

  		I have manually created 30 transactions. The customers are a variety of Beauty Insiders from the Online Store to customers who pay in Cash or Credit Card. There are Order Histories in progress that the Store Manager needs to deal with and the rest is in your hands. Have fun!