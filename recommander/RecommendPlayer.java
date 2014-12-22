import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RecommendPlayer {	
	
	// GUI components
	JTextArea incoming;
	JTextField outgoing;
	
	// playerName -> offensiveRating:defensiveRating
	HashMap<String, String> playerRating;

	// playerName -> salary
	HashMap<String, String> playerSalary;

	// playerName -> team
	HashMap<String, String> playerTeam;

	// teamName -> offensiveRank:defensiveRank
	HashMap<String, String> teamRank;
	
	// the main program
	public static void main(String[] args) {
		RecommendPlayer recommending = new RecommendPlayer();	 
		recommending.go(args);
	}

	public RecommendPlayer() {
		playerRating = new HashMap<String, String>();
		playerSalary = new HashMap<String, String>();
		teamRank = new HashMap<String, String>();
		playerTeam = new HashMap<String, String>();
	}
	
	public void go(String[] args) {
		try {

			File myFile1 = new File("rating.txt");
			FileReader fileReader1 = new FileReader(myFile1);
			BufferedReader reader1 = new BufferedReader(fileReader1);
			String line = null;

			while((line = reader1.readLine()) != null) {
				String datavalue[] = line.split("\t");
				if(datavalue.length == 3) {
		            String playerName = datavalue[0];
		            String offR = datavalue[1];
		            String defR = datavalue[2];
		            String rating = offR + ":" + defR;
		            playerRating.put(playerName, rating);
		        }
			}
			System.out.println(playerRating.size());

			File myFile2 = new File("Salaries_stat.csv");
			FileReader fileReader2 = new FileReader(myFile2);
			BufferedReader reader2 = new BufferedReader(fileReader2);
			
			line = null;
			while((line = reader2.readLine()) != null) {
				String datavalue[] = line.split(",");
				if(datavalue.length == 6) {
		            String num = datavalue[0];
		            String playerName = datavalue[1];
		            String salary = datavalue[2];
		            String team = datavalue[3];
		            String teamOffRank = datavalue[4];
		            String teamDefRank = datavalue[5];
		            playerSalary.put(playerName, salary);
		            //if(team != null)
		            playerTeam.put(playerName, team);
		            String rank = teamOffRank + ":" + teamDefRank;
		            teamRank.put(team, rank);
		        }
			}	
			//System.out.println(teamRank);
		    System.out.println(teamRank.size());
		    //System.out.println(playerSalary);
		    System.out.println(playerSalary.size());
		    //System.out.println(playerTeam);
		    System.out.println(playerTeam.size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}


		// construct the GUI for user input		
		JFrame frame = new JFrame("NBA Team");
		JPanel mainPanel = new JPanel();
		incoming = new JTextArea(15, 65);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		outgoing = new JTextField(20);
		
		Command sendListen = new Command();
		outgoing.addActionListener(sendListen);		
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
	
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(800,500);
		frame.setVisible(true);
	}

	public class Command implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {				
				String message = outgoing.getText();
				if(teamRank.containsKey(message)) {
					String team = message;
					String rank = teamRank.get(team);
					String[] rankArr = rank.split(":");
					int teamOffR = Integer.parseInt(rankArr[0]);
					int teamDefR = Integer.parseInt(rankArr[1]);
					
					ArrayList<String> teamPlayer = new ArrayList<String>();
					Iterator<Map.Entry<String, String>> iterator = playerTeam.entrySet().iterator();
					while(iterator.hasNext()) {
		   				Map.Entry<String, String> entry = iterator.next();
		   				String key = entry.getKey();
		  				String value = entry.getValue();
		  				if(value.equals(team)) {
		  					teamPlayer.add(key);
		  				}
		 			}
		 			System.out.println(teamPlayer);
		 			System.out.println(teamPlayer.size());
		 			HashMap<String, Double> teamPlayerR = new HashMap<String, Double>();
		 			HashMap<String, Double> teamPlayerS = new HashMap<String, Double>();
					if(teamOffR >= teamDefR) {
						System.out.println("find offensive!");
						for(int i = 0; i < teamPlayer.size();i++) {
							String name = teamPlayer.get(i);
							if(playerRating.containsKey(name)) {
								String rating = playerRating.get(name);
								String[] ratingArr = rating.split(":");
								double playerOffR = Double.parseDouble(ratingArr[0]);
								//int playerDefR = Integer.parseInt(ratingArr[1]);
								teamPlayerR.put(name, playerOffR);
								double salary = Double.parseDouble(playerSalary.get(name));
								teamPlayerS.put(name, salary);
							}
						}

						while(teamPlayerR.size() > 5) {
							Iterator<Map.Entry<String, Double>> iterator2 = teamPlayerR.entrySet().iterator();
							double maxR = 0;
		 			
							while(iterator2.hasNext()) {
								Map.Entry<String, Double> entry = iterator2.next();
		   						double playerOffR = entry.getValue();
		   						if(playerOffR > maxR) {
		   							maxR = playerOffR;
		   						}
							}
							Iterator<Map.Entry<String, Double>> iterator3 = teamPlayerR.entrySet().iterator();
							while(iterator3.hasNext()) {
								Map.Entry<String, Double> entry = iterator3.next();
								String name = entry.getKey();
		   						double playerOffR = entry.getValue();
		   						if(playerOffR == maxR) {
		   							maxR = playerOffR;
		   							iterator3.remove();
		   							teamPlayerS.remove(name);
		   						}
							}
						}
						int random = (int) (Math.random() * teamPlayerS.size());
						Iterator<Map.Entry<String, Double>> iterator4 = teamPlayerS.entrySet().iterator();
						int countRan = 1;
						String tradeOutName = null;
						double tradeOutSalary = 0;
						while(iterator4.hasNext()) {
		   					Map.Entry<String, Double> entry = iterator4.next();
		   					if(countRan == random) {
		   						tradeOutName = entry.getKey();
		   						tradeOutSalary = entry.getValue(); 
		   					}
		   					countRan++;
		   				}
		   				// name -> salary
		   				HashMap<String, Double> selectedPlayer = new HashMap<String, Double>();
						Iterator<Map.Entry<String, String>> iterator5 = playerSalary.entrySet().iterator();
						while(iterator5.hasNext()) {
		   					Map.Entry<String, String> entry = iterator5.next();
		   					String name = entry.getKey();
		   					double salary = Double.parseDouble(entry.getValue());
		   					if(tradeOutSalary - 1000000 <= salary && salary <= tradeOutSalary + 1000000) {
		   						if(!teamPlayerS.containsKey(name)) {
		   							selectedPlayer.put(name, salary);
		   						}	
		   					}
						}

						// name -> offensive rating
		   				HashMap<String, Double> selectedPlayerR = new HashMap<String, Double>();
		   				int countRev = 0;
						while(countRev < 5) {
							Iterator<Map.Entry<String, Double>> iterator6 = selectedPlayer.entrySet().iterator();
							double maxR = 0;
							double offensiveR = 0;
							String maxName = null;
							while(iterator6.hasNext()) {
								Map.Entry<String, Double> entry = iterator6.next();
								String name = entry.getKey();
		   						String rating = playerRating.get(name);
		   						//System.out.println("rating: " + rating);
		   						if(rating != null) {
			   						String[] ratingArr = rating.split(":");
			   						offensiveR = Double.parseDouble(ratingArr[0]);
			   						if(offensiveR > maxR) {
			   							maxR = offensiveR;
			   							maxName = name;
			   						}
			   					}	
							}
							selectedPlayer.remove(maxName);
							selectedPlayerR.put(maxName, offensiveR);
							countRev++;
						}

						System.out.println(selectedPlayerR);
						Iterator<Map.Entry<String, Double>> iterator8 = selectedPlayerR.entrySet().iterator();
						incoming.append(message + ":" + "\n");
						while(iterator8.hasNext()) {
		   					Map.Entry<String, Double> entry = iterator8.next();
		   					String name = entry.getKey();
		   					String offensiveR = Double.toString(entry.getValue());
		   					String salary = playerSalary.get(name);
		   					String teamName = playerTeam.get(name);
		   					if(!teamName.equals(team)) {		
			   					incoming.append("Recommended Player: " + entry.getKey() + "  " +  "Offensive Rating: " + 
			   						offensiveR + "  " + "Salary: " + salary + "  " + "Team: " + teamName + "\n");
			   				}	
		   				}

		   				String ratingFin = playerRating.get(tradeOutName);
		   				if(ratingFin != null) {
			   				String[] ratingArr = ratingFin.split(":");
			   				incoming.append("\n" + "Suggested Trade Out PLayer: " + tradeOutName + "  " + "Salary: " + tradeOutSalary 
			   					+ "  " + "Offensive Rating: " + ratingArr[0] + "  " + "Defensive Rating: " + ratingArr[1] + "\n");
			   			}	
					}
					else {
						System.out.println("find defensive!");
						for(int i = 0; i < teamPlayer.size();i++) {
							String name = teamPlayer.get(i);
							if(playerRating.containsKey(name)) {
								String rating = playerRating.get(name);
								String[] ratingArr = rating.split(":");
								double playerOffR = Double.parseDouble(ratingArr[1]);
								teamPlayerR.put(name, playerOffR);
								double salary = Double.parseDouble(playerSalary.get(name));
								teamPlayerS.put(name, salary);
							}
						}
	
						while(teamPlayerR.size() > 5) {
							Iterator<Map.Entry<String, Double>> iterator2 = teamPlayerR.entrySet().iterator();
							double minR = 150;
		 			
							while(iterator2.hasNext()) {
								Map.Entry<String, Double> entry = iterator2.next();
		   						double playerDefR = entry.getValue();
		   						if(playerDefR <= minR) {
		   							minR = playerDefR;
		   						}
							}
							Iterator<Map.Entry<String, Double>> iterator3 = teamPlayerR.entrySet().iterator();
							while(iterator3.hasNext()) {
								Map.Entry<String, Double> entry = iterator3.next();
								String name = entry.getKey();
		   						double playerDefR = entry.getValue();
		   						if(playerDefR == minR) {
		   							minR = playerDefR;
		   							iterator3.remove();
		   							teamPlayerS.remove(name);
		   						}
							}
						}
						int random = (int) (Math.random() * teamPlayerS.size());
						Iterator<Map.Entry<String, Double>> iterator4 = teamPlayerS.entrySet().iterator();
						int countRan = 1;
						String tradeOutName = null;
						double tradeOutSalary = 0;
						while(iterator4.hasNext()) {
		   					Map.Entry<String, Double> entry = iterator4.next();
		   					if(countRan == random) {
		   						tradeOutName = entry.getKey();
		   						tradeOutSalary = entry.getValue(); 
		   					}
		   					countRan++;
		   				}
		   				// name -> salary
		   				HashMap<String, Double> selectedPlayer = new HashMap<String, Double>();
						Iterator<Map.Entry<String, String>> iterator5 = playerSalary.entrySet().iterator();
						while(iterator5.hasNext()) {
		   					Map.Entry<String, String> entry = iterator5.next();
		   					String name = entry.getKey();
		   					double salary = Double.parseDouble(entry.getValue());
		   					if(tradeOutSalary - 1000000 <= salary && salary <= tradeOutSalary + 1000000) {
		   						if(!teamPlayerS.containsKey(name)) {
		   							selectedPlayer.put(name, salary);
		   						}	
		   					}
						}

						// name -> defensive rating
		   				HashMap<String, Double> selectedPlayerR = new HashMap<String, Double>();
		   				int countRev = 0;
						while(countRev < 5) {
							Iterator<Map.Entry<String, Double>> iterator6 = selectedPlayer.entrySet().iterator();
							double minR = 150;
							double defensiveR = 0;
							String minName = null;
							while(iterator6.hasNext()) {
								Map.Entry<String, Double> entry = iterator6.next();
								String name = entry.getKey();
		   						String rating = playerRating.get(name);
		   						//System.out.println("rating: " + rating);
		   						if(rating != null) {
			   						String[] ratingArr = rating.split(":");
			   						defensiveR = Double.parseDouble(ratingArr[1]);
			   						if(defensiveR < minR) {
			   							minR = defensiveR;
			   							minName = name;
			   						}
			   					}	
							}
							selectedPlayer.remove(minName);
							selectedPlayerR.put(minName, defensiveR);
							countRev++;
						}
						System.out.println(selectedPlayerR);
						System.out.println("here");
						Iterator<Map.Entry<String, Double>> iterator8 = selectedPlayerR.entrySet().iterator();
						incoming.append(message + ":" + "\n");
						while(iterator8.hasNext()) {
		   					Map.Entry<String, Double> entry = iterator8.next();
		   					String name = entry.getKey();
		   					String defensiveR = Double.toString(entry.getValue());
		   					String salary = playerSalary.get(name);
		   					String teamName = playerTeam.get(name);
		   					if(!teamName.equals(team)) {
			   					incoming.append("Recommended Player: " + entry.getKey() + "  " +  "Defensive Rating: " + 
			   						defensiveR + "  " + "Salary: " + salary + "  " + "Team: " + teamName + "\n");
			   				}	
		   				}

		   				String ratingFin = playerRating.get(tradeOutName);
		   				if(ratingFin != null) {
			   				String[] ratingArr = ratingFin.split(":");
			   				incoming.append("\n" + "Suggested Trade Out PLayer: " + tradeOutName + "  " + "Salary: " + tradeOutSalary 
			   					+ "  " + "Offensive Rating: " + ratingArr[0] + "  " + "Defensive Rating: " + ratingArr[1] + "\n");
			   			}	
					}	
				}
				else {
					incoming.append("Invalid Team Name!" + "\n");
				}	
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
	} // close Command inner class
}	