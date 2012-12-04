package com.dspsemi.testflash;

import com.sinovoice.tts.TtsPlayerWrapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TestTextSpeaker extends Activity {
	private WebView mWebView;

	private TtsPlayerWrapper mTTSPlayer;
	private Context mContext;
	private SharedPreferences mSharedData;
	private int mIntVoiceType = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		loadPreference();

		Toast.makeText(
				mContext,
				"Download and install http://tianxing.com:8090/Content/Android/SharedTTS.apk",
				Toast.LENGTH_LONG).show();

		initWebview();

		mTTSPlayer = new TtsPlayerWrapper(getApplicationContext());
		setVoiceType();
		mTTSPlayer.playText("你好");

	}

	private void initWebview() {
		mWebView = (WebView) findViewById(R.id.webView);

		WebSettings ws = mWebView.getSettings();
		ws.setPluginsEnabled(true);
		ws.setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(final ConsoleMessage cm) {
				Log.d(this.getClass().getName(), cm.message());
				Thread speak_thread = new Thread(new Runnable() {

					@Override
					public void run() {
						mTTSPlayer.ttsStop();
						mTTSPlayer.playText(cm.message());
					}
				});
				speak_thread.start();
				return true;
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				System.out.println("---------------------- scroll to");
				mWebView.scrollTo(0, 1024);
			}
		});
		mWebView.loadUrl("file:///android_asset/antusheng.html");

	}

	private void loadPreference() {
		mContext = getApplicationContext();
		mSharedData = mContext.getSharedPreferences("VoiceBookSharedData", 0);
	}

	private void setVoiceType() {
		int i = this.mSharedData.getInt("VoiceType", -1);
		if (i == -1) {
			i = 0;
			SharedPreferences.Editor localEditor = this.mSharedData.edit();
			localEditor.putInt("VoiceType", 0);
			localEditor.commit();
		}
		if (this.mIntVoiceType != i) {
			Log.e("ReadingActivity", "mIntVoiceType = " + this.mIntVoiceType);
			mIntVoiceType = i;
			mTTSPlayer.ttsStop();
			mTTSPlayer.ttsEnd();
			mTTSPlayer.initTTSLib(2); // 默认童声
		}
	}

	private void setVoiceSpeed() {
		mTTSPlayer.SetParam(1, -3000L);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTTSPlayer.ttsStop();
		mTTSPlayer.ttsEnd();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (mTTSPlayer != null)
			mTTSPlayer.ttsStop();
	}
}
