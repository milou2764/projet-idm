public class Output extends StorageMibField{
	String ownedOutputsId;
	String ownedOutputsName = ""; 
	
	
	Output(String field_ownedOutputsId, String field_ownedOutputsName){
		ownedOutputsId = field_ownedOutputsId;
		ownedOutputsName = field_ownedOutputsName;
	}
			
	public String GetId() {
		return  ownedOutputsId;
	}
}
