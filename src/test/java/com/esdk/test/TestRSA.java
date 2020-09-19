package com.esdk.test;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import com.esdk.esdk;
import com.esdk.cipher.RSAUtils;

public class TestRSA{

	static String publicKey;
	static String privateKey;

	static{
		try{
			Map<String,Object> keyMap=RSAUtils.genKeyPair();
			publicKey=RSAUtils.getPublicKey(keyMap);
			privateKey=RSAUtils.getPrivateKey(keyMap);
			/*System.err.println("公钥: \n\r"+publicKey);
			System.err.println("私钥： \n\r"+privateKey);*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	static void test() throws Exception{
		System.err.println("公钥加密——私钥解密");
		String source="这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
		System.out.println("\r加密前文字：\r\n"+source);
		byte[] data=source.getBytes();
		byte[] encodedData=RSAUtils.encryptByPublicKey(data,publicKey);
		System.out.println("加密后文字：\r\n"+new String(encodedData));
		byte[] decodedData=RSAUtils.decryptByPrivateKey(encodedData,privateKey);
		String target=new String(decodedData);
		System.out.println("解密后文字: \r\n"+target);
	}

	static void testSign() throws Exception{
		privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALECCmfaSubzjrStyo6XTGtwJJLZB1w2KG8GIt4a9VrIWHsF6tKskx7T/bUevQqhSMkXPsaRMxQk6KLKhxdmGdLNXFjZuWHXTwPv6NO7ni2g+0NQSvw9r1UqLaHQBd+LLTH0xRNrgcKVH82MhqkhBZv/zmeXLU9T2QsHDv3rm1aLAgMBAAECgYAj8jMkxfriDeIUJRr2fBlD1EFJJRPOkR0C9u8LxdO/vOHjEd+PKwaxgwGJz5U6XGiIldTkxEXoOFqZ/KEUimKBygEq5cTQG+wAjWIZjKXCdQR5rojLuRrDNmwFZyYcIIlKuI4SgU3xrTov1Ni5PrWpxjvHcwZ4k9vBfZfiPlv5IQJBAPypv9/YM5jV63XcDYT2agunsri31tYpkypBi3WFJqfIkAgCPAu3Phn8ynrVFwmbFYNVxCV+p6KsLFk5k2eP0RECQQCzWICRZmyvsE97U3va/n45zp4YTmArZ0iGxmi79Q0CmoY6r71cb39SnGw++0wMUbjTk43xZ6WM9+issQuum63bAkEApKO0XReT36adrQo8YQT06y1Wn1lkC9/BfsqBJo4iNzjQ6fcSy4uXUvXPtyS9w0ukRBWSH+CdObo5l9aVv+kOEQJABzBd/vYPFz/G/9eJ2G5pGuIYXjsOCc9bDeP4IMii297b0JBo08K4ZNRWVnP2SWwhL4Hzp6CS90Kctgdmw3oEDwJBAPYmtQgLb8x/OpihQN77JEe83dXPrTLN83GNUotjdyywGGUu9yULSY13BIWJ+D4Jr/Vmw4EM6a17UjY8oX0Naqo=";
		publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxAgpn2krm8460rcqOl0xrcCSS2QdcNihvBiLeGvVayFh7BerSrJMe0/21Hr0KoUjJFz7GkTMUJOiiyocXZhnSzVxY2blh108D7+jTu54toPtDUEr8Pa9VKi2h0AXfiy0x9MUTa4HClR/NjIapIQWb/85nly1PU9kLBw7965tWiwIDAQAB";
		System.err.println("私钥加密——公钥解密");
		String source="这是一行测试RSA数字签名的无意义文字";
		System.out.println("原文字：\r\n"+source);
		byte[] data=source.getBytes();
		byte[] encodedData=RSAUtils.encryptByPrivateKey(data,privateKey);
		System.out.println("加密后：\r\n"+new String(encodedData));
		byte[] decodedData=RSAUtils.decryptByPublicKey(encodedData,publicKey);
		String target=new String(decodedData);
		System.out.println("解密后: \r\n"+target);
		System.err.println("私钥签名——公钥验证签名");
		String sign=RSAUtils.sign(encodedData,privateKey);
		System.err.println("签名:\r"+sign);
		boolean status=RSAUtils.verify(encodedData,publicKey,sign);
		System.err.println("验证结果:\r"+status);
	}
	
	static void mytest() throws Exception {
		privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALECCmfaSubzjrStyo6XTGtwJJLZB1w2KG8GIt4a9VrIWHsF6tKskx7T/bUevQqhSMkXPsaRMxQk6KLKhxdmGdLNXFjZuWHXTwPv6NO7ni2g+0NQSvw9r1UqLaHQBd+LLTH0xRNrgcKVH82MhqkhBZv/zmeXLU9T2QsHDv3rm1aLAgMBAAECgYAj8jMkxfriDeIUJRr2fBlD1EFJJRPOkR0C9u8LxdO/vOHjEd+PKwaxgwGJz5U6XGiIldTkxEXoOFqZ/KEUimKBygEq5cTQG+wAjWIZjKXCdQR5rojLuRrDNmwFZyYcIIlKuI4SgU3xrTov1Ni5PrWpxjvHcwZ4k9vBfZfiPlv5IQJBAPypv9/YM5jV63XcDYT2agunsri31tYpkypBi3WFJqfIkAgCPAu3Phn8ynrVFwmbFYNVxCV+p6KsLFk5k2eP0RECQQCzWICRZmyvsE97U3va/n45zp4YTmArZ0iGxmi79Q0CmoY6r71cb39SnGw++0wMUbjTk43xZ6WM9+issQuum63bAkEApKO0XReT36adrQo8YQT06y1Wn1lkC9/BfsqBJo4iNzjQ6fcSy4uXUvXPtyS9w0ukRBWSH+CdObo5l9aVv+kOEQJABzBd/vYPFz/G/9eJ2G5pGuIYXjsOCc9bDeP4IMii297b0JBo08K4ZNRWVnP2SWwhL4Hzp6CS90Kctgdmw3oEDwJBAPYmtQgLb8x/OpihQN77JEe83dXPrTLN83GNUotjdyywGGUu9yULSY13BIWJ+D4Jr/Vmw4EM6a17UjY8oX0Naqo=";
		publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxAgpn2krm8460rcqOl0xrcCSS2QdcNihvBiLeGvVayFh7BerSrJMe0/21Hr0KoUjJFz7GkTMUJOiiyocXZhnSzVxY2blh108D7+jTu54toPtDUEr8Pa9VKi2h0AXfiy0x9MUTa4HClR/NjIapIQWb/85nly1PU9kLBw7965tWiwIDAQAB";
		String decodeText="ffybaby123+"+esdk.time.formatDate(new Date());
		System.out.println("decodeText:"+decodeText);
		byte[] v=RSAUtils.encryptByPublicKey(decodeText.getBytes(),publicKey); //" eg.: ffybaby+2016-12-31 23:59:59"
		String encodeText=Base64.getEncoder().encodeToString(v);
		System.out.println("encodeText:"+encodeText);
		System.out.println("urlencode:"+URLEncoder.encode(Base64.getEncoder().encodeToString(v),"utf8")); //如果要做为url参数，需要先URLEnocde处理，注意在解密时，就不能用URLDecode,否则就是二次URLDeocde了。
		String decodedText=new String(RSAUtils.decryptByPrivateKey(Base64.getDecoder().decode(encodeText.replaceAll(" ","+")),privateKey));
		System.out.println(decodedText.equals(decodeText));
	}

	public static void main(String[] args) throws Exception{
		mytest();
		/*test();
		testSign();*/
	}
}