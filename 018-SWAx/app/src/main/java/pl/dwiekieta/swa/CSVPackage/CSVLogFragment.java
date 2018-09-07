package pl.dwiekieta.swa.CSVPackage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.dwiekieta.swa.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CSVLogFragment.CSVLogListener} interface
 * to handle interaction events.
 */
public class CSVLogFragment extends Fragment {

    private CSVLogListener mListener;
    private View view;

    private TextView tv;

    private CSVManager lastState;
    private Context context;

    public CSVLogFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_csvlog, container, false);

        tv = view.findViewById(R.id.CSVLog);

        if((lastState != null) && (context != null))
            tv.setText(lastState.getStatus(context,null));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CSVLogListener) {
            mListener = (CSVLogListener) context;
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
    public interface CSVLogListener {
        void displayCSVManager(View view);
    }

    public void setLastState(CSVManager csvManager){
        lastState = csvManager;
    }

    public void setContext(Context context){
        this.context = context;
    }
}
