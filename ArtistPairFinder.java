import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

class ArtistPairFinder {
	HashMap<String, HashMap<String, Integer>> m;

	public static void main(String[] args) {
			if (args.length != 1){
				System.out.println("Usage: java ArtistPairFinder <filename>");
			}
			else{
				ArtistPairFinder apf = new ArtistPairFinder();
				apf.readFile(args[0]);
			}
		}	

	public ArtistPairFinder(){
		m = new HashMap<String, HashMap<String, Integer>>();
	}

	private void readFile(String fileName){
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			HashMap<String, HashMap<String, Integer>> artistMap = createMap(br);
			HashSet<ArtistPair> artistPairs = getPairs(artistMap);
			writeSet(artistPairs);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	/*
	Map each artist to another map, which maps other artists to times appearing together.
	Saves time and space by only including artists that appear together at least once,
	so we don't waste time checking every possible pair that never actually appears together.

	If there are n lines in the file, a total artists, and on average k artists per line,
	the algorithm should run in O(n*a*k) time.
	*/
	private HashMap<String, HashMap<String, Integer>> createMap(BufferedReader br) throws IOException{
		String s;
		while ((s = br.readLine()) != null){
			String[] artists = s.split(",");
			for (int i = 0; i < artists.length; i++){
				String artist = artists[i];
				//check if the artist already has an entry, add one if not
				Set<String> keys = m.keySet();
				if (!keys.contains(artist)){
					m.put(artist, new HashMap<String, Integer>());
				}
				//go through the other artists
				for (int j = 0; j < artists.length; j++){
					// don't compare the artist to themself
					if (j == i){
						continue;
					}
					String otherArtist = artists[j];
					HashMap<String, Integer> otherArtistMap = m.get(artist);
					Set<String> otherArtists = otherArtistMap.keySet();
					// check if they've had any appearances together yet, add an entry if not
					if (!otherArtists.contains(otherArtist)){
						otherArtistMap.put(otherArtist, 0);
					}
					// increment the count for times they appear together
					int count = otherArtistMap.get(otherArtist);
					otherArtistMap.put(otherArtist, count + 1);
				}
			}
		}
		return m;
	}

	private HashSet<ArtistPair> getPairs(HashMap<String, HashMap<String, Integer>> m){
		HashSet<ArtistPair> s = new HashSet<ArtistPair>();
		for (String artist : m.keySet()){
			HashMap<String, Integer> otherArtists = m.get(artist);
			for (String otherArtist : otherArtists.keySet()){
				int count = otherArtists.get(otherArtist);
				if (count >= 50){
					ArtistPair ap = new ArtistPair(artist, otherArtist);
					if (!s.contains(ap)){
						s.add(ap);
					}
				}
			}
		}
		return s;
	}

	private void writeSet(HashSet<ArtistPair> s) throws IOException{
		PrintWriter pw = new PrintWriter("Artist_Pairs.txt", "UTF-8");
		for (ArtistPair ap : s){
			pw.println(ap);
		}
		pw.close();
	}

	//so we don't have duplicate entries with the artists switched - i.e. if we have (A,B), we don't need (B,A)
	class ArtistPair {
		String a, b;

		public ArtistPair(String a, String b){
			this.a = a;
			this.b = b;
		}

		public boolean equals(Object o){
			if (o instanceof ArtistPair){
				ArtistPair other = (ArtistPair) o;
				return (a == other.a && b == other.b) || (b == other.a && a == other.b);
			}
			return false;
		}

		public String toString(){
			return a + "," + b;
		}
	}

}