package in.org.klp.ilpkonnect.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.ReportPojo.ReportIndiPojo;
import in.org.klp.ilpkonnect.utils.Constants;

/**
 * Created by shridhars on 8/29/2017.
 */

public class NewPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    TextView question_name,txtyes,txtno,txtdn,school_count,txtyesperc,txtnoperc,txtdnperc,school_resp_count,resp_count;
    Context context;
    ArrayList<ReportIndiPojo> reportIndiPojos;


    public NewPagerAdapter(Context context, ArrayList<ReportIndiPojo> reportIndiPojos) {
        this.context=context;
        this.reportIndiPojos=reportIndiPojos;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_display_report, container, false);

         question_name=(TextView) view.findViewById(R.id.question_name);
        txtyes=(TextView) view.findViewById(R.id.txtyes);
        txtno=(TextView) view.findViewById(R.id.txtno);
        txtdn=(TextView) view.findViewById(R.id.txtdn);
        school_count=(TextView) view.findViewById(R.id.school_count);
        txtyesperc=(TextView) view.findViewById(R.id.txtyesperc);
        txtnoperc=(TextView) view.findViewById(R.id.txtnoperc);
        txtdnperc=(TextView) view.findViewById(R.id.txtdnperc);
        school_resp_count=(TextView) view.findViewById(R.id.school_resp_count);
        resp_count=(TextView)view.findViewById(R.id.resp_count);



        if(Constants.Lang==1) {
            question_name.setText(reportIndiPojos.get(position).getTextEng());
        }else {
            question_name.setText(reportIndiPojos.get(position).getTextKan());
        }
        int yes=0,no=0,dn=0;
        double yesp=0,nop=0,dnp=0;
        yes=Integer.parseInt(reportIndiPojos.get(position).getYes());
        no=Integer.parseInt(reportIndiPojos.get(position).getNo());
        dn=Integer.parseInt(reportIndiPojos.get(position).getDont());
        txtyes.setText(yes+"");
        txtno.setText(no+"");
        txtdn.setText(dn+"");

        school_count.setText(reportIndiPojos.get(position).getTotal_school()+"");
        school_resp_count.setText(reportIndiPojos.get(position).getToal_schools_with_res()+"");
       // resp_count.setText(reportIndiPojos.get(position).getTotal_responses()+"");
        resp_count.setText((yes+no+dn)+"");
        yesp=getScorePercent(yes,(yes+no+dn));
        nop=getScorePercent(no,(yes+no+dn));
        dnp=getScorePercent(dn,(yes+no+dn));
        txtyesperc.setText(yesp+"%");
        txtnoperc.setText(nop+"%");
        txtdnperc.setText(dnp+"%");



        //question_name.setText(reportIndiPojos.get(position).getTextEng());






        container.addView(view);

        return view;
    }


    public double getScorePercent(int num, int total) {

        if (num != 0 && total != 0) {
            float res = 100f * num / total;
            try {
                return Double.parseDouble(new DecimalFormat("##.##").format(res));
            } catch (Exception e) {
                return 0d;
            }
        } else return 0d;
    }
    @Override
    public int getCount() {
        if(reportIndiPojos!=null)
        {
            return reportIndiPojos.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

