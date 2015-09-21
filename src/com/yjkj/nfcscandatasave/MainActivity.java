package com.yjkj.nfcscandatasave;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yjkj.nfcscandatasave.util.AppUtil;
import com.yjxx.nfccheck.R;

@SuppressLint("NewApi")
public class MainActivity extends Activity{
	// ��ʼ����
	private TextView title_tv;
	// NFC��ʾ����
	private LinearLayout nfcinfo_ll;
	// ʱ��
	private TextView nfcscantime_tv;
	// NFC��ʼ���кſؼ�
	private TextView nfcuid_tv;
	// NFC���к�
	private TextView nfcuidhex_tv;
	//��ǰʱ���
	private long systemtime;
	// ����
	Map<String, String> map;
	// NFC���к���Ϣ
	private String tagIdHex;
	// NFCԭʼ���к���Ϣ
	private String nfcId;
	// NFC������
	private NfcAdapter nfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		ndef.addCategory("*/*");
		mFilters = new IntentFilter[] { ndef };// ������
		mTechLists = new String[][] {
				new String[] { MifareClassic.class.getName() },
				new String[] { NfcA.class.getName() } };// ����ɨ��ı�ǩ����
		if (nfcAdapter == null) {
			Toast.makeText(MainActivity.this, "����豸��֧��NFC����",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		if (!nfcAdapter.isEnabled()) {
			Toast.makeText(MainActivity.this, "����豸δ����NFC����",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		onNewIntent(getIntent());
	}

	// ��ʼ���ؼ�
	private void initView() {
		title_tv = (TextView) findViewById(R.id.title_tv);
		nfcscantime_tv = (TextView) findViewById(R.id.nfcscantime_tv);
		nfcuid_tv = (TextView) findViewById(R.id.nfcuid_tv);
		nfcuidhex_tv = (TextView) findViewById(R.id.nfcuidhex_tv);
		title_tv = (TextView) findViewById(R.id.title_tv);
		nfcinfo_ll = (LinearLayout) findViewById(R.id.nfcinfo_ll);
		systemtime = 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK&&(System.currentTimeMillis()-systemtime)>2000){
			
			Toast.makeText(getBaseContext(), "�ٰ�һ�η���", Toast.LENGTH_SHORT).show();
		}else{
			finish();
		}
		systemtime = System.currentTimeMillis();
		return false;
	}

	private void hit_btn(){
		title_tv.setVisibility(View.GONE);
		nfcinfo_ll.setVisibility(View.VISIBLE);
	}
	
	public void onNewIntent(Intent intent) {
		System.out.println("Action:++++"+intent.getAction());
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			processIntent(intent);
			hit_btn();
			nfcuidhex_tv.setText("���кţ�" +tagIdHex);
			nfcuid_tv.setText("ԭʼ���кţ�" +nfcId);
			nfcscantime_tv.setText("ɨ��ʱ�䣺" + AppUtil.getNowTimeToString("yyyy-MM-dd HH:mm:ss"));
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
        }
	}

	private void processIntent(Intent intent) {
		Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if(rawArray!=null){
		NdefMessage nm= (NdefMessage) rawArray[0];
		NdefRecord[] records = nm.getRecords();
		NdefRecord ndefRecord = records[0];
		System.out.println("getTnf:"+ndefRecord.getTnf());
		String getId = bytes2sString(ndefRecord.getId());
		System.out.println("getId:"+getId);
		String getType = bytes2sString(ndefRecord.getType());
		System.out.println("getType:"+getType);
		String payload = bytes2sString(ndefRecord.getPayload());
		System.out.println("getPayload:"+payload);
		
		
		}
		Tag tag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		IsoDep isodep = IsoDep.get(tag);
		if(isodep!=null){
			try {
				isodep.connect();
			        byte[] DFI_EP = { (byte) 0x10, (byte) 0x01 };

			        byte[] result = isodep.transceive(DFI_EP);
			        System.out.println("result"+bytes2sString(result.clone()));
			        
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			String getHiLayerResponse = bytes2sString(isodep.getHiLayerResponse());
			String getHistoricalBytes = bytes2sString(isodep.getHistoricalBytes());
		}
		NfcV nfcv = NfcV.get(tag);
		if(nfcv!=null){
			String getDsfId = bytes2sString(new byte[]{nfcv.getDsfId()});
			String getResponseFlags = bytes2sString(new byte[]{nfcv.getResponseFlags()});
		}
		NfcF nfcf = NfcF.get(tag);
		if(nfcf!=null){
			String getManufacturer = bytes2sString(nfcf.getManufacturer());
			String getSystemCode = bytes2sString(nfcf.getSystemCode());
		}
		
		NfcA nfca=NfcA.get(tag);
		if(nfca!=null){
			String getAtqa = bytes2sString(nfca.getAtqa());
			System.out.println("nfca.getAtqa"+getAtqa);
		}
		MifareUltralight mifareultralight=MifareUltralight.get(tag);
		if(mifareultralight!=null){
			mifareultralight.getMaxTransceiveLength();
		}
		if(tag!=null){
			String id = bytes2sString(tag.getId());
			System.out.println("ID:"+id);
			for(String t:tag.getTechList()){
				System.out.println("TAG_Stype:"+t);
				
			}
		}
		
		byte[] arrayOfByte = intent.getByteArrayExtra("android.nfc.extra.ID");
		tagIdHex = AppUtil.ByteArrayToHexString(arrayOfByte);
		nfcId = AppUtil.getNFCUid(arrayOfByte);
	}

	private String bytes2sString(byte[] bytes){
		
		try {
			if(bytes!=null){
				System.out.println("toString---:"+new String(bytes, "UTF-8"));
			return new String(bytes, "UTF-8");
			}else{
				return "";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
}
