package Reception;

import java.io.*;
import java.net.URL;
import java.util.*;
import Reception.*;

public class Reasoner {
	// The main Class Object holding the Domain knowledge

	// Generate the classes automatically with: Opening a command console and
	// type:
	// Path to YOUR-PROJECTROOT-IN-WORKSPACE\xjc.bat yourschemaname.xsd -d src
	// -p yourclasspackagename
	
	
	public Hotel reception; //This is a candidate for a name change

	public MainReception Myface;

	// The lists holding the class instances of all domain entities

	public List receptionList = new ArrayList(); //This is a candidate for a name change
	public List theRoomList = new ArrayList();    //This is a candidate for a name change
	public List theCustomerList = new ArrayList();  //This is a candidate for a name change
	public List theAmenityList = new ArrayList(); //This is a candidate for a name change
	public List theBookingList = new ArrayList(); //This is a candidate for a name change
	public List theRecentThing = new ArrayList();
	public List help = new ArrayList();

	// Gazetteers to store synonyms for the domain entities names

	public Vector<String> HotelSyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> RoomSyn = new Vector<String>();     //This is a candidate for a name change
	public Vector<String> CustomerSyn = new Vector<String>();   //This is a candidate for a name change
	public Vector<String> AmenitySyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> BookingSyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> RecentObjectSyn = new Vector<String>();
	public Vector<String> HelpSyn = new Vector<String>();

	public String questiontype = "";         // questiontype selects method to use in a query
	public List classtype = new ArrayList(); // classtype selects which class list to query
	public String attributetype = "";        // attributetype selects the attribute to check for in the query

	public Object Currentitemofinterest; // Last Object dealt with
	public Integer Currentindex;         // Last Index used

	public String tooltipstring = "";
	public String URL = "";              // URL for Wordnet site
	public String URL2 = "";             // URL for Wikipedia entry

	public Reasoner(MainReception myface) {

		Myface = myface; // reference to GUI to update Tooltip-Text
		// basic constructor for the constructors sake :)
	}

