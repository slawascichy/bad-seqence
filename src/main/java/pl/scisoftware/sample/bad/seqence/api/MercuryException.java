package pl.scisoftware.sample.bad.seqence.api;

public class MercuryException extends Exception {

	private static final long serialVersionUID = 3589686147749404589L;

	public MercuryException(String message) {
		super(message);
	}

	public MercuryException(Throwable cause) {
		super(cause);
	}

}
