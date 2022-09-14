import java.io.*;

public class Arules {

	// attributs : using fake hashmap hashmaps :)
	// the size of the itemset array will be determined
	// by the number of item found in the items arguments.
	

	// Somthing to note About the size of the hashmap `hash`
	//	- Technically, the size of the hashmap is as a function
	//	  of the cardinality of the set of frequent itemsets each 
	//	  of length `1`, but decided, to guess a big value which 
	//	  is very bad because it might present a `nullpointerexception`
	//	  in case it's not sufficient to determine other frequent itemset
	Litemset[] hash = new Litemset[100];

	String transacs; 
	int pourcentagesup;
	float pourcentageconf, minconf;
	public Arules(String ts, int x, float y) {
		this.transacs = ts;
		this.pourcentagesup = x; this.pourcentageconf = y;
	}
	public Arules() { this(80, 80); }
	public Arules(int x, int y) { 
		this.transacs = "transactions";
		this.pourcentagesup = x; this.pourcentageconf = y;
	}
	private boolean PremierParcours() {
	
		int linenumber = 0;
		try {
			//////////////////////////////////////////////////////
			// put the api use to read excel files line by line //
			//////////////////////////////////////////////////////
			FileReader file = new FileReader(this.transacs);
			BufferedReader buff = new BufferedReader(file);

			String[] transaction, toget = new String[1];
			Litemset freqone = new Litemset(null);
			Itemset newnode = null;
			System.out.println("\nGenerating frequent itemsets ...\n");
			while(buff.ready())	{

				transaction = buff.readLine().split(";");
				newnode = freqone.tete;
				while(newnode != null) {

					for(int x = 0;x < transaction.length;x++)
						if(transaction[x] != null)
							if(transaction[x].equals(newnode.tabitem[0])) {
								newnode.supp++;
								transaction[x] = null;
							} 
					newnode = newnode.suivant;
				}
				for(String s : transaction)
					if(s != null) {
						toget[0] = s;
						freqone.Ajouter(toget, 0);
						freqone.queue.supp++;
					}
				linenumber++;
			}
			// setting the real value of the minsup for further test
			this.minconf = ((((float)linenumber) * ((float)this.pourcentagesup))/100);
			// filtering the non-sense before adding to hashmap
			// mark has as function of enabling and disabling insertion
			freqone = this.AfficheFrequent(freqone.tete, -1);
			hash[0] = freqone;
		}catch(FileNotFoundException e) {
			System.out.println("Error file not found in your current working directory");
			return false;
		}catch(IOException e) {
			e.printStackTrace();  
		}
		return true;
	}
	public boolean Apriori() {
		
		int cle = 0;
		boolean verifi = this.PremierParcours();
		if(verifi == false)
			return false;
		
		while(this.hash[cle].length != 0) {

			this.hash[cle + 1] = new Litemset(hash[cle].tete);
			this.hash[cle + 1].TrouvePck();
			this.hash[cle + 1] = this.CalculFrequencedesCk(this.hash[cle + 1]);
			// get the frequent itemsets
			hash[cle + 1] = this.AfficheFrequent(hash[cle + 1].tete, cle);
			cle++;
		}
		return true;
	}
	private Litemset CalculFrequencedesCk(Litemset aghdoit) {
		
		////////////////////////////////////////////////////////
		/// put the api use to read excel files line by line ///
		////////////////////////////////////////////////////////
		if(aghdoit.tete == null)
			return aghdoit;
		try {  

			FileReader fileread = new FileReader(this.transacs);
			BufferedReader reader = new BufferedReader(fileread);
			String[] transaction;
			Itemset gothrough = null, getit = aghdoit.tete;
			int size = 0;
			
			while(reader.ready()) {  

				transaction = reader.readLine().split(";");
				gothrough = aghdoit.tete;
				// break if transaction size is less than tabitem size
				// this makes sense 'cause no one can try to determine
				// inclusion of A in B if card(B) < card(A).
				if(transaction.length < gothrough.tabitem.length)
					continue;
				while(gothrough != null) {
					
					size = 0;
					for(String s : gothrough.tabitem)
						for(String k : transaction)
							if(s.equals(k)) size++;
					if(gothrough.tabitem.length <= size)
						gothrough.supp++;
					gothrough = gothrough.suivant;
				}
			}  
		}catch(IOException e) {
			e.printStackTrace();  
		}
		return aghdoit;
	}
	private Litemset AfficheFrequent(Itemset gothrough, int hashindex) {

		Litemset tmplitemset = new Litemset(null);
		boolean todo = true;
		while(gothrough != null) {
				
			if(((float)gothrough.supp) >= this.minconf) {
				if(todo) {
					System.out.println("Frequent Itemsets of length " + (hashindex + 2) + "\n" + "{ ");
					todo = false;
				}
				tmplitemset.Ajouter(gothrough.tabitem, gothrough.supp);
				for(String s : gothrough.tabitem)
					System.out.print(" [ " + s + " ] ");
				System.out.println();
			}
			gothrough = gothrough.suivant;
			if(todo == false && gothrough == null)
				System.out.println("} ");
		}
		return tmplitemset;
	}
	public boolean RegleAssociation() {

		//gothrough all frequent itemset in the hashmap
		int compteur = 1;
		if(this.hash[compteur] == null || this.hash[compteur - 1] == null) {

			System.out.println("Sorry, but there are NO association rules in the transaction set.");
			return false;
		}
		Litemset flitemset = new Litemset(null);
		Itemset itis = this.hash[compteur].tete;
		System.out.println("\nGenerating association rules ...");
		while(this.hash[compteur] != null) {

			itis = this.hash[compteur].tete;
			while(itis != null) {
		
				flitemset = this.SousensembleTailleUn(itis);
				flitemset = this.AfficheRegle(itis, flitemset);
				itis = itis.suivant;
			}
			if(itis != null)
				this.RegleRecursive(itis, flitemset, 1);
			compteur++;
		}
		return true;
	}
	private void RegleRecursive(Itemset argone, Litemset argtwo, int edge) {
		
		Litemset tmplitemset = new Litemset(argtwo.tete);

		if(argone.tabitem.length > edge) {
			
			tmplitemset.TrouvePck();
			tmplitemset = this.AfficheRegle(argone, tmplitemset);
			this.RegleRecursive(argone, tmplitemset, ++edge);
		}
	}
	private Litemset SousensembleTailleUn(Itemset togenerate) {

		Litemset tmplitemset = new Litemset(null);
		Itemset tmpitemset =  hash[0].tete;
		String[] block = new String[1];
		
		for(String s : togenerate.tabitem)
			while(tmpitemset != null) {
				if(tmpitemset.tabitem[0].equals(s)) {
					block[0] = s;
					tmplitemset.Ajouter(block, tmpitemset.supp);
				}
				tmpitemset = tmpitemset.suivant;	
			}
		return tmplitemset;
	}
	private Litemset AfficheRegle(Itemset bigset, Litemset calfrom) {

		Itemset tmpitemset, deuxitemset = calfrom.tete;
		Litemset tmplitemset = new Litemset(calfrom.avantca);
		boolean toknow = true;
		int leftlength = bigset.tabitem.length - 1, unionsup,
			rightlength = deuxitemset.tabitem.length - 1, index = 0;
		while(deuxitemset != null) {

			tmpitemset = hash[rightlength].tete;
			while(toknow) {
				
				toknow = true; index = 0;
				for(index = 0;index < tmpitemset.tabitem.length;index++)
					if(tmpitemset.tabitem[index].equals(deuxitemset.tabitem[index]))
						break;
				if((index + 1) == tmpitemset.tabitem.length) {
					toknow = false;
					deuxitemset.supp = tmpitemset.supp;
				}
				tmpitemset = tmpitemset.suivant;
			}
			toknow = true;
			unionsup = bigset.supp;
			bigset = this.Exclusion(bigset, deuxitemset.tabitem);
			if((unionsup/deuxitemset.supp) >= this.pourcentageconf) {

				System.out.print(" *====    { °° ");
				for(String k : deuxitemset.tabitem)
					System.out.print(k + " [ ");
				System.out.println("}\n |");
				System.out.println(" |");
				System.out.print(" *====||> { [ ");
				for(String s : bigset.tabitem)
					System.out.print(s + " ] ");
				System.out.println("}");
				tmplitemset.Ajouter(deuxitemset.tabitem, deuxitemset.supp);
			}
			deuxitemset = deuxitemset.suivant;
		}
		return tmplitemset;
	}
	private Itemset Exclusion(Itemset dumb, String[] defil) {

		boolean escape = false;
		String tmplitemset = "";
		Itemset	filtered;
		for(String s : dumb.tabitem) {
			
			for(String k : defil)
				if(s.equals(k))
					escape = true;
			if(escape) { escape = false; continue; }
			tmplitemset = tmplitemset + ";" + s;
		}
		tmplitemset = tmplitemset.substring(1, tmplitemset.length());
		filtered = new Itemset(tmplitemset);
		return filtered;
	}
}
