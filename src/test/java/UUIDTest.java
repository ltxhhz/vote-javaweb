import java.util.UUID;

public class UUIDTest {
  public static void main(String[] args) {

    //未加工的UUID
    String preUuid = UUID.randomUUID().toString();
    System.out.println(preUuid);

    //第一种方法生成UUID，去掉“-”符号
    System.out.println(UUID.randomUUID().toString().replace("-", ""));

    //未加工的UUID
    String preUuid2 = UUID.randomUUID().toString();
    System.out.println(preUuid2);
    //第二种生成UUID的方法，去掉“-”符号
    String changUuid = preUuid2.substring(0,8)+preUuid2.substring(9,13)+preUuid2.substring(14,18)+preUuid2.substring(19,23)+preUuid2.substring(24);

    System.out.println(changUuid);
  }
}
