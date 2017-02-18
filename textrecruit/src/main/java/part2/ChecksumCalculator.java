package part2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;

public class ChecksumCalculator extends Thread {

	Queue<String> uids = null;
	MessageDigest md = null;

	public ChecksumCalculator(Queue<String> uids) {
		try {
			this.uids = uids;
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error creating the message digest object " 
					+ e.getMessage());
		}
	}

	@Override
	public void run() {
		String uid = uids.poll();
		md.update(uid.getBytes());
		byte byteData[] = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		System.out.println(
				"Thread: " + Thread.currentThread().getName() 
				+ " " + "UID: " + uid + " Checksum: "
				+ hexString.toString());
	}

}
