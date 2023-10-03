class FunctionalChainInvolvment extends StorageMibField {
    String summary, involved;
	Object o_involved;
    

    public FunctionalChainInvolvment(String id,String summary, String involved){
        this.identifier = id;
        this.summary = summary;
        this.involved = involved;
    }
}

