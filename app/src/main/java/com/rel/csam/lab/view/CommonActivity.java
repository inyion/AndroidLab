package com.rel.csam.lab.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.rel.csam.lab.api.GoogleAPI;

import java.util.HashMap;


public abstract class CommonActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    //    public static final int NETWORK_CONNECTED = 9100;
    public static final int NETWORK_DISCONNECTED = 9200;

    public static final int HANDLE_MESSAGE_ON_PAUSE = 9111;
    public static final int HANDLE_MESSAGE_ON_PAUSE_LONG_TIME = 9112;

    private static final String TAG = "CommonActivity";

    /**
     * sQmsConnectionCheckCnt
     * 연속으로 10초사이에 3번의 실패를 받아야 처리되도록 되어있다.
     * 아이폰은 1초마다 10번 돌려서 그사이에 3번의 메세지를 받았는지를 체크하는데 결국 같다.
     */
    public static int sQmsConnectionCheckCnt = 0;
    protected int mAvailableNetworkType = -1;

    // BroadcastReceiver
    private HashMap<String, BroadcastReceiver> receiverMap = null;
    private BroadcastReceiver broadCastReceiver = null;

    private View keyboardCheckLayout;
    private View softInputView;
    private int mSelectTabID;
    private GoogleAPI googleAPI;
    private OnClickListener webListener;
    private OnClickListener loginListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initReceiver();         // 리시버 생성

    }

    @Override
    protected void onResume() {
        super.onResume();
        registReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int getSelectTabID() {
        return mSelectTabID;
    }

    public void setSelectTab(View view) {
        mSelectTabID = view.getId();
    }

    public void setSelectTabID(int id) {
        mSelectTabID = id;
    }

    public GoogleAPI getGoogleAPI() {
        googleAPI = new GoogleAPI(this);
        return googleAPI;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (googleAPI != null && requestCode == GoogleAPI.RC_SIGN_IN) {
            googleAPI.onActivityResult(data);
        }
    }

    private void initReceiver() {
        if (broadCastReceiver == null) {
            broadCastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (receiverMap != null && intent != null) {
                        receiverMap.get(intent.getAction()).onReceive(context, intent);
                    }
                }
            };
        }
    }

    /**
     * Date : 2014. 5. 22.
     * Creater : cookie
     * Description : 사용하는 곳에서 재정의할때 마지막에 super(); 필수
     * abstract으로 안한건 안쓰는곳도 많아서
     */
    public void registReceiver() {
        if (receiverMap == null) receiverMap = new HashMap<>();
        IntentFilter filter = new IntentFilter();
        for (String actionName : receiverMap.keySet()) {
            filter.addAction(actionName);
        }

        initReceiver();
        registerReceiver(broadCastReceiver, filter);

    }

    @Override
    public void onBackPressed() {
        try {
            setResult(RESULT_OK);
            super.onBackPressed();
        } catch (Exception e) {

        }
    }

    /**
     * 이결과가 요청하지도 않은게 오지는 않을거라는 전제하에 추가
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_HOME) {
//            //GMKT_Log.i("QTalkPresenceActivity onKeyUp KEYCODE_HOME");
//        }
        return super.onKeyUp(keyCode, event);
    }

    /*
     * Date : 2014. 11. 19.
     * Commenter : cookie
     * Description : 통화 볼륨 조절
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {

            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    break;

                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    break;

                default:
                    break;
            }

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_HOME) {
//			//GMKT_Log.i("QTalkPresenceActivity onKeyDown KEYCODE_HOME");
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // 기존 핸들링 처리 mvvm 리펙토링 처리가 버거울때.. 사용
    public void handleAction(String action) {
        // override 용 없으면 아무것도 안함
    }

    public void directAction(String action) {
        // override 용 없으면 아무것도 안함
    }

    protected void test() {

    }

    /**
     * 이거 오버라이드 하지마요 위에껄 오버라이드하세요 (레이아웃 바인드용)
     *
     * @param view
     */
    public void test(View view) {
        test();
    }
}
