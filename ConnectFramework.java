import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;
import javax.swing.JOptionPane;

public class ConnectFramework { 

	public static TreeMap<String, HashSet<Request>> requestIn = new TreeMap<String, HashSet<Request>>();
	public static TreeMap<RequestID, String> requestOut = new TreeMap<RequestID, String>();

	// send in new request
	// by adding requester's email to requestIn map as a value (with requested item as key)
	// and creating unique requestID in requestOut map
	public static void sendRequest(String request, String recipientEmail, long time){
		if (! requestIn.containsKey(request)){
			requestIn.put(request, new HashSet<Request>());
			requestOut.put(new RequestID(request, recipientEmail, time), null);
		}
		requestIn.get(request).add(new Request(recipientEmail, time));
	}

	// view (i.e. print) all requested items
	public static void viewRequests(){
		for (String aRequest : requestIn.keySet()){
			if (requestIn.get(aRequest).size() != 0){
				System.out.println(requestIn.get(aRequest).size()+" requests for "+
						aRequest+" have been made.");
			}
		}
	}

	// view (i.e. print) a given requested item
	public static void viewRequestsFor(String request){
		for (String aRequest : requestIn.keySet()){
			if (aRequest.equals(request)){
				System.out.println(requestIn.get(aRequest).size()+" requests for "
						+request+" have been made.");
				return;
			}
		}
		System.out.println("0 requests for "+request+" have been made.");
	}

	// mark a request as fulfilled:
	// find the earliest request (defined by natural ordering of requests)
	// and set its value equal to the email of the patron who will fulfill the request
	public static void fulfillRequest(String toFulfill, String patronEmail){
		for (RequestID aRequestID : requestOut.keySet()){
			if (aRequestID.theItem.equals(toFulfill)){
				requestIn.get(toFulfill).remove(new Request(aRequestID.theRecipientEmail, aRequestID.theTime));
				requestOut.put(aRequestID, patronEmail);
				return;
			}
		}
		System.out.println("No requests for "+toFulfill+" currently exist. "
				+ "We appreciate your generous intent!");
	}

	// modified from https://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/
	public static void pseudoSerializeIn(TreeMap<String, HashSet<Request>> requestInMap){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter("data/savedRequestIn.txt");
			bw = new BufferedWriter(fw);
			for (String key : requestInMap.keySet()){
				bw.write(key+"~");
				for (Request value : requestInMap.get(key)){
					bw.write(value.theRecipientEmail+"~"+value.theTime+"\n");
				}
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally{
			try{
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public static void pseudoDeserializeIn(){
		TreeMap<String, HashSet<Request>> requestInMap = new TreeMap<String, HashSet<Request>>();
		Scanner inFile = null;
		try{
			inFile = new Scanner(new File("data/savedRequestIn.txt"));
		}
		catch (IOException e){
			e.printStackTrace();
		}
		while (inFile.hasNext()){
			String[] splitLine = inFile.nextLine().split("~");
			String key = splitLine[0];
			HashSet<Request> value = new HashSet<Request>();
			for (int i=1; i<splitLine.length; i+=2){
				value.add(new Request(splitLine[i], Long.parseLong(splitLine[i+1])));
			}
			requestInMap.put(key, value);
		}
		inFile.close();
		requestIn = requestInMap;
	}

	public static void pseudoSerializeOut(TreeMap<RequestID, String> requestOutMap){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter("data/savedRequestOut.txt");
			bw = new BufferedWriter(fw);
			for (RequestID key : requestOutMap.keySet()){
				bw.write(key.theItem+"~"+key.theRecipientEmail+"~"+key.theTime+"~");
				bw.write(requestOutMap.get(key)+"\n");
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally{
			try{
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public static void pseudoDeserializeOut(){
		TreeMap<RequestID, String> requestOutMap = new TreeMap<RequestID, String>();
		Scanner inFile = null;
		try{
			inFile = new Scanner(new File("data/savedRequestOut.txt"));
		}
		catch (IOException e){
			e.printStackTrace();
		}
		while (inFile.hasNext()){
			String[] splitLine = inFile.nextLine().split("~");
			RequestID key = new RequestID(splitLine[0], splitLine[1], Long.parseLong(splitLine[2]));
			String value = splitLine[3];
			requestOutMap.put(key, value);
		}
		inFile.close();
		requestOut = requestOutMap;
	}

	public static void clearFile(String fileName){
		FileWriter fw = null;
		PrintWriter pw = null;
		try{
			fw = new FileWriter("data/"+fileName, false);
			pw = new PrintWriter(fw, false);
			pw.flush();
			pw.close();
			fw.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		String intent = JOptionPane.showInputDialog
				("Enter 'R' to submit a new request, 'V' to view all outstanding requests, "
						+ "'V1' to view outstanding requests for a specific item, "
						+ "and 'F' to fulfill a request.", "R");
		if (intent.equals("R")){
			String requestedItem = JOptionPane.showInputDialog("What item do you need?", "canned food");
			String requesterEmail = JOptionPane.showInputDialog("Type in your email.", "johndoe@example.com");
			long timeOfRequest = System.currentTimeMillis();
			pseudoDeserializeIn();
			pseudoDeserializeOut();
			clearFile("savedRequestIn.txt");
			clearFile("savedRequestOut.txt");
			sendRequest(requestedItem.toLowerCase(), requesterEmail, timeOfRequest);
			pseudoSerializeIn(requestIn);
			pseudoSerializeOut(requestOut);
			System.out.println("HERE IS THE REQUEST OUT MAP:");
			for (RequestID key : requestOut.keySet()){
				System.out.println(key.theItem+" "+key.theRecipientEmail+" "+key.theTime);
				System.out.println(requestOut.get(key));
			}
			System.out.println("Your request has been submitted. "
					+ "We will let you know when we can fulfill it!");
		}
		if (intent.equals("V")){
			pseudoDeserializeIn();
			viewRequests();
		}
		if (intent.equals("V1")){
			pseudoDeserializeIn();
			String request = JOptionPane.showInputDialog
					("For which item would you like to view outstanding requests?","canned food");
			viewRequestsFor(request.toLowerCase());
		}
		if (intent.equals("F")){
			pseudoDeserializeIn();
			pseudoDeserializeOut();
			clearFile("savedRequestIn.txt");
			clearFile("savedRequestOut.txt");
			String toFulfill = JOptionPane.showInputDialog("What item would you like to donate?", "canned food");
			String patronEmail = JOptionPane.showInputDialog("Type in your email.", "johndoe@example.com");
			fulfillRequest(toFulfill, patronEmail);
			pseudoSerializeIn(requestIn);
			pseudoSerializeOut(requestOut);
			System.out.println("HERE IS THE REQUEST OUT MAP:");
			for (RequestID key : requestOut.keySet()){
				System.out.println(key.theItem+" "+key.theRecipientEmail+" "+key.theTime);
				System.out.println(requestOut.get(key));
			}
			System.out.println("We will be contacting you shortly. "
					+ "Thank you so much for your generosity!");
		}
	}
}
