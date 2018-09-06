package pl.dwiekieta.swa.SensorsPackage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pl.dwiekieta.swa.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorsFragment.SensorListener} interface
 * to handle interaction events.
 */
public class SensorsFragment extends Fragment {

    private SensorListener mListener;

    TextView tv;
    View view;
    Button button;

    public SensorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sensors, container, false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SensorListener) {
            mListener = (SensorListener) context;
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
    public interface SensorListener {
        void onCaptureButton(View view);
        void onZeroPointSet(View view);
    }




    public void update(SensorData sensorData){
        tv = view.findViewById(R.id.sens_grav);
        tv.setText(sensorData.get_mGravs());
        tv = view.findViewById(R.id.sens_gravd);
        tv.setText(sensorData.get_dGravs());

        tv = view.findViewById(R.id.sens_mag);
        tv.setText(sensorData.get_mGeoMags());
        tv = view.findViewById(R.id.sens_magd);
        tv.setText(sensorData.get_dGeoMags());

        tv = view.findViewById(R.id.sens_inc);
        tv.setText(sensorData.get_mInclination());
        tv = view.findViewById(R.id.sens_incd);
        tv.setText(sensorData.get_dInclination());

        tv = view.findViewById(R.id.sens_rot);
        tv.setText(sensorData.get_mRotationMatrix());
        tv = view.findViewById(R.id.sens_rotd);
        tv.setText(sensorData.get_dRotationMatrix());

        tv = view.findViewById(R.id.sens_orient);
        tv.setText(sensorData.get_mOrientation());
        tv = view.findViewById(R.id.sens_orientd);
        tv.setText(sensorData.get_dOrientation());

        tv = view.findViewById(R.id.sens_angledd);
        tv.setText(sensorData.get_dAngle());

        tv = view.findViewById(R.id.sens_sampling);
        tv.setText(sensorData.get_sampling());

    }

    public void setCaptureButton(Boolean state){
        button = view.findViewById(R.id.startStopButt);
        if(!state)
            button.setText(R.string.sensorsapp_startButton);
        else
            button.setText(R.string.sensorsapp_stopButton);
    }
}
