package pl.dwiekieta.swa.CSVPackage;

import android.content.Context;


import pl.dwiekieta.swa.R;

public class CSVManager {
    public enum CapturingModule {SensorsModule, TimeModule}

    public enum SensorsModule {RAW, RELATIVE}
    private boolean SensorsModule_isActive;
    private boolean SensorsModule_isCapturing;
    private boolean SensorsModule_isCapturing_inRaw;
    private boolean SensorsModule_isCapturing_inRelative;
    private boolean SensorsModule_isInErrorState;

    private void initSensorsModule(){
        SensorsModule_isActive = false;
        SensorsModule_isCapturing = false;
        SensorsModule_isCapturing_inRaw = false;
        SensorsModule_isCapturing_inRelative = false;

        SensorsModule_isInErrorState = false;
    }

    public enum TimeModule {DEVICE, NETWORK}
    private boolean TimeModule_isActive;
    private boolean TimeModule_isCapturing;
    private boolean TimeModule_isCapturing_fromDevice;
    private boolean TimeModule_isCapturing_fromNetwork;
    private boolean TimeModule_isInErrorState;

    private void initTimeModule(){
        TimeModule_isActive = false;
        TimeModule_isCapturing = false;
        TimeModule_isCapturing_fromDevice = false;
        TimeModule_isCapturing_fromNetwork = false;

        TimeModule_isInErrorState = false;
    }

    /*
    **** INTERNAL METHODS *************
     */

    private CSVModule csvModule;

    private boolean isInErrorState;
    private boolean isCapturing;
    private boolean isPrepareToCapturing;
    private boolean isDirectorySet;

    private void init(){
        isCapturing = false;
        isInErrorState = false;
        isDirectorySet = false;
        isPrepareToCapturing = false;

        initSensorsModule();
        initTimeModule();
    }

    /*
    *** CONSTRUCTOR ************
     */

    public CSVManager(){
        init();

        csvModule = new CSVModule();

        getGeneralErrorStatus();
    }

    /*
     ****** Manager methods *********
     */

    public final boolean addModule(Context context, CapturingModule capturingModule){
        if(isCapturing)
            return false;

        switch (capturingModule){
            case SensorsModule:{
                if(SensorsModule_isActive)
                    return false;
                SensorsModule_isActive = true;
            } break;
            case TimeModule:{
                if(TimeModule_isActive)
                    return false;
                TimeModule_isActive = true;
            } break;
        }

        return true;
    }

    public final boolean subModule(Context context, CapturingModule capturingModule){
        if(isCapturing)
            return false;

        switch (capturingModule){
            case SensorsModule:{
                if(!SensorsModule_isActive)
                    return false;

                if(SensorsModule_isCapturing){
                    if(SensorsModule_isCapturing_inRaw)
                        csvModule.subNode(context.getString(R.string.CSVManager_suffix_SensorsModule_raw));
                    if(SensorsModule_isCapturing_inRelative)
                        csvModule.subNode(context.getString(R.string.CSVManager_suffix_SensorsModule_relative));
                }
                initSensorsModule();
            } break;
            case TimeModule:{
                if(!TimeModule_isActive)
                    return false;

                if(TimeModule_isCapturing){
                    if(TimeModule_isCapturing_fromDevice)
                        csvModule.subNode(context.getString(R.string.CSVManager_suffix_TimeModule_device));
                    if(TimeModule_isCapturing_fromNetwork)
                        csvModule.subNode(context.getString(R.string.CSVManager_suffix_TimeModule_network));
                }
                initTimeModule();
            } break;
        }

        return true;
    }

    public final boolean activateModule(Context context, CapturingModule capturingModule, int module){
        if(isCapturing)
            return false;

        switch (capturingModule){
            case SensorsModule:{
                if(!SensorsModule_isActive)
                    return false;

                SensorsModule sensorsModule = SensorsModule.values()[module];
                switch (sensorsModule){
                    case RAW:{
                        if(SensorsModule_isCapturing_inRaw)
                            return false;

                        SensorsModule_isCapturing_inRaw = csvModule.addNode(context.getString(R.string.CSVManager_suffix_SensorsModule_raw));
                        if(SensorsModule_isCapturing_inRaw)
                            SensorsModule_isCapturing = true;

                    } break;
                    case RELATIVE:{
                        if(SensorsModule_isCapturing_inRelative)
                            return false;

                        SensorsModule_isCapturing_inRelative = csvModule.addNode(context.getString(R.string.CSVManager_suffix_SensorsModule_relative));
                        if(SensorsModule_isCapturing_inRelative)
                            SensorsModule_isCapturing = true;
                    } break;
                }
                getGeneralErrorStatus();
            } break;
            case TimeModule:{
                if(!TimeModule_isActive)
                    return false;

                TimeModule timeModule = TimeModule.values()[module];
                switch (timeModule){
                    case DEVICE:{
                        if(TimeModule_isCapturing_fromDevice)
                            return false;

                        TimeModule_isCapturing_fromDevice = csvModule.addNode(context.getString(R.string.CSVManager_suffix_TimeModule_device));
                        if(TimeModule_isCapturing_fromDevice)
                            TimeModule_isCapturing = true;

                    } break;
                    case NETWORK:{
                        if(TimeModule_isCapturing_fromNetwork)
                            return false;

                        TimeModule_isCapturing_fromNetwork = csvModule.addNode(context.getString(R.string.CSVManager_suffix_TimeModule_network));
                        if(TimeModule_isCapturing_fromNetwork)
                            TimeModule_isCapturing = true;
                    } break;
                }
                getGeneralErrorStatus();
            } break;
        }

        return true;
    }

