package com.example.ufanet.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterContacts extends BaseAdapter {

	ArrayList<ItemContacts> data = new ArrayList<ItemContacts>();
	Context context;

	public AdapterContacts(Context context, ArrayList<ItemContacts> arr) {
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

	public ArrayList<ItemContacts> getData () {
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
			someView = inflater.inflate(R.layout.contact_list_view_item, arg2, false);
		}
		//Обявляем наши текствьюшки и связываем их с разметкой
		TextView header = (TextView) someView.findViewById(R.id.item_headerText);
		TextView subHeader = (TextView) someView.findViewById(R.id.item_subHeaderText);
		
		//Устанавливаем в каждую текствьюшку соответствующий текст
		// сначала заголовок
		header.setText(data.get(i).header);
		// потом подзаголовок
		subHeader.setText(data.get(i).subHeader);
		return someView;
	}

}
