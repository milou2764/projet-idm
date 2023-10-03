import java.util.ArrayList;

public class Input extends StorageMibField {
	String ownedInputsId;
	String ownedInputsName = ""; 
	
	
	Input(String field_ownedInputsId, String field_ownedInputsName){
		ownedInputsId = field_ownedInputsId;
		ownedInputsName = field_ownedInputsName;
	}
			
	public String GetId() {
		return  ownedInputsId;
	}
}
