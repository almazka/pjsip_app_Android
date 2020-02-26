package com.telefon.ufanet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ufanet.myapplication.R;

import java.util.ArrayList;

public class AdapterContactsWorkers extends BaseAdapter {

	ArrayList<ItemContactsWorkers> data = new ArrayList<ItemContactsWorkers>();
	Context context;

	public AdapterContactsWorkers(Context context, ArrayList<ItemContactsWorkers> arr) {
		if (arr != null) {
			data = arr;
		}
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public ArrayList<ItemContactsWorkers> getData () {
		return data;
	}

	@Override
	public Object getItem(int num) {
		// TODO Auto-generated method stub
		return data.get(num);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int i, View someView, ViewGroup arg2) {
		//Получение объекта inflater из контекста
		LayoutInflater inflater = LayoutInflater.from(context);
		//Если someView (View из ListView) вдруг оказался равен
		//null тогда мы загружаем его с помошью inflater 
		if (someView == null) {
			someView = inflater.inflate(R.layout.worked_contacts, arg2, false);
		}
		//Обявляем наши текствьюшки и связываем их с разметкой
		TextView header = (TextView) someView.findViewById(R.id.item_headerText);
		TextView subHeader = (TextView) someView.findViewById(R.id.item_subHeaderText);
		TextView number = (TextView) someView.findViewById(R.id.item_number);

		//Устанавливаем в каждую текствьюшку соответствующий текст
		// сначала заголовок
		header.setText(data.get(i).header);
		// потом подзаголовок
		subHeader.setText(data.get(i).subHeader);

		number.setText(data.get(i).mail);
		return someView;
	}

}
