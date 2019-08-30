package org.apromore.plugin.portal.generic;

public abstract class PluginOutputResult extends PluginParams {
    public static final int SUCCESS_CODE = 0;
    protected String resultMessage="";
    protected int resultCode = 0;
    
    public PluginOutputResult(Object...objects) {
        super(objects);
    }    
    
    public PluginOutputResult(int resultCode, String errorMessage) {
        super();
        this.resultMessage = errorMessage;
        this.resultCode = resultCode;
    }
    
    public String getMessage() {
        return this.resultMessage;
    }
    public int getResultCode() {
        return this.resultCode;
    }
    public boolean isSuccess() {
        return (this.resultCode == SUCCESS_CODE);
    }
}
