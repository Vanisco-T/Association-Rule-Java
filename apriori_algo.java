?  

//---- apriori.java

//---- input file need:
//----   1. config.txt 
//----      four lines, each line a integer
//----      item number, transaction number , minsup
//----   2. transa.txt

package apriori_algo ;

import java.io.*;
import java.lang.Integer.* ;
import java.lang.Object.* ;
import java.util.*;
import org.jdom.*;
import org.jdom.output.*;
import java.lang.String.*;
//-------------------------------------------------------------
//  Class Name : apriori
//  Purpose    : main program class
//-------------------------------------------------------------
public class apriori_algo {

  public static void main(String[] args) throws IOException {

    aprioriProcess process1=new aprioriProcess();
    System.exit(0);

  }
}

//-------------------------------------------------------------
//  Class Name : aprioriProcess
//  Purpose    : main processing class
//-------------------------------------------------------------
class aprioriProcess {
	
//Nous allons commencer notre arborescence en crant la racine XML
   //qui sera ici "personnes".
   static Element racine ;//= new Element("Apriori");

   //On cre un nouveau Document JDOM bas sur la racine que l'on vient de crer
   static org.jdom.Document document ;// = new Document(racine);	

  private final int HT=1; // state of tree node (hash table or
  private final int IL=2; // itemset list)
  int N; // total item #
  int M; // total transaction #
  int minsup ;
  
  Vector largeitemset = new Vector() ;
  Vector candidate = new Vector() ;
  Vector Support = new Vector() ;
  
  String fullitemset;
  String configfile = "config.txt" ;
  String transafile = "transa25.txt" ;


//-------------------------------------------------------------
//  Class Name : candidateelement
//  Purpose    : object that will be stored in Vector candidate
//             : include 2 item
//             : a hash tree and a candidate list
//-------------------------------------------------------------
  class candidateelement {
    hashtreenode htroot;
    Vector candlist;
  }


//-------------------------------------------------------------
//  Class Name : hashtreenode
//  Purpose    : node of hash tree
//-------------------------------------------------------------
  class hashtreenode {
    int nodeattr; //  IL or HT
    int depth;
    Hashtable ht;
    Vector itemsetlist;

    public void hashtreenode() {
      nodeattr=HT;
      ht=new Hashtable();
      itemsetlist=new Vector();
      depth=0;
    }

    public void hashtreenode(int i) {
      nodeattr=i;
      ht=new Hashtable();
      itemsetlist=new Vector();
      depth=0;
    }
  }  


//-------------------------------------------------------------
//  Class Name : itemsetnode
//  Purpose    : node of itemset
//-------------------------------------------------------------
  class itemsetnode {
    String itemset;
    int counter;
    
    public itemsetnode(String s1,int i1) {
      itemset=new String(s1);
      counter=i1;
    }

    public itemsetnode() {
      itemset=new String();
      counter=0;
    }

