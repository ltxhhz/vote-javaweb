import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpTest {
  public static void main(String[] args) {
    Pattern p = Pattern.compile("-(\\d+)$");
    String str = "-123";
    Matcher m = p.matcher(str);
    m.find();
    System.out.println(m.group(1));
  }
}
