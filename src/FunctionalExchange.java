class FunctionalExchange extends StorageMibField{
    String name;
    String s_source, s_target;
	FunctionalChainInvolvment source;
	FunctionalChainInvolvment target;
	Output output;
	Input input;
     
    public FunctionalExchange(String id, String name, String source, String target){
        this.identifier = id;
        this.name = name;
        this.s_source = source;
        this.s_target = target;
    }

   
    public String getSource(){
        return this.s_source;
    }

    public String getTarget(){
        return this.s_target;
    }

    public void setSource(FunctionalChainInvolvment source){
        this.source = source;
    }

    public void setTarget(FunctionalChainInvolvment target){
        this.source = target;
    }
}
