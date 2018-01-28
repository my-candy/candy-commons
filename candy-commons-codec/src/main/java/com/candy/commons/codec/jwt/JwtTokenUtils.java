package com.candy.commons.codec.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtils {
	
	
	private static final Logger log = LoggerFactory.getLogger(JwtTokenUtils.class);


	
	/**
	 * 由字符串生成加密key
	 * 
	 * @return
	 */
	private static SecretKey generalKey(String secret) {
		byte[] encodedKey = Base64.decodeBase64(secret);
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		System.out.println(key);
		return key;
	}

	/**
	 * 
	 * @param jwtPayload
	 * @param secret
	 * @return
	 */
	public static String createToken(JwtPayload jwtPayload, String secret) {
		try {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);
			SecretKey key = generalKey(secret);
			JwtBuilder builder = Jwts.builder().setId(jwtPayload.getJti()).setIssuedAt(now).setSubject(jwtPayload.getSub())
					.setAudience(jwtPayload.getAud()).setIssuer(jwtPayload.getKiss()).signWith(signatureAlgorithm, key);
			if (jwtPayload.getVal() >= 0) {
				long expMillis = nowMillis + jwtPayload.getVal();
				Date exp = new Date(expMillis);
				builder.setExpiration(exp);
			}
			return builder.compact();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param token
	 * @param secret
	 * @return
	 */
	public static Claims parseToken(String token, String secret) {
		SecretKey key = generalKey(secret);
		Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
		return claims;
	}

	public static String refreshToken(String toKen, String secret) {
		Claims claims = parseToken(toKen, secret);
		JwtPayload jwtPayload = new JwtPayload();
		jwtPayload.setAud(claims.getAudience());
		jwtPayload.setJti(UUID.randomUUID().toString().replaceAll("-", ""));
		jwtPayload.setKiss(claims.getIssuer());
		jwtPayload.setSub(claims.getSubject());
		jwtPayload.setVal(10000);
		String token = createToken(jwtPayload, secret);
		return token;
	}

	public static JwtResult validateToken(String tokenClient, String tokenServer, String secret) {
		JwtResult result = new JwtResult(true);
		try {

			if (StringUtils.isBlank(tokenClient)) {
				result.setSuccess(false);
			}

			if (StringUtils.isBlank(tokenServer)) {
				result.setSuccess(false);
			}

			if (!tokenClient.equals(tokenServer)) {
				result.setSuccess(false);
			}

			Claims clientClaims = parseToken(tokenClient, secret);
			Claims serverClaims = parseToken(tokenClient, secret);

			if (clientClaims == null) {
				result.setSuccess(false);
			}

			if (serverClaims == null) {
				result.setSuccess(false);
			}

			// token的发行者
			String clientIss = clientClaims.getIssuer() == null ? "" : clientClaims.getIssuer();
			// token的题目
			String clientSub = clientClaims.getSubject() == null ? "" : clientClaims.getSubject();
			// token的客户
			String clientAud = clientClaims.getAudience() == null ? "" : clientClaims.getAudience();
			// JWT唯一标识. 能用于防止 JWT重复使用，一次只用一个token
			String clientJti = clientClaims.getId();

			// token的发行者
			String serverIss = serverClaims.getIssuer() == null ? "" : serverClaims.getIssuer();
			// token的题目
			String serverSub = serverClaims.getSubject() == null ? "" : serverClaims.getSubject();
			// token的客户
			String serverAud = serverClaims.getAudience() == null ? "" : serverClaims.getAudience();
			// JWT唯一标识. 能用于防止 JWT重复使用，一次只用一个token
			String serverJti = clientClaims.getId();

			if (!clientIss.equals(serverIss)) {
				result.setSuccess(false);
				return result;
			}
			if (!clientSub.equals(serverSub)) {
				result.setSuccess(false);
				return result;
			}
			if (!clientAud.equals(serverAud)) {
				result.setSuccess(false);
				return result;
			}
			if (!clientJti.equals(serverJti)) {
				result.setSuccess(false);
				return result;
			}
		} catch (Exception e) {
			log.error("validateToken is error",e);
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;

	}

	public static void main(String[] args) {


//		JwtPayload jwtPayload = new JwtPayload();
//		jwtPayload.setAud("zhongg");
//		jwtPayload.setJti(UUID.randomUUID().toString().replaceAll("-", ""));
//		jwtPayload.setKiss("quanjj");
//		jwtPayload.setSub("{'sub':'aaa'}");
//		jwtPayload.setVal(10000);
//		String token = createToken(jwtPayload, "aaaaaaaaaaaaaaaaaa");
//
//		JwtResult result = validateToken(token, token, "aaaaaaaaaaaaaaaaaa");
//		System.out.println(result.isSuccess());
//
//		System.out.println(token);
//		Claims util = parseToken(token, "aaaaaaaaaaaaaaaaaa");
//		System.out.println(util);
//		String retoken = refreshToken(token, "aaaaaaaaaaaaaaaaaa");
//		System.out.println(retoken);
//		Claims reutil = parseToken(token, "aaaaaaaaaaaaaaaaaa");
//		System.out.println(reutil.getSubject());
//		result = validateToken(token, retoken, "aaaaaaaaaaaaaaaaaa");
//		System.out.println(result.isSuccess());

	}

}
