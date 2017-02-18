package part2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Class to calculate the checksum of the 'uid' value
 */
public class MainClass {

	// Default url used to retrieve the json data
	private static String DEFAULT_URL = "https://gist.githubusercontent.com" + 
		"/anonymous/8f60e8f49158efdd2e8fed6fa97373a4/raw" +
		"/01add7ea44ed12f5d90180dc1367915af331492e/java-data2.json";

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
	 * Reads the json input and calculates the checksum
	 * @param url used to retrieve the json data
	 */
	private void execute(String url) {
		try {
			List<String> jsonData = getJsonData(url);
			Queue<String> uids = getUIDs(jsonData);
			ExecutorService executor =  Executors.newFixedThreadPool(4);
			while (!uids.isEmpty()) {
				executor.submit(new ChecksumCalculator(uids));
			}
			executor.shutdown();
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
	 * Retrieves the uid field from all the entries in the json array
	 * @param data Json array with fields index and uid
	 * @return Queue<String> queue with the uids 
	 */
	private Queue<String> getUIDs(List<String> data) {
		Queue<String> queue = new ConcurrentLinkedQueue<>();
		for (String line : data) {
			JSONTokener tokener = new JSONTokener(line);
			JSONObject json = new JSONObject(tokener);
			JSONArray items = json.getJSONArray("items");
			Iterator<Object> iterator = items.iterator();
			while(iterator.hasNext()) {
				JSONObject o = (JSONObject) iterator.next();
				queue.add(o.getString("uid"));
			}
		}
		return queue;
	}

}
