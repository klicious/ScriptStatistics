import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AnalyzeScript {
	/*
	 * 텍스트 문서를 문장 단위로 분리
	 * 각 문장을 단어 단위로 분리
	 * 2단어 조합부터 문장전체까지 "표현" 리스트에 추가
	 * 중복 단어조합이 있을 때 마다 빈도수 +1 추가
	 * 
	 * x단어 조합 추출
	 */
	static String[] exceptions = {
			"he",
			"him",
			"his",
			"she",
			"her",
			"they",
			"them",
			"their",
			"we",
			"us",
			"our",
			"you",
			"your",
			"it",
			"its",
			"I",
			"my",
			"me",
			"mine",
			"Ross",
			"Rosss",
			"Chandler",
			"Chandlers",
			"Joey",
			"Joeys",
			"Rachel",
			"Rachels",
			"Monica",
			"Monicas",
			"Phoebe",
			"Phoebes"
			};
	
	static PrintWriter writer = null;
	static Map<String, Integer> expressions;
	static Set<String> sentences;
	static int min, wordCount, sentenceCount;
	
	public static void main(String[] args) {

		min = 2;
		wordCount = 0;
		sentenceCount = 0;
		
		
		//Create HashSet
		expressions = new HashMap<>();
		sentences = new HashSet<String>();

		for (int a = 0; a < 10; a++) {
			int season = a + 1;
			String fileInput ="Friends_Season" + season + ".txt";
			/*
			 * Read INPUT from INPUT_FILE
			 */
			String line = "";
			
			//Read from inputfile.txt
			try (BufferedReader br = new BufferedReader(new FileReader(fileInput))){
				
				while((line = br.readLine()) != null) {
					line = line.replaceAll("<.*>", " ").replaceAll("-", " ").replaceAll("\\(.*\\)", "").trim();
					
					if(line.length() < 2) continue;
					//Split into sentences.
					
					BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
					iterator.setText(line);
					int start = iterator.first();
					for (int end = iterator.next();
					    end != BreakIterator.DONE;
					    start = end, end = iterator.next()) {
						String sentence = line.substring(start, end);
					  //System.out.println(sentence);
					  sentences.add(sentence);
					}
					
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		
			sentenceCount = sentences.size();
			
		}
		
		/*
		 *  Write to File "i" number of times into i-indexed files.
		 */
		for (int i = 2; i < 6; i++) {
			wordCount = 0;
			expressions.clear();
			minXNoSubject(i);
			//onlyXNoSubject(i);
			//onlyXWithSubject(i);
			// Sort Map by Value
			Map<String, Integer> result = new LinkedHashMap<>();
			result = MapUtil.sortByValue(expressions);
			
			/*
			 * Write OUTPUT to OUTPUT_FILE
			 */
			
			try {
				writer = new PrintWriter("script_analysis_output_min" + i +"_no_subject.txt", "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			writer.println("Total # of Sentences = " + sentenceCount + " : Total # of Words = " + wordCount);
			for (Map.Entry<String, Integer> entry : result.entrySet()) {
				if(entry.getValue() > 1) {
					writer.println(entry.getValue() + " : " + entry.getKey());
				}
				//System.out.println("Occurrence = " + entry.getValue() + " :: " + "expression = " + entry.getKey());
			}
			writer.close();
		}	
		
		
	}
	
	private static void minXNoSubject(int minimum) {
		/*
		 * Play with set of all sentences from the script.
		 */
		for(String ret : sentences) {
			String[] words = ret.replaceAll("[^A-Za-z ]+", "").split("\\W+");
			wordCount += words.length;
			/*
			System.out.println(ret);
			System.out.println("=========================");
			for(String r : words) {
				System.out.println(r);
			}
			System.out.println("=========================");
			*/
			
			boolean skip = false;
			for(int i = 0; i < words.length; i++) {
				for(int j = minimum; i + j < words.length; j++) {
					StringBuilder sb = new StringBuilder();
					int count = 0;
					for(int k = i; k < i + j; k++) {
						for(int a = 0; a < exceptions.length; a++) {
							if(words[k].toLowerCase().equals(exceptions[a].toLowerCase())) {
								//System.out.println(words[k] + exceptions[a]);
								skip = true;
							}
						}
						
						if(skip) {
							skip = false;
							continue;
						}
						
						//System.out.println("words.length = " + words.length + " :: i = " + i + " :: j = " + j + " :: k = " + k);
						sb.append(words[k]);
						sb.append(" ");
						count++;
					}
					
					if(count < min) continue;
					
					String key = sb.toString().trim();
					int value = 1;
					if(expressions.containsKey(key)) {
						value = expressions.get(key);
						value++;
						expressions.put(key, value);
					} else {
						expressions.put(key, value);
					}
					//System.out.println(value + " :: " + key);
				}
			}
			
		}
	}
	
	private static void onlyXNoSubject(int number) {
		/*
		 * Play with set of all sentences from the script.
		 */
		for(String ret : sentences) {
			String[] words = ret.replaceAll("[^A-Za-z ]+", "").split("\\W+");
			wordCount += words.length;
			/*
			System.out.println(ret);
			System.out.println("=========================");
			for(String r : words) {
				System.out.println(r);
			}
			System.out.println("=========================");
			*/
			
			boolean skip = false;
			for(int i = 0; i < words.length; i++) {
					StringBuilder sb = new StringBuilder();
					int count = 0;
					for(int k = i; k < i + number && i + number < words.length; k++) {
						
						for(int a = 0; a < exceptions.length; a++) {
							if(words[k].toLowerCase().equals(exceptions[a].toLowerCase())) {
								//System.out.println(words[k] + exceptions[a]);
								skip = true;
							}
						}
						
						if(skip) {
							skip = false;
							continue;
						}
						
						//System.out.println("words.length = " + words.length + " :: i = " + i + " :: j = " + j + " :: k = " + k);
						sb.append(words[k]);
						sb.append(" ");
						count++;
					}
					
					if(count < number) continue;
					
					String key = sb.toString().trim();
					int value = 1;
					if(expressions.containsKey(key)) {
						value = expressions.get(key);
						value++;
						expressions.put(key, value);
					} else {
						expressions.put(key, value);
					}
					//System.out.println(value + " :: " + key);
			
			}
			
		}
	}
	
	private static void onlyXWithSubject(int number) {
		/*
		 * Play with set of all sentences from the script.
		 */
		for(String ret : sentences) {
			String[] words = ret.replaceAll("[^A-Za-z ]+", "").split("\\W+");
			wordCount += words.length;
			/*
			System.out.println(ret);
			System.out.println("=========================");
			for(String r : words) {
				System.out.println(r);
			}
			System.out.println("=========================");
			*/
			
			for(int i = 0; i < words.length; i++) {
					StringBuilder sb = new StringBuilder();
					int count = 0;
					for(int k = i; k < i + number && i + number < words.length; k++) {
						//System.out.println("words.length = " + words.length + " :: i = " + i + " :: j = " + j + " :: k = " + k);
						sb.append(words[k]);
						sb.append(" ");
						count++;
					}
					
					if(count < number) continue;
					
					String key = sb.toString().trim();
					int value = 1;
					if(expressions.containsKey(key)) {
						value = expressions.get(key);
						value++;
						expressions.put(key, value);
					} else {
						expressions.put(key, value);
					}
					//System.out.println(value + " :: " + key);
			
			}
			
		}
	}

}
