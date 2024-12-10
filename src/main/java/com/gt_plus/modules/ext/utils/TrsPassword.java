package com.gt_plus.modules.ext.utils;

import java.util.Random;

public class TrsPassword {
	static final int S11 = 7;
	static final int S12 = 12;
	static final int S13 = 17;
	static final int S14 = 22;
	static final int S21 = 5;
	static final int S22 = 9;
	static final int S23 = 14;
	static final int S24 = 20;
	static final int S31 = 4;
	static final int S32 = 11;
	static final int S33 = 16;
	static final int S34 = 23;
	static final int S41 = 6;
	static final int S42 = 10;
	static final int S43 = 15;
	static final int S44 = 21;
	static final byte[] PADDING = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };

	private long[] state = new long[4];

	private long[] count = new long[2];

	private byte[] buffer = new byte[64];
	public String digestHexStr;
	private byte[] digest = new byte[16];

	static double ulE = 84716293.0D;

	static double ulD = 16587853.0D;

	static double ulN = 59987833.0D;

	static double dDiff = 13645307.0D;

	public String encodePassword(String userPassword) {
		String password = "";
		md5Update(userPassword.getBytes(), userPassword.length());
		md5Final();
		for (int i = 0; i < 16; i++) {
			password += byteHEX(this.digest[i]);
		}
		return password.substring(0, 15);
	}

	public TrsPassword() {
		md5Init();
	}

	private void md5Init() {
		this.count[0] = 0L;
		this.count[1] = 0L;

		this.state[0] = 1732584193L;
		this.state[1] = 4023233417L;
		this.state[2] = 2562383102L;
		this.state[3] = 271733878L;
	}

	private long F(long paramLong1, long paramLong2, long paramLong3) {
		return paramLong1 & paramLong2 | (paramLong1 ^ 0xFFFFFFFF) & paramLong3;
	}

	private long G(long paramLong1, long paramLong2, long paramLong3) {
		return paramLong1 & paramLong3 | paramLong2 & (paramLong3 ^ 0xFFFFFFFF);
	}

	private long H(long paramLong1, long paramLong2, long paramLong3) {
		return paramLong1 ^ paramLong2 ^ paramLong3;
	}

	private long I(long paramLong1, long paramLong2, long paramLong3) {
		return paramLong2 ^ (paramLong1 | paramLong3 ^ 0xFFFFFFFF);
	}

	private long FF(long paramLong1, long paramLong2, long paramLong3,
			long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
		paramLong1 += F(paramLong2, paramLong3, paramLong4) + paramLong5
				+ paramLong7;
		paramLong1 = (int) paramLong1 << (int) paramLong6
				| (int) paramLong1 >>> (int) (32L - paramLong6);
		paramLong1 += paramLong2;
		return paramLong1;
	}

	private long GG(long paramLong1, long paramLong2, long paramLong3,
			long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
		paramLong1 += G(paramLong2, paramLong3, paramLong4) + paramLong5
				+ paramLong7;
		paramLong1 = (int) paramLong1 << (int) paramLong6
				| (int) paramLong1 >>> (int) (32L - paramLong6);
		paramLong1 += paramLong2;
		return paramLong1;
	}

	private long HH(long paramLong1, long paramLong2, long paramLong3,
			long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
		paramLong1 += H(paramLong2, paramLong3, paramLong4) + paramLong5
				+ paramLong7;
		paramLong1 = (int) paramLong1 << (int) paramLong6
				| (int) paramLong1 >>> (int) (32L - paramLong6);
		paramLong1 += paramLong2;
		return paramLong1;
	}

	private long II(long paramLong1, long paramLong2, long paramLong3,
			long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
		paramLong1 += I(paramLong2, paramLong3, paramLong4) + paramLong5
				+ paramLong7;
		paramLong1 = (int) paramLong1 << (int) paramLong6
				| (int) paramLong1 >>> (int) (32L - paramLong6);
		paramLong1 += paramLong2;
		return paramLong1;
	}

	private void md5Update(byte[] paramArrayOfByte, int paramInt) {
		byte[] arrayOfByte = new byte[64];
		int j = (int) (this.count[0] >>> 3) & 0x3F;

		if ((this.count[0] += paramInt << 3) < paramInt << 3)
			this.count[1] += 1L;
		this.count[1] += paramInt >>> 29;

		int k = 64 - j;
		int i;
		if (paramInt >= k) {
			md5Memcpy(this.buffer, paramArrayOfByte, j, 0, k);
			md5Transform(this.buffer);

			for (i = k; i + 63 < paramInt; i += 64) {
				md5Memcpy(arrayOfByte, paramArrayOfByte, 0, i, 64);
				md5Transform(arrayOfByte);
			}
			j = 0;
		} else {
			i = 0;
		}
		md5Memcpy(this.buffer, paramArrayOfByte, j, i, paramInt - i);
	}

	private void md5Final() {
		byte[] arrayOfByte = new byte[8];

		Encode(arrayOfByte, this.count, 8);

		int i = (int) (this.count[0] >>> 3) & 0x3F;
		int j = i < 56 ? 56 - i : 120 - i;
		md5Update(PADDING, j);

		md5Update(arrayOfByte, 8);

		Encode(this.digest, this.state, 16);
	}

	private void md5Memcpy(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2,
			int paramInt1, int paramInt2, int paramInt3) {
		for (int i = 0; i < paramInt3; i++)
			paramArrayOfByte1[(paramInt1 + i)] = paramArrayOfByte2[(paramInt2 + i)];
	}

	private void md5Transform(byte[] paramArrayOfByte) {
		long l1 = this.state[0];
		long l2 = this.state[1];
		long l3 = this.state[2];
		long l4 = this.state[3];
		long[] arrayOfLong = new long[16];

		Decode(arrayOfLong, paramArrayOfByte, 64);

		l1 = FF(l1, l2, l3, l4, arrayOfLong[0], 7L, 3614090360L);
		l4 = FF(l4, l1, l2, l3, arrayOfLong[1], 12L, 3905402710L);
		l3 = FF(l3, l4, l1, l2, arrayOfLong[2], 17L, 606105819L);
		l2 = FF(l2, l3, l4, l1, arrayOfLong[3], 22L, 3250441966L);
		l1 = FF(l1, l2, l3, l4, arrayOfLong[4], 7L, 4118548399L);
		l4 = FF(l4, l1, l2, l3, arrayOfLong[5], 12L, 1200080426L);
		l3 = FF(l3, l4, l1, l2, arrayOfLong[6], 17L, 2821735955L);
		l2 = FF(l2, l3, l4, l1, arrayOfLong[7], 22L, 4249261313L);
		l1 = FF(l1, l2, l3, l4, arrayOfLong[8], 7L, 1770035416L);
		l4 = FF(l4, l1, l2, l3, arrayOfLong[9], 12L, 2336552879L);
		l3 = FF(l3, l4, l1, l2, arrayOfLong[10], 17L, 4294925233L);
		l2 = FF(l2, l3, l4, l1, arrayOfLong[11], 22L, 2304563134L);
		l1 = FF(l1, l2, l3, l4, arrayOfLong[12], 7L, 1804603682L);
		l4 = FF(l4, l1, l2, l3, arrayOfLong[13], 12L, 4254626195L);
		l3 = FF(l3, l4, l1, l2, arrayOfLong[14], 17L, 2792965006L);
		l2 = FF(l2, l3, l4, l1, arrayOfLong[15], 22L, 1236535329L);

		l1 = GG(l1, l2, l3, l4, arrayOfLong[1], 5L, 4129170786L);
		l4 = GG(l4, l1, l2, l3, arrayOfLong[6], 9L, 3225465664L);
		l3 = GG(l3, l4, l1, l2, arrayOfLong[11], 14L, 643717713L);
		l2 = GG(l2, l3, l4, l1, arrayOfLong[0], 20L, 3921069994L);
		l1 = GG(l1, l2, l3, l4, arrayOfLong[5], 5L, 3593408605L);
		l4 = GG(l4, l1, l2, l3, arrayOfLong[10], 9L, 38016083L);
		l3 = GG(l3, l4, l1, l2, arrayOfLong[15], 14L, 3634488961L);
		l2 = GG(l2, l3, l4, l1, arrayOfLong[4], 20L, 3889429448L);
		l1 = GG(l1, l2, l3, l4, arrayOfLong[9], 5L, 568446438L);
		l4 = GG(l4, l1, l2, l3, arrayOfLong[14], 9L, 3275163606L);
		l3 = GG(l3, l4, l1, l2, arrayOfLong[3], 14L, 4107603335L);
		l2 = GG(l2, l3, l4, l1, arrayOfLong[8], 20L, 1163531501L);
		l1 = GG(l1, l2, l3, l4, arrayOfLong[13], 5L, 2850285829L);
		l4 = GG(l4, l1, l2, l3, arrayOfLong[2], 9L, 4243563512L);
		l3 = GG(l3, l4, l1, l2, arrayOfLong[7], 14L, 1735328473L);
		l2 = GG(l2, l3, l4, l1, arrayOfLong[12], 20L, 2368359562L);

		l1 = HH(l1, l2, l3, l4, arrayOfLong[5], 4L, 4294588738L);
		l4 = HH(l4, l1, l2, l3, arrayOfLong[8], 11L, 2272392833L);
		l3 = HH(l3, l4, l1, l2, arrayOfLong[11], 16L, 1839030562L);
		l2 = HH(l2, l3, l4, l1, arrayOfLong[14], 23L, 4259657740L);
		l1 = HH(l1, l2, l3, l4, arrayOfLong[1], 4L, 2763975236L);
		l4 = HH(l4, l1, l2, l3, arrayOfLong[4], 11L, 1272893353L);
		l3 = HH(l3, l4, l1, l2, arrayOfLong[7], 16L, 4139469664L);
		l2 = HH(l2, l3, l4, l1, arrayOfLong[10], 23L, 3200236656L);
		l1 = HH(l1, l2, l3, l4, arrayOfLong[13], 4L, 681279174L);
		l4 = HH(l4, l1, l2, l3, arrayOfLong[0], 11L, 3936430074L);
		l3 = HH(l3, l4, l1, l2, arrayOfLong[3], 16L, 3572445317L);
		l2 = HH(l2, l3, l4, l1, arrayOfLong[6], 23L, 76029189L);
		l1 = HH(l1, l2, l3, l4, arrayOfLong[9], 4L, 3654602809L);
		l4 = HH(l4, l1, l2, l3, arrayOfLong[12], 11L, 3873151461L);
		l3 = HH(l3, l4, l1, l2, arrayOfLong[15], 16L, 530742520L);
		l2 = HH(l2, l3, l4, l1, arrayOfLong[2], 23L, 3299628645L);

		l1 = II(l1, l2, l3, l4, arrayOfLong[0], 6L, 4096336452L);
		l4 = II(l4, l1, l2, l3, arrayOfLong[7], 10L, 1126891415L);
		l3 = II(l3, l4, l1, l2, arrayOfLong[14], 15L, 2878612391L);
		l2 = II(l2, l3, l4, l1, arrayOfLong[5], 21L, 4237533241L);
		l1 = II(l1, l2, l3, l4, arrayOfLong[12], 6L, 1700485571L);
		l4 = II(l4, l1, l2, l3, arrayOfLong[3], 10L, 2399980690L);
		l3 = II(l3, l4, l1, l2, arrayOfLong[10], 15L, 4293915773L);
		l2 = II(l2, l3, l4, l1, arrayOfLong[1], 21L, 2240044497L);
		l1 = II(l1, l2, l3, l4, arrayOfLong[8], 6L, 1873313359L);
		l4 = II(l4, l1, l2, l3, arrayOfLong[15], 10L, 4264355552L);
		l3 = II(l3, l4, l1, l2, arrayOfLong[6], 15L, 2734768916L);
		l2 = II(l2, l3, l4, l1, arrayOfLong[13], 21L, 1309151649L);
		l1 = II(l1, l2, l3, l4, arrayOfLong[4], 6L, 4149444226L);
		l4 = II(l4, l1, l2, l3, arrayOfLong[11], 10L, 3174756917L);
		l3 = II(l3, l4, l1, l2, arrayOfLong[2], 15L, 718787259L);
		l2 = II(l2, l3, l4, l1, arrayOfLong[9], 21L, 3951481745L);

		this.state[0] += l1;
		this.state[1] += l2;
		this.state[2] += l3;
		this.state[3] += l4;
	}

	private void Encode(byte[] paramArrayOfByte, long[] paramArrayOfLong,
			int paramInt) {
		int i = 0;
		for (int j = 0; j < paramInt; j += 4) {
			paramArrayOfByte[j] = (byte) (int) (paramArrayOfLong[i] & 0xFF);
			paramArrayOfByte[(j + 1)] = (byte) (int) (paramArrayOfLong[i] >>> 8 & 0xFF);
			paramArrayOfByte[(j + 2)] = (byte) (int) (paramArrayOfLong[i] >>> 16 & 0xFF);
			paramArrayOfByte[(j + 3)] = (byte) (int) (paramArrayOfLong[i] >>> 24 & 0xFF);

			i++;
		}
	}

	private void Decode(long[] paramArrayOfLong, byte[] paramArrayOfByte,
			int paramInt) {
		int i = 0;
		for (int j = 0; j < paramInt; j += 4) {
			paramArrayOfLong[i] = (b2iu(paramArrayOfByte[j])
					| b2iu(paramArrayOfByte[(j + 1)]) << 8
					| b2iu(paramArrayOfByte[(j + 2)]) << 16 | b2iu(paramArrayOfByte[(j + 3)]) << 24);

			i++;
		}
	}

	public static long b2iu(byte paramByte) {
		return paramByte < 0 ? paramByte & 0xFF : paramByte;
	}

	public static String byteHEX(byte paramByte) {
		char[] arrayOfChar1 = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };

		char[] arrayOfChar2 = new char[2];
		arrayOfChar2[0] = arrayOfChar1[(paramByte >>> 4 & 0xF)];
		arrayOfChar2[1] = arrayOfChar1[(paramByte & 0xF)];
		String str = new String(arrayOfChar2);
		return str;
	}

	public static String setAnotherChar(String paramString) {
		String str = "";

		for (int i = 0; i < paramString.length(); i++) {
			int j = paramString.charAt(i);
			int k = j;

			if ((k >= 97) && (k <= 109))
				k += 13;
			else if ((k >= 110) && (k <= 122))
				k -= 13;
			else if ((k >= 65) && (k <= 77))
				k += 13;
			else if ((k >= 78) && (k <= 90))
				k -= 13;
			str = str + (char) k;
		}

		return str;
	}

	public static String RASEncrypt(String paramString) {
		Random localRandom = new Random(System.currentTimeMillis());
		String str1 = "";
		String str2 = paramString;
		String str3 = "";
		if (paramString.length() <= 0)
			return "";
		int i = 0;
		for (int j = str2.length(); i < j; i++) {
			str3 = str2.substring(i, i + 1);
			int k = Integer.parseInt(str3, 36);
			str1 = str1 + (Mult(k, ulE, ulN) + dDiff);
			str1 = str1 + "-";
		}

		str3 = String.valueOf(localRandom.nextLong());

		str1 = str1 + str3.substring(1, 8);
		return str1.toUpperCase();
	}

	public static String RASDecrypt(String paramString) {
		String str1 = "";
		String str2 = new String(paramString);
		String str3 = "";
		int i = 0;
		int j = 0;
		for (int k = str2.length(); i < k; i = j + 1) {
			j = str2.indexOf("-", i);
			if (j == -1) {
				break;
			}
			double d = Double.parseDouble(str2.substring(i, j)) - dDiff;

			str3 = Integer.toString((int) Mult(d, ulD, ulN), 36);

			str1 = str1 + str3;
		}

		str1 = str1.toUpperCase();

		return str1;
	}

	private static double Mult(double paramDouble1, double paramDouble2,
			double paramDouble3) {
		double d = 1.0D;
		try {
			while (paramDouble2 > 0.0D) {
				while (paramDouble2 / 2.0D == (int) (paramDouble2 / 2.0D)) {
					paramDouble1 = paramDouble1 * paramDouble1 % paramDouble3;
					paramDouble2 /= 2.0D;
				}
				d = paramDouble1 * d % paramDouble3;
				paramDouble2 -= 1.0D;
			}
			return d;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return 0.0D;
	}
	
	public static void main(String[] args) {
		TrsPassword trsPassword = new TrsPassword();
		System.out.println("TRS 123456=" + trsPassword.encodePassword("123456"));
	}
}
