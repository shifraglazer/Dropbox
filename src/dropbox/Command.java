package dropbox;

import java.util.regex.Pattern;



public abstract class Command {

	Pattern pattern;
	public Command(Pattern pattern){
		this.pattern=pattern;
	}
	
	abstract void executeCommand(String line);
}
