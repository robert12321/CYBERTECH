package pl.dwiekieta.swa.CSVPackage;

import android.content.Context;

import pl.dwiekieta.swa.R;

public class CSVNode {
    volatile private static int MainID = 0;

    private String ID;
    private int ClassID;

    private CSVFile DataFile;
    private CSVNode NextNode;
    private CSVNode PreviousNode;

    private String Suffix;

    private boolean isInErrorState;
    private boolean isReadyToCapturing;
    private boolean isCapturing;

    /*
    ******** CONSTRUCTORS **********
     */
    private void init(){
        isReadyToCapturing = false;
        isCapturing = false;

        ClassID = ++MainID;
    }

    public CSVNode(Context context){
        init();

        DataFile = new CSVFile();
        NextNode = null;
        PreviousNode = null;

        ID = context.getString(R.string.CSVNode_Default) + String.valueOf(ClassID);
        Suffix = ID;

        getFileErrorState();
    }

    public CSVNode(String id, CSVNode previousNode){
        init();

        DataFile = new CSVFile();
        NextNode = null;
        PreviousNode = previousNode;

        ID = id;
        Suffix = ID;

        getFileErrorState();
    }

    public CSVNode(String id, String directory, CSVNode previousNode){
        init();

        DataFile = new CSVFile(directory);
        NextNode = null;
        PreviousNode = previousNode;

        ID = id;
        Suffix = ID;

        getFileErrorState();
    }

    public CSVNode(String id, String directory, String suffix, CSVNode previousNode){
        init();

        DataFile = new CSVFile(directory);
        NextNode = null;
        PreviousNode = previousNode;

        ID = id;
        Suffix = suffix;

        getFileErrorState();
    }

    /*
    ***** FILE OPERATION ******************
     */
    public final boolean setDirectory(String directory){
        if(DataFile == null)
            return false;

        Boolean status = DataFile.setFileDirectory(directory);

        getFileErrorState();
        return status;
    }

    public final boolean prepareToCapturing(String prefix){
        if(DataFile == null)
            return false;

        isReadyToCapturing = DataFile.setFile(prefix + "_" + Suffix + ".csv");

        getFileErrorState();
        return isReadyToCapturing;
    }

    public final boolean startCapturing(){
        if(!isReadyToCapturing)
            return false;

        isCapturing = DataFile.startCapturing();

        getFileErrorState();
        return isCapturing;
    }

    public final boolean stopCapturing(){
        if(!isCapturing)
            return false;

        isCapturing = false;
        Boolean status = DataFile.stopCapturing();

        getFileErrorState();
        return status;
    }

    public final boolean writeLine(String dataLine){
        if(!isCapturing)
            return false;

        return DataFile.writeLine(dataLine);
    }

    /*
    ****** ERRORS ***********
     */
    public final boolean getFileErrorState(){
        if(DataFile == null)
            return true;

        isInErrorState = DataFile.checkErrors();
        return isInErrorState;
    }

    public final String getErrorMessage(Context context){
        if(DataFile == null)
            return context.getString(R.string.CSVNode_errorMessage_FileIsNull);
        else
            return DataFile.getErrors(context,null);
    }

    /*
    ******* STATUS ***********
     */
    public final String getStatus(Context context, String tabs){
        String tab, nextTab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        nextTab = (tab + "\t");

        StringBuffer status = new StringBuffer();
        status.append(tab + context.getString(R.string.CSVNode_status_Status) + '\n');

        if(DataFile == null) {
            status.append(nextTab + context.getString(R.string.CSVNode_errorMessage_FileIsNull) + '\n');
            return status.toString();
        }

        status.append(nextTab + context.getString(R.string.CSVNode_status_ClassID) + ": " + ClassID + '\n');
        status.append(nextTab + context.getString(R.string.CSVNode_status_id) + ": " + ID + '\n');
        status.append(nextTab + context.getString(R.string.CSVNode_status_Suffix) + ": " + Suffix + '\n');
        status.append(nextTab + DataFile.getStatus(context,nextTab));

        return status.toString();
    }

    /*
    ***** SETS *******
     */
    public final boolean setNextNode(CSVNode nextNode){
        if(NextNode == null){
            NextNode = nextNode;
            return  false;
        }

        NextNode = nextNode;
        return true;
    }

    public final boolean setPreviousNode(CSVNode previousNose){
        if((PreviousNode == null) || (previousNose.getNextNode() == this)){
            PreviousNode = previousNose;
            return false;
        }

        PreviousNode = previousNose;
        return true;
    }

    public final boolean setSuffix(String suffix){
        if(Suffix.equals(ID)){
            Suffix = suffix;
            return  false;
        }

        Suffix = suffix;
        return true;
    }

    /*
    ***** GETS *************
     */
    public final CSVFile getDataFile(){
        return DataFile;
    }

    public final CSVNode getNextNode(){
        return NextNode;
    }

    public final CSVNode getPreviousNode(){
        return PreviousNode;
    }

    public final int getMainID(){
        return MainID;
    }

    public final String getID(){
        return ID;
    }

    public final String getSuffix(){
        return Suffix;
    }

    public final boolean getErrorStatus(){
        return isInErrorState;
    }
}
