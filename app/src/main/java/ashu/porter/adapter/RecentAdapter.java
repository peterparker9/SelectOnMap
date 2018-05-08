package ashu.porter.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ashu.porter.R;
import ashu.porter.home.MapsActivity;
import ashu.porter.model.Recent;
import io.realm.RealmResults;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{

    Context context;
    RealmResults<Recent> recent;

    public RecentAdapter(Context context, RealmResults<Recent> recent){
        this.context = context;
        this.recent = recent;
    }

    @Override
    public RecentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_result, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecentAdapter.ViewHolder holder, final int position) {
        holder.txtRecent.setText(recent.get(position).getTitle());
        holder.txtAddress.setText("");

        holder.txtRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(context, MapsActivity.class);

                resultIntent.putExtra("val", recent.get(position).getTitle());
                resultIntent.putExtra("from", 0);
                ((AppCompatActivity)context).startActivityForResult(resultIntent, Activity.RESULT_OK);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtRecent;
        TextView txtAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            txtRecent = (TextView) itemView.findViewById(R.id.txtRecent);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
        }
    }


}
