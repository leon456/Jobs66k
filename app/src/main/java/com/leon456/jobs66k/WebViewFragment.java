package com.leon456.jobs66k;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class WebViewFragment extends Fragment {
	

	public WebViewFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = this.getArguments();
        String url = args.getString("detail");


		View view = inflater.inflate(R.layout.fragment_web_view, container,false);


		
		WebView webView = (WebView) view.findViewById(R.id.webView);


		WebSettings settings = webView.getSettings();
		settings.setLoadsImagesAutomatically(true);
		settings.setAllowFileAccess(true);
		settings.setSaveFormData(true);
		// webView.getSettings().setBuiltInZoomControls(false);
		// webView.getSettings().setSupportZoom(true);
		// webView.getSettings().setLoadWithOverviewMode(true);
		// webView.getSettings().setUseWideViewPort(true);
		// webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		// webView.setScrollbarFadingEnabled(false);

		webView.loadUrl(url);

		return view;
	}
	

}
