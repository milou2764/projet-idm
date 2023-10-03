//package fileMngt;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;


import java.nio.file.*;


public class main {

    /** Main method whose behavior is:
     * - Parse of the original xml file thanks to the DOM library.
     * Generates the files requested: methods GenerateXCFiles and GenerateROCFiles
     * 
     * Note: This method calls provides in stderr the following errors information:
     * 
     * Avertissement : la validation a été activée mais aucun élément org.xml.sax.ErrorHandler n'a été défini, ce qui devrait probablement être le cas. L'analyseur utilisera un gestionnaire d'erreurs par défaut pour imprimer les 0 premières erreurs. Appelez la méthode 'setErrorHandler' pour résoudre ce problème.
     * Error: URI=file:/C:/Users/Public/Metamodel_Capella/Workspace/Parser/Capella%20Light%20Metamodel.capella Line=20: L'élément racine de document "org.polarsys.capella.core.data.capellamodeller:Project" doit correspondre à la racine DOCTYPE "null".
     * Error: URI=file:/C:/Users/Public/Metamodel_Capella/Workspace/Parser/Capella%20Light%20Metamodel.capella Line=20: Le document nest pas valide : aucune grammaire détectée.
     * This is due to the fact that coding has been done without predefined grammar rules, coded by hands in the file.
     * This might be done as future work.
     * 
     * Method to be used in case of generation of command-line application.
     * 
     * @param args:
     */
    //  static OwnedPhysicalFunctions physicalFunction = null;

    public static void main(String[] args) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


        try {
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // input xml file */
            File fileXML = new File("TP_modelling.capella");
            //File fileXML = new File("Cours_ENSIBS.capella");

            /* DOM library element used to store elements of xml files (only syntactic) */
            Document xml;
            try {   
                // DOM method to parse the xml File */
                xml = builder.parse(fileXML);
                /* root DOM structure storing the XML files elements */
                Element root = xml.getDocumentElement();       

                // HashTable to store all the parsed elements
                //key Id : identifier
                //Element: storage
                Hashtable<String, StorageMibField> mibFieldTable = new Hashtable<String,StorageMibField>();

                // Models Files generation*/ 
                GenerateModelExtraction(root, "", mibFieldTable);
                //           getTheFunctionalChains(mibFieldTable);

            } catch (SAXParseException e) { }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

    }


