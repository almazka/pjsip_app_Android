package com.telefon.ufanet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ufanet.myapplication.R;

import java.util.ArrayList;

public class AdapterCalls extends BaseAdapter {

	ArrayList<ItemCalls> data = new ArrayList<>();
	Context context;

	public AdapterCalls(Context context, ArrayList<ItemCalls> arr) {
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
		ImageView img = (ImageView) someView.findViewById(R.id.profile_photo) ;
		
		//Устанавливаем в каждую текствьюшку соответствующий текст
		// сначала заголовок

		if (data.get(i).name == null) {
			header.setText(data.get(i).header);
		}
		else {
			header.setText(data.get(i).name);
		}
		// потом подзаголовок

		if (data.get(i).subHeader == "Входящий") {
			img.setImageResource(R.drawable.downleft);
		}
		if (data.get(i).subHeader == "Исходящий") {
			img.setImageResource(R.drawable.upright);
		}

		if (data.get(i).subHeader == "Пропущенный") {
			img.setImageResource(R.drawable.missed_call);
		}


		subHeader.setText(data.get(i).message);




		return someView;
	}

}
