package com.groupeight.user_service.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtKeysConfig {

	@Value("classpath:private.pem")
	private Resource privatePem;

	@Value("classpath:public.pem")
	private Resource publicPem;

	@Bean
	JwtEncoder jwtEncoder() throws Exception {
		RSAPrivateKey priv = readPrivateKey(privatePem);
		RSAPublicKey pub = readPublicKey(publicPem);
		var rsa = new RSAKey.Builder(pub).privateKey(priv).keyID("k1").build();
		var jwks = new ImmutableJWKSet<SecurityContext>(new JWKSet(rsa));
		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	JwtDecoder jwtDecoder() throws Exception {
		return NimbusJwtDecoder.withPublicKey(readPublicKey(publicPem)).build();
	}

	private RSAPrivateKey readPrivateKey(Resource r) throws Exception {
		String pem = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
				.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
		var kf = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
		// If you accidentally generated "BEGIN RSA PRIVATE KEY", convert it to PKCS#8
		// (see commands above).
	}

	private RSAPublicKey readPublicKey(Resource r) throws Exception {
		String pem = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
				.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");
		var kf = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pem)));
	}
}
