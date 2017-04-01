package com.chebyr.royalpatiala.ui.payments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chebyr.royalpatiala.R;
import com.chebyr.appshell.medialoader.MediaLoader;

import java.util.List;

/**
 * Created by Administrator on 24/03/2017.
 */

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.PaymentsViewHolder>
{
    private List<String> contactList;
    private OnClickListener callback;

    public PaymentsAdapter(OnClickListener callback, List<String> contactList)
    {
        this.contactList = contactList;
        this.callback = callback;
    }

    @Override
    public PaymentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_payments, parent, false);

        return new PaymentsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PaymentsViewHolder holder, final int position)
    {
        final String contactName = contactList.get(position);
        holder.contactNameView.setText(contactName);
        holder.contactImage.setOnClickListener(holder);
        holder.contactImage.setTag(position);

    }

    @Override
    public int getItemCount()
    {
        return contactList.size();
    }

    public void addContact(String contact)
    {
        if (contact == null) {
            throw new NullPointerException("Infos may not be null!");
        }

        contactList.add(contact);
        notifyDataSetChanged();
    }

    public class PaymentsViewHolder extends RecyclerView.ViewHolder
            implements MediaLoader.SuccessCallback, View.OnClickListener
    {
        private ImageView contactImage;
        private TextView contactNameView;
        private TextView amountTotal;
        private TextView amountPaid;
        private TextView amountBalance;
        private Button receivePayment;

        public PaymentsViewHolder(View view)
        {
            super(view);

            contactImage = (ImageView) view.findViewById(R.id.contact_image);
            contactNameView = (TextView)view.findViewById(R.id.contact_name);
            amountTotal = (TextView)view.findViewById(R.id.amount_total);
            amountPaid = (TextView)view.findViewById(R.id.amount_paid);
            amountBalance = (TextView)view.findViewById(R.id.amount_balance);
            receivePayment = (Button) view.findViewById(R.id.receive_payment);
        }

        @Override
        public void onSuccess()
        {

        }

        @Override
        public void onClick(View v)
        {
            int position = (int)v.getTag();
            callback.onClick(position);
        }
    }



    public interface OnClickListener
    {
        void onClick(int position);
    }

}