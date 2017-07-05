public class RequestID implements Comparable<RequestID> {

	String theItem;
	String theRecipientEmail;
	Long theTime;

	public RequestID(String item, String recipientEmail, long time) {
		theItem = item;
		theRecipientEmail = recipientEmail;
		theTime = time;
	}

	public int compareTo(RequestID r){
		return this.theTime.compareTo(r.theTime);
	}