    public final boolean deactivateModule(Context context, CapturingModule capturingModule, int module){
        if(isCapturing)
            return false;

        switch (capturingModule){
            case SensorsModule:{
                if(!SensorsModule_isActive)
                    return false;

                SensorsModule sensorsModule = SensorsModule.values()[module];
                switch (sensorsModule){
                    case RAW:{
                        if(!SensorsModule_isCapturing_inRaw)
                            return false;

                        SensorsModule_isCapturing_inRaw = !csvModule.subNode(context.getString(R.string.CSVManager_suffix_SensorsModule_raw));
                        if(!SensorsModule_isCapturing_inRaw && !SensorsModule_isCapturing_inRelative)
                            SensorsModule_isCapturing = false;

                    } break;
                    case RELATIVE:{
                        if(!SensorsModule_isCapturing_inRelative)
                            return false;

                        SensorsModule_isCapturing_inRelative = !csvModule.subNode(context.getString(R.string.CSVManager_suffix_SensorsModule_relative));
                        if(!SensorsModule_isCapturing_inRaw && !SensorsModule_isCapturing_inRelative)
                            SensorsModule_isCapturing = false;
                    } break;
                }

                getGeneralErrorStatus();
            } break;
            case TimeModule:{
                if(!TimeModule_isActive)
                    return false;

                TimeModule timeModule = TimeModule.values()[module];
                switch (timeModule){
                    case DEVICE:{
                        if(!TimeModule_isCapturing_fromDevice)
                            return false;

                        TimeModule_isCapturing_fromDevice = !csvModule.subNode(context.getString(R.string.CSVManager_suffix_TimeModule_device));
                        if(!TimeModule_isCapturing_fromDevice && !TimeModule_isCapturing_fromNetwork)
                            TimeModule_isCapturing = false;

                    } break;
                    case NETWORK:{
                        if(!TimeModule_isCapturing_fromNetwork)
                            return false;

                        TimeModule_isCapturing_fromNetwork = !csvModule.subNode(context.getString(R.string.CSVManager_suffix_TimeModule_network));
                        if(!TimeModule_isCapturing_fromDevice && !TimeModule_isCapturing_fromNetwork)
                            TimeModule_isCapturing = false;
                    } break;
                }

                getGeneralErrorStatus();
            } break;
        }

        return true;
    }

    public final boolean setDirectory(String directory){
        isDirectorySet = csvModule.setDirectory(directory);

        return isDirectorySet;
    }

    public final boolean prepareToCapturing(String prefix){
        if(isCapturing)
            return false;

        if(!isDirectorySet)
            return false;

        isPrepareToCapturing = csvModule.prepareCapturing(prefix);
        return isPrepareToCapturing;
    }

    public final boolean startCapturing(){
        if(isCapturing)
            return false;

        if(!isPrepareToCapturing)
            return false;

        isCapturing = csvModule.startCapturing();
        return isCapturing;
    }

    public final boolean stopCapturing(){
        if(!isCapturing)
            return false;

        isCapturing = !csvModule.stopCapturing();
        return isCapturing;
    }

    public final boolean writeLinr(Context context, CapturingModule capturingModule, int module, String dataLine){
        if(!isCapturing)
            return false;

        String id = null;

        switch (capturingModule){
            case SensorsModule:{
                if(!SensorsModule_isActive)
                    return false;

                SensorsModule sensorsModule = SensorsModule.values()[module];
                switch (sensorsModule){
                    case RAW:{
                        if(!SensorsModule_isCapturing_inRaw)
                            return false;

                        id = context.getString(R.string.CSVManager_suffix_SensorsModule_raw);
                    } break;
                    case RELATIVE:{
                        if(!SensorsModule_isCapturing_inRelative)
                            return false;

                        id = context.getString(R.string.CSVManager_suffix_SensorsModule_relative);
                    } break;
                }
            } break;
            case TimeModule:{
                if(!TimeModule_isActive)
                    return false;

                TimeModule timeModule = TimeModule.values()[module];
                switch (timeModule){
                    case DEVICE:{
                        if(!TimeModule_isCapturing_fromDevice)
                            return false;

                        id = context.getString(R.string.CSVManager_suffix_TimeModule_device);
                    } break;
                    case NETWORK:{
                        if(!TimeModule_isCapturing_fromNetwork)
                            return false;

                        id = context.getString(R.string.CSVManager_suffix_TimeModule_network);
                    } break;
                }
            } break;
        }

        csvModule.writeLine(id,dataLine);
        return true;
    }

