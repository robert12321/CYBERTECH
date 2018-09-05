package pl.dwiekieta.swa.CSVPackage;

import android.content.Context;
import android.widget.Toast;

import pl.dwiekieta.swa.R;


public class CSVModule {
    private CSVNode HeadNode;
    private CSVReferents Refs;

    private String Directory;

    private boolean isDirectorySet;
    private boolean isPreparedToCapturing;
    private boolean isCapturing;

    private boolean isInErrorState;

    private void referentsUpdate(){
        Refs.clear();

        if(HeadNode == null)
            return;

        CSVNode temp = HeadNode;
        while(temp != null){
            Refs.add(temp,temp.getID());

            temp = temp.getNextNode();
        }
    }
    /*
    ***** CONSTRUCTORS *****
     */
    private void init(){
        isDirectorySet = false;
        isPreparedToCapturing = false;
        isCapturing = false;
        isInErrorState = false;
    }

    public CSVModule(){
        init();

        HeadNode = null;
        Refs = new CSVReferents();
    }

    /*
    ***** METHODS **********
     */
    public final boolean addNode(String id){
        if(HeadNode == null){
            if(isDirectorySet)
                HeadNode = new CSVNode(id, Directory, null);
            else
                HeadNode = new CSVNode(id, null);

            referentsUpdate();
            return true;
        }

        if(isCapturing)
            return false;

        if(!Refs.isIdFree(id))
            return false;

        try {
            if (isDirectorySet)
                Refs.getLastNode().setNextNode(new CSVNode(id, Directory, null));
            else
                Refs.getLastNode().setNextNode(new CSVNode(id, null));
        } catch (NullPointerException e) {
            return false;
        }

        Refs.add(Refs.getLastNode().getNextNode(),id);
        return true;
    }

    public final boolean subNode(String id){
        if(HeadNode == null)
            return false;

        if(isCapturing)
            return false;

        CSVNode toSubtract = Refs.findNodeById(id);
        if(toSubtract == null)
            return false;

        if(toSubtract.getPreviousNode() != null)
            toSubtract.getPreviousNode().setNextNode(toSubtract.getNextNode());
        else
            HeadNode = toSubtract.getNextNode();

        if(toSubtract.getNextNode() != null)
            toSubtract.getNextNode().setPreviousNode(toSubtract.getPreviousNode());

        referentsUpdate();
        return true;
    }

    public final boolean setDirectory(String directory){
        if(HeadNode == null)
            return false;

        if(isCapturing)
            return false;

        boolean status = true;
        for(int i = 0; i < Refs.getNumberOfReferences(); ++i)
            status &= Refs.findNodeByIndex(i).getDataFile().setFileDirectory(directory);


        isDirectorySet = status;
        if(status)
            Directory = directory;
        return status;
    }

    public final boolean prepareCapturing(String prefix){
        if(HeadNode == null)
            return false;

        if(isCapturing)
            return false;

        boolean status = true;
        for(int i = 0; i < Refs.getNumberOfReferences(); ++i)
            status &= Refs.findNodeByIndex(i).prepareToCapturing(prefix);

        isPreparedToCapturing = status;
        return status;
    }

    public final boolean startCapturing(){
        if(!isPreparedToCapturing)
            return false;

        if(isCapturing)
            return false;

        boolean status = true;
        for(int i = 0; i < Refs.getNumberOfReferences(); ++i)
            status &= Refs.findNodeByIndex(i).startCapturing();

        isCapturing = status;
        return status;
    }

    public final boolean stopCapturing(){
        if(!isCapturing)
            return false;

        boolean status = true;
        for(int i = 0; i < Refs.getNumberOfReferences(); ++i)
            status &= Refs.findNodeByIndex(i).stopCapturing();

        isCapturing = false;
        isPreparedToCapturing = false;
        return status;
    }

    public final boolean writeLine(String id, String dataLine){
        if(HeadNode == null)
            return false;

        if(Refs.isIdFree(id))
            return false;

        if(!isCapturing)
            return false;

        return Refs.findNodeById(id).writeLine(dataLine);
    }

    /*
    **** GETS ***********
     */
    public final String getStatus(Context context, String tabs){
        String tab, nextTab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        nextTab = (tab + "\t");

        StringBuffer status = new StringBuffer("");

        if(HeadNode == null) {
            status.append(tab + context.getString(R.string.CSVModule_status_isEmpty) + '\n');
            return status.toString();
        }

        for(int i = 0; i < Refs.getNumberOfReferences(); ++i)
            status.append(Refs.findNodeByIndex(i).getStatus(context, nextTab) + '\n');

        return status.toString();
    }

    public final String getDirectory(){
        return Directory;
    }
}