	public void initknowledge() { // load all the library knowledge from XML 

		JAXB_XMLParser xmlhandler = new JAXB_XMLParser(); // we need an instance of our parser

		//This is a candidate for a name change
		File xmlfiletoload = new File("Library.xml"); // we need a (CURRENT)  file (xml) to load  

		// Init synonmys and typo forms in gazetteers

		HotelSyn.add("room");   	//This is a candidate for a name change
		HotelSyn.add("place");		//This is a candidate for a name change
		HotelSyn.add("bookstore");	//This is a candidate for a name change
		HotelSyn.add("bookhouse"); 	//This is a candidate for a name change
		HotelSyn.add("hotel");		//This is a candidate for a name change
		HotelSyn.add("hotels");		//This is a candidate for a name change
		HotelSyn.add("help");			//This is a candidate for a name change

		RoomSyn.add("room");    //All of the following is a candidate for a name change

		

		CustomerSyn.add("Amenity"); //All of the following is a candidate for a name change
		CustomerSyn.add("services");
		CustomerSyn.add("service");
		CustomerSyn.add("dining");
		CustomerSyn.add("pool");
		CustomerSyn.add("excersise");

		AmenitySyn.add("pool");  //All of the following is a candidate for a name change
		AmenitySyn.add("disco");
		AmenitySyn.add("inventor");

		BookingSyn.add(" lending");   //All of the following is a candidate for a name change

		RecentObjectSyn.add(" this");   //All of the following is a candidate for a name change
		RecentObjectSyn.add(" that");
		RecentObjectSyn.add(" him");
		RecentObjectSyn.add(" her");	// spaces to prevent collision with "wHERe"	
		RecentObjectSyn.add(" it");

		try {
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload); // initiate input stream

			reception = xmlhandler.loadXML(readthatfile);

			// Fill the Lists with the objects data just generated from the xml

			theRoomList = reception.getRoom();  		//This is a candidate for a name change
			theCustomerList = reception.getCustomer(); 	//This is a candidate for a name change
			theAmenityList = reception.getAmenity(); 	//This is a candidate for a name change
			theBookingList = reception.getBooking(); 	//This is a candidate for a name change
			receptionList.add(reception);             // force it to be a List, //This is a candidate for a name change

			System.out.println("List reading");
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in init");
		}
	}

	

	public  Vector<String> generateAnswer(String input) // Generate an answer (String Vector)
	{ 

		Vector<String> out = new Vector<String>();
		out.clear();                 // just to make sure this is a new and clean vector
		
		questiontype = "none";

		Integer Answered = 0;        // check if answer was generated

		Integer subjectcounter = 0;  // Counter to keep track of # of identified subjects (classes)
		
		// Answer Generation Idea: content = Questiontype-method(classtype class) (+optional attribute)

		// ___________________________ IMPORTANT _____________________________

		input = input.toLowerCase(); // all in lower case because thats easier to analyse
		
		// ___________________________________________________________________

		String answer = "";          // the answer we return

		// ----- Check for the kind of question (number, location, etc)------------------------------

		if (input.contains("how many")){questiontype = "amount"; input = input.replace("how many", "<b>how many</b>");} 
		if (input.contains("number of")){questiontype = "amount"; input = input.replace("number of", "<b>number of</b>");}
		if (input.contains("amount of")){questiontype = "amount"; input = input.replace("amount of", "<b>amount of</b>");} 
		if (input.contains("count")){questiontype = "amount"; input = input.replace("count", "<b>count</b>");}

		if (input.contains("what kind of")){questiontype = "list"; input = input.replace("what kind of", "<b>what kind of</b>");}
		if (input.contains("list all")){questiontype = "list"; input = input.replace("list all", "<b>list all</b>");}
		if (input.contains("diplay all")){questiontype = "list"; input = input.replace("diplay all", "<b>diplay all</b>");}
		
		if (input.contains("locate")){questiontype = "checkfor"; input = input.replace("locate", "<b>locate</b>");}
		if (input.contains("is there a")){questiontype = "checkfor"; input = input.replace("is there a", "<b>is there a</b>");}
		if (input.contains("i am searching")){questiontype = "checkfor"; input = input.replace("i am searching", "<b>i am searching</b>");}
		if (input.contains("i am looking for")){questiontype = "checkfor"; input = input.replace("i am looking for", "<b>i am looking for</b>");}
		if (input.contains("do you have")&&!input.contains("how many")){questiontype = "checkfor";input = input.replace("do you have", "<b>do you have</b>");}
		if (input.contains("i look for")){questiontype = "checkfor"; input = input.replace("i look for", "<b>i look for</b>");}
		if (input.contains("is there")){questiontype = "checkfor"; input = input.replace("is there", "<b>is there</b>");}

		if (input.contains("where") 
				|| input.contains("can't find")
				|| input.contains("can i find") 
				|| input.contains("way to"))

		{
			questiontype = "location";
			System.out.println("Find Location");
		}
		
		//Command - [Can i] - Start
		if (input.contains("can i book") 
				|| input.contains("can i reserve")
				|| input.contains("can i book a")
				|| input.contains("am i able to")
				|| input.contains("could i book") 
				|| input.contains("i want to book"))

		{
			questiontype = "intent";
			System.out.println("Find BookAvailability");
		}
		//Command - [Can i] - End
		
		//Command - [Thank you] - Start
		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("cool thank")) 			
			{
				questiontype = "farewell";
				System.out.println("farewell");
			}
		//Command - [Thank you] - End
		
		
		//Command - [Help] - Start
		if (input.contains("Help") 
				|| input.contains("?")
				|| input.contains("help")
				|| input.contains("Help"))
			{
				questiontype = "Help";
				System.out.println("Help");
			}
		//Command - [Help] - End
		
		
		//Command - [Exit] - Start
		if (input.contains("Exit") 
				|| input.contains("exit")
				|| input.contains("Exit"))
			{
				questiontype = "Exit";
				System.out.println("Exit");
			}
		//Command - [Exit] - End
		
		//Command - [CLS] - Start
		if (input.contains("cls") 
				|| input.contains("Clean")
				|| input.contains("clean"))
			{
				questiontype = "CLS";
				System.out.println("CLS");
			}
		//Command - [CLS] - End
		
		
		//Check - [Command Subject] - Start
		for (int x = 0; x < RoomSyn.size(); x++) {   //This is a candidate for a name change
			if (input.contains(RoomSyn.get(x))) {    //This is a candidate for a name change
				classtype = theRoomList;             //This is a candidate for a name change
				
				input = input.replace(RoomSyn.get(x), "<b>"+RoomSyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Book recognised.");
			}
		}
		for (int x = 0; x < CustomerSyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(CustomerSyn.get(x))) {   //This is a candidate for a name change
				classtype = theCustomerList;            //This is a candidate for a name change
				
				input = input.replace(CustomerSyn.get(x), "<b>"+CustomerSyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Member recognised.");
			}
		}
		for (int x = 0; x < AmenitySyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(AmenitySyn.get(x))) {   //This is a candidate for a name change
				classtype = theAmenityList;            //This is a candidate for a name change
				
				input = input.replace(AmenitySyn.get(x), "<b>"+AmenitySyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Catalog recognised.");
			}
		}
		for (int x = 0; x < BookingSyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(BookingSyn.get(x))) {   //This is a candidate for a name change
				classtype = theBookingList;            //This is a candidate for a name change
				
				input = input.replace(BookingSyn.get(x), "<b>"+BookingSyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Lending recognised.");
			}
		}
		
		if(subjectcounter == 0){
			for (int x = 0; x < RecentObjectSyn.size(); x++) {  
				if (input.contains(RecentObjectSyn.get(x))) {
					classtype = theRecentThing;
					
					input = input.replace(RecentObjectSyn.get(x), "<b>"+RecentObjectSyn.get(x)+"</b>");
					
					subjectcounter = 1;
					System.out.println("Class type recognised as"+RecentObjectSyn.get(x));
				}
			}
		}
		//Check - [Command Subject] - End
		
		
		// More than one subject in question + Library ...
		// "Does the Library has .. Subject 2 ?"
		
		//Check - [Command Subject (More than one Subject)] - Start
		System.out.println("subjectcounter = "+subjectcounter);

		for (int x = 0; x < HotelSyn.size(); x++) {  //This is a candidate for a name change

			if (input.contains(HotelSyn.get(x))) {   //This is a candidate for a name change

				// Problem: "How many Books does the Library have ?" -> classtype = Library
				// Solution:
				
				if (subjectcounter == 0) { // Library is the first subject in the question
					
					input = input.replace(HotelSyn.get(x), "<b>"+HotelSyn.get(x)+"</b>");
					
					classtype = receptionList;        //This is a candidate for a name change

					System.out.println("class type Library recognised");		

				}
			}
		}
		//Check - [Command Subject (More than one Subject)] - End
		
		// Compose Method call and generate answerVector
		
		//Response - [Number of Rooms] - Start
		if (questiontype == "amount") { // Number of Subject

			Integer numberof = Count(classtype);

			answer=("There are " + numberof + " Rooms Available" );

			Answered = 1; // An answer was given

		}
		//Response - [Number of Rooms] - End
		
		
		if (questiontype == "list") { // List all Subjects of a kind

			answer=("You asked for the listing of all "
					+ classtype.get(0).getClass().getSimpleName() + "s. <br>"
					+ "We have the following "
					+ classtype.get(0).getClass().getSimpleName() + "s:"
					+ ListAll(classtype));
			Answered = 1; // An answer was given

		}

		if (questiontype == "checkfor") { // test for a certain Subject instance

			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			Answered = 1; // An answer was given
			if (check.size() > 1) {
				Currentitemofinterest = classtype.get(Integer.valueOf(check
						.get(1)));
				System.out.println("Classtype List = "
						+ classtype.getClass().getSimpleName());
				System.out.println("Index in Liste = "
						+ Integer.valueOf(check.get(1)));
				Currentindex = Integer.valueOf(check.get(1));
				theRecentThing.clear(); // Clear it before adding (changing) the
				// now recent thing
				theRecentThing.add(classtype.get(Currentindex));
			}
		}

		// Location Question in Pronomial form "Where can i find it"

		if (questiontype == "location") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Location(classtype, input));

			Answered = 1; // An answer was given
		}

		if ((questiontype == "intent" && classtype == theRoomList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// Can I lend the book or not (Can I lent "it" or not)
			answer=("Reception: "+ RoomAvailable(classtype, input));
			Answered = 1; // An answer was given
		}

		//Response - [Thank You] - Start
		if (questiontype == "farewell") {
			String name = System.getenv("USERNAME");
			answer=("You are very welcome"+name);

			Answered = 1; // An answer was given
		}
		//Response - [Thank You] - Start
		
		
		//Response - [Help] - Start
		if (questiontype == "Help") 
			{       
				answer= "<br>" +
						"You can use following commands:  " 
						+ "<br>" +
						"---------------------------------------"
						+ "<br>" +  
						"Exit: Quit the program" + "<br>" +
						"CLS: Clear the screen"
						+ "<br>" + "<br>" + "<br>" +
						"Also you can ask following questions:  "
						+ "<br>" + 
						"---------------------------------------"
						+ "<br>" +
						"- where is the hotel"
						+ "<br>" +
						"- I am looking for a double room"
						+ "<br>" +
						"- What kind of rooms are available"
						+ "<br>" +
						"- Where are the room locations"
						+ "<br>" +
						"- How many rooms are available" 
						+ "<br>" +
						"- Can i book a room"
						+ "<br>" 
						;
				Answered = 1; // An answer was given
			}
		
		if (questiontype == "?") 
			{       
				answer=("You can type: ");
				Answered = 1; // An answer was given
			}
		//Response - [Help] - End		
		
		
		//Response - [Exit] - Start
		if (questiontype == "Exit") 
			{    
				System.exit(0);
			}
		//Response - [Exit] - End
		
		//Response - [CLS] - Start
		if ((questiontype == "CLS") || (questiontype == "CLEAN"))
			{    
			
			Reception.MainReception.Info.setText("<font face=\"Verdana\">Background information about the conversations topic will be displayed in this window.");
			Reception.MainReception.dialoghistory.removeAllElements();
			Reception.MainReception.dialoghistory.add("<H2><font face=\"Verdana\">Welcome to the Hotel Reception Helpdesk, please type your question.</H2> " +
			          "<H3><font face=\"Verdana\">Following services are available: Available Rooms, Bookings, Checkin and Checkouts, " +
				      "Just ask me.</H3><br>" +
			          "<H3><font face=\"Verdana\">To Start, you can type help to explore more. </H3><br>");
			Answered = 1;
			
			}
		//Response - [CLS] - End
		
		//Response - [Null] - Start
		if (Answered == 0)
			{ 
				answer=("Sorry I didn't understand that." + "<br> " + "You can type [ Help ] for more information and list of commands.");
			}

			out.add(input);
			out.add(answer);
			return out;
	}
		//Response - [Null] - End
	

	
	
	
	// Methods to generate answers for the different kinds of Questions
	// Answer a question of the "Is a book or "it (meaning a book) available ?" kind
	public String RoomAvailable(List thelist, String input) 
	{

		boolean available =true;
		String answer ="";
		Room curbook = new Room();
		String booktitle="";

		if (thelist == theRoomList) {                      //This is a candidate for a name change

			int counter = 0;

			//Identify which book is asked for 

			for (int i = 0; i < thelist.size(); i++) {

				curbook = (Room) thelist.get(i);         //This is a candidate for a name change

				if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = theRoomList;                                    //This is a candidate for a name change
					theRecentThing.add(classtype.get(Currentindex));
					booktitle=curbook.getTitle();
										
					if (input.contains(curbook.getTitle().toLowerCase())){input = input.replace(curbook.getTitle().toLowerCase(), "<b>"+curbook.getTitle().toLowerCase()+"</b>");}          
					if (input.contains(curbook.getIsbn().toLowerCase())) {input = input.replace(curbook.getIsbn().toLowerCase(), "<b>"+curbook.getIsbn().toLowerCase()+"</b>");}     
					if (input.contains(curbook.getAutor().toLowerCase())){input = input.replace(curbook.getAutor().toLowerCase(), "<b>"+curbook.getAutor().toLowerCase()+"</b>");}
										
					i = thelist.size() + 1; 									// force break
				}
			}
		}

		
		// maybe other way round or double 
		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("room")) {                  //This is a candidate for a name change

				curbook = (Room) theRecentThing.get(0);               //This is a candidate for a name change		
				booktitle=curbook.getTitle();
			}
		}

		
		// check all lendings if they contain the books ISBN
		for (int i = 0; i < theBookingList.size(); i++) {

			Booking curlend = (Booking) theBookingList.get(i);         //This is a candidate for a name change

			// If there is a lending with the books ISBN, the book is not available

			if ( curbook.getIsbn().toLowerCase().equals(curlend.getIsbn().toLowerCase())) {           //This is a candidate for a name change

				input = input.replace(curlend.getIsbn().toLowerCase(), "<b>"+curlend.getIsbn().toLowerCase()+"</b>");
				
				available=false;
				i = thelist.size() + 1; 									// force break
			}
		}

		if(available){
			answer="There are rooms available to book";
		}
		else{ 
			answer="Sorry that type of rooms are fully booked";
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ booktitle;
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return(answer);

	}

	
	//Response Web Area - [Number of Rooms] - Start
	public Integer Count(List thelist) { // List "thelist": List of Class Instances (e.g. theRoomList)

		//URL = "http://en.wiktionary.org/wiki/"		

		//URL = "http://moafaq.com/HotelRec/room.html";
		//URL2 = "http://moafaq.com/HotelRec/room.html";
		URL = "http://osm.org/go/euu4KvUh--?relation=65606";
		URL2 = "http://osm.org/go/euu4KvUh--?relation=65606";
		
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return thelist.size();
	}
	//Response Web Area - [Number of Rooms] - Start
	
	
	// Answer a question of the "What kind of..." kind
	public String ListAll(List thelist) {

		String listemall = "<ul>";

		if (thelist == theRoomList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Room curbook = (Room) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curbook.getTitle() + "</li>");    //This is a candidate for a name change
			}
		}

		if (thelist == theCustomerList) {                                //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Customer curmem = (Customer) thelist.get(i);               //This is a candidate for a name change
				listemall = listemall + "<li>"                         //This is a candidate for a name change
						+ (curmem.getSurname() + " " + curmem.getLastname() + "</li>");  //This is a candidate for a name change
			}
		}

		if (thelist == theAmenityList) {                               //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Amenity curcat = (Amenity) thelist.get(i);             //This is a candidate for a name change
				listemall = listemall 
						+ "<li>" + (curcat.getName() + "</li>");      //This is a candidate for a name change
			}
		}
		
		if (thelist == theBookingList) {                               //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Booking curlend = (Booking) thelist.get(i);             //This is a candidate for a name change
				listemall = listemall + "<li>" 
						+ (curlend.getIsbn() + "</li>");                //This is a candidate for a name change
			}
		}
		
		listemall += "</ul>";

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);
		
		return listemall;
	}

	
	// Answer a question of the "Do you have..." kind
	public Vector<String> CheckFor(List thelist, String input) {

		Vector<String> yesorno = new Vector<String>();
		if (classtype.isEmpty()){
			yesorno.add("Sorry I didn't understand that." + "<br> " + "You can type [ Help ] for more information and list of commands.");
		} else {
			yesorno.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (thelist == theRoomList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Room curbook = (Room) thelist.get(i);                           //This is a candidate for a name change

				if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Requested Room is Available.");                  //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1; // force break
				}
			}
		}

		if (thelist == theCustomerList) {                                      //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Customer curmem = (Customer) thelist.get(i);                      //This is a candidate for a name change
				if (input.contains(curmem.getSurname().toLowerCase())         //This is a candidate for a name change
						|| input.contains(curmem.getLastname().toLowerCase()) //This is a candidate for a name change
						|| input.contains(curmem.getCity().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we do have such a Amenitiy");               //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (thelist == theAmenityList) {                                    //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Amenity curcat = (Amenity) thelist.get(i);                  //This is a candidate for a name change
				if (input.contains(curcat.getName().toLowerCase())          //This is a candidate for a name change
						|| input.contains(curcat.getUrl().toLowerCase())) { //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a room");           //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}
		
		if (thelist == theBookingList) {                                     //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Booking curlend = (Booking) thelist.get(i);                  //This is a candidate for a name change
				if (input.contains(curlend.getIsbn().toLowerCase())          //This is a candidate for a name change
					|| input.contains(curlend.getMemberid().toLowerCase())){ //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Lending");            //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (classtype.isEmpty()) {
			System.out.println("Not class type given.");
		} else {
			URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			System.out.println("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL2);
		}
	
		return yesorno;
	}

	
	
	
	//  Method to retrieve the location information from the object (Where is...) kind
	public String Location(List classtypelist, String input) {

		List thelist = classtypelist;
		String location = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("room")) {                  //This is a candidate for a name change

				Room curbook = (Room) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curbook.getLocation() + " ");             //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("member")) {               //This is a candidate for a name change

				Customer curmem = (Customer) theRecentThing.get(0);      //This is a candidate for a name change
				location = (curmem.getCity() + " " + curmem.getStreet() + " " + curmem  //This is a candidate for a name change
						.getHousenumber());                                    //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()  
					.toLowerCase().equals("catalog")) {                 //This is a candidate for a name change

				Amenity curcat = (Amenity) theRecentThing.get(0);       //This is a candidate for a name change
				location = (curcat.getLocation() + " ");                //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("library")) {                  //This is a candidate for a name change

				location = (reception.getCity() + " " + reception.getStreet() + reception   //This is a candidate for a name change
						.getHousenumber());                                           //This is a candidate for a name change
			}

		}

		// if a direct noun was used (book, member, etc)

		else {

			if (thelist == theRoomList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Room curbook = (Room) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curbook.getLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theRoomList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

			if (thelist == theCustomerList) {                                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Customer curmember = (Customer) thelist.get(i);         				  //This is a candidate for a name change

					if (input.contains(curmember.getSurname().toLowerCase())              //This is a candidate for a name change
							|| input.contains(curmember.getLastname().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curmember.getMemberid().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						location = (curmember.getCity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 										// Clear it before adding (changing) the
						classtype = theCustomerList;            	 						//This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 				             	        // force break
					}
				}
			}

			if (thelist == theAmenityList) {                                       	 //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Amenity curcatalog = (Amenity) thelist.get(i);                    //This is a candidate for a name change

					if (input.contains(curcatalog.getName().toLowerCase())            //This is a candidate for a name change						     
							|| input.contains(curcatalog.getUrl().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						location = (curcatalog.getLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear();                                      // Clear it before adding (changing) the	
						classtype = theAmenityList;                                  //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1;                                      // force break
					}
				}
			}

			if (thelist == receptionList) {                                                  //This is a candidate for a name change

				location = (reception.getCity() + " " + reception.getStreet() + reception  //This is a candidate for a name change
						.getHousenumber());                                                   //This is a candidate for a name change
			}
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return location;
	}

	
	
	
	public String testit() {   // test the loaded knowledge by querying for books written by dostoyjewski

		String answer = "";

		System.out.println("room List = " + theRoomList.size());  //This is a candidate for a name change

		for (int i = 0; i < theRoomList.size(); i++) {   // check each book in the List, //This is a candidate for a name change

			Room curbook = (Room) theRoomList.get(i);    // cast list element to Book Class //This is a candidate for a name change												
			System.out.println("Testing Book" + curbook.getAutor());

			if (curbook.getAutor().equalsIgnoreCase("dostoyjewski")) {     // check for the author //This is a candidate for a name change

				answer = "A book written by " + curbook.getAutor() + "\n"  //This is a candidate for a name change
						+ " is for example the classic " + curbook.getTitle()      //This is a candidate for a name change
						+ ".";
			}
		}
		return answer;
	}

	
	
	
	public String readwebsite(String url) {

		String webtext = "";
		try {
			BufferedReader readit = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			String lineread = readit.readLine();

			System.out.println("Reader okay");

			while (lineread != null) {
				webtext = webtext + lineread;
				lineread = readit.readLine();				
			}

			// Hard coded cut out from "wordnet website source text"
			//Check if website still has this structure	


			webtext = webtext.substring(webtext.indexOf("<ul>"),webtext.indexOf("</ul>"));

			webtext = "<table width=\"700\"><tr><td>" + webtext
					+ "</ul></td></tr></table>";

		} catch (Exception e) {
			webtext = "Not yet";
			System.out.println("Error connecting to wordnet");
		}
		return webtext;
	}
}
