package dropbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

public class PatternTest {

	@Test
	public void testFilePattern() {
		Pattern file = Pattern.compile("FILE \\S+\\s\\d+\\s\\d+");
		Matcher match = file.matcher("FILE hello.txt 34 33");
		Assert.assertTrue(match.matches());

	}

	@Test
	public void testFilesPattern() {
		Pattern files = Pattern.compile("FILES \\d+");
		Matcher match = files.matcher("FILES 33");
		Assert.assertTrue(match.matches());

	}

	@Test
	public void testChunkPattern() {
		Pattern chunk = Pattern.compile("CHUNK \\S+\\s\\d+\\s\\d+\\s\\d+\\s[a-zA-Z0-9=-]*{0,}");
		Matcher match = chunk.matcher("CHUNK filenameglazer.txt 47834 432 0 ahjc7ahjc78hujnsnhhs865djj");
		Assert.assertTrue(match.matches());

	}

}