    public String toString() {
      String tmp=new String();
      tmp=tmp.concat("<\"");
      tmp=tmp.concat(itemset);
      tmp=tmp.concat("\",");
      tmp=tmp.concat(Integer.toString(counter));
      tmp=tmp.concat(">");
      return tmp;
    }
  }


//-------------------------------------------------------------
//  Method Name: printhashtree
//  Purpose    : print the whole hash tree
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : special transaction with all items occurr in it.
//             : a : recursive depth
//  Return     : 
//-------------------------------------------------------------
  public void printhashtree(hashtreenode htn,String transa,int a) {
    if (htn.nodeattr == IL ) {
      System.out.println("Node is an itemset list");
      System.out.println("	depth :<"+htn.depth+">");
      System.out.println("	iteset:<"+htn.itemsetlist+">");
    }
    else { // HT
      System.out.println("Node is a hashtable");
      if (htn.ht==null)
        return;
      for (int b=a+1;b<=N;b++)
        if (htn.ht.containsKey(Integer.toString(getitemat(b,transa)))) {
          System.out.println("	key:<"+getitemat(b,transa));
          printhashtree((hashtreenode)htn.ht.get(Integer.toString(getitemat(b,transa))),transa,b);
        }
    }
  }


//-------------------------------------------------------------
//  Method Name: getconfig
//  Purpose    : open file config.txt
//             : get the total number of items of transaction file
//             : and the total number of transactions
//             : and minsup
//-------------------------------------------------------------
  public void getconfig() throws IOException {

    FileInputStream file_in;
    DataInputStream data_in;
    String oneline=new String();
    int i=0;

    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    String response = "";

    System.out.println("Appuyer sur 'C' changer la configuration and le fichier de transaction par défault");
    System.out.print("Ou sur n'import quelle touche pour continuer.  ");
    try {
      response = reader.readLine();
    } catch (Exception e) {
      System.out.println(e);
    }

    int res=response.compareTo("C") * response.compareTo("c");

    if(res == 0) {
      System.out.print("\nEnter new transaction filename: ");
      try {
        transafile = reader.readLine();
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.print("Enter new configuration filename: ");
      try {
        configfile = reader.readLine();
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.println("Filenames changed");
    }

    try {
      file_in = new FileInputStream(configfile);
      data_in = new DataInputStream(file_in);

      oneline=data_in.readLine();
      N=Integer.valueOf(oneline).intValue();
      oneline=data_in.readLine();
      M=Integer.valueOf(oneline).intValue();
      oneline=data_in.readLine();
      minsup=Integer.valueOf(oneline).intValue();
      System.out.print("\n configuration: "+N+" items, "+M+" transactions, ");
      System.out.println("minsup = "+minsup+"%");
      System.out.println();
    } catch (IOException e) {
      System.out.println(e);
    }
  }


//-------------------------------------------------------------
//  Method Name: getitemat
//  Purpose    : get an item from an itemset
//             : get the total number of items of transaction file
//  Parameter  : int i : i-th item ; itemset : string itemset
//  Return     : int : the item at i-th in the itemset 
//-------------------------------------------------------------
  public int getitemat(int i,String itemset) {

    String str1=new String(itemset);
    StringTokenizer st=new StringTokenizer(itemset);
    int j;

    if (i > st.countTokens())
      System.out.println("eRRor! in getitemat, !!!!");

    for (j=1;j<=i;j++)
      str1=st.nextToken();

    return(Integer.valueOf(str1).intValue());
  }


//-------------------------------------------------------------
//  Method Name: itesetsize
//  Purpose    : get item number of an itemset
//  Parameter  : itemset : string itemset
//  Return     : int : the number of item of the itemset 
//-------------------------------------------------------------
  public int itemsetsize(String itemset) {
    StringTokenizer st=new StringTokenizer(itemset);
    return st.countTokens();
  }


//-------------------------------------------------------------
//  Method Name: gensubset
//  Purpose    : generate all subset given an itemset
//  Parameter  : itemset
//  Return     : a string contains all subset deliminated by ","
//             : e.g. "1 2,1 3,2 3" is subset of "1 2 3"
//-------------------------------------------------------------
  public String gensubset(String itemset) {

    int len=itemsetsize(itemset);
    int i,j;
    String str1;
    String str2=new String();
    String str3=new String();

    if (len==1)
      return null;
    for (i=1;i<=len;i++) {
      StringTokenizer st=new StringTokenizer(itemset);
      str1=new String();
      for (j=1;j<i;j++) {
        str1=str1.concat(st.nextToken());
        str1=str1.concat(" ");
      }
      str2=st.nextToken();
      for (j=i+1;j<=len;j++) {
        str1=str1.concat(st.nextToken());
        str1=str1.concat(" ");
      }
      if (i!=1)
        str3=str3.concat(",");
      str3=str3.concat(str1.trim());
    }

    return str3;

  } //end public String gensubset(String itemset)


//-------------------------------------------------------------
//  Method Name: createcandidate
//  Purpose    : generate candidate n-itemset
//  Parameter  : int n : n-itemset
//  Return     : Vector : candidate is stored in a Vector
//-------------------------------------------------------------
  public Vector createcandidate(int n) { 

    Vector tempcandlist=new Vector();
    Vector ln_1=new Vector();
    int i,j,length1;
    String cand1=new String();
    String cand2=new String();
    String newcand=new String();
    
//System.out.println("Generating "+n+"-candidate item set ....");
    if (n==1)
      for (i=1;i<=N;i++)
        tempcandlist.addElement(Integer.toString(i));
    else {
      ln_1=(Vector)largeitemset.elementAt(n-2);
      length1=ln_1.size();
      for (i=0;i<length1;i++) {
        cand1=(String)ln_1.elementAt(i);
        for (j=i+1;j<length1;j++) {
          cand2=(String)ln_1.elementAt(j);
          newcand=new String();
          if (n==2) {
            newcand=cand1.concat(" ");
            newcand=newcand.concat(cand2);
            tempcandlist.addElement(newcand.trim());
          }
          else {
            int c,i1,i2;
            boolean same=true;

            for (c=1;c<=n-2;c++) {
              i1=getitemat(c,cand1);
              i2=getitemat(c,cand2);
              if ( i1!=i2 ) {
                same=false;
                break;
              }
              else {
                newcand=newcand.concat(" ");
                newcand=newcand.concat(Integer.toString(i1));
              }
            }
            if (same) {
              i1=getitemat(n-1,cand1);
              i2=getitemat(n-1,cand2);
              newcand=newcand.concat(" ");
              newcand=newcand.concat(Integer.toString(i1));
              newcand=newcand.concat(" ");
              newcand=newcand.concat(Integer.toString(i2));
              tempcandlist.addElement(newcand.trim());
            }
          } //end if n==2 else
        } //end for j
      } //end for i
    } //end if n==1 else

    if (n<=2) 
      return tempcandlist;

    Vector newcandlist=new Vector();
    for (int c=0; c<tempcandlist.size(); c++) {
      String c1=(String)tempcandlist.elementAt(c);
      String subset=gensubset(c1);
      StringTokenizer stsubset=new StringTokenizer(subset,",");
      boolean fake=false;
      while (stsubset.hasMoreTokens())
	if (!ln_1.contains(stsubset.nextToken())) {
          fake=true;
	  break;
        }
      if (!fake)
	newcandlist.addElement(c1);
    }

    return newcandlist;

  } //end public createcandidate(int n)

  
//-------------------------------------------------------------
//  Method Name: createcandidatehashtre
//  Purpose    : generate candidate hash tree
//  Parameter  : int n : n-itemset
//  Return     : hashtreenode : root of the hashtree
//-------------------------------------------------------------
  public hashtreenode createcandidatehashtree(int n) {  

    int i,len1;
    hashtreenode htn=new hashtreenode();

//System.out.println("Generating candidate "+n+"-itemset hashtree ....");
    if (n==1)
      htn.nodeattr=IL;
    else
      htn.nodeattr=HT;

    len1=((candidateelement)candidate.elementAt(n-1)).candlist.size();
    for (i=1;i<=len1;i++) {
      String cand1=new String();
      cand1=(String)((candidateelement)candidate.elementAt(n-1)).candlist.elementAt(i-1);
      genhash(1,htn,cand1);
    }

    return htn;

  } //end public createcandidatehashtree(int n)


//-------------------------------------------------------------
//  Method Name: genhash
//  Purpose    : called by createcandidatehashtree
//             : recursively generate hash tree node
//  Parameter  : htnf is a hashtreenode (when other method call this method,it is the root)
//             : cand : candidate itemset string
//             : int i : recursive depth,from i-th item, recursive
//  Return     : 
//-------------------------------------------------------------
  public void genhash(int i, hashtreenode htnf, String cand) {
    
    int n=itemsetsize(cand);
    if (i==n) {
      htnf.nodeattr=IL;
      htnf.depth=n;
      itemsetnode isn=new itemsetnode(cand,0);
      if (htnf.itemsetlist==null)
        htnf.itemsetlist=new Vector();
      htnf.itemsetlist.addElement(isn);
    }
    else {
      if (htnf.ht==null) 
        htnf.ht=new Hashtable(HT);
      if (htnf.ht.containsKey(Integer.toString(getitemat(i,cand)))) {
        htnf=(hashtreenode)htnf.ht.get(Integer.toString(getitemat(i,cand)));
        genhash(i+1,htnf,cand);
      }
      else {
        hashtreenode htn=new hashtreenode();
        htnf.ht.put(Integer.toString(getitemat(i,cand)),htn);
        if (i==n-1) {
          htn.nodeattr=IL;
          Vector isl=new Vector();
          htn.itemsetlist=isl;
          genhash(i+1,htn,cand);
        }
        else {
          htn.nodeattr=HT;
          Hashtable ht=new Hashtable();
          htn.ht=ht;
          genhash(i+1,htn,cand);
        }
      }
    }
  } //end public void genhash(int i, hashtreenode htnf, String cand)


//-------------------------------------------------------------
//  Method Name: createlargeitemset
//  Purpose    : find all itemset which have their counters>=minsup
//  Parameter  : int n : n-itemset
//  Return     : 
//-------------------------------------------------------------
  public void createlargeitemset(int n) {

    Vector candlist=new Vector();
    Vector lis=new Vector(); //large item set
    hashtreenode htn=new hashtreenode();
    int i;

//    System.out.println("Generating "+n+"-large item set ....");
    candlist=((candidateelement)candidate.elementAt(n-1)).candlist;
    htn=((candidateelement)candidate.elementAt(n-1)).htroot;
      
    getlargehash(0,htn,fullitemset,lis);

    largeitemset.addElement(lis);

  } // end public void createlargeitemset(int n)


//-------------------------------------------------------------
//  Method Name: getlargehash
//  Purpose    : recursively traverse candidate hash tree 
//             : to find all large itemset
//  Parameter  : htnf is a hashtreenode (when other method call this method,it is the root)
//             : cand : candidate itemset string
//             : int i : recursive depth
//             : Vector lis : Vector that stores large itemsets
//  Return     : 
//-------------------------------------------------------------
  public void getlargehash(int i,hashtreenode htnf,String transa,Vector lis) {

    Vector tempvec=new Vector();
    int j;

   if (htnf.nodeattr==IL) {
      tempvec=htnf.itemsetlist;
      for (j=1;j<=tempvec.size();j++)
        if (((itemsetnode)tempvec.elementAt(j-1)).counter >= ((minsup * M) / 100))
          { 
          	lis.addElement( ((itemsetnode)tempvec.elementAt(j-1)).itemset ) ;
          	Support.addElement(((itemsetnode)tempvec.elementAt(j-1)).counter ) ;
          }
          
    }
    else {
      if (htnf.ht==null)
        return;
      for (int b=i+1;b<=N;b++)
      {  
      	if (htnf.ht.containsKey(Integer.toString(getitemat(b,transa))))
          getlargehash(b,(hashtreenode)htnf.ht.get(Integer.toString(getitemat(b,transa))),transa,lis);
      }
        
    }
  }


//-------------------------------------------------------------
//  Method Name: transatraverse
//  Purpose    : read each transaction, traverse hashtree, 
//               incrment approporiate itemset counter.
//  Parameter  : int n : n-itemset
//  Return     : 
//-------------------------------------------------------------
  public void transatraverse(int n) {

    FileInputStream file_in;
    DataInputStream data_in;
    String oneline=new String();
    int i=0,j=0,len=0;
    String transa;
    hashtreenode htn=new hashtreenode();
    StringTokenizer st;
    String str0;
    int numRead=0;

    //System.out.println("Traverse "+n+"-candidate hashtree ... ");
    htn=((candidateelement)candidate.elementAt(n-1)).htroot;
    try {
      file_in = new FileInputStream(transafile);
      data_in = new DataInputStream(file_in);

      while ( true ) {
        transa=new String();
        oneline=data_in.readLine();
	numRead++;
        if ((oneline==null)||(numRead > M))
          break;
        st=new StringTokenizer(oneline.trim());
	j=0;
        while ((st.hasMoreTokens()) && j < N) {
	  j++;
	  str0=st.nextToken();
          i=Integer.valueOf(str0).intValue();
          if (i!=0) {
            transa=transa.concat(" ");
            transa=transa.concat(Integer.toString(j));
            len++;
          }
        } 
        transa=transa.trim();
        //transa=oneline.trim();
        //System.out.println(transa);
        transatrahash(0,htn,transa);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }


//-------------------------------------------------------------
//  Method Name: transatrahash
//  Purpose    : called by transatraverse
//             : recursively traverse hash tree
//  Parameter  : htnf is a hashtreenode (when other method call this method,it is the root)
//             : cand : candidate itemset string
//             : int i : recursive depth,from i-th item, recursive
//  Return     : 
//-------------------------------------------------------------
  public void transatrahash(int i,hashtreenode htnf,String transa) {

    String stris=new String();
    Vector itemsetlist=new Vector();
    int j,lastpos,len,d;
    itemsetnode tmpnode=new itemsetnode();

    if (htnf.nodeattr==IL) {
      itemsetlist=(Vector)htnf.itemsetlist;
      len=itemsetlist.size();
      for (j=0;j<len;j++) {
	tmpnode=(itemsetnode)itemsetlist.elementAt(j);
	d=getitemat(htnf.depth,tmpnode.itemset);
	String v =Integer.toString(d) ;
        lastpos=transa.indexOf(" "+v+" ");
        if (lastpos!=-1) 
          ((itemsetnode)(itemsetlist.elementAt(j))).counter++;
      }
      //return;
    }
    else  //HT
      for (int b=i+1;b<=itemsetsize(transa);b++) 
        if (htnf.ht.containsKey(Integer.toString(getitemat(b,transa)))) 
          transatrahash(i,(hashtreenode)htnf.ht.get(Integer.toString(getitemat(b,transa))),transa);

  } // public transatrahash(int ii,hashtreenode htnf,String transa)


//-------------------------------------------------------------
//  Method Name: aprioriProcess()
//  Purpose    : main processing method
//  Parameters :
//  Return     : 
//-------------------------------------------------------------
  public aprioriProcess()  throws IOException {

    candidateelement cande;
    int k=0;
    Vector large=new Vector();
    Date d=new Date();
    long s1,s2;

    System.out.println();
    System.out.println("Algorithm apriori starting now.....");
    System.out.println();

    getconfig();

    fullitemset=new String();
    
    fullitemset=fullitemset.concat("1");
    for (int i=2;i<=N;i++) {
      fullitemset=fullitemset.concat(" ");
      fullitemset=fullitemset.concat(Integer.toString(i));
                           }
    
    d=new Date();
    s1=d.getTime();
    
     
    while (true) {
      k++;
      cande=new candidateelement();
      cande.candlist=createcandidate(k);   
         
          

//System.out.println("C"+k+"("+k+"-candidate-itemset): "+cande.candlist);

      if (cande.candlist.isEmpty())
	break;

      cande.htroot=null;
      candidate.addElement(cande);

      ((candidateelement)candidate.elementAt(k-1)).htroot=createcandidatehashtree(k);

System.out.println("\nNow reading transactions, increment counters of itemset");
      transatraverse(k);

      createlargeitemset(k);
      System.out.println("\nFrequent "+k+"-itemsets:");         
      System.out.println((Vector)(largeitemset.elementAt(k-1)));
              
      String itemfrequent =String.valueOf(largeitemset.elementAt(k-1));
      
      itemfrequent=itemfrequent.replace("[",",");
      itemfrequent=itemfrequent.replace("]",",");
      StringTokenizer sttt=new StringTokenizer(itemfrequent,",");
              
      racine = new Element("Apriori");
      document = new Document(racine);  
       
      Element Frequent = new Element("Frequent");
      racine.addContent(Frequent);
      String h= String.valueOf(k);
      Attribute Niveau = new Attribute("Niveau",h);
      Frequent.setAttribute(Niveau);
      
      int o=1;
      while (sttt.hasMoreTokens()) {
      	
      	String hh = String.valueOf(o);
      	Element itemfreqent = new Element("itemfreqent");
      	Frequent.addContent(itemfreqent);
      	
      	Attribute Num = new Attribute("Num",hh);
      	Attribute support = new Attribute("Support",String.valueOf(Support.elementAt(o-1)));
      	
      	itemfreqent.setAttribute(Num);
      	itemfreqent.setAttribute(support);
     	String fer =sttt.nextToken().toString();
     	
      	itemfreqent.setText(fer);
      	         
         o++;
                                  } 
        enregistre("Frequents du Niveau "+k+".xml"); 
        //affiche(); 
        Support = new Vector() ; 
     }
   
    hashtreenode htn=new hashtreenode();
    htn=((candidateelement)candidate.elementAt(k-2)).htroot;

    d=new Date();
    s2=d.getTime();
    System.out.println();
     
    System.out.println("Execution time is: "+((s2-s1)/1000) + " seconds.");
    
    System.out.println("End.");

//affiche();
//enregistre("Itemfrequent.xml");
  
  }
//==============================================================================   
//
//					Afficher le contenue du fichier XML
//
//==============================================================================   
  static void affiche()
{
   try
   {
      //On utilise ici un affichage classique avec getPrettyFormat()
      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
      sortie.output(document, System.out);
   }
   catch (java.io.IOException e){}
}

//==============================================================================   
//
//					Enregistrer dans le fichier XML
//
//==============================================================================   

  static void enregistre(String fichier)
{
   try
   {
      //On utilise ici un affichage classique avec getPrettyFormat()
      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
      //Remarquez qu'il suffit simplement de crer une instance de FileOutputStream
      //avec en argument le nom du fichier pour effectuer la srialisation.
      sortie.output(document, new FileOutputStream(fichier));
   }
   catch (java.io.IOException e){}
}

//==============================================================================
//
//
//     Apriori Parametrer
//
//
//==============================================================================
  


}
