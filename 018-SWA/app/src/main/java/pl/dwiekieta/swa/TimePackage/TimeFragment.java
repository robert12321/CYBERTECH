package pl.dwiekieta.swa.TimePackage;

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
 * {@link TimeFragment.TimerListener} interface
 * to handle interaction events.
 */
public class TimeFragment extends Fragment {

    private TimerListener mListener;

    View view;

    TextView tv;
    Button button;

    public TimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_time, container, false);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimerListener) {
            mListener = (TimerListener) context;
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
    public interface TimerListener {
        void onCaptureTimeButton(View view);
    }

    public void update(TimeClass timeClass){
        tv = view.findViewById(R.id.devTime);
        tv.setText(timeClass.getDeviceTimeString());

        if(!timeClass.getSynchronizedStatus()) {
            tv = view.findViewById(R.id.netTimeLabel);
            tv.setVisibility(View.INVISIBLE);
            tv = view.findViewById(R.id.netTime);
            tv.setVisibility(View.INVISIBLE);
            tv = view.findViewById(R.id.diffTimeLabel);
            tv.setVisibility(View.INVISIBLE);
            tv = view.findViewById(R.id.diffTime);
            tv.setVisibility(View.INVISIBLE);
            return;
        }

        tv = view.findViewById(R.id.netTimeLabel);
        tv.setVisibility(View.VISIBLE);
        tv = view.findViewById(R.id.netTime);
        tv.setVisibility(View.VISIBLE);
        tv.setText(timeClass.getNetworkTimeString());

        tv = view.findViewById(R.id.diffTimeLabel);
        tv.setVisibility(View.VISIBLE);
        tv = view.findViewById(R.id.diffTime);
        tv.setVisibility(View.VISIBLE);
        tv.setText(String.valueOf(timeClass.getDifferentTimeInMilliseconds()) + "ms");

    }

    public void setCaptureButton(boolean state){
        button = view.findViewById(R.id.timeCapturingButton);

        if(state)
            button.setText(R.string.timeapp_stopcapturing);
        else
            button.setText(R.string.timeapp_startcapturing);
    }
}
