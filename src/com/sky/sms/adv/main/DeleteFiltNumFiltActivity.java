package com.sky.sms.adv.main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.sky.sms.adv.dao.MyMessageDao;
import com.sky.sms.adv.domain.DataException;
import com.sky.sms.adv.domain.FiltInfo;
import com.sky.sms.adv.utils.DaoFactory;

public class DeleteFiltNumFiltActivity extends ListActivity implements
		OnClickListener {

	private Button deleteSelectButton = null;

	private Button deleteNoSelectButton = null;

	private Button deleteFiltNumButton = null;

	private List<FiltInfo> deleteNumList = null;

	private MyMessageDao myMessageDao = null;

	private ListView deleteNumListView;

	private DeleteListMyAdapter delNumAdapter = null;

	private AlertDialog messageOperSureDialog = null;

	private String TAG = "DeleteFiltNumFiltActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.delete_sms_filt_tel_num_list);
		deleteNumListView = (ListView) getListView();
		DaoFactory.getMessageDao(getApplicationContext());
		myMessageDao = DaoFactory.getMessageDao(DeleteFiltNumFiltActivity.this);
		try {
			deleteNumList = myMessageDao.getFiltNum();
		} catch (DataException e) {
			Log.e(TAG, "onCreate " + e.getMessage());
		}
		deleteSelectButton = (Button) findViewById(R.id.delete_select_filt_num_button);
		deleteNoSelectButton = (Button) findViewById(R.id.delete_no_select_filt_num_button);
		deleteFiltNumButton = (Button) findViewById(R.id.delete_more_filt_num_button);
		deleteSelectButton.setOnClickListener(this);
		deleteNoSelectButton.setOnClickListener(this);
		deleteFiltNumButton.setOnClickListener(this);
		delNumAdapter = new DeleteListMyAdapter(getApplicationContext(),
				deleteNumList);
		deleteNumListView.setAdapter(delNumAdapter);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_select_filt_num_button:
			int length = deleteNumListView.getChildCount();
			for (int i = 0; i < length; i++) {
				View view = deleteNumListView.getChildAt(i);
				CheckBox cb = (CheckBox) view
						.findViewById(R.id.delete_filt_num_checkbox);
				cb.setChecked(true);
			}
			break;
		case R.id.delete_no_select_filt_num_button:
			int length2 = deleteNumListView.getChildCount();
			for (int i = 0; i < length2; i++) {
				View view = deleteNumListView.getChildAt(i);
				CheckBox cb = (CheckBox) view
						.findViewById(R.id.delete_filt_num_checkbox);
				cb.setChecked(false);
			}
			break;
		case R.id.delete_more_filt_num_button:
			final ArrayList<FiltInfo> tempDeleteList = getSelectDelete(deleteNumList);
			if (tempDeleteList.size() > 0) {
				messageOperSureDialog = new AlertDialog.Builder(
						DeleteFiltNumFiltActivity.this)
						.setIcon(R.drawable.filt_num_del_icon)
						.setTitle(
								getResources()
										.getString(
												R.string.single_filt_num_delete_confirm_title))
						.setMessage(
								getResources()
										.getString(
												R.string.single_filt_num_delete_confirm_message))
						.setPositiveButton(
								getResources().getString(
										R.string.single_filt_num_yes_delete),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										for (FiltInfo filtInfo : tempDeleteList) {
											try {
												myMessageDao
														.deleteFiltNum(filtInfo
																.getNum());
											} catch (DataException e) {
												Log.e(TAG, e.getMessage());
											}
										}
										setResult(RESULT_OK);
										DeleteFiltNumFiltActivity.this.finish();
									}
								})
						.setNegativeButton(
								getResources().getString(
										R.string.single_filt_num_no_delete), // 设置“取消”按钮
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										finish();
										// 点击"取消"按钮之后退出程序
										messageOperSureDialog.dismiss();
									}
								}).create();
				messageOperSureDialog.show();
			}
			break;
		}
	}

	// 返回被选择的短信
	public ArrayList<FiltInfo> getSelectDelete(List<FiltInfo> deleteNumList) {
		ArrayList<FiltInfo> myMessageList = new ArrayList<FiltInfo>();
		for (FiltInfo filtInfo : deleteNumList) {
			if (filtInfo.isChecked()) {
				myMessageList.add(filtInfo);
			}
		}
		return myMessageList;
	}

	static class ViewHolder {

		public CheckBox checkbox;

		public TextView num;// 标题

		public TextView name;// 摘要

	}

	class DeleteListMyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		private List<FiltInfo> deleteNumList;

		public DeleteListMyAdapter(Context context, List<FiltInfo> deleteNumList) {
			// 参数初始化
			mInflater = LayoutInflater.from(context);
			this.deleteNumList = deleteNumList;
		}

		public int getCount() {
			return deleteNumList.size();
		}

		public FiltInfo getItem(int position) {
			return deleteNumList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			holder = new ViewHolder();
			final FiltInfo filtInfo = deleteNumList.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.delete_filt_num_list,
						null);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.delete_filt_num_checkbox);
				holder.name = ((TextView) convertView
						.findViewById(R.id.delete_filt_name));
				holder.num = (TextView) convertView
						.findViewById(R.id.delete_filt_num);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setChecked(filtInfo.isChecked());
			holder.name.setText(filtInfo.getName());
			holder.num.setText(filtInfo.getNum());
			holder.checkbox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							filtInfo.setChecked(isChecked);
						}
					});
			convertView.setTag(holder);
			return convertView;
		}
	}
}
