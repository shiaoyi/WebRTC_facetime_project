package org.appspot.apprtc;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReceiptFragment extends Fragment {

    ReceiptAdapter receiptAdapter;
//    int year = 2018;
//    int month = 4;
//    String time = year + "年" + month + "月明細";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_receipt, container, false);

        List<Receipt> receiptList = new ArrayList<>();
        receiptList.add(new Receipt("2018年5月","141354782","未入款","TWD "+180,"2018-5-31"));
        receiptList.add(new Receipt("2018年4月","780686796","已匯入","TWD "+120,"2018-4-30"));
        receiptList.add(new Receipt("2018年3月","526534987","已匯入","TWD "+70,"2018-3-31"));
        receiptList.add(new Receipt("2018年2月","245672548","已匯入","TWD "+2000,"2018-2-28"));
        receiptList.add(new Receipt("2018年1月","534287287","已匯入","TWD "+20,"2018-1-31"));


        RecyclerView lvReceipt = (RecyclerView) view.findViewById(R.id.lvReceipt);
        lvReceipt.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        receiptAdapter = new ReceiptAdapter(inflater, receiptList);
        lvReceipt.setAdapter(receiptAdapter);
        return view;
    }

    private class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
        private LayoutInflater inflater;
        List<Receipt> receiptList;

        ReceiptAdapter(LayoutInflater inflater, List<Receipt> receiptList) {
            this.inflater = inflater;
            this.receiptList = receiptList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.receipt_view, viewGroup, false);
            return new ViewHolder(itemView);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTime, tvId, tvSituate, tvDollar, tvPayday;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTime = (TextView) itemView.findViewById(R.id.tvTimeReceipt);
                tvId = (TextView) itemView.findViewById(R.id.idReceipt);
                tvSituate = (TextView) itemView.findViewById(R.id.situationReceipt);
                tvDollar = (TextView) itemView.findViewById(R.id.dollarReceipt);
                tvPayday = (TextView) itemView.findViewById(R.id.paydayReceipt);
            }
        }

        @Override
        public int getItemCount() {
            return receiptList.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            Receipt receipt = receiptList.get(position);

            viewHolder.tvTime.setText(receipt.getTime());


            viewHolder.tvId.setText(receipt.getId());


            viewHolder.tvSituate.setText(receipt.getSituate());


            viewHolder.tvDollar.setText(receipt.getDollar());


            viewHolder.tvPayday.setText(receipt.getPayday());

        }

    }

}
