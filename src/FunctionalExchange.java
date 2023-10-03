class FunctionalExchange extends StorageMibField{
    String id, name;
    String s_source, s_target;
	FunctionalChainInvolvment source;
	FunctionalChainInvolvment target;
	Output output;
	Input input;
     
    public FunctionalExchange(String id, String name, String source, String target){
        this.id = id;
        this.name = name;
        this.s_source = source;
        this.s_target = target;
    }

   
    public String getSource(){
        return this.source;
    }

    public String getName(){
        return this.name;
    }

    public void setSource(FunctionalChainInvolvment source){
        this.source = source;
    }

    public void setTarget(FunctionalChainInvolvment target){
        this.source = target;
    }


    public String GetId(){
        return this.id;
    }

}
