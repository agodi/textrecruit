package part1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Class to calculate the sum of the fields recv and sent in a json object
 */
public class MainClass {

	// Default url used to retrieve the json data
	private static String DEFAULT_URL = "https://gist.githubusercontent.com/jed204/"
			+ "92f90060d0fabf65792d6d479c45f31c/raw/" 
			+ "346c44a23762749ede009623db37f4263e94ef2a/java2.json";

	public static void main(String[] args) {
		MainClass main = new MainClass();
		// If no arguments are given, use DEFAULT_URL
		if (args == null || args.length == 0) {
			main.execute(DEFAULT_URL);
		} else {
			main.execute(args[0]);
		}
	}

	/**
	 * Reads the json input and calculates the sum of each field
	 * @param url used to retrieve the json string
	 */
	private void execute(String url) {
		Map<String, ValueObject> result = new HashMap<>();
		try {
			List<String> jsonData = getJsonData(url);
			result = calculateSums(jsonData);
			for (String key : result.keySet()) {
				ValueObject vo = result.get(key);
				System.out.println(
						key + "-> recv: " + vo.recv + " sent: " + vo.sent);
			}
		} catch (IOException e) {
			System.err.println(
					"There was an error processing the json document " 
					+ e.getMessage());
		}
	}

	/**
	 * Reads json data from the given url
	 * @param url used to retrieve the json data
	 * @return List<String> containing all the json strings
	 * @throws IOException
	 */
	private List<String> getJsonData(String url) throws IOException {
		URLConnection conn = null;
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			conn = new URL(url).openConnection();
			br = new BufferedReader(
					new InputStreamReader(
							conn.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			return list;
		} finally {
			try {
				if (br != null) { br.close(); }
			} catch (IOException e) {
				System.err.println(
						"There was an error trying to close the input stream "
						+ e.getMessage());
			}
		}
	}

	/**
	 * Calculates the sum for each field "recv" and "sent" in the json data
	 * @param data List of string with the json data
	 * @return Map<String, ValueObject> stores the sum of the fields for each
	 * key
	 */
	private Map<String, ValueObject> calculateSums(List<String> data) {
		Map<String, ValueObject> map = new TreeMap<String, ValueObject>();
		ValueObject totals = new ValueObject();
		for (String line : data) {
			JSONTokener tokener = new JSONTokener(line);
			JSONObject json = new JSONObject(tokener);

			for (String key : json.keySet()) {
				JSONObject objC = json.getJSONObject(key);
				ValueObject vo = new ValueObject();

				for (String m : objC.keySet()) {
					JSONObject objM = objC.getJSONObject(m);
					vo.sent += objM.getInt("sent");
					vo.recv += objM.getInt("recv");
				}

				map.put(key, vo);
				totals.sent += vo.sent;
				totals.recv += vo.recv;
			}

		}

		map.put("Totals", totals);
		return map;
	}

	/**
	 * Helper class to store the values of fields: recv and sent
	 */
	class ValueObject {
		private int sent;
		private int recv;

		public int getSent() {
			return sent;
		}

		public int getRecv() {
			return recv;
		}
	}

}
