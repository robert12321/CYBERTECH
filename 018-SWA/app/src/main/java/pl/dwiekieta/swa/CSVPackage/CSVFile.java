package pl.dwiekieta.swa.CSVPackage;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.dwiekieta.swa.R;

public class CSVFile {
    private File RootPath;
    private File FileDirectory;
    private File FileCSV;
    private FileOutputStream FOS;

    private boolean isExternalStorageMounted;
    private boolean isRootPathSet;
    private boolean isFileDirectorySet;
    private boolean isFileCreated;
    private boolean isFOSCreated;

    private boolean isInErrorState;
    private boolean Mutex;
    private boolean isCapturing;

    private boolean checkMountedExternalStorage(){
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state))
            return true;

        return false;
    }

    /*
    ******** CONSTRUCTORS **************8
     */

    private void init(){
        Mutex = true;
        isCapturing = false;
    }

    public CSVFile(){
        if(checkMountedExternalStorage())
            RootPath = Environment.getExternalStorageDirectory();
        else
            RootPath = null;

        FileDirectory = null;
        FileCSV = null;

        init();
        checkErrors();
    }

    public CSVFile(String directory) {
        init();

        if (!checkMountedExternalStorage()) {
            RootPath = null;
            FileDirectory = null;
            FileCSV = null;

            checkErrors();
            return;
        }

        RootPath = Environment.getExternalStorageDirectory();
        FileCSV = null;
        setFileDirectory(directory);
    }

    public CSVFile(String directory, String fileName) {
        init();

        if (!checkMountedExternalStorage()) {
            RootPath = null;
            FileDirectory = null;
            FileCSV = null;

            checkErrors();
            return;
        }

        RootPath = Environment.getExternalStorageDirectory();
        setFileDirectory(directory);
        setFile(fileName);
    }

    /*
    ****** FileFunctions *********
     */
    public final boolean setFileDirectory(String directory){
        if(isCapturing) {
            checkErrors();
            return false;
        }

        if(RootPath == null){
            FileDirectory = null;

            checkErrors();
            return false;
        }

        FileDirectory = new File(RootPath.getAbsolutePath() + '/' + directory);

        if(!FileDirectory.exists())
            FileDirectory.mkdir();

        checkErrors();
        return true;
    }

    public final boolean setFile(String fileName){
        if(isCapturing) {
            checkErrors();
            return false;
        }

        if(FileDirectory == null){
            FileCSV = null ;

            checkErrors();
            return false;
        }

        FileCSV = new File(FileDirectory,fileName);

        if(!FileCSV.exists()){
            try {
                FileCSV.createNewFile();
            } catch (IOException e) {
                FileCSV = null;

                checkErrors();
                return false;
            }
        }

        checkErrors();
        return true;
    }

    /*
    ***** CAPTURING ************
     */
    public final boolean startCapturing(){
        if(isCapturing)
            return false;

        if(isInErrorState)
            return false;

        try {
            FOS = new FileOutputStream(FileCSV);
        } catch (FileNotFoundException e) {
            FOS = null;

            Mutex = false;
            isCapturing = false;
            return false;
        }

        Mutex = true;
        isCapturing = true;
        return true;
    }

    public final boolean stopCapturing(){
        if(!isCapturing)
            return false;

        while (!Mutex);
        Mutex = false;

        FOS = null;
        FileCSV = null;

        isCapturing = false;
        checkErrors();
        return true;
    }

    public final boolean writeLine(String dataLine){
        if(!isCapturing)
            return false;

        while (!Mutex);
        Mutex = false;

        String ln = dataLine;
        ln += '\n';

        try {
            FOS.write(ln.getBytes());
        } catch (IOException e) {
            stopCapturing();
            return false;
        }

        Mutex = true;
        return true;
    }

    /*
    ****** STATUS ********
     */
    public final String getStatus(Context context, String tabs) {
        String tab, nextTab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        nextTab = (tab + "\t");

        StringBuffer status = new StringBuffer("");
        status.append(tab + context.getString(R.string.CSVFile_status_status)  + '\n');
        status.append(tab + context.getString(R.string.CSVFile_status_RunStatus) + ":\n");
        status.append(getRuns(context, nextTab));
        if(isInErrorState){
            status.append(tab + context.getString(R.string.CSVFile_status_Errors) + ":\n");
            status.append(getErrors(context, nextTab));
        }
        if(isFileDirectorySet){
            status.append(tab + context.getString(R.string.CSVFile_status_Path) + ":\n");
            status.append(getPath(context, nextTab));
        }

        return status.toString();
    }
    /*
    ****** RUNS **********
     */
    public final String getRuns(Context context, String tabs){
        String tab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        if(isInErrorState)
            return (tab + context.getString(R.string.CSVFile_errorMessage_IsNotReadyToCapturing) + '\n');

        if(isCapturing)
            return (tab + context.getString(R.string.CSVFile_errorMessage_IsCapturing) + '\n');

            return (tab + context.getString(R.string.CSVFile_errorMessage_IsReadyToCapturing) + '\n');
    }

    public final String getPath(Context context, String tabs){
        String tab;
        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        if(isFileCreated)
            return tab + FileCSV.getAbsolutePath();

        return tab + FileDirectory.getAbsolutePath();
    }

    /*
    ****** ERRORS *******************
     */
    public final boolean checkErrors() {
        boolean errorState = false;

        if (RootPath == null) {
            isRootPathSet = false;
            errorState = true;
        } else
            isRootPathSet = true;

        if (FileDirectory == null){
            isFileDirectorySet = false;
            errorState = true;
        }
        else
            isFileDirectorySet = true;

        if(FileCSV == null) {
            isFileCreated = false;
            errorState = true;
        }
        else
            isFileCreated = true;

        if(!checkMountedExternalStorage()) {
            isExternalStorageMounted = false;
            errorState = true;
        }
        else
            isExternalStorageMounted = true;

        if(isCapturing && (FOS == null)){
            isFOSCreated = false;
            errorState = true;
        } else
            isFOSCreated = true;

        isInErrorState = errorState;

        return isInErrorState;
    }

    public final String getErrors(Context context, String tabs){
        String errorMessage = null;
        StringBuffer errorBuffor = new StringBuffer("");
        String tab;

        if(tabs == null)
            tab = "";
        else
            tab = tabs;

        if(RootPath == null){
            isRootPathSet = false;
            errorBuffor.append(tab + context.getString(R.string.CSVFile_errorMessage_RootPath) + '\n');
        } else
            isRootPathSet = true;

        if(FileDirectory == null){
            isFileDirectorySet = false;
            errorBuffor.append(tab + context.getString(R.string.CSVFile_errorMessage_FileDirectory) + '\n');
        } else
            isFileDirectorySet = true;

        if(FileCSV == null){
            isFileCreated = false;
            errorBuffor.append(tab + context.getString(R.string.CSVFile_errorMessage_FileCreated) + '\n');
        } else
            isFileCreated = true;

        if(!checkMountedExternalStorage()){
            isExternalStorageMounted = false;
            errorBuffor.append(tab + context.getString(R.string.CSVFile_errorMessage_ExternalStorage) + '\n');
        } else
            isExternalStorageMounted = true;

        if(isCapturing && (FOS == null)){
            isFOSCreated = false;
            errorBuffor.append(tab + context.getString(R.string.CSVFile_errorMessage_FOSCreated) + '\n');
        } else
            isFOSCreated = true;

        if(errorBuffor.equals(""))
            isInErrorState = false;
        else {
            errorMessage = errorBuffor.toString();
            isInErrorState = true;
        }

        return errorMessage;
    }

    public final boolean isExternalStorageMounted(){
        return isExternalStorageMounted;
    }

    public final boolean isRootPathSet(){
        return isRootPathSet;
    }

    public final boolean isFileDirectorySet(){
        return isFileDirectorySet;
    }

    public final boolean isFileCreated(){
        return isFileCreated;
    }

    public final boolean isInErrorState(){
        return isInErrorState;
    }

    public final boolean isCapturing(){
        return isCapturing;
    }

    public final boolean isFOSCreated(){
        return isFOSCreated;
    }

    /*
    ****** GETS *********
     */

    public final String getDirectoryPath(Context context){
        if(!isFileDirectorySet)
            return context.getString(R.string.CSVFile_errorMessage_FileDirectory);
        else
            return FileDirectory.getAbsolutePath();
    }

    public final String getFilePath(Context context){
        if(!isFileCreated)
            return context.getString(R.string.CSVFile_errorMessage_FileCreated);
        else
            return FileCSV.getAbsolutePath();
    }
}
