package mrchenli.crypt.rsa;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA抽象实现，进行初始化及常用方法的封装
 */
public abstract class AbstractRsaService implements RsaService {

	protected RSAPublicKey publicKey;
	protected RSAPrivateKey privateKey;
	protected PKCS8EncodedKeySpec pkeySpec;
	protected X509EncodedKeySpec xkeySpec;
	final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * 构造函数，传递私钥，公钥进行初始化
	 *
	 * @param privateKey
	 * @param publicKey
	 */
	public AbstractRsaService(String privateKey, String publicKey) {
		try {
			this.privateKey = getPrivateKey(privateKey);
			this.publicKey = getPublicKey(publicKey);
			this.pkeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			this.xkeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据公钥str生成RSAPublicKey
	 *
	 * @param publicKeyStr
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private RSAPublicKey getPublicKey(String publicKeyStr) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] buffer = Base64.decodeBase64(publicKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	/**
	 * 根据私钥str生成RSAPrivateKey
	 *
	 * @param privateKeyStr
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private RSAPrivateKey getPrivateKey(String privateKeyStr) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] buffer = Base64.decodeBase64(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 加密
	 *
	 * @param plain_data
	 * @return
	 */
	@Override
	public String encrypt(String plain_data) {
		try {
			Cipher cipher = getCipper();
			cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
			return doRSAFinal(plain_data, cipher);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract Cipher getCipper();

	/**
	 * 解密
	 *
	 * @param encry_data
	 * @return
	 */
	@Override
	public String decrypt(String encry_data) {
		try {
			Cipher cipher = getCipper();
			cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
			return doRSAFinal(encry_data, cipher);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 实际rsa加密过程
	 *
	 * @param sourData
	 * @param cipher
	 * @return
	 */
	private String doRSAFinal(String sourData, Cipher cipher) {
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			byte[] data = sourData.getBytes("UTF-8");
			int inputLen = data.length;
			int offSet = 0;
			byte[] cache;
			while (inputLen - offSet > 0) {
				//是否解决完了
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
					offSet += MAX_ENCRYPT_BLOCK;
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
					offSet += inputLen - offSet;
				}
				outputStream.write(cache, 0, cache.length);
			}
			return Base64.encodeBase64String(outputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 验签
	 *
	 * @param sign
	 * @return
	 */
	@Override
	public boolean verifySign(String sign, String param) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey key = keyFactory.generatePublic(xkeySpec);
			//生成签名的类 生成签名的算法
			//生成签名需要 私钥
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(key);
			//生成签名 参数
			signature.update(param.getBytes());
			return signature.verify(Base64.decodeBase64(sign));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 生成签名
	 *
	 * @param params
	 * @return
	 */
	@Override
	public String generateSign(String params) {
		try {
			return generateSignWithspec(params, pkeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据指定私钥生成签名
	 *
	 * @param params
	 * @param privateKey
	 * @return
	 */
	@Override
	public String generateSignWithPrivateKey(String params, String privateKey) {
		try {
			PKCS8EncodedKeySpec pspec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			return generateSignWithspec(params, pspec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据pkspec生成签名
	 *
	 * @param params
	 * @param pkspec
	 * @return
	 */
	public String generateSignWithspec(String params, PKCS8EncodedKeySpec pkspec) {
		try {
			// RSA 指定的加密算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			//取私钥对象
			PrivateKey key = keyFactory.generatePrivate(pkspec);
			//用私钥对信息生成数字签名
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(key);
			signature.update(params.getBytes());
			return Base64.encodeBase64String(signature.sign());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
