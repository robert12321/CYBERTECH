package pl.dwiekieta.swa.CSVPackage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import pl.dwiekieta.swa.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CSVFragment.CSVListener } interface
 * to handle interaction events.
 */
public class CSVFragment extends Fragment {

    private CSVListener mListener;
    private View view;
    private Context context;

    private Switch activitySwitch;
    private TableRow tr;
    private RadioButton rb;
    private TextView tv;
    private EditText et;
    private Button bt;

    private CSVManager lastCSVState;
    private boolean lastZeroSet;
    private boolean lastNetworkSet;

    /*
    ******* DirectoryButton ***********
     */

    private void initDirectoryButton(CSVManager csvManager){
        et = view.findViewById(R.id.CSVDirectoryInputText);
        bt = view.findViewById(R.id.CSVDirectoryInputButton);

        if(csvManager.getDirectorySetStatus()) {
            bt.setText(context.getString(R.string.CSVManager_layout_changeCapturingDirectory));
            et.setText(csvManager.getDirectory());
        }
        else {
            bt.setText(context.getString(R.string.CSVManager_layout_setCapturingDirectory));
            et.setText(context.getString(R.string.CSVManager_layout_CapturingDefaultDirectory));
        }
    }

    /*
    ****** SensorsModule **************
     */
    private void clickableSensorButton(CSVManager csvManager, boolean isZeroSet){
        if(csvManager.SensorsModule_getActivityStatus())
            activitySwitch.setClickable(true);
        else
            activitySwitch.setClickable(false);

        rb = view.findViewById(R.id.SensorsModule_RAWButton);
        rb.setClickable(true);

        if(isZeroSet){
            rb = view.findViewById(R.id.SensorsModule_RelativeButton);
            rb.setClickable(true);
            rb = view.findViewById(R.id.SensorsModule_FullButton);
            rb.setClickable(true);

            return;
        }

        rb = view.findViewById(R.id.SensorsModule_RelativeButton);
        rb.setClickable(false);
        rb = view.findViewById(R.id.SensorsModule_FullButton);
        rb.setClickable(false);
    }

    private void initSensorsModule(CSVManager csvManager, boolean isZeroSet){
        activitySwitch = view.findViewById(R.id.SensorsModule_PowerButton);
        tr = view.findViewById(R.id.SensorsModule_CapturingSelector);

        clickableSensorButton(csvManager, isZeroSet);

        rb = view.findViewById(R.id.SensorsModule_RAWButton);
        rb.setChecked(false);
        rb = view.findViewById(R.id.SensorsModule_RelativeButton);
        rb.setChecked(false);
        rb = view.findViewById(R.id.SensorsModule_FullButton);
        rb.setChecked(false);


        if(!activitySwitch.isClickable()){
            activitySwitch.setChecked(false);
            tr.setVisibility(View.INVISIBLE);
            return;
        }

        if(csvManager.SensorsModule_getCapturingStatus())
            activitySwitch.setChecked(true);

        if(activitySwitch.isChecked())
            tr.setVisibility(View.VISIBLE);
        else{
            tr.setVisibility(View.INVISIBLE);
            return;
        }

        if(csvManager.SensorsModule_getCapturingRAWStatus() &&
                csvManager.SensorsModule_getCapturingRelativeStatus()){
            rb = view.findViewById(R.id.SensorsModule_FullButton);
            rb.setChecked(true);
            return;
        }

        if(csvManager.SensorsModule_getCapturingRAWStatus()){
            rb = view.findViewById(R.id.SensorsModule_RAWButton);
            rb.setChecked(true);
            return;
        }

        if(csvManager.SensorsModule_getCapturingRelativeStatus()) {
            rb = view.findViewById(R.id.SensorsModule_RelativeButton);
            rb.setChecked(true);
        }
    }

    /*
    ***** TimeModule ******************
     */

    private void clickableTimeModule(CSVManager csvManager, boolean isNetworkSet){
        activitySwitch = view.findViewById(R.id.TimeModule_PowerButton);

        if(csvManager.TimeModule_getActivityStatus())
            activitySwitch.setClickable(true);
        else
            activitySwitch.setClickable(false);

        rb = view.findViewById(R.id.TimeModule_DeviceButton);
        rb.setClickable(true);

        if(isNetworkSet){
            rb = view.findViewById(R.id.TimeModule_NetworkButton);
            rb.setClickable(true);
            rb = view.findViewById(R.id.TimeModule_FullButton);
            rb.setClickable(true);

            return;
        }

        rb = view.findViewById(R.id.TimeModule_NetworkButton);
        rb.setClickable(false);
        rb = view.findViewById(R.id.TimeModule_FullButton);
        rb.setClickable(false);
    }

