package cn.ltxhhz.vote.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * 对token进行加密的类
 */
public class JwtToken {
  /**
   * 秘钥-保存在服务端,客户端是不会知道秘钥的,以防被攻击
   */
  private static final String SECRET = "ltxhhz";

  /**
   * 生成token
   */
  public static String createToken(String account) {
    return createToken(account, false);
  }

  /**
   * 生成token
   */
  public static String createToken(String account, boolean remember) {
    //签发时间
    Date iatDate = new Date();
    Calendar nowTime = Calendar.getInstance();
    //设置过期时间
    nowTime.add(remember ? Calendar.MONTH : Calendar.DAY_OF_YEAR, 1);
    //防止超过一年年份不变
    if (nowTime.getTimeInMillis() < iatDate.getTime()) nowTime.add(Calendar.YEAR, 1);
    //得到时间
    Date expiresDate = nowTime.getTime();
    //实例化组成头部header的map
    Map<String, Object> map = new HashMap<>();
    //声明类型，这里是jwt
    map.put("typ", "JWT");
    //声明加密的算法，通常直接使用HMAC SHA256
    map.put("alg", "HS256");
    //plyload 载荷，可以理解为承载的物品
    //        iss: jwt签发者
    //        sub: jwt所面向的用户
    //        aud: 接收jwt的一方
    //        exp: jwt的过期时间，这个过期时间必须要大于签发时间
    //        nbf: 定义在什么时间之前，该jwt都是不可用的.
    //        iat: jwt的签发时间
    //        jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
    return JWT.create().withHeader(map)//头部 header
      .withClaim("iss", "ltxhhz") // 载荷 payload
      .withClaim("aud", account)
      .withExpiresAt(expiresDate)//设置过期时间
      .withIssuedAt(iatDate)//设置签发时间
      .sign(Algorithm.HMAC256(SECRET));
  }

  /**
   * 解密token
   */
  public static Map<String, Claim> decryptToken(String token) {
    JWTVerifier verify = JWT.require(Algorithm.HMAC256(SECRET)).build();
    DecodedJWT jwt;
    try {
      jwt = verify.verify(token);
    } catch (Exception e) {
      System.out.println("登录凭证已过期，请重新登录");
      return null;
    }
    return jwt.getClaims();
  }

  public static boolean verifyToken(String token, String account) {
    if (token == null || account == null) return false;
    Map<String, Claim> res = decryptToken(token);
    if (res == null) return false;
    return res.get("aud").asString().equals(account);
  }
}