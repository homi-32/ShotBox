package sk.homisolutions.shotbox.tools.models;

/**
 * Created by homi on 10/8/16.
 */
public class ShotBoxMessage {
    private ShotBoxModulesEnum senderType;
    private String senderName;
    private Object senderReference;
    private ShotBoxModulesEnum receiverType;
    private String receiverName;
    private Object receiverReferences;
    private String messageTitle;
    private String contentMetadata;
    private Object content;

    private boolean shutdown;

    private ShotBoxMessage(){
        senderType = ShotBoxModulesEnum.ALL_OR_NOT_SPECIFIED;
        senderName = "";
        senderReference = null;
        receiverType = ShotBoxModulesEnum.ALL_OR_NOT_SPECIFIED;
        receiverName = "";
        receiverReferences = null;
        messageTitle = "";
        contentMetadata = "";
        content = null;
        shutdown = false;
    }

    public static ShotBoxMessage newMessage(){
        ShotBoxMessage message = new ShotBoxMessage();
        return message;
    }

    public ShotBoxModulesEnum getSenderType() {
        return senderType;
    }

    public void setSenderType(ShotBoxModulesEnum senderType) {
        this.senderType = senderType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Object getSenderReference() {
        return senderReference;
    }

    public void setSenderReference(Object senderReference) {
        this.senderReference = senderReference;
    }

    public ShotBoxModulesEnum getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(ShotBoxModulesEnum receiverType) {
        this.receiverType = receiverType;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Object getReceiverReferences() {
        return receiverReferences;
    }

    public void setReceiverReferences(Object receiverReferences) {
        this.receiverReferences = receiverReferences;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getContentMetadata() {
        return contentMetadata;
    }

    public void setContentMetadata(String contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
