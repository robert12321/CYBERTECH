package pl.dwiekieta.swa;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private MainListener mListener;
    private View view;

    private TextView tv;
    private ImageView iv;
    private ColorDrawable cd;
    private Button bt;

    public enum ConnectedClass{CSV, SENSORS, TIME}
    public enum CapturingType{NOPREPARE,PREPARE,CAPTURING}

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main,container,false);


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainListener) {
            mListener = (MainListener) context;
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

    public interface MainListener {
        void homeCapturingButton(View view);
    }

    public void setCapturingButtonClickable(boolean isReadyToCapturing){
        bt = view.findViewById(R.id.home_CapturingButton);

        if(isReadyToCapturing)
            bt.setClickable(true);
        else
            bt.setClickable(false);

    }

    public final CapturingType setCapturingButtonText(final Context context, boolean prepareStatus, boolean capturingStatus){
        bt = view.findViewById(R.id.home_CapturingButton);

        if(prepareStatus){
            if(capturingStatus){
                bt.setText(context.getString(R.string.main_capturingButton_StopCapturing));
                return CapturingType.CAPTURING;
            } else {
                bt.setText(context.getString(R.string.main_capturingButton_StartCapturing));
                return CapturingType.PREPARE;
            }
        }

        bt.setText(context.getString(R.string.main_capturingButton_GoToCapturing));
        return CapturingType.NOPREPARE;
    }

    public void setErrorState(Context context, ConnectedClass cClass, int message){

        switch (cClass) {
            case CSV:{
                iv = view.findViewById(R.id.CSVWarning);
                iv.setVisibility(View.INVISIBLE);

                tv = view.findViewById(R.id.CSVStatus);
                tv.setText(context.getString(message));

                cd = null;
                int colorCode = 0;
                if(tv.getBackground() instanceof ColorDrawable) {
                    cd = (ColorDrawable) tv.getBackground();
                    colorCode = cd.getColor();
                }
                if(cd == null)
                    return;

                if(colorCode == 0xffff0000)
                    tv.setBackgroundColor(0xffffffff);
                else
                    tv.setBackgroundColor(0xffff0000);
            } break;

            case SENSORS: {
                iv = view.findViewById(R.id.sensorsWarning);
                iv.setVisibility(View.INVISIBLE);

                tv = view.findViewById(R.id.sensorStatus);
                tv.setText(context.getString(message));

                cd = null;
                int colorCode = 0;
                if(tv.getBackground() instanceof ColorDrawable) {
                    cd = (ColorDrawable) tv.getBackground();
                    colorCode = cd.getColor();
                }
                if(cd == null)
                    return;

                if(colorCode == 0xffff0000)
                    tv.setBackgroundColor(0xffffffff);
                else
                    tv.setBackgroundColor(0xffff0000);
            } break;
            case TIME: {
                iv = view.findViewById(R.id.timeWarning);
                iv.setVisibility(View.INVISIBLE);

                tv = view.findViewById(R.id.timeStatus);
                tv.setText(context.getString(message));

                cd = null;
                int colorCode = 0;
                if(tv.getBackground() instanceof ColorDrawable) {
                    cd = (ColorDrawable) tv.getBackground();
                    colorCode = cd.getColor();
                }
                if(cd == null)
                    return;

                if(colorCode == 0xffff0000)
                    tv.setBackgroundColor(0xffffffff);
                else
                    tv.setBackgroundColor(0xffff0000);
            } break;
        }
    }

    public void setWarningState(Context context, ConnectedClass cClass, int message){

        switch (cClass) {
            case CSV: {
                iv = view.findViewById(R.id.CSVWarning);
                tv = view.findViewById(R.id.CSVStatus);
                tv.setText(context.getString(message));

                tv.setBackgroundColor(0xffffffff);

                if(iv.getVisibility() == View.VISIBLE)
                    iv.setVisibility(View.INVISIBLE);
                else
                    iv.setVisibility(View.VISIBLE);
            } break;
            case SENSORS: {
                iv = view.findViewById(R.id.sensorsWarning);
                tv = view.findViewById(R.id.sensorStatus);
                tv.setText(context.getString(message));

                tv.setBackgroundColor(0xffffffff);

                if(iv.getVisibility() == View.VISIBLE)
                    iv.setVisibility(View.INVISIBLE);
                else
                    iv.setVisibility(View.VISIBLE);
            } break;
            case TIME: {
                iv = view.findViewById(R.id.timeWarning);
                tv = view.findViewById(R.id.timeStatus);
                tv.setText(context.getString(message));

                tv.setBackgroundColor(0xffffffff);

                if(iv.getVisibility() == View.VISIBLE)
                    iv.setVisibility(View.INVISIBLE);
                else
                    iv.setVisibility(View.VISIBLE);
            } break;
        }
    }

    public void setNormalState(Context context, ConnectedClass cClass, int message){

        switch (cClass) {
            case CSV: {
                iv = view.findViewById(R.id.CSVWarning);
                iv.setVisibility(View.INVISIBLE);
                tv = view.findViewById(R.id.CSVStatus);
                tv.setText(context.getString(message));

                tv.setBackgroundColor(0xffffffff);
            } break;
            case SENSORS: {
                iv = view.findViewById(R.id.sensorsWarning);
                iv.setVisibility(View.INVISIBLE);
                tv = view.findViewById(R.id.sensorStatus);
                tv.setText(context.getString(message));

                tv.setBackgroundColor(0xffffffff);
            } break;
            case TIME: {
                iv = view.findViewById(R.id.timeWarning);
                iv.setVisibility(View.INVISIBLE);
                tv = view.findViewById(R.id.timeStatus);
                tv.setText(context.getString(message));

                tv.setBackgroundColor(0xffffffff);
            } break;
        }
    }
}
