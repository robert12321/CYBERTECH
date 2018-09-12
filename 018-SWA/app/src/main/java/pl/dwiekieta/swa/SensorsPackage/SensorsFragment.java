package pl.dwiekieta.swa.SensorsPackage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import pl.dwiekieta.swa.MainActivity;
import pl.dwiekieta.swa.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorsFragment.SensorListener} interface
 * to handle interaction events.
 */
public class SensorsFragment extends Fragment {

    private SensorListener mListener;
    String testtt = String.valueOf(getTimeFromLocalHost());
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

        tv = view.findViewById(R.id.start_time);
        //tv.setText(sensorData.get_sampling());
        tv.setText(testtt);
    }

    public void setCaptureButton(Boolean state){
        button = view.findViewById(R.id.startStopButt);
        if(!state)
            button.setText(R.string.sensorsapp_startButton);
        else
            button.setText(R.string.sensorsapp_stopButton);
    }
    public Long getTimeFromLocalHost()
    {
        //http://192.168.1.181:8000/Home.html
        URL url = null;
        try {
            url = new URL("http://192.168.1.181:8000/Home.html");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            for (String line; (line = reader.readLine()) != null;) {
                builder.append(line.trim());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
        }

        String start = "<h1>";
        String end = "</h1>";
        if(builder.indexOf(start) < 0 )
        {
            return Long.valueOf(1000000000);
        }
        String part = builder.substring(builder.indexOf(start) + start.length());
        if(!part.contains(end))
        {
            return Long.valueOf(1000000000);
        }
        String time = part.substring(0, part.indexOf(end));
        return  Long.parseLong(time);

    }
}
