package pl.dwiekieta.swa;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import pl.dwiekieta.swa.CSVPackage.CSVManager;
import pl.dwiekieta.swa.SensorsPackage.SensorData;
import pl.dwiekieta.swa.TimePackage.TimeClass;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CapturingFragment.CapturingListener} interface
 * to handle interaction events.
 */
public class CapturingFragment extends Fragment {

    private View view;
    private Context context;

    private TextView tv;
    private Button bt;
    private ImageView iv;

    private CapturingListener mListener;

    public CapturingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_capturing, container, false);

        capturingButtonManager(context,true);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CapturingListener) {
            mListener = (CapturingListener) context;
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
    public interface CapturingListener {
        void capturingButton(View view);
    }

    public void setViewRefresh(CSVManager csvManager, SensorData sensorData, TimeClass timeClass){
        if(csvManager.SensorsModule_getCapturingStatus()){
            tv = view.findViewById(R.id.Capturing_SensorsView);

            if(csvManager.SensorsModule_getCapturingRelativeStatus())
                tv.setText(sensorData.get_RelativeSensors());
            else
                tv.setText(sensorData.get_RAWSensors());
        }

        if(csvManager.TimeModule_getCapturingStatus()){
            tv = view.findViewById(R.id.Capturing_TimeView);

            if(csvManager.TimeModule_getCapturingNetworkStatus())
                tv.setText(timeClass.getNetworkTimeString());
            else
                tv.setText(timeClass.getDeviceTimeString());
        }
    }

    public void update(Context context, CSVManager csvManager, SensorData sensorData, TimeClass timeClass){
        if(csvManager.SensorsModule_getCapturingStatus()){
            if(csvManager.SensorsModule_getCapturingRAWStatus())
                csvManager.writeLinr(context, CSVManager.CapturingModule.SensorsModule,CSVManager.SensorsModule.RAW.ordinal(),sensorData.CSV_RAWSensors());
            if(csvManager.SensorsModule_getCapturingRelativeStatus())
                csvManager.writeLinr(context, CSVManager.CapturingModule.SensorsModule,CSVManager.SensorsModule.RELATIVE.ordinal(),sensorData.CSV_RelativeSensors());
        }

        if(csvManager.TimeModule_getCapturingStatus()){
            if(csvManager.TimeModule_getCapturingDeviceStatus())
                csvManager.writeLinr(context, CSVManager.CapturingModule.TimeModule,CSVManager.TimeModule.DEVICE.ordinal(), String.valueOf(timeClass.getDeviceTimeInMilliseconds()));
            if(csvManager.TimeModule_getCapturingNetworkStatus())
                csvManager.writeLinr(context, CSVManager.CapturingModule.TimeModule,CSVManager.TimeModule.NETWORK.ordinal(), String.valueOf(timeClass.getNetworkTimeInMilliseconds()));
        }
    }

    public void capturingButtonManager(Context context, boolean init){
        bt = view.findViewById(R.id.Capturing_button);

        if(init)
            bt.setText(context.getString(R.string.main_capturingButton_StartCapturing));
        else
            bt.setText(context.getString(R.string.main_capturingButton_StopCapturing));
    }

    public void capturingIndicator(boolean isCapruring){
        iv = view.findViewById(R.id.Capturing_indicator);

        if(!isCapruring){
            iv.setVisibility(View.INVISIBLE);
            return;
        }

        if(iv.getVisibility() == View.INVISIBLE)
            iv.setVisibility(View.VISIBLE);
        else
            iv.setVisibility(View.INVISIBLE);
    }

    public void setContext(Context context){
        this.context = context;
    }
}
