public class Litemset {

	// this class is composed of linked itemsets
	// which is important for Hashmap class.
	
	// contructors
	public Itemset tete;
	public Itemset queue;
	public Itemset avantca; 
	int length = 0;
	public Litemset(Itemset behind) { 
		avantca = behind; 
		tete = null;
		queue = null;
	}
	// ATTENTION : you need to know that order counts 
	// now, ie the order of the items in the array.
	// if it's not taken into consideration, then a 
	// problem may arise on 'Itemset.java' implemented methods.

	// definition of the methods
	public String[] AllerA(int index) {

		Itemset parcours = this.tete;
		int gothrough = 1;
		while(gothrough != index) {
			parcours = parcours.suivant;
			gothrough++;
		}
		return parcours.tabitem;
	}
	public void Ajouter(String[] toadd, int supp) {
	
		String tous = "";
		for(String s : toadd)
			tous = tous + ";" +  s;
		// deleting the last ';' character
		tous = tous.substring(1, tous.length());
		Itemset nelement = new Itemset(tous);
		nelement.supp = supp;
		if(this.length == 0) {
			queue = nelement;
			tete = nelement;
		}else {
			queue.suivant =  nelement;
			queue = nelement;
		}
		this.length++;
	}
	private boolean trouveck(String[] verification) {

		String[] chaine = new String[verification.length - 1];
		int compteur = 0,resultat = 0, scompteur;
		Itemset tmpitemset;

		for(String k : verification) {
			for(String j : verification)
				if(!j.equals(k)) {
					chaine[compteur] = j; // or j
					compteur++;
				}
			tmpitemset = this.avantca;
			while(tmpitemset != null) {
				scompteur = 0;
				for(String s : tmpitemset.tabitem) {
					if(!s.equals(chaine[scompteur]))
						break;
					scompteur++;
				}
				if(scompteur == tmpitemset.tabitem.length) break;
				tmpitemset = tmpitemset.suivant; 
			}
			if(tmpitemset == null)
				return false;	
			compteur = 0;
		}
		this.Ajouter(verification, 0);
		return true;
	}
	public void TrouvePck() {
		
		String[] tabchain;
		int x = 0;
		Itemset tmpitemset = this.avantca, secondtmp;
		while(tmpitemset.suivant != null) {
			
			secondtmp = tmpitemset.suivant;
			while(secondtmp != null) {

				tabchain = tmpitemset.jointure(secondtmp.tabitem);
				x = (secondtmp.tabitem.length - 1);
				if(tabchain[x] != null)
					this.trouveck(tabchain);
				secondtmp = secondtmp.suivant;
			}
			tmpitemset = tmpitemset.suivant;
		}
	}

}