    private void initTimeModule(CSVManager csvManager, boolean isNetworkSet){
        activitySwitch = view.findViewById(R.id.TimeModule_PowerButton);
        tr = view.findViewById(R.id.TimeModule_CapturingSelector);

        clickableTimeModule(csvManager, isNetworkSet);

        rb = view.findViewById(R.id.TimeModule_DeviceButton);
        rb.setChecked(false);
        rb = view.findViewById(R.id.TimeModule_NetworkButton);
        rb.setChecked(false);
        rb = view.findViewById(R.id.TimeModule_FullButton);
        rb.setChecked(false);


        if(!activitySwitch.isClickable()){
            activitySwitch.setChecked(false);
            tr.setVisibility(View.INVISIBLE);
            return;
        }

        if(csvManager.TimeModule_getCapturingStatus())
            activitySwitch.setChecked(true);

        if(activitySwitch.isChecked())
            tr.setVisibility(View.VISIBLE);
        else{
            tr.setVisibility(View.INVISIBLE);
            return;
        }

        if(csvManager.TimeModule_getCapturingDeviceStatus() &&
                csvManager.TimeModule_getCapturingNetworkStatus()){
            rb = view.findViewById(R.id.TimeModule_FullButton);
            rb.setChecked(true);
            return;
        }

        if(csvManager.TimeModule_getCapturingDeviceStatus()){
            rb = view.findViewById(R.id.TimeModule_DeviceButton);
            rb.setChecked(true);
            return;
        }

        if(csvManager.TimeModule_getCapturingNetworkStatus()) {
            rb = view.findViewById(R.id.TimeModule_NetworkButton);
            rb.setChecked(true);
        }
    }



    /*
    ****** CONSTRUCTORS ************
     */
    public CSVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_csv, container, false);

        if(lastCSVState !=null) {
            initDirectoryButton(lastCSVState);
            initSensorsModule(lastCSVState, lastZeroSet);
            initTimeModule(lastCSVState, lastNetworkSet);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CSVListener) {
            mListener = (CSVListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface CSVListener {
        void activeModules(View view);
        void sensorModuleSelector(View view);
        void timeModuleSelector(View view);
        void displayCSVLog(View view);
        void directoryButton(View view);
    }

    public void modulesRefresh(CSVManager csvManager, boolean isZeroSet, boolean isNetworkSet){
        clickableSensorButton(csvManager, isZeroSet);
        clickableTimeModule(csvManager, isNetworkSet);
    }

    public void activeSensorsModule(Context context, CSVManager csvManager, boolean checked, boolean isZeroSet){
        tr = view.findViewById(R.id.SensorsModule_CapturingSelector);
        if(checked) {
            tr.setVisibility(View.VISIBLE);
            initSensorsModule(csvManager, isZeroSet);
        }
        else {
            tr.setVisibility(View.INVISIBLE);
            csvManager.deactivateModule(context, CSVManager.CapturingModule.SensorsModule,
                    CSVManager.SensorsModule.RAW.ordinal());
            csvManager.deactivateModule(context, CSVManager.CapturingModule.SensorsModule,
                    CSVManager.SensorsModule.RELATIVE.ordinal());
        }
    }

    public void activeTimeModule(Context context, CSVManager csvManager, boolean checked, boolean isNetworkSet){
        tr = view.findViewById(R.id.TimeModule_CapturingSelector);
        if(checked) {
            tr.setVisibility(View.VISIBLE);
            initTimeModule(csvManager, isNetworkSet);
        }
        else {
            tr.setVisibility(View.INVISIBLE);
            csvManager.deactivateModule(context, CSVManager.CapturingModule.TimeModule,
                    CSVManager.TimeModule.DEVICE.ordinal());
            csvManager.deactivateModule(context, CSVManager.CapturingModule.TimeModule,
                    CSVManager.TimeModule.NETWORK.ordinal());
        }
    }

    public void displayCSVLog(Context context, CSVManager csvManager, LayoutInflater inflater, ViewGroup container){
        view = inflater.inflate(R.layout.fragment_csv, container, false);
        tv = view.findViewById(R.id.CSVLog);
        tv.setText(csvManager.getStatus(context,null));
    }

    public void setLastCSVState(Context context, CSVManager csvManager){
        this.context = context;
        lastCSVState = csvManager;
    }

    public void setLastZeroSet(boolean lastZeroSet){
        this.lastZeroSet = lastZeroSet;
    }

    public void setLastNetworkSet(boolean lastNetworkSet){
        this.lastNetworkSet = lastNetworkSet;
    }


    public final String directoryButtonManager(Context context, CSVManager csvManager){
        et = view.findViewById(R.id.CSVDirectoryInputText);
        bt = view.findViewById(R.id.CSVDirectoryInputButton);

        if(csvManager.getDirectorySetStatus())
            bt.setText(context.getString(R.string.CSVManager_layout_changeCapturingDirectory));
        else
            bt.setText(context.getString(R.string.CSVManager_layout_setCapturingDirectory));

        return et.getText().toString();
    }
}
