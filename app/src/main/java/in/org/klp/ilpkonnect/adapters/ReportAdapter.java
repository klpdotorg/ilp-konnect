package in.org.klp.ilpkonnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
 * Created by shridhars on 8/28/2017.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    Context context;
    ArrayList<ReportIndiPojo> reportIndiPojos;


    public ReportAdapter(Context applicationContext, ArrayList<ReportIndiPojo> reportIndiPojos) {
        this.context = applicationContext;
        this.reportIndiPojos = reportIndiPojos;
        notifyDataSetChanged();
    }


    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_display_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {

        if(Constants.Lang==1) {
            holder.question_name.setText(reportIndiPojos.get(position).getTextEng());
        }else {
            holder.question_name.setText(reportIndiPojos.get(position).getTextKan());
        }
        int yes=0,no=0,dn=0;
        double yesp=0,nop=0,dnp=0;
        yes=Integer.parseInt(reportIndiPojos.get(position).getYes());
        no=Integer.parseInt(reportIndiPojos.get(position).getNo());
        dn=Integer.parseInt(reportIndiPojos.get(position).getDont());
        holder.txtyes.setText(yes+"");
        holder.txtno.setText(no+"");
        holder.txtdn.setText(dn+"");
        holder.school_count.setText(100+"");

        yesp=getScorePercent(yes,(yes+no+dn));
        nop=getScorePercent(no,(yes+no+dn));
        dnp=getScorePercent(dn,(yes+no+dn));
        holder.txtyesperc.setText(yesp+"%");
        holder.txtnoperc.setText(nop+"%");
        holder.txtdnperc.setText(dnp+"%");

    }

    @Override
    public int getItemCount() {
        if (reportIndiPojos != null) {
           // Toast.makeText(context, reportIndiPojos.size() + "", Toast.LENGTH_SHORT).show();
        }
        return reportIndiPojos.size();
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





    public class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView question_name,txtyes,txtno,txtdn,school_count,txtyesperc,txtnoperc,txtdnperc;

        public ReportViewHolder(View itemView) {
            super(itemView);
            question_name = (TextView) itemView.findViewById(R.id.question_name);
            txtyes=(TextView)itemView.findViewById(R.id.txtyes);
            txtno=(TextView)itemView.findViewById(R.id.txtno);
            txtdn=(TextView)itemView.findViewById(R.id.txtdn);
            school_count=(TextView)itemView.findViewById(R.id.school_count);
            txtyesperc=(TextView)itemView.findViewById(R.id.txtyesperc);
            txtnoperc=(TextView)itemView.findViewById(R.id.txtnoperc);
            txtdnperc=(TextView)itemView.findViewById(R.id.txtdnperc);

        }
    }
}