    /**
     *   
     * @param root
     * @param genDirectory
     * @param table
     */
    public static void   GenerateModelExtraction(Element root, String genDirectory, Hashtable<String, StorageMibField> table)
    {
        ArrayList<StorageMibField> storageMibFieldsList = new ArrayList<StorageMibField>(0) ;
        /*added*/
        /* used to keep the link between classes and properties related*/
        /* to build back the mib path of an OID */
        Hashtable<String, StorageMibField> mibFieldTable = new Hashtable<String,StorageMibField>();
        Hashtable<String, ArrayList<String>> oidToOpTable = new Hashtable<String,ArrayList<String>>();
        ParseAndStore(root, "Logical Architecture", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
        /*       ParseAndStore(root, "oa", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
                 ParseAndStore(root, "sa", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
                 ParseAndStore(root, "la", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
                 ParseAndStore(root, "pa", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
                 ParseAndStore(root, "datavalue", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
                 ParseAndStore(root, "classes", storageMibFieldsList, mibFieldTable, oidToOpTable, table);
                 */
        System.out.println("end of parsing");
    }  
    /**
     * Method which Parse and Store in the storageMibFieldsList of the xml file part related to the package pkgName
     * @param root
     * @param pkgName
     * @param storageMibFieldsList
     */
    public static void ParseAndStore(Node root, String pkgName, ArrayList<StorageMibField> storageMibFieldsList, Hashtable<String,StorageMibField> operationTable, Hashtable oidToOpTable, Hashtable<String, StorageMibField> table) {   
        NodeList list = getNode(root, pkgName, storageMibFieldsList, operationTable,oidToOpTable, pkgName, table);
        if(list != null){
            for(int i = 0; i < list.getLength(); i++){
                Node n2 = list.item(i);
                //si le nœud enfant est un Element, nous le traitons
                if (n2 instanceof Element){
                    //appel récursif à la méthode pour le traitement du nœud et de ses enfants 
                    n2.getNodeValue();
                    description(n2, "",  storageMibFieldsList, operationTable, oidToOpTable, pkgName, table);
                }
            }
        }          
    }

    /**
     * Méthode qui va parser le contenu d'un nœud. 
     * Adapté d'un exemple d'utilisation de la librairie DOM.
     * L'appel à ExtractFields sert à la singularisation des balises et récupération 
     * A faire: voir comment extraire l'appel à ExtractFieldshors de la méthode.
     * Used in ParseAndStore
     * @param n
     * @param tab
     * @return
     */

    public static String description(Node n, String tab, ArrayList storageMibFieldsList, Hashtable<String,StorageMibField> operationTable, Hashtable oidToOpTable, String pkgName, Hashtable<String, StorageMibField> table){
        String str = new String();
        //Nous nous assurons que le nœud passé en paramètre est une instance d'Element
        //juste au cas où il s'agisse d'un texte ou d'un espace, etc.
        if(n instanceof Element){

            //Nous sommes donc bien sur un élément de notre document
            //Nous castons l'objet de type Node en type Element
            Element element = (Element)n;

            //Nous pouvons récupérer le nom du nœud actuellement parcouru 
            //grâce à cette méthode, nous ouvrons donc notre balise
            str += "<" + n.getNodeName();
            //System.out.println(str);

            //nous contrôlons la liste des attributs présents
            if(n.getAttributes() != null && n.getAttributes().getLength() > 0){           

                //nous pouvons récupérer la liste des attributs d'un élément
                NamedNodeMap att = n.getAttributes();
                int nbAtt = att.getLength();            

                //nous parcourons tous les nœuds pour les afficher
                for(int j = 0; j < nbAtt; j++){
                    Node noeud = att.item(j);
                    //On récupère le nom de l'attribut et sa valeur grâce à ces deux méthodes
                    str += " " + noeud.getNodeName() + "=\"" + noeud.getNodeValue() + "\" ";
                    //Extraction des champs
                }
            }

            //nous refermons notre balise car nous avons traité les différents attributs
            str += ">";        
            // ExtractFields(str, storageMibFieldsList);

            //La méthode getChildNodes retournant le contenu du nœud + les nœuds enfants

            //Nous récupérons le contenu texte uniquement lorsqu'il n'y a que du texte, donc un seul enfant

            if(n.getChildNodes().getLength() == 1)
                str += n.getTextContent();

            //Nous allons maintenant traiter les nœuds enfants du nœud en cours de traitement
            int nbChild = n.getChildNodes().getLength();

            //Nous récupérons la liste des nœuds enfants
            NodeList list = n.getChildNodes();

            String tab2 = tab + "\t";        

            //nous parcourons la liste des nœuds

            //reorder modification
            for(int i = 0; i < nbChild; i++){
                //       for(int i = nbChild -1; i == 0 ; i--){
                Node n2 = list.item(i);

                //si le nœud enfant est un Element, nous le traitons
                if (n2 instanceof Element){
                    //appel récursif à la méthode pour le traitement du nœud et de ses enfants 
                    str += "\n " + tab2 + description(n2,  tab2, storageMibFieldsList, operationTable, oidToOpTable, pkgName, table);
                }
                }

            //Nous fermons maintenant la balise

            if(n.getChildNodes().getLength() < 2){
                str += "</" + n.getNodeName() + ">";
                //             System.out.println(str);
            }
            else
            {
                str += "\n" + tab +"</" + n.getNodeName() + ">";
                //            System.out.println(str);
            }
            }

            ExtractFields(str, pkgName, storageMibFieldsList, operationTable, oidToOpTable, table);
            return str;

        }   

        /** 
         * Méthode qui remonte sous la forme d'une liste de noeud l'arbre syntaxique xml à partir du noeud n 
         * dont la racine a comme valeur strId. 
         * storageMibFieldsList used only in a call to the "description" method.
         * @param n
         * @param tab
         * @param storageMibFieldsList
         * @return
         */

        public static NodeList getNode(Node n, String strId, ArrayList storageMibFieldsList, Hashtable operationTable,  Hashtable oidToOpTable, String pkgName, Hashtable<String, StorageMibField> table){
            NodeList n_return = null;
            Boolean reached = false;

            //Nous nous assurons que le nœud passé en paramètre est une instance d'Element
            //juste au cas où il s'agisse d'un texte ou d'un espace, etc.
            if(n instanceof Element){

                //Nous sommes donc bien sur un élément de notre document
                //Nous castons l'objet de type Node en type Element
                Element element = (Element)n;

                //nous contrôlons la liste des attributs présents
                if(n.getAttributes() != null && n.getAttributes().getLength() > 0){           

                    //nous pouvons récupérer la liste des attributs d'un élément
                    NamedNodeMap att = n.getAttributes();
                    int nbAtt = att.getLength();            

                    //nous parcourons tous les nœuds pour les afficher
                    for(int j = 0; j < nbAtt; j++){
                        Node noeud = att.item(j);
                        //On récupère le nom de l'attribut et sa valeur grâce à ces deux méthodes
                        if(noeud.getNodeValue().equals(strId)) {
                            n_return = n.getChildNodes();
                            reached = true;
                            //            	   System.out.println("nbAtt=" + nbAtt);
                        }
                        //               System.out.println(noeud.getNodeValue());
                    }
                }

                //Nous allons maintenant traiter les nœuds enfants du nœud en cours de traitement
                if (reached == false){        	 
                    int nbChild = n.getChildNodes().getLength();

                    //Nous récupérons la liste des nœuds enfants

                    NodeList list = n.getChildNodes();         
                    //nous parcourons la liste des nœuds
                    //        for(int i = 0; i < nbChild; i++){
                    //reorder
                    for(int i = nbChild - 1; i > 0; i--){
                        Node n2 = list.item(i);            
                        //si le nœud enfant est un Element, nous le traitons

                        if (n2 instanceof Element){
                            //appel récursif à la méthode pour le traitement du nœud et de ses enfants 
                            n_return =  getNode(n2, strId, storageMibFieldsList, operationTable, oidToOpTable, pkgName, table);
                        }
                    }      
                    }
                }

                if(reached == true){
                    String str ="";
                    String tab = "";
                    int nbChild = n.getChildNodes().getLength();

                    //Nous récupérons la liste des nœuds enfants

                    NodeList list = n.getChildNodes();
                    String tab2 = tab + "\t";

                    //nous parcourons la liste des nœuds

                    for(int i = 0; i < nbChild; i++){
                        Node n2 = list.item(i);             

                        //si le nœud enfant est un Element, nous le traitons
                        if (n2 instanceof Element){
                            //appel récursif à la méthode pour le traitement du nœud et de ses enfants 
                            str += "\n " + tab2 + description(n2, tab2, storageMibFieldsList, operationTable,  oidToOpTable, pkgName, table);
                        }
                    }          
                }
                return n_return;
            } 

            /**
             * Method which, for a particular xml code related to a beacon parse this string (str) to store it as a subclass instance of StorageMibField.
             * This instance may have as attibutes previous parsed xml parts stored in the StorageMibField list.
             * @param str
             * @param storageMibFieldsList
             */

            public static void ExtractFields(String str, String pkgName, ArrayList<StorageMibField> storageMibFieldsList, Hashtable<String,StorageMibField> operationTable, Hashtable oidToOpTable, Hashtable<String, StorageMibField> table){
                // Get the Beacon Type
                int cnt;
                StorageMibField.BeaconType beacon = StorageMibField.BeaconType.NOBEACON;

                if(str.startsWith("<ownedAssociations") )      		beacon = StorageMibField.BeaconType.OWNEDASSOCIATION;
                if(str.startsWith("<ownedMembers") )           		beacon = StorageMibField.BeaconType.OWNEDMEMBER;
                if(str.startsWith("<ownedMinCard") )           		beacon = StorageMibField.BeaconType.OWNEDCARD;
                if(str.startsWith("<ownedMaxCard") )           		beacon = StorageMibField.BeaconType.OWNEDCARD;
                if(str.startsWith("<ownedClasses") )           		beacon = StorageMibField.BeaconType.OWNEDCLASS;
                if(str.startsWith("<ownedExtensions") )        		beacon = StorageMibField.BeaconType.OWNEDEXTENSION;
                if(str.startsWith("<containedGenericTraces") ) 		beacon = StorageMibField.BeaconType.CONTAINEDGENERICTRACE;
                if(str.startsWith("<containedProperties") )    		beacon = StorageMibField.BeaconType.CONTAINEDPROPERTY;
                if(str.startsWith("<ownedDataTypes") )         		beacon = StorageMibField.BeaconType.OWNEDDATATYPE;
                if(str.startsWith("<ownedPropertyValues") )    		beacon = StorageMibField.BeaconType.OWNEDPROPERTYVALUE;
                if(str.startsWith("<domainValue") )    		  		beacon = StorageMibField.BeaconType.DOMAINVALUE;
                if(str.startsWith("<ownedLiterals") )    	  		beacon = StorageMibField.BeaconType.OWNEDLITERAL;
                if(str.startsWith("<ownedVariabilityFeatures") )    	beacon = StorageMibField.BeaconType.VARIABILITYFEATURES;
                //Change of name from ownedVariabilityFeatures to ownedFilteringCriteria in case of orchestra 5.8.1
                if(str.startsWith("<ownedFilteringCriteria") )    	beacon = StorageMibField.BeaconType.VARIABILITYFEATURES;
                if(str.startsWith("<ownedExchangeItem") )			beacon = StorageMibField.BeaconType.OWNEDEXCHANGEITEM;
                if(str.startsWith("<ownedElements") )				beacon = StorageMibField.BeaconType.OWNEDELEMENT;
                if(str.startsWith("<ownedInterfaces") )				beacon = StorageMibField.BeaconType.OWNEDINTERFACES;
                if(str.startsWith("<ownedExchangeItemAllocations") )	beacon = StorageMibField.BeaconType.OWNEDEXCHANGEITEMALLOCATIONS;
                if(str.startsWith("<ownedInterfacePkgs") )			beacon = StorageMibField.BeaconType.OWNEDINTERFACEPKGS;
                if(str.startsWith("<ownedMessages") )			    beacon = StorageMibField.BeaconType.OWNEDMESSAGES;

                //added in the switch to comply with .melodymodeler files beacons
                if(str.startsWith("<ownedGeneralizations") )          beacon = StorageMibField.BeaconType.OWNEDGENERALIZATIONS;
                if(str.startsWith("<ownedFeatures") )          		beacon = StorageMibField.BeaconType.OWNEDFEATURES;
                if(str.startsWith("<ownedTraces") )          		beacon = StorageMibField.BeaconType.OWNEDTRACES;
                if(str.startsWith("<ownedInformationRealizations") ) beacon = StorageMibField.BeaconType.OWNEDINFORMATIONREALIZATIONS;
                if(str.startsWith("<ownedDefaultValue") )  			beacon = StorageMibField.BeaconType.OWNEDDEFAULTVALUE;
                if(str.startsWith("<ownedUnits") )  					beacon = StorageMibField.BeaconType.OWNEDUNITS;
                if(str.startsWith("<ownedMinValue") )  				beacon = StorageMibField.BeaconType.OWNEDMINVALUE;
                if(str.startsWith("<ownedMaxValue") )  				beacon = StorageMibField.BeaconType.OWNEDMAXVALUE;
                if(str.startsWith("<ownedMinLength") )  				beacon = StorageMibField.BeaconType.OWNEDMINLENGTH;
                if(str.startsWith("<ownedMaxLength") )  				beacon = StorageMibField.BeaconType.OWNEDMAXLENGTH;
                if(str.startsWith("<ownedInstanceRoles") )  			beacon = StorageMibField.BeaconType.OWNEDINSTANCEROLES;
                if(str.startsWith("<ownedInteractionFragments") )  	beacon = StorageMibField.BeaconType.OWNEDINTERACTIONFRAGMENTS;
                if(str.startsWith("<ownedTimeLapses") )  			beacon = StorageMibField.BeaconType.OWNEDTIMELAPSES;
                if(str.startsWith("<ownedEvents") )  				beacon = StorageMibField.BeaconType.OWNEDEVENTS;

                if(str.startsWith("<ownedScenarios") )  				beacon = StorageMibField.BeaconType.OWNEDSCENARIOS;
                if(str.startsWith("<ownedSystemCapabilityInvolvement") ) beacon = StorageMibField.BeaconType.OWNEDSYSTEMCAPABILITYINVOLVMENT;
                if(str.startsWith("<ownedSpecification") )  				beacon = StorageMibField.BeaconType.OWNEDSPECIFICATION;
                if(str.startsWith("<ownedConstraints") )  				beacon = StorageMibField.BeaconType.OWNEDCONSTRAINTS;
                if(str.startsWith("<ownedActorCapabilityInvolvements") )  				beacon = StorageMibField.BeaconType.OWNEDACTORCAPABILITYINVOLVMENTS;

                if(str.startsWith("<ownedCapabilities") )  				beacon = StorageMibField.BeaconType.OWNEDCAPABILITIES;
                if(str.startsWith("<ownedCapabilityPkgs") )				beacon = StorageMibField.BeaconType.OWNEDCAPABILITYPKG;

                //added from physical part parsing
                if(str.startsWith("<ownedFunctionalChainInvolvements"))  beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALCHAININVOLVMENTS;
                if(str.startsWith("<ownedFunctionalChainRealizations"))  beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALCHAINREALIZATIONS;
                if(str.startsWith("<ownedPhysicalComponents"))  			beacon = StorageMibField.BeaconType.OWNEDPHYSICALCOMPONENTS;
                if(str.startsWith("<ownedPhysicalComponentPkg"))  		beacon = StorageMibField.BeaconType.OWNEDPHYSICALCOMPONENTPKG;
                if(str.startsWith("<ownedLogicalArchitectureRealizations"))  beacon = StorageMibField.BeaconType.OWNEDLOGICALARCHITECTUREREALIZATIONS;
                if(str.startsWith("<ownedFunctions"))  					beacon = StorageMibField.BeaconType.OWNEDFUNCTIONS;
                if(str.startsWith("<ownedFunctionalChains"))  			beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALCHAINS;
                if(str.startsWith("<ownedPortRealizations"))  			beacon = StorageMibField.BeaconType.OWNEDPORTREALIZATIONS;
                if(str.startsWith("<ownedFunctionRealizations"))  			beacon = StorageMibField.BeaconType.OWNEDFUNCTIONREALIZATIONS;
                if(str.startsWith("<outputs"))  			beacon = StorageMibField.BeaconType.OUTPUTS;
                if(str.startsWith("<inputs"))  			beacon = StorageMibField.BeaconType.INPUTS;
                if(str.startsWith("<ownedFunctionalExchangeRealizations"))  			beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALEXCHANGEREALIZATIONS;
                if(str.startsWith("<ownedFunctionalExchanges"))  			beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALEXCHANGES;
                if(str.startsWith("<ownedPhysicalFunctions"))  			beacon = StorageMibField.BeaconType.OWNEDPHYSICALFUNCTIONS;
                if(str.startsWith("<ownedFunctionPkg"))  			beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALPKG;
                if(str.startsWith("<ownedCapabilityRealizationInvolvements"))  			beacon = StorageMibField.BeaconType.OWNEDCAPABILITYREALIZATIONINVOLVMENTS;
                if(str.startsWith("<ownedCapabilityRealizations"))  			beacon = StorageMibField.BeaconType.OWNEDCAPABILITYREALIZATIONS;
                if(str.startsWith("<ownedAbstractCapabilityPkg"))  			beacon = StorageMibField.BeaconType.OWNEDABSTRACTCAPABILITYPKG;
                if(str.startsWith("<ownedInterfacePkg"))  			beacon = StorageMibField.BeaconType.OWNEDINTERFACEPKG;
                if(str.startsWith("<ownedDataPkg"))  			beacon = StorageMibField.BeaconType.OWNEDDATAPKG;
                if(str.startsWith("<ownedParts"))  			beacon = StorageMibField.BeaconType.OWNEDPARTS;
                if(str.startsWith("<ownedPhysicalLinks"))  			beacon = StorageMibField.BeaconType.OWNEDPHYSICALLINKS;
                if(str.startsWith("<ownedComponentExchanges"))  			beacon = StorageMibField.BeaconType.OWNEDCOMPONENTEXCHANGES;
                if(str.startsWith("<ownedDeploymentLinks"))  			beacon = StorageMibField.BeaconType.OWNEDDEPLOYMENTLINKS;
                if(str.startsWith("<ownedComponentRealizations"))  			beacon = StorageMibField.BeaconType.OWNEDCOMPONENTREALIZATIONS;
                if(str.startsWith("<ownedFunctionalAllocation"))  			beacon = StorageMibField.BeaconType.OWNEDFUNCTIONALALLOCATIONS;
                if(str.startsWith("<ownedPortAllocations"))  			beacon = StorageMibField.BeaconType.OWNEDPORTALLOCATIONS;
                if(str.startsWith("<ownedComponentPortAllocations"))  			beacon = StorageMibField.BeaconType.OWNEDCOMPONENTPORTSALLOCATIONS;
                if(str.startsWith("<<ownedFeatures"))  			beacon = StorageMibField.BeaconType.OWNEDFEATURES;

                //wrt the type fill a StorageMibField object
                switch (beacon){

                    case OWNEDFUNCTIONALCHAININVOLVMENTS : System.out.println("OWNEDFUNCTIONALCHAININVOLVMENTS");
                                                           FunctionalChainInvolvment functionalChainInvolvment = ExtractFunctionalChainInvolvment(str, storageMibFieldsList, table);
                                                           //System.out.println("ID : " + functionalChainInvolvment.GetId());
                                                           break;
                    case OWNEDFUNCTIONS : System.out.println("OWNEDFUNCTIONS " + str);
                                          OwnedFunction ownedFunction = ExtractOwnedFunction ( str, storageMibFieldsList, table);
                                          //System.out.println("ID : " + ownedFunction.GetId());
                                          break;
                    case OWNEDFUNCTIONALEXCHANGES : System.out.println("OWNEDFUNCTIONALEXCHANGES " + str);
                                                    FunctionalExchange functionalExchange = ExtractFunctionalExchange(str, storageMibFieldsList, table);
                                                    //System.out.println("ID : " + functionalExchange.GetId()); 
                                                    break;
                    case OUTPUTS : System.out.println("OUTPUT" + str);
                                   Output output = ExtractOutput(str, storageMibFieldsList, table);
                                   System.out.println("ID : " +output.GetId());
                                   break;
                    case INPUTS : System.out.println("INPUTS" + str);
                                  Input input = ExtractInput(str, storageMibFieldsList, table);
                                  System.out.println("ID : " + input.GetId());
                                  break;
                    default:
                }			  
            }

private static FunctionalChainInvolvment ExtractFunctionalChainInvolvment(String str, ArrayList<StorageMibField> StorageMibFieldsList, Hashtable<String, StorageMibField> table){
    return null;
}

private static FunctionalExchange ExtractFunctionalExchange(String str, ArrayList<StorageMibField> StorageMibFieldsList, Hashtable<String, StorageMibField> table){
    return null;
}


private static Output ExtractOutput(String str, ArrayList<StorageMibField> StorageMibFieldsList, Hashtable<String, StorageMibField> table){
               
                //  Claim id
                int index_id = str.indexOf("id=") + 4;
                int index_end_id = index_id + 40;
                String id = (String) str.subSequence(index_id, index_end_id);

                //  Claim name
                int index_name = str.indexOf("name=") + 6;
                int index_end_name = str.indexOf("xsi:type") - 3;
                String name = (String) str.subSequence(index_name, index_end_name);

                Output output = new Output(id, name);
                return output;     
            }

            private static Input ExtractInput(String str, ArrayList<StorageMibField> StorageMibFieldsList, Hashtable<String, StorageMibField> table){
               
                //  Claim id
                int index_id = str.indexOf("id=") + 4;
                int index_end_id = index_id - 3;
                String id = (String) str.subSequence(index_id, index_end_id);

                //  Claim name
                int index_name = str.indexOf("name=") + 6;
                int index_end_name = str.indexOf("xsi:type") - 3;
                String name = (String) str.subSequence(index_name, index_end_name);

                Input input = new Input(id, name);
                return input;     
            }

            private static OwnedFunction ExtractOwnedFunction(String str, ArrayList<StorageMibField> storageMibFieldsList, Hashtable<String, StorageMibField> table) {
                OwnedFunction ownedFunctions = null;

                String ownedFunctionsid = "";
                String ownedFunctionsName = ""; 
                int next_id = -1;
                String id ="";

                StorageMibField field = null;

                //id, source, target
                int index_id = str.indexOf("id=");
                ownedFunctionsid = (String) str.subSequence(index_id+4, index_id+40);
                next_id = index_id +1;

                index_id = str.indexOf("name=");
                int index_id2 = str.indexOf("\"", index_id+6);
                ownedFunctionsName = (String) str.subSequence(index_id+6, index_id2);

                ownedFunctions = new OwnedFunction(ownedFunctionsid, ownedFunctionsName);

                do {
                    next_id = str.indexOf("id=", next_id+1);
                    id = (String) str.subSequence(next_id+4, next_id+40);
                    /* Check if ownedFunctionalAllocation */
                    field = table.get(id);

                    /* Check if owned Features */
                    if (field instanceof Output) {
                        ownedFunctions.AddOutputs((Output)field);
                    }
                    if (field instanceof Input) {
                        ownedFunctions.Addinputs((Input)field);
                    }
                }
                while(next_id != -1);

                //to be completed
                return ownedFunctions;
            }
        }
