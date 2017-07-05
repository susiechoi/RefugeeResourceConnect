public class Request implements Comparable<Request> {

	String theRecipientEmail;
	Long theTime;

	public Request(String recipientEmail, long time){
		theRecipientEmail = recipientEmail;
		theTime = time;
	}

	public int compareTo(Request r){
		return this.theTime.compareTo(r.theTime);
	}

}