    /*
     ****** Errors ********
     */

    public final boolean getSensorsModuleErrorStatus(){
        if(!SensorsModule_isActive) {
            SensorsModule_isInErrorState = false;
        } else {
            if (!SensorsModule_isCapturing)
                SensorsModule_isInErrorState = true;
            else
                SensorsModule_isInErrorState = false;
        }

        return SensorsModule_isInErrorState;
    }

    public final boolean getTimeModuleErrorStatus(){
        if(!TimeModule_isActive) {
            TimeModule_isInErrorState = false;
        } else {
            if (!TimeModule_isCapturing)
                TimeModule_isInErrorState = true;
            else
                TimeModule_isInErrorState = false;
        }

        return TimeModule_isInErrorState;
    }

    public final String getSensorsModuleErrorMessage(Context context, String tabs){
        String tab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        if(SensorsModule_isActive && !SensorsModule_isCapturing)
            return tab + context.getString(R.string.CSVManager_errorMessage_SensorsModuleNotCapturing) + '\n';

        return null;
    }

    public final String getTimeModuleErrorMessage(Context context, String tabs){
        String tab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        if(TimeModule_isActive && !TimeModule_isCapturing)
            return tab + context.getString(R.string.CSVManager_errorMessage_TimeModuleNotCapturing) + '\n';

        return null;
    }

    /*
     **** GENERAL ERROR ************
     */

    public final boolean getGeneralErrorStatus(){
        Boolean status = false;

        status |= getSensorsModuleErrorStatus();
        status |= getTimeModuleErrorStatus();

        isInErrorState = status;
        return status;
    }

    public final String getGeneralErrorMessage(Context context, String tabs){
        String tab, nextTab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        nextTab = (tab + "\t");

        StringBuffer status = new StringBuffer("");
        if(getGeneralErrorStatus())
            status.append(tab + context.getString(R.string.CSVManager_errorMessage_Alias) + ":\n");
        else
            status.append(tab + context.getString(R.string.CSVManager_errorMessage_noError) + "\n");

        if(SensorsModule_isInErrorState) {
            status.append(tab + context.getString(R.string.CSVManager_errorMessage_SensorsModuleAlias) + ":\n");
            status.append(getSensorsModuleErrorMessage(context,nextTab) + '\n');
        }

        if(TimeModule_isInErrorState) {
            status.append(tab + context.getString(R.string.CSVManager_errorMessage_TimeModuleAlias) + ":\n");
            status.append(getTimeModuleErrorMessage(context,nextTab) + '\n');
        }

        return status.toString();
    }

    /*
    ******* GETS *******
     */
    public final String getStatus(Context context, String tabs){
        String tab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        StringBuffer status = new StringBuffer("");
        status.append(getGeneralErrorMessage(context,tab) + '\n');
        status.append(csvModule.getStatus(context,tab) + '\n');

        return status.toString();
    }

    public final boolean getCapturingStatus(){
        return isCapturing;
    }

    public final boolean getPrepareToCapturingStatus(){return isPrepareToCapturing;}

    public final boolean getDirectorySetStatus() {return isDirectorySet; }

    public  final String getDirectory(){return csvModule.getDirectory();}

    public final boolean getGeneralErrorFlag(){
        return isInErrorState;
    }

    //*** SensorsModule *****
    public final boolean SensorsModule_getActivityStatus(){
        return SensorsModule_isActive;
    }

    public final boolean SensorsModule_getCapturingStatus(){
        return SensorsModule_isCapturing;
    }

    public final boolean SensorsModule_getCapturingRAWStatus(){
        return SensorsModule_isCapturing_inRaw;
    }

    public final boolean SensorsModule_getCapturingRelativeStatus(){
        return SensorsModule_isCapturing_inRelative;
    }

    //***** TimeModule ********
    public final boolean TimeModule_getActivityStatus(){
        return TimeModule_isActive;
    }

    public final boolean TimeModule_getCapturingStatus(){
        return TimeModule_isCapturing;
    }

    public final boolean TimeModule_getCapturingDeviceStatus(){
        return TimeModule_isCapturing_fromDevice;
    }

    public final boolean TimeModule_getCapturingNetworkStatus(){
        return TimeModule_isCapturing_fromNetwork;
    }
}
