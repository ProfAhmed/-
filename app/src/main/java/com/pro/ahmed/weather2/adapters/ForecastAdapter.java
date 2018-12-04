package com.pro.ahmed.weather2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pro.ahmed.weather2.R;
import com.pro.ahmed.weather2.model.Weather5dayesForecastModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    Context mContext;
    List<Weather5dayesForecastModel> foreCastes;

    public ForecastAdapter(Context mContext, List<Weather5dayesForecastModel> foreCastes) {
        this.mContext = mContext;
        this.foreCastes = foreCastes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_forecast_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather5dayesForecastModel model = foreCastes.get(position);
        Log.v("Adapter", String.valueOf(foreCastes.size()));
        Log.v("AdapterTemp", String.valueOf(foreCastes.get(0).getTemp()));
        holder.tvListForecastTemp.setText("℃" + String.valueOf(model.getTemp()));
        holder.tvListForecastDescription.setText(model.getDescription());
        holder.tvListForecastHumidity.setText("الرطوبة:" + String.valueOf(model.getHumidity()));
        holder.tvListForecastWind.setText("سرعة الرياح:" + String.valueOf(model.getWindSpeed()));
        holder.tvListForecastPressure.setText("الضغط:" + "hPa " + String.valueOf(model.getPressure()));
        holder.tvListForecastDateAndDayName.setText(String.valueOf(model.getDayName()) + ",");
        String dt_txt = model.getDt_txt();
        String date = dt_txt.substring(0, dt_txt.indexOf(" "));
        String time = dt_txt.substring(dt_txt.indexOf(" ") + 1);
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));// to get the time
        if (hour >= 12) {
            if (hour == 12) {
                Log.v("HourAdapter", String.valueOf(hour));
                holder.tvListForecastTime.setText(String.valueOf(hour) + time.substring(2, 5) + "م");
            } else {
                holder.tvListForecastTime.setText(String.valueOf(hour - 12) + time.substring(2, 5) + "م");
            }
        } else {
            if (hour == 0) {
                holder.tvListForecastTime.setText(String.valueOf(12) + time.substring(2, 5) + "ص");
            } else {
                holder.tvListForecastTime.setText(String.valueOf(hour) + time.substring(2, 5) + "ص");
            }
        }
        holder.tvListForecastDateAndDayName.append(date);

        Glide.with(mContext)
                .load("http://openweathermap.org/img/w/" + model.getIcon() + ".png")
                .into(holder.imListForecastListIcon);
    }

    @Override
    public int getItemCount() {
        return foreCastes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // @BindView(R.id.tvListForecastTemp)
        TextView tvListForecastTemp;

        //  @BindView(R.id.tvListForecastDescription)
        TextView tvListForecastDescription;

        //@BindView(R.id.tvListForecastWind)
        TextView tvListForecastWind;

        // @BindView(R.id.tvListForecastHumidity)
        TextView tvListForecastHumidity;

        //  @BindView(R.id.tvListForecastPressure)
        TextView tvListForecastPressure;

        // @BindView(R.id.tvListForecastDateAndDayName)
        TextView tvListForecastDateAndDayName;

        // @BindView(R.id.tvListForecastTime)
        TextView tvListForecastTime;

        // @BindView(R.id.imListForecastListIcon)
        ImageView imListForecastListIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            //   ButterKnife.bind(mContext, itemView);
            tvListForecastTemp = (TextView) itemView.findViewById(R.id.tvListForecastTemp);
            tvListForecastDescription = (TextView) itemView.findViewById(R.id.tvListForecastDescription);
            tvListForecastHumidity = (TextView) itemView.findViewById(R.id.tvListForecastHumidity);
            tvListForecastPressure = (TextView) itemView.findViewById(R.id.tvListForecastPressure);
            tvListForecastWind = (TextView) itemView.findViewById(R.id.tvListForecastWind);
            tvListForecastTime = (TextView) itemView.findViewById(R.id.tvListForecastTime);
            tvListForecastDateAndDayName = (TextView) itemView.findViewById(R.id.tvListForecastDateAndDayName);
            imListForecastListIcon = (ImageView) itemView.findViewById(R.id.imListForecastListIcon);
        }
    }
}
