public class Itemset {

	String[] tabitem;
	Itemset suivant;
	int supp;
	public Itemset(String concat) {

		this.tabitem = concat.split(";");
		this.suivant = null;
	}
	public Itemset() {

		System.out.print("ObjectInstaciationError : the object <Itemsets>");
		System.out.println("with an argument must be instanciated.");
	}

	// Defintion of the methods
	public void AfficheItemset() {
		for(String s : tabitem)
			System.out.println("item : " + s);
	}
	// joining : joining the two itemsets with
	// the laws respected ie the k-1 elements must be the same
	public String[] jointure(String[] freqcandi) {
	
		// Don't forget that you need the preserve the
		// order of the items, to reduce operations
		int adeterminer = this.tabitem.length, compteur = 0;
		String[] rstring = new String[adeterminer + 1];
		if(freqcandi.length == adeterminer) {

			for(String scmp : freqcandi) {

				if(compteur != (adeterminer - 1)) {
					if(!scmp.equals(this.tabitem[compteur])) 
						return rstring;
					else
						rstring[compteur] = scmp;
				}else { break; }
				compteur++;
			}
			rstring[compteur] = this.tabitem[adeterminer - 1];
			rstring[++compteur] = freqcandi[adeterminer - 1];
			return rstring;
		}
		return rstring;
	}
}
