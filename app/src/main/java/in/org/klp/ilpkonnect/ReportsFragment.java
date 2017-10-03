package in.org.klp.ilpkonnect;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "question_name";
    private static final String ARG_PARAM3 = "agg";
    private static final String ARG_PARAM4 = "blck_agg";
    private static final String ARG_PARAM5 = "dist_agg";

    private String mParam1;
    private String q_name, schoolcount, schoolwithresponse, responses, yes, no, dn;

    private OnFragmentInteractionListener mListener;

    public ReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsFragment.
     */
    public static ReportsFragment newInstance(String param1, String param2, String param3) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int y, n, d, t;

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            q_name = getArguments().getString(ARG_PARAM2);
            String[] output = getArguments().getString(ARG_PARAM3).toString().trim().split("\\|");
            y=Integer.parseInt(output[3]);
            n=Integer.parseInt(output[4]);
            d=Integer.parseInt(output[5]);
            schoolcount=output[0];
            schoolwithresponse=output[1];
            responses=output[2];
            if (responses.equals("0")){
                yes="0:0%";
                no="0:0%";
                dn="0:0%";
            } else {
                yes=String.valueOf(y) + ":" + String.valueOf(getScorePercent(y*100/Float.parseFloat(responses))) + "%";
                no=String.valueOf(n) + ":" + String.valueOf(getScorePercent(n*100/Float.parseFloat(responses))) + "%";
                dn=String.valueOf(d) + ":" + String.valueOf(getScorePercent(d*100/Float.parseFloat(responses))) + "%";
            }
        }
    }
    public double getScorePercent(float val) {

        return Double.parseDouble(new DecimalFormat("##.##").format(val));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_display_report, container, false);
        TextView tv=(TextView) view.findViewById(R.id.question_name);
        tv.setText(q_name);
        //tv=(TextView) view.findViewById(R.id.aggregate);
        //tv.setText(agg);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
