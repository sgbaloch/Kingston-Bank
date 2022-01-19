package com.agbaloch.kingstonbank.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agbaloch.kingstonbank.Models.Transaction;
import com.agbaloch.kingstonbank.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ViewHolder>{

    private List<Transaction> transactionList;

    private double balance = 0;

    public StatementAdapter(List<Transaction> list){

        transactionList = list;
    }

    @NonNull
    @Override
    public StatementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_statement, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatementAdapter.ViewHolder holder, int position) {

        Transaction transaction = transactionList.get(position);


        String pattern = "dd MMM, kk:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(transaction.getDateTime().toDate());

        holder.txtDate.setText(date);

        if(transaction.getTransactionType() == 1){

            holder.txtType.setText("Cash deposit");
            holder.txtIn.setText(String.valueOf(transaction.getAmount()));
            holder.txtOut.setVisibility(View.INVISIBLE);

            balance = balance + transaction.getAmount();
            holder.txtBalance.setText(String.valueOf(balance));
        }

        else if(transaction.getTransactionType() == 2){

            holder.txtType.setText("Cash withdrawal");
            holder.txtOut.setText(String.valueOf(transaction.getAmount()));
            holder.txtIn.setVisibility(View.INVISIBLE);

            balance = balance - transaction.getAmount();

            holder.txtBalance.setText(String.valueOf(balance));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate, txtType, txtIn, txtOut, txtBalance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txt_date);
            txtType = itemView.findViewById(R.id.txt_type);
            txtIn = itemView.findViewById(R.id.txt_in);
            txtOut = itemView.findViewById(R.id.txt_out);
            txtBalance = itemView.findViewById(R.id.txt_balance);
        }
    }
}
